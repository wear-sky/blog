<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { search } from '@/api/search'
import {
  NInput,
  NButton,
  NSpin,
  NResult,
  NEmpty,
  NPagination,
  NText,
  NIcon
} from 'naive-ui'
import { SearchOutline } from '@vicons/ionicons5'
import SearchResultCard from '@/components/SearchResultCard.vue'

const route = useRoute()
const router = useRouter()

const keyword = ref(route.query.q || '')
const results = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const error = ref('')
const searched = ref(false)

function handleSearch() {
  const q = keyword.value.trim()
  if (!q) return
  pageNum.value = 1
  router.replace({ query: { q, page: undefined } })
  doSearch()
}

async function doSearch() {
  const q = keyword.value.trim()
  if (!q) return

  loading.value = true
  error.value = ''
  searched.value = true
  try {
    const data = await search({
      q,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    results.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    error.value = e.message || '搜索失败'
    results.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handlePageChange(page) {
  pageNum.value = page
  router.replace({ query: { ...route.query, page: page > 1 ? page : undefined } })
  doSearch()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// URL 参数恢复搜索
onMounted(() => {
  if (keyword.value) {
    doSearch()
  }
})

watch(() => route.query.q, (newQ) => {
  if (newQ && newQ !== keyword.value) {
    keyword.value = newQ
    pageNum.value = 1
    doSearch()
  }
})
</script>

<template>
  <div class="search-page">
    <header class="search-header">
      <div class="search-box">
        <NInput
          v-model:value="keyword"
          placeholder="搜索博客和回复..."
          clearable
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <NIcon :component="SearchOutline" />
          </template>
        </NInput>
        <NButton type="primary" size="large" @click="handleSearch">
          搜索
        </NButton>
      </div>
      <NText v-if="searched && !loading" depth="3" class="search-summary">
        找到 {{ total }} 条结果
      </NText>
    </header>

    <main class="search-body">
      <!-- Loading -->
      <div v-if="loading" class="state-container">
        <NSpin size="medium" />
        <NText depth="3" class="state-text">搜索中...</NText>
      </div>

      <!-- Error -->
      <div v-else-if="error" class="state-container">
        <NResult
          status="error"
          :title="error"
          size="small"
        >
          <template #footer>
            <NButton @click="doSearch">重试</NButton>
          </template>
        </NResult>
      </div>

      <!-- Empty -->
      <div v-else-if="searched && !results.length" class="state-container">
        <NEmpty description="没有找到相关内容">
          <template #extra>
            <NText depth="3" class="empty-hint">换个关键词试试</NText>
          </template>
        </NEmpty>
      </div>

      <!-- Not searched yet -->
      <div v-else-if="!searched" class="state-container">
        <NText depth="3" class="empty-hint">输入关键词开始搜索</NText>
      </div>

      <!-- Results -->
      <div v-else class="result-list">
        <SearchResultCard
          v-for="(item, index) in results"
          :key="item.id"
          :result="item"
          :index="index"
        />

        <div v-if="Math.ceil(total / pageSize) > 1" class="pagination-wrapper">
          <NPagination
            :page="pageNum"
            :page-count="Math.ceil(total / pageSize)"
            :page-slot="7"
            @update:page="handlePageChange"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.search-page {
  min-height: calc(100vh - 64px);
}

.search-header {
  text-align: center;
  padding: 48px 32px 24px;
}

.search-box {
  display: flex;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.search-summary {
  display: block;
  margin-top: 16px;
  font-size: 0.85rem;
}

.search-body {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 32px;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  font-size: 0.9rem;
  color: #A89888;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}
</style>
