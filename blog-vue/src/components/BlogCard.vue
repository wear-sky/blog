<script setup>
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {NAvatar, NCard, NIcon, NText} from 'naive-ui'
import {ArrowForwardOutline} from '@vicons/ionicons5'

const props = defineProps({
  blog: { type: Object, required: true },
  index: { type: Number, default: 0 }
})

const router = useRouter()

const excerpt = computed(() => {
  const html = props.blog.content || ''
  // 去除 HTML 标签，提取纯文本
  const div = document.createElement('div')
  div.innerHTML = html
  const text = div.textContent || div.innerText || ''
  return text.length > 180 ? text.slice(0, 180) + '...' : text
})

const formattedDate = computed(() => {
  if (!props.blog.createdAt) return ''
  const d = new Date(props.blog.createdAt)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

const animDelay = computed(() => `${props.index * 0.06}s`)

const authorName = computed(() => {
  const author = props.blog.author
  return author?.nickname || author?.username || '未知作者'
})

const authorInitial = computed(() => {
  return authorName.value.charAt(0).toUpperCase()
})

function goDetail() {
  router.push(`/blog/${props.blog.id}`)
}
</script>

<template>
  <NCard
    class="blog-card"
    :style="{ animationDelay: animDelay }"
    hoverable
    @click="goDetail"
  >
    <div class="card-inner">
      <div class="card-author" v-if="blog.author">
        <NAvatar
          round
          :size="24"
          :style="{
            backgroundColor: '#5b8a6e',
            color: '#fff',
            fontSize: '12px',
            fontWeight: '700'
          }"
        >
          {{ authorInitial }}
        </NAvatar>
        <NText class="author-name">{{ authorName }}</NText>
      </div>
      <h2 class="card-title">{{ blog.title }}</h2>
      <p class="card-excerpt">{{ excerpt }}</p>
      <div class="card-meta">
        <NText depth="3" class="meta-date">{{ formattedDate }}</NText>
        <NText class="meta-arrow">
          <NIcon :component="ArrowForwardOutline" :size="16" />
        </NText>
      </div>
    </div>
  </NCard>
</template>

<style scoped>
.blog-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.5s ease both;
  border-radius: 12px;
  border-left: 3px solid transparent;
}

.blog-card:hover {
  border-left-color: #5b8a6e;
  transform: translateY(-2px);
}

.blog-card:hover .card-title {
  color: #5b8a6e;
}

.blog-card:hover .meta-arrow {
  opacity: 1;
  transform: translateX(0);
  color: #5b8a6e;
}

.card-inner {
  padding: 4px 0;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.author-name {
  font-size: 0.85rem;
  color: #6b665c;
}

.card-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.45rem;
  font-weight: 600;
  color: #3d3a33;
  margin-bottom: 12px;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.card-excerpt {
  font-size: 0.9rem;
  color: #6b665c;
  line-height: 1.8;
  margin-bottom: 18px;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-date {
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-weight: 600;
}

.meta-arrow {
  opacity: 0;
  transform: translateX(-8px);
  transition: all 0.3s ease;
  color: #8a8478;
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