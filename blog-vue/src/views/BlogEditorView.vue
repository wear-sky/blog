<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  NCard,
  NInput,
  NButton,
  NSpace,
  NText,
  NSpin,
  useMessage
} from 'naive-ui'
import { getBlogDetail, createBlog, updateBlog } from '@/api/blog'
import RichTextEditor from '@/components/RichTextEditor.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const message = useMessage()

const form = ref({
  title: '',
  content: ''
})
const loading = ref(false)
const fetching = ref(false)

const isEdit = computed(() => !!route.params.id)

async function fetchBlog() {
  if (!isEdit.value) return
  fetching.value = true
  try {
    const data = await getBlogDetail(route.params.id)
    if (auth.user && auth.user.id !== data.author?.id) {
      message.error('只能编辑自己的博客')
      return
    }
    form.value.title = data.title
    form.value.content = data.content
  } catch (e) {
    message.error(e.message || '加载博客失败')
  } finally {
    fetching.value = false
  }
}

// 检查富文本是否只有空标签
function isContentEmpty(html) {
  const div = document.createElement('div')
  div.innerHTML = html
  return !div.textContent?.trim()
}

async function handleSubmit() {
  if (!form.value.title.trim()) {
    message.warning('请输入标题')
    return
  }
  if (!form.value.content || isContentEmpty(form.value.content)) {
    message.warning('请输入内容')
    return
  }

  loading.value = true
  try {
    if (isEdit.value) {
      await updateBlog({
        id: Number(route.params.id),
        title: form.value.title.trim(),
        content: form.value.content.trim()
      })
      message.success('保存成功')
      router.push(`/blog/${route.params.id}`)
    } else {
      const blogId = await createBlog({
        title: form.value.title.trim(),
        content: form.value.content.trim()
      })
      message.success('发布成功')
      router.push(`/blog/${blogId}`)
    }
  } catch (e) {
    message.error(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}

function goBack() {
  if (isEdit.value) {
    router.push(`/blog/${route.params.id}`)
  } else {
    router.push('/')
  }
}

onMounted(fetchBlog)
</script>

<template>
  <div class="editor-page">
    <div class="container">
      <div class="editor-header">
        <NSpace align="center" :size="10">
          <NText class="header-icon">✎</NText>
          <h1 class="editor-title">{{ isEdit ? '编辑博客' : '写博客' }}</h1>
        </NSpace>
        <NButton quaternary @click="goBack">取消</NButton>
      </div>

      <!-- Loading -->
      <div v-if="fetching" class="state-container">
        <NSpin size="medium" />
        <NText depth="3">加载中...</NText>
      </div>

      <!-- Editor -->
      <NCard v-else class="editor-card" :bordered="true">
        <div class="form-group">
          <NText class="form-label">标题</NText>
          <NInput
            v-model:value="form.title"
            placeholder="给你的博客起个标题..."
            size="large"
            round
            :maxlength="200"
            show-count
          />
        </div>

        <div class="form-group">
          <NText class="form-label">内容</NText>
          <RichTextEditor v-model="form.content" />
        </div>

        <div class="editor-actions">
          <NButton size="large" @click="goBack">取消</NButton>
          <NButton
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ isEdit ? '保存修改' : '发布博客' }}
          </NButton>
        </div>
      </NCard>
    </div>
  </div>
</template>

<style scoped>
.editor-page {
  padding-bottom: 80px;
  animation: fadeIn 0.4s ease;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 32px;
}

.editor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 48px;
  margin-bottom: 28px;
}

.header-icon {
  font-size: 1.2rem;
  color: #C9A882;
  opacity: 0.8;
}

.editor-title {
  font-family: 'Cormorant Garamond', Georgia, serif;
  font-size: 1.8rem;
  font-weight: 600;
  color: #3D3028;
}

.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  gap: 16px;
}

.editor-card {
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid #EDE5DA;
}

.form-group {
  margin-bottom: 28px;
}

.form-label {
  display: block;
  font-size: 0.8rem;
  font-weight: 700;
  color: #C9A882;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.editor-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>