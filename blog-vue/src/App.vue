<script setup>
import { computed } from 'vue'
import {
  NConfigProvider,
  NMessageProvider,
  NDialogProvider,
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  darkTheme
} from 'naive-ui'
import Navbar from './components/Navbar.vue'

const themeOverrides = {
  common: {
    primaryColor: '#5b8a6e',
    primaryColorHover: '#6d9e80',
    primaryColorPressed: '#4a7a5e',
    primaryColorSuppl: '#7c9a82',
    borderRadius: '6px',
    borderRadiusSmall: '4px',
    fontFamily: "'Nunito', 'Segoe UI', system-ui, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    bodyColor: '#f8f6f1',
    cardColor: '#ffffff',
    modalColor: '#ffffff',
    popoverColor: '#ffffff',
    inputColor: '#ffffff',
    tableColor: '#ffffff',
    borderColor: '#e4e0d7',
    dividerColor: '#e4e0d7',
    hoverColor: 'rgba(91, 138, 110, 0.08)',
    textColorBase: '#3d3a33',
    textColor1: '#3d3a33',
    textColor2: '#5a5649',
    textColor3: '#8a8478',
    placeholderColor: '#b0a99d',
  },
  Card: {
    borderRadius: '12px',
    borderColor: '#e4e0d7',
    color: '#ffffff',
    titleFontWeight: '600',
  },
  Button: {
    borderRadiusMedium: '8px',
    borderRadiusSmall: '6px',
    fontWeightStrong: '600',
  },
  Input: {
    borderRadius: '8px',
    borderHover: '#5b8a6e',
    borderFocus: '#5b8a6e',
    boxShadowFocus: '0 0 0 2px rgba(91, 138, 110, 0.15)',
  },
  Tag: {
    borderRadius: '12px',
  },
  Pagination: {
    itemBorderRadius: '50%',
  },
  Message: {
    borderRadius: '8px',
  }
}
</script>

<template>
  <NConfigProvider :theme-overrides="themeOverrides">
    <NMessageProvider>
      <NDialogProvider>
        <NLayout class="app-layout">
          <NLayoutHeader bordered class="app-header">
            <Navbar />
          </NLayoutHeader>
          <NLayoutContent class="app-content">
            <router-view v-slot="{ Component }">
              <transition name="page" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </NLayoutContent>
        </NLayout>
      </NDialogProvider>
    </NMessageProvider>
  </NConfigProvider>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: var(--body-color, #f8f6f1);
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  height: 64px;
}

.app-content {
  background: transparent;
}
</style>
