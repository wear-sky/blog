<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { NButton, NSpace, NIcon, useMessage } from 'naive-ui'
import { ThumbsUpOutline, ThumbsDownOutline } from '@vicons/ionicons5'
import { getBlogLikeCount, getBlogDislikeCount } from '@/api/click'
import { likeBlog, dislikeBlog } from '@/api/blog'
import { likeReply, dislikeReply } from '@/api/reply'

const props = defineProps({
  targetId: { type: Number, required: true },
  type: { type: String, required: true }
})

const router = useRouter()
const auth = useAuthStore()
const message = useMessage()

const likeCount = ref(0)
const dislikeCount = ref(0)
const loading = ref(false)

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
    if (props.type === 'blog') {
      await likeBlog(props.targetId)
    } else {
      await likeReply(props.targetId)
    }
    likeCount.value++
    message.success('已点赞')
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
    if (props.type === 'blog') {
      await dislikeBlog(props.targetId)
    } else {
      await dislikeReply(props.targetId)
    }
    dislikeCount.value++
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.type === 'blog') {
    fetchCounts()
  }
})

defineExpose({ fetchCounts, likeCount, dislikeCount })
</script>

<template>
  <NSpace :size="8">
    <NButton
      quaternary
      size="small"
      :loading="loading"
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
      @click.stop="handleDislike"
    >
      <template #icon>
        <NIcon :component="ThumbsDownOutline" />
      </template>
      {{ dislikeCount }}
    </NButton>
  </NSpace>
</template>