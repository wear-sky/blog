<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { NButton, NSpace, NIcon, useMessage } from 'naive-ui'
import { ThumbsUpOutline, ThumbsDownOutline } from '@vicons/ionicons5'
import { getBlogLikeCount, getBlogDislikeCount } from '@/api/click'
import { likeBlog, dislikeBlog, undoLikeBlog, undoDislikeBlog } from '@/api/blog'
import { likeReply, dislikeReply, undoLikeReply, undoDislikeReply } from '@/api/reply'

const props = defineProps({
  targetId: { type: Number, required: true },
  type: { type: String, required: true },
  initialLikeCount: { type: Number, default: null },
  initialDislikeCount: { type: Number, default: null },
  // null: 未操作, 'like': 已点赞, 'dislike': 已点踩
  clickStatus: { type: String, default: null }
})

const router = useRouter()
const auth = useAuthStore()
const message = useMessage()

const likeCount = ref(0)
const dislikeCount = ref(0)
const loading = ref(false)
const currentStatus = ref(props.clickStatus)

const isLiked = computed(() => currentStatus.value === 'like')
const isDisliked = computed(() => currentStatus.value === 'dislike')

async function fetchCounts() {
  try {
    if (props.type === 'blog') {
      const [likes, dislikes] = await Promise.all([
        getBlogLikeCount(props.targetId),
        getBlogDislikeCount(props.targetId)
      ])
      likeCount.value = likes || 0
      dislikeCount.value = dislikes || 0
    }
  } catch {
    // silent
  }
}

async function handleLike() {
  if (!auth.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (loading.value) return
  loading.value = true
  try {
    if (isLiked.value) {
      // 取消点赞
      if (props.type === 'blog') {
        await undoLikeBlog(props.targetId)
      } else {
        await undoLikeReply(props.targetId)
      }
      likeCount.value = Math.max(0, likeCount.value - 1)
      currentStatus.value = null
      message.success('已取消点赞')
    } else {
      // 点赞
      if (props.type === 'blog') {
        await likeBlog(props.targetId)
      } else {
        await likeReply(props.targetId)
      }
      likeCount.value++
      // 如果之前是点踩状态，踩数减1
      if (isDisliked.value) {
        dislikeCount.value = Math.max(0, dislikeCount.value - 1)
      }
      currentStatus.value = 'like'
      message.success('已点赞')
    }
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function handleDislike() {
  if (!auth.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (loading.value) return
  loading.value = true
  try {
    if (isDisliked.value) {
      // 取消点踩
      if (props.type === 'blog') {
        await undoDislikeBlog(props.targetId)
      } else {
        await undoDislikeReply(props.targetId)
      }
      dislikeCount.value = Math.max(0, dislikeCount.value - 1)
      currentStatus.value = null
      message.success('已取消点踩')
    } else {
      // 点踩
      if (props.type === 'blog') {
        await dislikeBlog(props.targetId)
      } else {
        await dislikeReply(props.targetId)
      }
      dislikeCount.value++
      // 如果之前是点赞状态，赞数减1
      if (isLiked.value) {
        likeCount.value = Math.max(0, likeCount.value - 1)
      }
      currentStatus.value = 'dislike'
      message.success('已点踩')
    }
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.initialLikeCount !== null) {
    likeCount.value = props.initialLikeCount
  }
  if (props.initialDislikeCount !== null) {
    dislikeCount.value = props.initialDislikeCount
  }
  if (props.type === 'blog' && props.initialLikeCount === null) {
    fetchCounts()
  }
})

defineExpose({ fetchCounts, likeCount, dislikeCount, currentStatus })
</script>

<template>
  <NSpace :size="8">
    <NButton
      quaternary
      size="small"
      :loading="loading"
      :type="isLiked ? 'success' : 'default'"
      @click.stop="handleLike"
    >
      <template #icon>
        <NIcon :component="ThumbsUpOutline" />
      </template>
      {{ likeCount }}
    </NButton>
    <NButton
      quaternary
      size="small"
      :loading="loading"
      :type="isDisliked ? 'default' : 'default'"
      :style="isDisliked ? { backgroundColor: '#e0e0e0', color: '#666' } : {}"
      @click.stop="handleDislike"
    >
      <template #icon>
        <NIcon :component="ThumbsDownOutline" />
      </template>
      {{ dislikeCount }}
    </NButton>
  </NSpace>
</template>