<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth'
import {NAvatar, NButton, NIcon, NText, useDialog, useMessage} from 'naive-ui'
import {ChatbubbleOutline, TrashOutline} from '@vicons/ionicons5'
import {deleteReply} from '@/api/reply'
import ReplyForm from './ReplyForm.vue'
import ClickButtons from './ClickButtons.vue'

const props = defineProps({
  replies: { type: Array, required: true },
  blogId: { type: Number, required: true },
  userNames: { type: Object, default: () => ({}) },
  replyClickCounts: { type: Object, default: () => ({}) },
  replyClickStatuses: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['refresh'])

const router = useRouter()
const auth = useAuthStore()
const message = useMessage()
const dialog = useDialog()

const replyingTo = ref(null)

function getUserName(userId) {
  return props.userNames[userId] || `用户#${userId}`
}

function getUserInitial(userId) {
  const name = getUserName(userId)
  return name.charAt(0).toUpperCase()
}

function getAvatarColor(userId) {
  const colors = ['#C9A882', '#D4B892', '#DFC8A2', '#EAD8B2', '#F5E8C2', '#FFF8D2']
  return colors[(userId || 0) % colors.length]
}

function getReplyLikes(replyId) {
  return props.replyClickCounts[replyId]?.likes ?? 0
}

function getReplyDislikes(replyId) {
  return props.replyClickCounts[replyId]?.dislikes ?? 0
}

function getReplyClickStatus(replyId) {
  return props.replyClickStatuses[replyId] || null
}

function toggleReply(replyId) {
  if (!auth.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  replyingTo.value = replyingTo.value === replyId ? null : replyId
}

function handleSubmitted() {
  replyingTo.value = null
  emit('refresh')
}

function handleDelete(replyId) {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除这条评论吗？',
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteReply(replyId)
        message.success('已删除')
        emit('refresh')
      } catch {
        // handled
      }
    }
  })
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 30) return `${days} 天前`
  return d.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}
</script>

<template>
  <div class="reply-tree">
    <div v-for="reply in replies" :key="reply.id" class="reply-item">
      <div class="reply-main">
        <NAvatar
          round
          :size="32"
          :style="{
            backgroundColor: getAvatarColor(reply.userId),
            color: '#fff',
            fontSize: '12px',
            fontWeight: '700',
            flexShrink: '0'
          }"
        >
          {{ getUserInitial(reply.userId) }}
        </NAvatar>

        <div class="reply-body">
          <div class="reply-header">
            <NText strong class="reply-user">{{ getUserName(reply.userId) }}</NText>
            <NText v-if="reply.replyToUserId" depth="3" class="reply-to">
              回复 {{ getUserName(reply.replyToUserId) }}
            </NText>
            <NText depth="3" class="reply-time">{{ formatDate(reply.createdAt) }}</NText>
          </div>

          <NText class="reply-content">{{ reply.content }}</NText>

          <div class="reply-actions">
            <NButton text size="tiny" @click="toggleReply(reply.id)">
              <template #icon>
                <NIcon :component="ChatbubbleOutline" />
              </template>
              回复
            </NButton>
            <NButton
              v-if="auth.user && auth.user.id === reply.userId"
              text
              size="tiny"
              type="error"
              @click="handleDelete(reply.id)"
            >
              <template #icon>
                <NIcon :component="TrashOutline" />
              </template>
              删除
            </NButton>
            <ClickButtons
              :target-id="reply.id"
              type="reply"
              :initial-like-count="getReplyLikes(reply.id)"
              :initial-dislike-count="getReplyDislikes(reply.id)"
              :click-status="getReplyClickStatus(reply.id)"
            />
          </div>

          <ReplyForm
            v-if="replyingTo === reply.id"
            :blog-id="blogId"
            :parent-id="reply.id"
            :reply-to-user-id="reply.userId"
            :placeholder="`回复 ${getUserName(reply.userId)}...`"
            @submitted="handleSubmitted"
          />
        </div>
      </div>

      <div v-if="reply.children && reply.children.length" class="reply-children">
        <ReplyTree
          :replies="reply.children"
          :blog-id="blogId"
          :user-names="userNames"
          :reply-click-counts="replyClickCounts"
          :reply-click-statuses="replyClickStatuses"
          @refresh="emit('refresh')"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.reply-tree {
  display: flex;
  flex-direction: column;
}

.reply-item {
  padding: 20px 0;
  border-bottom: 1px solid #F0F2F5;
  animation: fadeIn 0.3s ease both;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-main {
  display: flex;
  gap: 14px;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.reply-user {
  font-size: 0.88rem;
  color: #C9A882;
  font-weight: 600;
}

.reply-to {
  font-size: 0.8rem;
  color: #A89888;
}

.reply-time {
  font-size: 0.78rem;
  margin-left: auto;
  color: #A89888;
}

.reply-content {
  display: block;
  font-size: 0.92rem;
  line-height: 1.8;
  margin-bottom: 10px;
  color: #3D3028;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
  flex-wrap: wrap;
}

.reply-children {
  margin-left: 28px;
  padding-left: 24px;
  border-left: 2px solid #F0F2F5;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>