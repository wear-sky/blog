<script setup>
import { ref, onMounted, computed } from 'vue'
import { getBlogList } from '@/api/blog'
import {
  NSpace,
  NText,
  NSpin,
  NResult,
  NButton,
  NPagination,
  NEmpty
} from 'naive-ui'
import BlogCard from '@/components/BlogCard.vue'

const blogs = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(true)
const error = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

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

function handlePageChange(page) {
  pageNum.value = page
  fetchBlogs()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(fetchBlogs)
</script>

<template>
  <div class="home-page">
    <header class="page-header">
      <div class="header-deco">✦</div>
      <h1 class="page-title">最新博文</h1>
      <NText depth="3" class="page-subtitle">在文字间，遇见春天</NText>
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
  padding: 72px 28px 48px;
  animation: fadeIn 0.6s ease;
}

.header-deco {
  font-size: 1.1rem;
  color: #5b8a6e;
  margin-bottom: 16px;
  opacity: 0.5;
}

.page-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 2.8rem;
  font-weight: 700;
  color: #3d3a33;
  margin-bottom: 12px;
  letter-spacing: 0.04em;
}

.page-subtitle {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1rem;
  font-style: italic;
  letter-spacing: 0.06em;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 28px;
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-bottom: 60px;
}

.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  gap: 16px;
}

.state-text {
  font-size: 0.9rem;
}

.empty-hint {
  font-size: 1.6rem;
  opacity: 0.4;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
