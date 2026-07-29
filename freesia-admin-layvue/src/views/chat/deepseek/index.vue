<template>
  <div class="deepseek-chat-container">
    <FsesAiChat
      ref="chatRef"
      :api-base-url="apiBaseUrl"
      :transport="transport"
      :headers="authHeaders"
      welcome-text="开始和 DeepSeek 对话吧"
      :enable-chat-history="true"
      :auto-load-conversations="true"
      :chatMode="'runtime'"
      @error="onError"
      @attachment-upload="onAttachmentUpload"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { FsesAiChat } from '@fses/ai-chat'
import type { FsesAiChatTransport } from '@fses/ai-chat'
import '@fses/ai-chat/dist/style.css'
import '@opentiny/tiny-robot/dist/style.css'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'

const baseUrl = import.meta.env.VITE_APP_BASE_URL as string
const apiBaseUrl = `${baseUrl}/api`

const userStore = useUserStore()
const appStore = useAppStore()

// FsesAiChat 的 headers getter —— 每次请求前调用，注入认证信息
const authHeaders = () => {
  const headers: Record<string, string> = {}
  if (userStore.token) headers['Authorization'] = `Bearer ${userStore.token}`
  if (appStore.currentTenant) headers['X-Tenant-Id'] = appStore.currentTenant
  return headers
}

// 构建 fetch 用的 headers
function buildFetchHeaders(): Record<string, string> {
  const headers: Record<string, string> = {}
  if (userStore.token) headers['Authorization'] = `Bearer ${userStore.token}`
  if (appStore.currentTenant) headers['X-Tenant-Id'] = appStore.currentTenant
  return headers
}

const chatRef = ref<any>(null)
// 缓存待分析的文件内容，仅加入 DeepSeek 请求，不存入 DB
const fileCache = ref<{ fileName: string; content: string } | null>(null)

const onError = (err: Error) => {
  console.error('[DeepseekChat]', err)
}

function isBinaryFile(fileName: string, mimeType: string): boolean {
  const lower = fileName.toLowerCase()
  return lower.endsWith('.xlsx') || lower.endsWith('.xls') ||
    lower.endsWith('.csv') || lower.endsWith('.pdf') ||
    lower.endsWith('.docx') || lower.endsWith('.doc') ||
    lower.endsWith('.pptx') || lower.endsWith('.ppt')
}

async function uploadAndParse(file: File, fileName: string): Promise<string> {
  const formData = new FormData()
  formData.append('file', file, fileName)
  const res = await fetch(`${baseUrl}/api/chat/upload`, {
    method: 'POST',
    headers: buildFetchHeaders(),
    body: formData,
  })
  if (!res.ok) throw new Error(`上传失败: HTTP ${res.status}`)
  const data = await res.json()
  if (!data.success) throw new Error(data.error || '解析失败')
  return data.content
}

function readImageAsBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = () => reject(new Error('图片读取失败'))
    reader.readAsDataURL(file)
  })
}

async function onAttachmentUpload(payload: any) {
  const { file, fileName, mimeType, messageId } = payload
  if (!file) return

  chatRef.value?.updateImageUploadMessage?.(messageId, {
    content: `正在解析 ${fileName}...`,
    loading: true,
  })

  try {
    let content: string
    if (isBinaryFile(fileName, mimeType)) {
      content = await uploadAndParse(file, fileName)
    } else if (mimeType.startsWith('image/')) {
      content = await readImageAsBase64(file)
    } else {
      content = await file.text()
    }

    chatRef.value?.updateImageUploadMessage?.(messageId, {
      content: `已解析 ${fileName}`,
      loading: false,
    })

    // 缓存文件内容，仅给 DeepSeek 看，不存入数据库
    fileCache.value = { fileName, content }

    // 发送简短消息，存储到数据库的只有这一行
    const shortMsg = `我上传了一个文件：${fileName}，请帮我分析其中的内容`
    chatRef.value?.sendText?.(shortMsg)
  } catch (err: any) {
    chatRef.value?.updateImageUploadMessage?.(messageId, {
      content: `解析失败: ${err.message}`,
      error: err.message,
      loading: false,
    })
  }
}

const transport: FsesAiChatTransport = async (ctx) => {
  const { appendMessage, input, messages, signal } = ctx

  const assistantMsg = appendMessage({
    role: 'assistant',
    content: '',
    loading: true,
    streaming: true,
  })

  const historyMessages: any[] = messages
    .filter((m) => m.role === 'user' || m.role === 'assistant')
    .map((m) => ({ role: m.role, content: m.content }))

  // 如果有缓存的文件内容，作为 system 消息注入到 DeepSeek 请求中
  // 注意：不修改 ctx.messages，文件内容不会被保存到数据库
  if (fileCache.value) {
    historyMessages.push({
      role: 'system',
      content: `用户上传了文件"${fileCache.value.fileName}"，文件内容如下：\n\n${fileCache.value.content}\n\n请基于以上文件内容回答用户的问题。`,
    })
  }

  try {
    const response = await fetch(`${baseUrl}/api/chat/stream`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...buildFetchHeaders() },
      body: JSON.stringify({ prompt: input, messages: historyMessages }),
      signal,
    })

    if (!response.ok) {
      assistantMsg.content = `请求失败: HTTP ${response.status}`
      assistantMsg.loading = false
      assistantMsg.streaming = false
      return
    }

    const reader = response.body!.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() ?? ''

      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed) continue

        let data = trimmed
        if (data.startsWith('data:')) data = data.slice(5).trim()
        if (!data || data === '[DONE]') continue

        try {
          const parsed = JSON.parse(data)
          const delta = parsed?.choices?.[0]?.delta?.content
          if (delta) {
            if (assistantMsg.loading) assistantMsg.loading = false
            assistantMsg.content += delta
          }
        } catch {
          // skip
        }
      }
    }

    const remaining = buffer.trim()
    if (remaining.startsWith('data:')) {
      const data = remaining.slice(5).trim()
      if (data && data !== '[DONE]') {
        try {
          const parsed = JSON.parse(data)
          const delta = parsed?.choices?.[0]?.delta?.content
          if (delta) {
            if (assistantMsg.loading) assistantMsg.loading = false
            assistantMsg.content += delta
          }
        } catch { /* skip */ }
      }
    }
  } catch (err: unknown) {
    if (!(err instanceof DOMException && err.name === 'AbortError')) {
      assistantMsg.content = assistantMsg.content || `请求异常: ${String(err)}`
    }
  } finally {
    assistantMsg.loading = false
    assistantMsg.streaming = false
    // 清除文件缓存
    fileCache.value = null
  }
}
</script>

<style>
.deepseek-chat-container {
  width: 60%;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin: 0 auto;
  --chat-font: 16px;
  --chat-font-mono: 16px;
  --runtime-chat-font-body: 16px;
}

.deepseek-chat-container .chat-workspace__recommend-grid,
.deepseek-chat-container .chat-workspace__prompt-list,
.deepseek-chat-container .chat-workspace__recommend,
.deepseek-chat-container .chat-workspace__prompt {
  display: none !important;
}
</style>
