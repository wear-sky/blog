<script setup>
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const props = defineProps({
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '写下你的想法...' }
})

const emit = defineEmits(['update:modelValue'])

function updateContent(content) {
  emit('update:modelValue', content)
}
</script>

<template>
  <div class="rich-text-editor">
    <QuillEditor
      :content="modelValue"
      content-type="html"
      :placeholder="placeholder"
      theme="snow"
      @update:content="updateContent"
    />
  </div>
</template>

<style scoped>
.rich-text-editor :deep(.ql-toolbar) {
  border-radius: 12px 12px 0 0;
  border-color: #e0e0e6;
  background: #fafafa;
}

.rich-text-editor :deep(.ql-container) {
  border-radius: 0 0 12px 12px;
  border-color: #e0e0e6;
  min-height: 400px;
  font-size: 1rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.rich-text-editor :deep(.ql-editor) {
  min-height: 400px;
  line-height: 1.8;
}

.rich-text-editor :deep(.ql-editor.ql-blank::before) {
  color: #c2c2c2;
  font-style: normal;
}
</style>