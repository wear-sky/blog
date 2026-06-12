<script setup>
import {
  NConfigProvider,
  NMessageProvider,
  NDialogProvider,
  NLayout,
  NLayoutHeader,
  NLayoutContent
} from 'naive-ui'
import Navbar from './components/Navbar.vue'

const themeOverrides = {
  common: {
    primaryColor: '#C9A882',
    primaryColorHover: '#D4B892',
    primaryColorPressed: '#BE9872',
    primaryColorSuppl: '#FBF5EE',
    borderRadius: '8px',
    borderRadiusSmall: '6px',
    fontFamily: "'Nunito', 'Segoe UI', system-ui, sans-serif",
    fontFamilyMono: "'JetBrains Mono', 'Fira Code', monospace",
    bodyColor: '#FBF9F6',
    cardColor: '#ffffff',
    modalColor: '#ffffff',
    popoverColor: '#ffffff',
    inputColor: '#ffffff',
    tableColor: '#ffffff',
    borderColor: '#EDE5DA',
    dividerColor: '#F3EDE5',
    hoverColor: 'rgba(201, 168, 130, 0.06)',
    textColorBase: '#3D3028',
    textColor1: '#3D3028',
    textColor2: '#6B5D52',
    textColor3: '#A89888',
    placeholderColor: '#C5B8A8',
  },
  Card: {
    borderRadius: '16px',
    borderColor: '#EDE5DA',
    color: '#ffffff',
    titleFontWeight: '600',
  },
  Button: {
    borderRadiusMedium: '10px',
    borderRadiusSmall: '8px',
    fontWeightStrong: '600',
  },
  Input: {
    borderRadius: '10px',
    borderHover: '#C9A882',
    borderFocus: '#C9A882',
    boxShadowFocus: '0 0 0 3px rgba(201, 168, 130, 0.12)',
  },
  Tag: {
    borderRadius: '12px',
  },
  Pagination: {
    itemBorderRadius: '50%',
  },
  Message: {
    borderRadius: '10px',
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
  background: #FBF9F6;
}

.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  height: 64px;
  border-bottom: 1px solid rgba(232, 236, 240, 0.8);
}

.app-content {
  background: transparent;
  padding-top: 64px;
}
</style>
