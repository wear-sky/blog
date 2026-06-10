<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth'
import {NAvatar, NButton, NCard, NDivider, NIcon, NResult, NSpace, NSpin, NText, useDialog, useMessage} from 'naive-ui'
import {CreateOutline, TrashOutline} from '@vicons/ionicons5'
import {deleteBlog, getBlogDetail} from '@/api/blog'
import {getReplyTree} from '@/api/reply'
import {checkClickedBlog, checkClickedReplies, getReplyDislikeCounts, getReplyLikeCounts} from '@/api/click'
import {getUserById} from '@/api/user'
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
// 博客点赞/踩状态: null, 'like', 'dislike'
const blogClickStatus = ref(null)
// 评论点赞/踩状态映射 { replyId: 'like' | 'dislike' | null }
const replyClickStatuses = ref({})

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

// 查询当前用户对博客的点赞/踩状态
async function fetchBlogClickStatus() {
  if (!auth.isLoggedIn) return
  try {
    const result = await checkClickedBlog(blogId.value)
    if (result && result.isLike !== undefined && result.isLike !== null) {
      blogClickStatus.value = result.isLike === 1 ? 'like' : 'dislike'
    } else {
      blogClickStatus.value = null
    }
  } catch {
    blogClickStatus.value = null
  }
}

// 查询当前用户对评论的点赞/踩状态
async function fetchReplyClickStatuses(repliesList) {
  if (!auth.isLoggedIn) return
  const replyIds = collectReplyIds(repliesList)
  if (!replyIds.length) return

  try {
    const result = await checkClickedReplies(replyIds)
    const map = {}
    for (const item of (result || [])) {
      if (item && item.replyId !== undefined && item.isLike !== undefined && item.isLike !== null) {
        map[item.replyId] = item.isLike === 1 ? 'like' : 'dislike'
      }
    }
    replyClickStatuses.value = map
  } catch {
    replyClickStatuses.value = {}
  }
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

    // 并行获取评论用户信息、点赞数据和用户点赞/踩状态
    const promises = [fetchBlogClickStatus()]
    if (replyData?.length) {
      promises.push(fetchUserNames(replyData))
      promises.push(fetchReplyClickCounts(replyData))
      promises.push(fetchReplyClickStatuses(replyData))
    }
    await Promise.all(promises)
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
                    backgroundColor: '#C9A882',
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
          <div class="blog-content" v-html="blog.content"></div>

          <NDivider />

          <!-- Click buttons -->
          <ClickButtons
            :target-id="blogId"
            type="blog"
            :click-status="blogClickStatus"
          />
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
              :reply-click-statuses="replyClickStatuses"
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
  padding: 0 32px;
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
  margin-top: 48px;
  animation: fadeInUp 0.5s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid #E8ECF0;
}

.blog-header {
  margin-bottom: 8px;
}

.header-ornament {
  font-size: 0.9rem;
  color: #C9A882;
  margin-bottom: 20px;
  opacity: 0.6;
}

.blog-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 2.6rem;
  font-weight: 700;
  color: #3D3028;
  line-height: 1.3;
  margin-bottom: 28px;
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
  font-weight: 500;
}

.meta-date {
  font-size: 0.82rem;
  color: #A89888;
}

.meta-actions {
  margin-left: auto;
}

.blog-content {
  font-size: 1.05rem;
  line-height: 2;
  color: #3D3028;
  padding: 12px 0;
  white-space: pre-wrap;
}

.blog-content :deep(p) {
  margin-bottom: 1.4em;
}

.blog-content :deep(p:last-child) {
  margin-bottom: 0;
}

.blog-content :deep(h1),
.blog-content :deep(h2),
.blog-content :deep(h3) {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 600;
  color: #3D3028;
}

.blog-content :deep(h1) { font-size: 1.8rem; }
.blog-content :deep(h2) { font-size: 1.5rem; }
.blog-content :deep(h3) { font-size: 1.3rem; }

.blog-content :deep(ul),
.blog-content :deep(ol) {
  padding-left: 2em;
  margin-bottom: 1em;
}

.blog-content :deep(li) {
  margin-bottom: 0.5em;
}

.blog-content :deep(blockquote) {
  border-left: 3px solid #C9A882;
  color: #6B5D52;
  margin: 1.5em 0;
  font-style: italic;
  background: #FBF5EE;
  padding: 1em 1.2em;
  border-radius: 0 8px 8px 0;
}

.blog-content :deep(pre) {
  background: #FBF5EE;
  padding: 1.2em;
  border-radius: 10px;
  overflow-x: auto;
  margin: 1.5em 0;
  border: 1px solid #EDE5DA;
}

.blog-content :deep(code) {
  background: #F3EDE5;
  padding: 0.2em 0.5em;
  border-radius: 5px;
  font-size: 0.9em;
}

.blog-content :deep(pre code) {
  background: none;
  padding: 0;
}

.blog-content :deep(img) {
  max-width: 100%;
  border-radius: 10px;
  margin: 1.5em 0;
}

.blog-content :deep(a) {
  color: #C9A882;
  text-decoration: underline;
  text-underline-offset: 2px;
}

.blog-content :deep(hr) {
  border: none;
  border-top: 1px solid #EDE5DA;
  margin: 2em 0;
}

.replies-card {
  border-radius: 16px;
  margin-top: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid #EDE5DA;
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 24px;
}

.section-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.5rem;
  font-weight: 600;
  color: #3D3028;
}

.section-count {
  font-size: 0.8rem;
  font-weight: 500;
  color: #A89888;
}

.reply-list {
  margin-top: 8px;
}

.no-replies {
  text-align: center;
  padding: 48px 0;
}

.no-replies-text {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-style: italic;
  font-size: 0.95rem;
  color: #A89888;
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