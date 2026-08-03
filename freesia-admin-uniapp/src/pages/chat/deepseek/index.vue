<template>
  <view class="chat-page">
    <MessageBell />
    <!-- 顶部工具栏 -->
    <view class="chat-toolbar">
      <text class="chat-toolbar-toggle" @click="showHistory = !showHistory">☰</text>
      <view class="chat-toolbar-title">DeepSeek AI</view>
      <text class="chat-toolbar-btn" @click="startNewChat">新对话</text>
    </view>

    <view class="chat-body">
      <!-- 左侧滑出遮罩 -->
      <view v-if="showHistory" class="chat-overlay" @click="showHistory = false"></view>
      <!-- 历史会话列表 -->
      <view :class="['chat-history-panel', { 'chat-history-panel--open': showHistory }]">
        <view class="chat-history-header">
          <text class="chat-history-header-title">历史会话</text>
          <text class="chat-history-header-close" @click="showHistory = false">✕</text>
        </view>
        <scroll-view class="chat-history-list" scroll-y>
          <view
            v-for="conv in conversations"
            :key="conv.id"
            :class="['chat-history-item', { active: conv.id === activeId }]"
            @click="switchConversation(conv)"
          >
            <text class="chat-history-item-title">{{ conv.title }}</text>
            <text class="chat-history-item-delete" @click.stop="deleteConversation(conv)">×</text>
          </view>
          <view v-if="conversations.length === 0" class="chat-history-empty">
            暂无历史会话
          </view>
        </scroll-view>
      </view>

      <!-- 消息区域 -->
      <scroll-view
        class="chat-messages"
        scroll-y
        :scroll-into-view="scrollToId"
        :scroll-with-animation="true"
      >
        <view v-if="messages.length === 0" class="chat-empty">
          <image src="@/assets/logo/deepseek.png" style="width:160rpx;height:160rpx" mode="aspectFit"/>
          <text class="chat-empty-title">DeepSeek AI</text>
          <text class="chat-empty-desc">基于 DeepSeek 大模型的智能对话助手</text>
<!--          <view class="chat-suggestions">-->
<!--            <view-->
<!--              v-for="(item, idx) in suggestions"-->
<!--              :key="idx"-->
<!--              class="chat-suggestion-item"-->
<!--              @click="handleSend(item)"-->
<!--            >{{ item }}</view>-->
<!--          </view>-->
        </view>

        <view v-for="msg in messages" :key="msg.id" :id="'msg-' + msg.id">
          <view :class="['chat-msg-row', msg.role === 'user' ? 'chat-msg-row--right' : 'chat-msg-row--left']">
            <!-- AI: 头像在左 -->
            <view v-if="msg.role === 'assistant'" class="chat-avatar chat-avatar--ai">AI</view>
            <view class="chat-msg-body">
              <view :class="['chat-bubble', msg.role === 'user' ? 'chat-bubble--user' : 'chat-bubble--ai']">
                <!-- 思考中动画 -->
                <view v-if="msg.loading && !msg.content" class="chat-thinking">
                  <view class="chat-thinking-dot"></view>
                  <view class="chat-thinking-dot"></view>
                  <view class="chat-thinking-dot"></view>
                </view>
                <!-- 流式输出时用纯文本，保证打字机效果 -->
                <text v-if="msg.streaming && msg.content" class="chat-content">{{ msg.content }}<text class="chat-typing-cursor">|</text></text>
                <!-- 完成后渲染 Markdown -->
                <rich-text
                  v-else-if="msg.content && !msg.streaming"
                  :nodes="renderMarkdown(msg.content)"
                  class="chat-md-content"
                ></rich-text>
              </view>
              <!-- 操作按钮 -->
              <view v-if="msg.role === 'assistant' && !msg.loading && msg.content" class="chat-msg-actions">
                <text class="chat-action-btn" @click="copyMessage(msg)">复制</text>
              </view>
            </view>
            <!-- 用户: 头像在右 -->
            <view v-if="msg.role === 'user'" class="chat-avatar chat-avatar--user">我</view>
          </view>
        </view>

        <view style="height: 20rpx"></view>
      </scroll-view>
    </view>

    <!-- 底部输入区 -->
    <view class="chat-footer">
      <view class="chat-input-bar">
        <input
          v-model="inputText"
          class="chat-input"
          type="text"
          placeholder="输入消息..."
          :disabled="isStreaming"
          confirm-type="send"
          @confirm="handleSend(inputText)"
        />
        <view v-if="!isStreaming" class="chat-send-btn" :class="{ disabled: !inputText.trim() }" @click="handleSend(inputText)">
          <text class="chat-send-icon">↑</text>
        </view>
        <view v-else class="chat-stop-btn" @click="stopStream">
          <text class="chat-stop-icon">■</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { Marked } from 'marked'
import Http from '@/api/Http'
import { getToken } from '@/utils/storage'
const baseUrl = import.meta.env.VITE_APP_BASE_URL || ''
const apiBaseUrl = `/api/chat`

// --- Markdown 渲染器 ---
const marked = new Marked({
  breaks: true,
  gfm: true,
})

// --- 状态 ---
const inputText = ref('')
const isStreaming = ref(false)
const messages = ref([])
const conversations = ref([])
const activeId = ref(null)
const pendingTitle = ref('') // 新会话的待定标题（由首条消息生成）
const scrollToId = ref('')
const showHistory = ref(false)
let abortController = null

const suggestions = [
  '用一句话介绍你自己',
  '写一个快速排序算法（Python）',
  '解释什么是量子计算',
]

// --- 生命周期 ---
onMounted(() => {
  loadConversations()
})

// --- 会话管理 ---
async function loadConversations() {
  try {
    const res = await Http.get(`${apiBaseUrl}/conversations?chatMode=runtime`)
    conversations.value = (res.conversations || []).map(c => ({
      id: c.conversationId,
      title: c.title,
    }))
  } catch { /* ignore */ }
}

function startNewChat() {
  activeId.value = null
  messages.value = []
  pendingTitle.value = ''
  showHistory.value = false
}

async function switchConversation(conv) {
  activeId.value = conv.id
  messages.value = []
  pendingTitle.value = ''
  try {
    const res = await Http.get(`${apiBaseUrl}/${conv.id}/history`)
    messages.value = (res.messages || []).map(m => ({
      id: m.id,
      role: m.role,
      content: m.content,
    }))
    await nextTick()
    scrollToBottom()
  } catch { /* ignore */ }
}

async function deleteConversation(conv) {
  const res = await uni.showModal({
    title: '删除会话',
    content: `确定要删除"${conv.title}"吗？删除后不可恢复。`,
    confirmText: '删除',
    confirmColor: '#ef4444',
    cancelText: '取消',
  })
  if (!res.confirm) return

  try {
    await Http.delete(`${apiBaseUrl}/${conv.id}`)
    conversations.value = conversations.value.filter(c => c.id !== conv.id)
    if (activeId.value === conv.id) {
      activeId.value = null
      messages.value = []
    }
  } catch { /* ignore */ }
}

// --- Markdown 渲染 ---
function renderMarkdown(content) {
  try {
    const html = marked.parse(content)
    return html
  } catch {
    return content.replace(/</g, '&lt;').replace(/>/g, '&gt;')
  }
}

// --- 滚动 ---
function scrollToBottom() {
  if (messages.value.length > 0) {
    const last = messages.value[messages.value.length - 1]
    scrollToId.value = 'msg-' + last.id
    // 清空后重新赋值以触发 scroll-into-view
    nextTick(() => { scrollToId.value = '' })
    nextTick(() => { scrollToId.value = 'msg-' + last.id })
  }
}

// --- 复制 ---
function copyMessage(msg) {
  uni.setClipboardData({
    data: msg.content,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'none', duration: 1500 })
    },
  })
}

// --- 发送消息 ---
async function handleSend(text) {
  const content = typeof text === 'string' ? text.trim() : inputText.value.trim()
  if (!content || isStreaming.value) return

  const userMsg = { id: `u-${Date.now()}`, role: 'user', content }
  messages.value.push(userMsg)
  inputText.value = ''
  nextTick(() => scrollToBottom())

  if (!activeId.value) {
    activeId.value = `conv-${Date.now()}`
    // 用首条用户消息生成会话标题
    pendingTitle.value = content.length > 20 ? content.substring(0, 20) + '…' : content
  }

  let assistantMsg = { id: `a-${Date.now()}`, role: 'assistant', content: '', loading: true, streaming: false }
  messages.value.push(assistantMsg)
  nextTick(() => scrollToBottom())

  isStreaming.value = true
  abortController = new AbortController()

  const historyMessages = messages.value
    .filter(m => !m.loading && (m.role === 'user' || m.role === 'assistant'))
    .map(m => ({ role: m.role, content: m.content }))

  try {
    const streamUrl = `${baseUrl}/api/chat/stream`
    console.log('[Chat] 请求:', streamUrl)

    const headers = { 'Content-Type': 'application/json' }
    const token = getToken()
    if (token) {
      headers['Authorization'] = 'Bearer ' + token
    }
    const response = await fetch(streamUrl, {
      method: 'POST',
      headers,
      body: JSON.stringify({ prompt: content, messages: historyMessages }),
      signal: abortController.signal,
    })

    if (!response.ok) {
      assistantMsg.content = `请求失败 HTTP ${response.status}`
      assistantMsg.loading = false
      isStreaming.value = false
      uni.showToast({ title: `请求失败: ${response.status}`, icon: 'none' })
      return
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let chunkCount = 0
    let updateCount = 0
    console.log('[Chat] 开始读取流...')

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      chunkCount++
      buffer += decoder.decode(value, { stream: true })
      console.log(`[Chat] 收到第${chunkCount}个网络包, 大小=${value?.length || 0}`)
      const lines = buffer.split('\n')
      buffer = lines.pop() ?? ''

      let lineCount = 0
      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed) continue

        let data = trimmed
        if (data.startsWith('data:')) data = data.slice(5).trim()
        if (!data || data === '[DONE]') continue

        lineCount++
        try {
          const parsed = JSON.parse(data)
          const delta = parsed?.choices?.[0]?.delta?.content
          if (delta) {
            updateCount++
            if (assistantMsg.loading) {
              console.log('[Chat] 首个token到达! 关闭loading')
              assistantMsg.loading = false
              assistantMsg.streaming = true
            }
            assistantMsg.content += delta
            // 强制触发 uni-app 响应式更新：替换数组元素触发视图刷新
            const idx = messages.value.findIndex(m => m.id === assistantMsg.id)
            if (idx >= 0) {
              const updated = { ...messages.value[idx], content: assistantMsg.content, loading: assistantMsg.loading, streaming: assistantMsg.streaming }
              messages.value[idx] = updated
              assistantMsg = updated
            }
            if (updateCount % 10 === 0) {
              console.log(`[Chat] 已更新${updateCount}次, 当前长度=${assistantMsg.content.length}`)
            }
          }
        } catch (e) {
          console.log('[Chat] 解析SSE行失败:', trimmed.substring(0, 50))
        }
      }
      if (lineCount > 0) console.log(`[Chat] 包#${chunkCount} 包含${lineCount}条SSE行`)
    }

    console.log(`[Chat] 流结束. 总共${chunkCount}个包, ${updateCount}次内容更新, 输出${assistantMsg.content.length}字`)

    // flush
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
      const errMsg = `请求异常: ${String(err)}`
      assistantMsg.content = assistantMsg.content || errMsg
      console.error('[Chat]', errMsg)
      uni.showToast({ title: '网络请求失败，请检查后端服务', icon: 'none', duration: 2000 })
    }
  } finally {
    assistantMsg.loading = false
    assistantMsg.streaming = false
    // 最后一次替换，触发 Markdown 渲染切换
    const idx = messages.value.findIndex(m => m.id === assistantMsg.id)
    if (idx >= 0) {
      messages.value[idx] = { ...messages.value[idx], loading: false, streaming: false }
    }
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
  const last = messages.value[messages.value.length - 1]
  if (last?.role === 'assistant') {
    last.loading = false
    last.streaming = false
  }
}

async function saveHistory() {
  if (!activeId.value) return
  const msgs = messages.value
    .filter(m => !m.loading)
    .map(m => ({ role: m.role, content: m.content }))
  if (msgs.length === 0) return

  try {
    await Http.put(`${apiBaseUrl}/${activeId.value}/history`, {
      title: conversations.value.find(c => c.id === activeId.value)?.title || pendingTitle.value || '新会话',
      chatMode: 'runtime',
      messages: msgs,
    })
    loadConversations()
  } catch { /* ignore */ }
}
</script>

<style lang="scss" scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 86vh;
  overflow: hidden;
  background: #f0f2f5;
}

// --- 工具栏 ---
.chat-toolbar {
  display: flex;
  align-items: center;
  padding: 12rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #eee;
  flex-shrink: 0;
  gap: 16rpx;
}
.chat-toolbar-toggle {
  font-size: 32rpx;
  color: #333;
  padding: 4rpx;
  flex-shrink: 0;
}
.chat-toolbar-title {
  flex: 1;
  font-size: 28rpx;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
}
.chat-toolbar-btn {
  font-size: 22rpx;
  color: #4f46e5;
  padding: 4rpx 14rpx;
  border-radius: 6rpx;
  background: #eef2ff;
  flex-shrink: 0;
}

// --- 主布局 ---
.chat-body {
  flex: 1;
  display: flex;
  min-height: 0;
  position: relative;
  overflow: hidden;
}

// --- 遮罩 ---
.chat-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 10;
}

// --- 历史面板（滑出式） ---
.chat-history-panel {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  width: 520rpx;
  max-width: 80vw;
  background: #fff;
  z-index: 20;
  transform: translateX(-100%);
  transition: transform 0.25s ease;
  display: flex;
  flex-direction: column;
  box-shadow: 2rpx 0 16rpx rgba(0, 0, 0, 0.1);

  &--open {
    transform: translateX(0);
  }
}

.chat-history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 16rpx;
  border-bottom: 1rpx solid #eee;
  flex-shrink: 0;
}
.chat-history-header-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #1a1a1a;
}
.chat-history-header-close {
  font-size: 32rpx;
  color: #999;
  padding: 4rpx 8rpx;
}

.chat-history-list {
  flex: 1;
  overflow-y: auto;
}
.chat-history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14rpx 16rpx;
  border-radius: 10rpx;
  margin-bottom: 4rpx;
  background: #f8f9fb;
  &.active {
    background: #e0e7ff;
  }
}
.chat-history-item-title {
  flex: 1;
  font-size: 26rpx;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.chat-history-item-delete {
  font-size: 22rpx;
  color: #ccc;
  padding: 0 4rpx;
}
.chat-history-empty {
  text-align: center;
  color: #999;
  font-size: 22rpx;
  padding-top: 60rpx;
}

// --- 消息区 ---
.chat-messages {
  flex: 1;
  min-height: 0;
  padding: 20rpx 20rpx;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120rpx;
}
.chat-empty-emoji {
  font-size: 96rpx;
  margin-bottom: 16rpx;
}
.chat-empty-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 6rpx;
}
.chat-empty-desc {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 32rpx;
}

.chat-suggestions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16rpx;
  padding: 0 40rpx;
}
.chat-suggestion-item {
  padding: 12rpx 24rpx;
  border: 1rpx solid #dde;
  border-radius: 40rpx;
  background: #fff;
  color: #4f46e5;
  font-size: 24rpx;
}

// --- 消息行 ---
.chat-msg-row {
  display: flex;
  margin-bottom: 28rpx;

  &--left { justify-content: flex-start; }
  &--right { justify-content: flex-end; }
}

.chat-avatar {
  width: 52rpx;
  height: 52rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18rpx;
  font-weight: 700;
  flex-shrink: 0;
  &--ai {
    background: #e0e7ff;
    color: #4f46e5;
    margin-right: 12rpx;
  }
  &--user {
    background: #4f46e5;
    color: #fff;
    margin-left: 12rpx;
  }
}

.chat-msg-body {
  max-width: 88%;
  min-width: 0;
}

.chat-bubble {
  padding: 16rpx 20rpx;
  border-radius: 18rpx;
  font-size: 26rpx;
  line-height: 1.6;
  word-break: break-word;

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

// --- 思考动画 ---
.chat-thinking {
  display: flex;
  gap: 8rpx;
  align-items: center;
  padding: 10rpx 0;
}
.chat-thinking-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #c4b5fd;
  animation: thinking-bounce 1.2s infinite;
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.4s; }
}
@keyframes thinking-bounce {
  0%, 60%, 100% { transform: scale(0.6); opacity: 0.4; }
  30% { transform: scale(1); opacity: 1; }
}

// --- 流式文本 ---
.chat-content {
  white-space: pre-wrap;
  font-size: 26rpx;
  line-height: 1.6;
  color: #333;
}

// --- 打字光标 ---
.chat-typing-cursor {
  color: #4f46e5;
  animation: blink 0.7s infinite;
  font-weight: 700;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

// --- 操作按钮 ---
.chat-msg-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 8rpx;
  padding-left: 4rpx;
}
.chat-action-btn {
  font-size: 22rpx;
  color: #999;
  padding: 4rpx 12rpx;
}

// --- Markdown 内容 ---
.chat-md-content {
  :deep(p) { margin: 6rpx 0; }
  :deep(code) {
    background: #f1f5f9;
    padding: 2rpx 8rpx;
    border-radius: 4rpx;
    font-size: 24rpx;
  }
  :deep(pre) {
    background: #1e293b;
    color: #e2e8f0;
    padding: 16rpx 20rpx;
    border-radius: 12rpx;
    overflow-x: auto;
    margin: 12rpx 0;
    code { background: none; color: inherit; padding: 0; }
  }
  :deep(ul), :deep(ol) { padding-left: 32rpx; margin: 8rpx 0; }
  :deep(blockquote) {
    border-left: 4rpx solid #c7d2fe;
    padding-left: 16rpx;
    margin: 12rpx 0;
    color: #666;
  }
  :deep(table) {
    border-collapse: collapse;
    margin: 12rpx 0;
    width: 100%;
    th, td {
      border: 1rpx solid #ddd;
      padding: 8rpx 12rpx;
      font-size: 24rpx;
    }
    th { background: #f8f9fb; font-weight: 600; }
  }
  :deep(strong) { font-weight: 700; }
  :deep(em) { font-style: italic; }
  :deep(h1), :deep(h2), :deep(h3), :deep(h4) {
    margin: 16rpx 0 8rpx;
    font-weight: 700;
    line-height: 1.4;
  }
  :deep(h1) { font-size: 34rpx; }
  :deep(h2) { font-size: 30rpx; }
  :deep(h3) { font-size: 28rpx; }
  :deep(a) { color: #4f46e5; }
}

// --- 底部输入 ---
.chat-footer {
  flex-shrink: 0;
  padding: 12rpx 20rpx;
  padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #eee;
}
.chat-input-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.chat-input {
  flex: 1;
  height: 64rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 32rpx;
  background: #f5f6fa;
  font-size: 26rpx;
}
.chat-send-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #4f46e5;
  display: flex;
  align-items: center;
  justify-content: center;
  &.disabled {
    background: #c7d2fe;
  }
}
.chat-send-icon {
  color: #fff;
  font-size: 32rpx;
  font-weight: 700;
}
.chat-stop-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  border: 2rpx solid #ef4444;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chat-stop-icon {
  color: #ef4444;
  font-size: 24rpx;
}
</style>
