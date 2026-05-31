<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { NInput, NButton, NSpace, useMessage } from 'naive-ui'
import { createReply } from '@/api/reply'

const props = defineProps({
  blogId: { type: Number, required: true },
  parentId: { type: Number, default: null },
  replyToUserId: { type: Number, default: null },
  placeholder: { type: String, default: '写下你的评论...' }
})

const emit = defineEmits(['submitted'])

const router = useRouter()
const auth = useAuthStore()
const message = useMessage()
const content = ref('')
const submitting = ref(false)

async function handleSubmit() {
  if (!auth.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  if (!content.value.trim()) {
    message.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await createReply({
      blogId: props.blogId,
      parentId: props.parentId,
      replyToUserId: props.replyToUserId,
      content: content.value.trim()
    })
    content.value = ''
    message.success('评论成功')
    emit('submitted')
  } catch (e) {
    message.error(e.message || '评论失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="reply-form">
    <NInput
      v-model:value="content"
      type="textarea"
      :placeholder="placeholder"
      :rows="3"
      round
      show-count
      :maxlength="1000"
    />
    <div class="reply-form-footer">
      <NButton
        type="primary"
        size="small"
        round
        :loading="submitting"
        :disabled="!content.trim()"
        @click="handleSubmit"
      >
        发表评论
      </NButton>
    </div>
  </div>
</template>

<style scoped>
.reply-form {
  margin-top: 14px;
}

.reply-form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}
</style>