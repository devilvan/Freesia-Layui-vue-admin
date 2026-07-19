<template>
  <div class="deepseek-chat-container">
    <FsesAiChat
      :api-base-url="apiBaseUrl"
      :transport="transport"
      welcome-text="开始和 DeepSeek 对话吧"
      :enable-chat-history="true"
      :auto-load-conversations="true"
      @error="onError"
    />
  </div>
</template>

<script setup lang="ts">
import { FsesAiChat } from '@fses/ai-chat'
import type { FsesAiChatTransport } from '@fses/ai-chat'
import '@fses/ai-chat/dist/style.css'
import '@opentiny/tiny-robot/dist/style.css'

const baseUrl = import.meta.env.VITE_APP_BASE_URL as string
const apiBaseUrl = `${baseUrl}/api`

const onError = (err: Error) => {
  console.error('[DeepseekChat]', err)
}

const transport: FsesAiChatTransport = async (ctx) => {
  const { appendMessage, input, messages, signal } = ctx

  const assistantMsg = appendMessage({
    role: 'assistant',
    content: '',
    loading: true,
    streaming: true,
  })

  const historyMessages = messages
    .filter((m) => m.role === 'user' || m.role === 'assistant')
    .map((m) => ({ role: m.role, content: m.content }))

  try {
    const response = await fetch(`${baseUrl}/api/deepseek/chat/stream`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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
        if (data.startsWith('data:')) {
          data = data.slice(5).trim()
        }
        if (!data || data === '[DONE]') continue

        try {
          const parsed = JSON.parse(data)
          const delta = parsed?.choices?.[0]?.delta?.content
          if (delta) assistantMsg.content += delta
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
          if (delta) assistantMsg.content += delta
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
}
</style>
