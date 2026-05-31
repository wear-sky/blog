<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  NCard,
  NText,
  NButton,
  NSpace,
  NSpin,
  NResult,
  NAvatar,
  NDivider,
  NIcon,
  useMessage,
  useDialog
} from 'naive-ui'
import { CreateOutline, TrashOutline } from '@vicons/ionicons5'
import { getBlogDetail, deleteBlog } from '@/api/blog'
import { getReplyTree } from '@/api/reply'
import { getReplyLikeCounts, getReplyDislikeCounts } from '@/api/click'
import { getUserById } from '@/api/user'
import ReplyTree from '@/components/ReplyTree.vue'
import ReplyForm from '@/components/ReplyForm.vue'
import ClickButtons from '@/components/ClickButtons.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const message = useMessage()
const dialog = useDialog()

const blog = ref(null)
const replies = ref([])
const loading = ref(true)
const error = ref('')

// 评论用户昵称映射 { userId: nickname }
const userNames = ref({})
// 评论点赞/踩映射 { replyId: { likes, dislikes } }
const replyClickCounts = ref({})

const blogId = computed(() => Number(route.params.id))
const isAuthor = computed(() => {
  return auth.user && blog.value && auth.user.id === blog.value.author?.id
})

const formattedDate = computed(() => {
  if (!blog.value?.createdAt) return ''
  return new Date(blog.value.createdAt).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
})

const authorInitial = computed(() => {
  const name = blog.value?.author?.nickname || blog.value?.author?.username || ''
  return name.charAt(0).toUpperCase() || 'U'
})

// 递归收集评论树中所有 userId
function collectUserIds(repliesList) {
  const ids = new Set()
  for (const reply of repliesList) {
    ids.add(reply.userId)
    if (reply.replyToUserId) ids.add(reply.replyToUserId)
    if (reply.children?.length) {
      for (const id of collectUserIds(reply.children)) {
        ids.add(id)
      }
    }
  }
  return ids
}

// 递归收集评论树中所有 replyId
function collectReplyIds(repliesList) {
  const ids = []
  for (const reply of repliesList) {
    ids.push(reply.id)
    if (reply.children?.length) {
      ids.push(...collectReplyIds(reply.children))
    }
  }
  return ids
}

// 批量获取评论用户昵称
async function fetchUserNames(repliesList) {
  const userIds = [...collectUserIds(repliesList)]
  const map = {}
  await Promise.all(
    userIds.map(async (id) => {
      try {
        const user = await getUserById(id)
        map[id] = user.nickname || user.username || `用户#${id}`
      } catch {
        map[id] = `用户#${id}`
      }
    })
  )
  userNames.value = map
}

// 批量获取评论点赞/踩数
async function fetchReplyClickCounts(repliesList) {
  const replyIds = collectReplyIds(repliesList)
  if (!replyIds.length) return

  const map = {}
  try {
    const [likesData, dislikesData] = await Promise.all([
      getReplyLikeCounts(replyIds),
      getReplyDislikeCounts(replyIds)
    ])
    for (const item of (likesData || [])) {
      if (!map[item.replyId]) map[item.replyId] = { likes: 0, dislikes: 0 }
      map[item.replyId].likes = item.count || 0
    }
    for (const item of (dislikesData || [])) {
      if (!map[item.replyId]) map[item.replyId] = { likes: 0, dislikes: 0 }
      map[item.replyId].dislikes = item.count || 0
    }
  } catch {
    // silent
  }
  replyClickCounts.value = map
}

async function fetchBlog() {
  loading.value = true
  error.value = ''
  try {
    const [blogData, replyData] = await Promise.all([
      getBlogDetail(blogId.value),
      getReplyTree(blogId.value)
    ])
    blog.value = blogData
    replies.value = replyData || []

    // 并行获取评论用户信息和点赞数据
    if (replyData?.length) {
      await Promise.all([
        fetchUserNames(replyData),
        fetchReplyClickCounts(replyData)
      ])
    }
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function handleDelete() {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除这篇博客吗？此操作不可撤销。',
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteBlog(blogId.value)
        message.success('已删除')
        router.push('/')
      } catch {
        // handled
      }
    }
  })
}

function goEdit() {
  router.push(`/blog/${blogId.value}/edit`)
}

onMounted(fetchBlog)
</script>

<template>
  <div class="detail-page">
    <!-- Loading -->
    <div v-if="loading" class="state-container">
      <NSpin size="medium" />
      <NText depth="3">加载中...</NText>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="state-container">
      <NResult status="error" :title="error" size="small">
        <template #footer>
          <NButton @click="fetchBlog">重试</NButton>
        </template>
      </NResult>
    </div>

    <!-- Content -->
    <template v-else-if="blog">
      <div class="container">
        <NCard class="blog-card" :bordered="true">
          <!-- Header -->
          <header class="blog-header">
            <div class="header-ornament">✦</div>
            <h1 class="blog-title">{{ blog.title }}</h1>
            <div class="blog-meta">
              <NSpace align="center" :size="8" v-if="blog.author">
                <NAvatar
                  round
                  :size="30"
                  :style="{
                    backgroundColor: '#5b8a6e',
                    color: '#fff',
                    fontSize: '13px',
                    fontWeight: '700'
                  }"
                >
                  {{ authorInitial }}
                </NAvatar>
                <NText strong class="author-name">
                  {{ blog.author.nickname || blog.author.username }}
                </NText>
              </NSpace>
              <NText depth="3" class="meta-date">{{ formattedDate }}</NText>
              <NSpace v-if="isAuthor" :size="4" class="meta-actions">
                <NButton text size="small" @click="goEdit">
                  <template #icon>
                    <NIcon :component="CreateOutline" />
                  </template>
                  编辑
                </NButton>
                <NButton text size="small" type="error" @click="handleDelete">
                  <template #icon>
                    <NIcon :component="TrashOutline" />
                  </template>
                  删除
                </NButton>
              </NSpace>
            </div>
          </header>

          <NDivider />

          <!-- Content -->
          <div class="blog-content">
            <p v-for="(paragraph, i) in blog.content.split('\n')" :key="i" class="content-paragraph">
              {{ paragraph }}
            </p>
          </div>

          <NDivider />

          <!-- Click buttons -->
          <ClickButtons :target-id="blogId" type="blog" />
        </NCard>

        <!-- Replies Section -->
        <NCard class="replies-card" :bordered="true">
          <div class="section-header">
            <h2 class="section-title">评论</h2>
            <NText v-if="replies.length" depth="3" class="section-count">
              {{ replies.length }} 条
            </NText>
          </div>

          <ReplyForm
            :blog-id="blogId"
            placeholder="写下你的想法..."
            @submitted="fetchBlog"
          />

          <NDivider v-if="replies.length" />

          <div v-if="replies.length" class="reply-list">
            <ReplyTree
              :replies="replies"
              :blog-id="blogId"
              :user-names="userNames"
              :reply-click-counts="replyClickCounts"
              @refresh="fetchBlog"
            />
          </div>
          <div v-else class="no-replies">
            <NText depth="3" class="no-replies-text">暂无评论，来写第一条吧 ✎</NText>
          </div>
        </NCard>
      </div>
    </template>
  </div>
</template>

<style scoped>
.detail-page {
  padding-bottom: 80px;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 28px;
}

.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  gap: 16px;
}

.blog-card {
  border-radius: 16px;
  margin-top: 40px;
  animation: fadeInUp 0.5s ease;
  box-shadow: 0 2px 20px rgba(61, 58, 51, 0.05);
}

.blog-header {
  margin-bottom: 8px;
}

.header-ornament {
  font-size: 0.9rem;
  color: #5b8a6e;
  margin-bottom: 20px;
  opacity: 0.5;
}

.blog-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 2.4rem;
  font-weight: 700;
  color: #3d3a33;
  line-height: 1.3;
  margin-bottom: 24px;
  letter-spacing: 0.01em;
}

.blog-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.author-name {
  font-size: 0.9rem;
}

.meta-date {
  font-size: 0.82rem;
}

.meta-actions {
  margin-left: auto;
}

.blog-content {
  font-size: 1.05rem;
  line-height: 2;
  color: #3d3a33;
  padding: 8px 0;
}

.content-paragraph {
  margin-bottom: 1.4em;
}

.content-paragraph:last-child {
  margin-bottom: 0;
}

.replies-card {
  border-radius: 16px;
  margin-top: 20px;
  box-shadow: 0 2px 20px rgba(61, 58, 51, 0.05);
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 20px;
}

.section-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.5rem;
  font-weight: 600;
  color: #3d3a33;
}

.section-count {
  font-size: 0.8rem;
  font-weight: 500;
}

.reply-list {
  margin-top: 8px;
}

.no-replies {
  text-align: center;
  padding: 40px 0;
}

.no-replies-text {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-style: italic;
  font-size: 0.95rem;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>