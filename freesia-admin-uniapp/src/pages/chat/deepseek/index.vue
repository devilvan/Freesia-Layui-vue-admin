<template>
  <view class="chat-page">
    <!-- 消息列表 -->
    <scroll-view
      ref="msgListRef"
      class="chat-messages"
      scroll-y
      :scroll-into-view="scrollToId"
      :scroll-with-animation="true"
    >
      <view v-if="messages.length === 0" class="chat-empty">
        <text class="chat-empty-icon">🤖</text>
        <text class="chat-empty-text">开始和 DeepSeek 对话吧</text>
      </view>

      <view v-for="msg in messages" :key="msg.id" :id="'msg-' + msg.id">
        <view :class="['chat-bubble-row', msg.role === 'user' ? 'chat-bubble-row--right' : 'chat-bubble-row--left']">
          <view v-if="msg.role === 'assistant'" class="chat-avatar chat-avatar--ai">AI</view>
          <view :class="['chat-bubble', msg.role === 'user' ? 'chat-bubble--user' : 'chat-bubble--ai']">
            <text v-if="msg.loading && !msg.content" class="chat-loading">思考中...</text>
            <text v-else class="chat-content">{{ msg.content }}</text>
            <text v-if="msg.streaming" class="chat-cursor">|</text>
          </view>
          <view v-if="msg.role === 'user'" class="chat-avatar chat-avatar--user">我</view>
        </view>
      </view>

      <view style="height: 20rpx"></view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="chat-input-bar">
      <view class="chat-input-wrap">
        <input
          v-model="inputText"
          class="chat-input"
          type="text"
          placeholder="输入您的问题..."
          :disabled="isStreaming"
          confirm-type="send"
          @confirm="handleSend"
        />
      </view>
      <button
        v-if="!isStreaming"
        class="chat-send-btn"
        :disabled="!inputText.trim()"
        @click="handleSend"
      >发送</button>
      <button
        v-else
        class="chat-stop-btn"
        @click="stopStream"
      >停止</button>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'

const baseUrl = import.meta.env.VITE_APP_BASE_URL || ''
const apiBaseUrl = `${baseUrl}/api/chat`

const inputText = ref('')
const isStreaming = ref(false)
const messages = ref([])
const conversations = ref([])
const activeId = ref(null)
const scrollToId = ref('')
let abortController = null

onMounted(() => {
  loadConversations()
})

async function loadConversations() {
  try {
    const res = await uni.request({
      url: `${apiBaseUrl}/conversations?chatMode=runtime`,
      method: 'GET',
    })
    if (res.statusCode === 200) {
      conversations.value = (res.data.conversations || []).map(c => ({
        id: c.conversationId,
        title: c.title || '新会话',
      }))
    }
  } catch { /* ignore */ }
}

function scrollToBottom() {
  if (messages.value.length > 0) {
    const last = messages.value[messages.value.length - 1]
    scrollToId.value = 'msg-' + last.id
  }
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || isStreaming.value) return

  const userMsg = { id: `u-${Date.now()}`, role: 'user', content: text }
  messages.value.push(userMsg)
  inputText.value = ''
  nextTick(() => scrollToBottom())

  if (!activeId.value) {
    activeId.value = `conv-${Date.now()}`
  }

  const assistantMsg = { id: `a-${Date.now()}`, role: 'assistant', content: '', loading: true, streaming: false }
  messages.value.push(assistantMsg)
  nextTick(() => scrollToBottom())

  isStreaming.value = true
  abortController = new AbortController()

  const historyMessages = messages.value
    .filter(m => !m.loading && (m.role === 'user' || m.role === 'assistant'))
    .map(m => ({ role: m.role, content: m.content }))

  try {
    const response = await fetch(`${baseUrl}/api/chat/stream`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ prompt: text, messages: historyMessages }),
      signal: abortController.signal,
    })

    if (!response.ok) {
      assistantMsg.content = `请求失败: HTTP ${response.status}`
      assistantMsg.loading = false
      return
    }

    const reader = response.body.getReader()
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
            if (assistantMsg.loading) {
              assistantMsg.loading = false
              assistantMsg.streaming = true
            }
            assistantMsg.content += delta
          }
        } catch { /* skip */ }
      }
    }

    // flush remaining buffer
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
  } catch (err) {
    if (!(err instanceof DOMException && err.name === 'AbortError')) {
      assistantMsg.content = assistantMsg.content || `请求异常: ${String(err)}`
    }
  } finally {
    assistantMsg.loading = false
    assistantMsg.streaming = false
    isStreaming.value = false
    abortController = null
    nextTick(() => scrollToBottom())
    saveHistory()
  }
}

function stopStream() {
  if (abortController) {
    abortController.abort()
    abortController = null
  }
  isStreaming.value = false
}

async function saveHistory() {
  if (!activeId.value) return
  const msgs = messages.value
    .filter(m => !m.loading)
    .map(m => ({ role: m.role, content: m.content }))
  if (msgs.length === 0) return

  try {
    await uni.request({
      url: `${apiBaseUrl}/${activeId.value}/history`,
      method: 'PUT',
      header: { 'Content-Type': 'application/json' },
      data: {
        title: conversations.value.find(c => c.id === activeId.value)?.title || '新会话',
        chatMode: 'runtime',
        messages: msgs,
      },
    })
    loadConversations()
  } catch { /* ignore */ }
}
</script>

<style lang="scss" scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f6fa;
}

.chat-messages {
  flex: 1;
  padding: 20rpx 24rpx;
  overflow-y: auto;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
  &-icon {
    font-size: 80rpx;
    margin-bottom: 24rpx;
  }
  &-text {
    font-size: 28rpx;
    color: #999;
  }
}

.chat-bubble-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24rpx;

  &--left {
    justify-content: flex-start;
  }
  &--right {
    justify-content: flex-end;
  }
}

.chat-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  font-weight: bold;
  flex-shrink: 0;

  &--ai {
    background: #e0e7ff;
    color: #4f46e5;
    margin-right: 16rpx;
  }
  &--user {
    background: #d1fae5;
    color: #059669;
    margin-left: 16rpx;
  }
}

.chat-bubble {
  max-width: 70%;
  padding: 18rpx 24rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  line-height: 1.6;
  word-break: break-all;

  &--ai {
    background: #fff;
    color: #333;
    border-top-left-radius: 4rpx;
  }
  &--user {
    background: #4f46e5;
    color: #fff;
    border-top-right-radius: 4rpx;
  }
}

.chat-loading {
  color: #999;
  font-size: 26rpx;
}

.chat-content {
  white-space: pre-wrap;
}

.chat-cursor {
  color: #4f46e5;
  animation: blink 0.8s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.chat-input-bar {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #eee;
  gap: 16rpx;
}

.chat-input-wrap {
  flex: 1;
  min-width: 0;
}

.chat-input {
  width: 100%;
  height: 72rpx;
  padding: 0 24rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 36rpx;
  background: #f5f6fa;
  font-size: 28rpx;
  box-sizing: border-box;
}

.chat-send-btn {
  flex-shrink: 0;
  height: 72rpx;
  padding: 0 32rpx;
  border: none;
  border-radius: 36rpx;
  background: #4f46e5;
  color: #fff;
  font-size: 28rpx;
  line-height: 72rpx;
}
.chat-send-btn[disabled] {
  background: #c7d2fe;
  color: #fff;
}

.chat-stop-btn {
  flex-shrink: 0;
  height: 72rpx;
  padding: 0 32rpx;
  border: 1rpx solid #ef4444;
  border-radius: 36rpx;
  background: #fff;
  color: #ef4444;
  font-size: 28rpx;
  line-height: 72rpx;
}
</style>
