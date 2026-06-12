<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getBlogList } from '@/api/blog'
import {
  NText,
  NSpin,
  NResult,
  NButton,
  NPagination,
  NEmpty
} from 'naive-ui'
import BlogCard from '@/components/BlogCard.vue'

const route = useRoute()

const blogs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(true)
const error = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 从 URL query 恢复页码
const initPage = Number(route.query.page)
if (initPage > 1) pageNum.value = initPage

async function fetchBlogs() {
  loading.value = true
  error.value = ''
  try {
    const data = await getBlogList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    blogs.value = data.blogs || []
    total.value = data.total || 0
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

import { useRouter } from 'vue-router'

const router = useRouter()

function handlePageChange(page) {
  pageNum.value = page
  router.replace({ query: { ...route.query, page: page > 1 ? page : undefined } })
  fetchBlogs()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(fetchBlogs)
</script>

<template>
  <div class="home-page">
    <header class="page-header">
      <NText depth="3" class="page-subtitle">用文字，记录生活的温度</NText>
    </header>

    <main class="container">
      <!-- Loading -->
      <div v-if="loading" class="state-container">
        <NSpin size="medium" />
        <NText depth="3" class="state-text">正在加载...</NText>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="state-container">
        <NResult
          status="error"
          :title="error"
          size="small"
        >
          <template #footer>
            <NButton @click="fetchBlogs">重试</NButton>
          </template>
        </NResult>
      </div>

      <!-- Empty -->
      <div v-else-if="!blogs.length" class="state-container">
        <NEmpty description="还没有任何博客，等待第一朵花开">
          <template #extra>
            <NText depth="3" class="empty-hint">✿</NText>
          </template>
        </NEmpty>
      </div>

      <!-- Blog List -->
      <div v-else class="blog-list">
        <BlogCard
          v-for="(blog, index) in blogs"
          :key="blog.id"
          :blog="blog"
          :index="index"
        />

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="pagination-wrapper">
          <NPagination
            :page="pageNum"
            :page-count="totalPages"
            :page-slot="7"
            @update:page="handlePageChange"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-page {
  min-height: calc(100vh - 64px);
}

.page-header {
  text-align: center;
  padding: 48px 32px 32px;
  animation: fadeIn 0.6s ease;
}

.page-subtitle {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.05rem;
  font-style: italic;
  letter-spacing: 0.06em;
  color: #999;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 32px;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: 80px;
}

.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  gap: 16px;
}

.state-text {
  font-size: 0.9rem;
  color: #999;
}

.empty-hint {
  font-size: 1.8rem;
  opacity: 0.3;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
