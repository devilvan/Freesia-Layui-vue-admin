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
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { FsesAiChat } from '@fses/ai-chat'
import type { FsesAiChatTransport } from '@fses/ai-chat'
import '@fses/ai-chat/dist/style.css'
import '@opentiny/tiny-robot/dist/style.css'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

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

// ---- 代码块增强：语言标签 + 复制按钮 ----
let codeBlockObserver: MutationObserver | null = null

function enhanceCodeBlocks(container: HTMLElement) {
  // 暂停观察，避免 highlightElement 修改 DOM 时触发自身
  codeBlockObserver?.disconnect()
  const pres = container.querySelectorAll<HTMLElement>('pre:not([data-code-enhanced])')
  pres.forEach(pre => {
    const code = pre.querySelector('code')
    // 提取语言
    const langClass = code?.className?.match(/language-(\w+)/)
    const lang = langClass ? langClass[1] : ''

    // 语法高亮
    if (code) {
      try {
        if (lang && hljs.getLanguage(lang)) {
          code.className = `hljs language-${lang}`
          hljs.highlightElement(code)
        } else {
          code.className = 'hljs'
          hljs.highlightElement(code)
        }
      } catch {
        // 高亮失败则保持原样
      }
    }

    // 工具栏
    const toolbar = document.createElement('div')
    toolbar.className = 'code-block-toolbar'
    if (lang) {
      const langTag = document.createElement('span')
      langTag.className = 'code-block-lang'
      langTag.textContent = lang
      toolbar.appendChild(langTag)
    }
    const copyBtn = document.createElement('button')
    copyBtn.className = 'code-block-copy-btn'
    copyBtn.textContent = '复制'
    copyBtn.onclick = () => {
      const text = code?.textContent || pre.textContent || ''
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
          copyBtn.textContent = '已复制'
          setTimeout(() => { copyBtn.textContent = '复制' }, 2000)
        }).catch(() => {
          copyBtn.textContent = '失败'
          setTimeout(() => { copyBtn.textContent = '复制' }, 1500)
        })
      } else {
        // HTTP 环境降级：document.execCommand
        const textarea = document.createElement('textarea')
        textarea.value = text
        textarea.style.position = 'fixed'
        textarea.style.opacity = '0'
        document.body.appendChild(textarea)
        textarea.select()
        try {
          document.execCommand('copy')
          copyBtn.textContent = '已复制'
        } catch {
          copyBtn.textContent = '失败'
        }
        document.body.removeChild(textarea)
        setTimeout(() => { copyBtn.textContent = '复制' }, 2000)
      }
    }
    toolbar.appendChild(copyBtn)

    pre.style.position = 'relative'
    pre.insertBefore(toolbar, pre.firstChild)
    // 标记已处理
    pre.setAttribute('data-code-enhanced', '')
  })
  // 恢复观察
  codeBlockObserver?.observe(container, { childList: true, subtree: true })
}

function setupCodeBlockObserver() {
  const container = document.querySelector('.deepseek-chat-container')
  if (!container) return
  // 初始扫描
  enhanceCodeBlocks(container)
  // 监听新增内容
  codeBlockObserver = new MutationObserver(() => {
    enhanceCodeBlocks(container)
  })
  codeBlockObserver.observe(container, { childList: true, subtree: true })
}

function destroyCodeBlockObserver() {
  codeBlockObserver?.disconnect()
  codeBlockObserver = null
}

onMounted(() => {
  nextTick(() => setupCodeBlockObserver())
})

onUnmounted(() => {
  destroyCodeBlockObserver()
})
// ---- 代码块增强 END ----

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

  // 系统指令：要求代码块使用标准 markdown 格式
  historyMessages.unshift({
    role: 'system',
    content: '你是一个专业的编程助手。回答中涉及代码时，必须使用标准 markdown 代码块格式：\n\n```语言标识\n代码内容\n```\n\n例如 ```python、```javascript、```java、```sql、```bash 等。不要使用缩进方式表示代码，不要省略语言标识，不要将代码直接写在段落中。',
  })

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

/* 代码块增强 */
.deepseek-chat-container .code-block-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  background: #1e293b;
  border-bottom: 1px solid #334155;
}

.deepseek-chat-container .code-block-lang {
  font-size: 12px;
  color: #94a3b8;
  font-family: monospace;
  text-transform: lowercase;
}

.deepseek-chat-container .code-block-copy-btn {
  padding: 2px 10px;
  font-size: 12px;
  color: #94a3b8;
  background: transparent;
  border: 1px solid #475569;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.15s;
}

.deepseek-chat-container .code-block-copy-btn:hover {
  color: #e2e8f0;
  border-color: #64748b;
  background: #334155;
}

/* 代码块整体统一深色主题 */
.deepseek-chat-container pre {
  padding-top: 0 !important;
  margin: 12px 0 !important;
  border-radius: 8px !important;
  overflow: hidden;
  background: #1e293b !important;
  border: 1px solid #334155;
}

.deepseek-chat-container pre code {
  display: block;
  padding: 12px 16px !important;
  background: #1e293b !important;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  overflow-x: auto;
}

/* highlight.js 主题中的代码颜色优先级提升 */
.deepseek-chat-container pre code .hljs-keyword,
.deepseek-chat-container pre code .hljs-selector-tag,
.deepseek-chat-container pre code .hljs-literal,
.deepseek-chat-container pre code .hljs-section,
.deepseek-chat-container pre code .hljs-link { color: #c678dd; }
.deepseek-chat-container pre code .hljs-string,
.deepseek-chat-container pre code .hljs-title,
.deepseek-chat-container pre code .hljs-name,
.deepseek-chat-container pre code .hljs-type,
.deepseek-chat-container pre code .hljs-attr,
.deepseek-chat-container pre code .hljs-symbol,
.deepseek-chat-container pre code .hljs-bullet,
.deepseek-chat-container pre code .hljs-addition,
.deepseek-chat-container pre code .hljs-variable,
.deepseek-chat-container pre code .hljs-template-tag,
.deepseek-chat-container pre code .hljs-template-variable { color: #98c379; }
.deepseek-chat-container pre code .hljs-comment,
.deepseek-chat-container pre code .hljs-quote,
.deepseek-chat-container pre code .hljs-deletion,
.deepseek-chat-container pre code .hljs-meta { color: #5c6370; font-style: italic; }
.deepseek-chat-container pre code .hljs-number,
.deepseek-chat-container pre code .hljs-regexp,
.deepseek-chat-container pre code .hljs-selector-id,
.deepseek-chat-container pre code .hljs-selector-class { color: #d19a66; }
.deepseek-chat-container pre code .hljs-attr,
.deepseek-chat-container pre code .hljs-attribute,
.deepseek-chat-container pre code .hljs-selector-attr,
.deepseek-chat-container pre code .hljs-selector-pseudo { color: #e5c07b; }
.deepseek-chat-container pre code .hljs-built_in,
.deepseek-chat-container pre code .hljs-class .hljs-title { color: #e6c07b; }
</style>
