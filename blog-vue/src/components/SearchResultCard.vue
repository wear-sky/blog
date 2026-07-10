<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NTag, NText } from 'naive-ui'

const props = defineProps({
  result: { type: Object, required: true },
  index: { type: Number, default: 0 }
})

const router = useRouter()

const animDelay = computed(() => `${props.index * 0.06}s`)

const isBlog = computed(() => props.result.type === 'blog')

const formattedDate = computed(() => {
  if (!props.result.createdAt) return ''
  const d = new Date(props.result.createdAt)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

function goDetail() {
  const blogId = isBlog.value ? props.result.id : props.result.blogId
  router.push(`/blog/${blogId}`)
}
</script>

<template>
  <NCard
    class="search-card"
    :style="{ animationDelay: animDelay }"
    hoverable
    @click="goDetail"
  >
    <div class="card-inner">
      <div class="card-header">
        <NTag
          :type="isBlog ? 'success' : 'warning'"
          size="small"
          round
          :bordered="false"
        >
          {{ isBlog ? '博客' : '回复' }}
        </NTag>
        <NText depth="3" class="meta-date">{{ formattedDate }}</NText>
      </div>

      <h3 class="card-title" v-if="result.title" v-html="result.title"></h3>

      <p class="card-content" v-html="result.content"></p>

      <div class="card-footer" v-if="!isBlog">
        <NText depth="3" class="blog-hint">所属博客 #{{ result.blogId }}</NText>
      </div>
    </div>
  </NCard>
</template>

<style scoped>
.search-card {
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.5s ease both;
  border-radius: 16px;
  border: 1px solid #E8ECF0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.search-card:hover {
  border-color: #EDE5DA;
  box-shadow: 0 4px 12px rgba(143, 168, 154, 0.12);
  transform: translateY(-2px);
}

.search-card:hover .card-title {
  color: #C9A882;
}

.card-inner {
  padding: 4px 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.3rem;
  font-weight: 600;
  color: #3D3028;
  margin-bottom: 10px;
  line-height: 1.4;
  transition: color 0.25s ease;
}

.card-content {
  font-size: 0.9rem;
  color: #6B5D52;
  line-height: 1.8;
  margin-bottom: 8px;
}

.card-content :deep(em) {
  color: #C9A882;
  font-style: normal;
  font-weight: 700;
}

.card-title :deep(em) {
  color: #C9A882;
  font-style: normal;
  font-weight: 700;
}

.card-footer {
  margin-top: 8px;
}

.blog-hint {
  font-size: 0.8rem;
  color: #A89888;
}

.meta-date {
  font-size: 0.8rem;
  color: #A89888;
  font-weight: 500;
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
