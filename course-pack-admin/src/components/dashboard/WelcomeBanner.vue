<template>
  <div class="welcome-banner card-leaf-lg">
    <!-- 小芽水印：右上角露出 2/3（对齐小程序端 HERO 卡水印规范） -->
    <svg class="sprout-watermark" viewBox="0 0 64 64" fill="none" aria-hidden="true">
      <path d="M32 56V28" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <path d="M32 30c-9 0-15-5-15-13 8 0 15 5 15 13Z" stroke="currentColor" stroke-width="2.5" stroke-linejoin="round" />
      <path d="M32 30c9-1 15-6 15-14-8-1-15 4-15 14Z" stroke="currentColor" stroke-width="2.5" stroke-linejoin="round" />
    </svg>
    <div class="welcome-text">
      <h3 class="title">欢迎回来，{{ props.data.userName }}</h3>
      <p class="sub">今天有 {{ props.data.pendingReview }} 节课程待审核 · {{ props.data.date }}</p>
    </div>
    <div class="banner-right">
      <div class="todo-entry" @click="$router.push('/audit')">
        <span class="todo-label">全部待办</span>
        <span class="todo-num">{{ props.data.todoCount }} 项</span>
      </div>
      <svg class="sprout" viewBox="0 0 64 64" fill="none" aria-hidden="true">
        <path d="M32 56V28" style="stroke: var(--line-brand)" stroke-width="2.5" stroke-linecap="round" />
        <path d="M32 30c-9 0-15-5-15-13 8 0 15 5 15 13Z" style="stroke: var(--line-brand)" stroke-width="2.5" stroke-linejoin="round" />
        <path d="M32 30c9-1 15-6 15-14-8-1-15 4-15 14Z" style="stroke: var(--line-brand)" stroke-width="2.5" stroke-linejoin="round" />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { WelcomeInfo } from '@/types/dashboard';

interface Props {
  data: WelcomeInfo;
}

const props = defineProps<Props>();
</script>

<style scoped>
.welcome-banner {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 96px;
  padding: 20px 28px;
  background: var(--bg-oat);            /* 燕麦底大卡 */
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf-lg 叶片裁角提供（V3） */
  gap: var(--space-l);
  overflow: hidden;                     /* 水印露出 2/3，裁掉出界部分 */
}
.sprout-watermark {
  position: absolute;
  top: -22px;                           /* 露出 2/3（64px 高、上移 22px） */
  right: -14px;
  width: 160px;
  height: 160px;
  color: var(--brand-ink);
  opacity: 0.06;
  pointer-events: none;
}
.title {
  margin: 0;
  font-family: var(--font-serif);       /* H1 衬线 */
  font-size: var(--font-size-xl);
  font-weight: 650;
  color: var(--text-primary);
}
.sub {
  margin: 6px 0 0;
  font-size: var(--font-size-s);
  color: var(--text-regular);
}
.banner-right {
  display: flex;
  align-items: center;
  gap: var(--space-l);
  flex-shrink: 0;
}
.todo-entry {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  cursor: pointer;
}
.todo-label {
  font-size: 13px;
  color: var(--text-secondary);
}
.todo-num {
  font-family: var(--font-serif);
  font-size: var(--font-size-xxl);
  font-weight: 700;                     /* 精修：加粗强化待办锚点 */
  color: var(--accent-warm);             /* 精修：暖橙点缀（每页限 1 处） */
  line-height: 1.2;
}
.sprout {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  opacity: 0.9;
}
</style>