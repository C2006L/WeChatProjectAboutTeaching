<template>
  <div class="welcome-banner card-leaf-lg">
    <!-- 小芽水印：右上角露出 2/3（对齐小程序端 HERO 卡水印规范） -->
    <svg class="sprout-watermark" viewBox="0 0 64 64" fill="none" aria-hidden="true">
      <path d="M32 56V28" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" />
      <path d="M32 30c-9 0-15-5-15-13 8 0 15 5 15 13Z" stroke="currentColor" stroke-width="2.5" stroke-linejoin="round" />
      <path d="M32 30c9-1 15-6 15-14-8-1-15 4-15 14Z" stroke="currentColor" stroke-width="2.5" stroke-linejoin="round" />
    </svg>
    <div class="welcome-text">
      <span class="eyebrow">今日工作</span>
      <h3 class="title">{{ props.data.userName }}，今天有 {{ props.data.pendingReview }} 节课程待审核</h3>
      <p class="sub">{{ props.data.date }} · 优先处理审核与教师反馈</p>
    </div>
    <div class="banner-right">
      <div class="todo-entry" @click="$router.push('/audit')">
        <span class="todo-label">进入审核中心</span>
        <span class="todo-num">{{ props.data.todoCount }} 项待办</span>
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
  min-height: 174px;
  padding: 30px 32px;
  background: var(--brand-ink);
  border: 0;
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
  color: #fff;
  opacity: 0.08;
  pointer-events: none;
}
.eyebrow {
  display: block;
  margin-bottom: 10px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.62);
  letter-spacing: 0.12em;
}
.title {
  margin: 0;
  max-width: 620px;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
}
.sub {
  margin: 6px 0 0;
  font-size: var(--font-size-s);
  color: rgba(255, 255, 255, 0.7);
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
  padding: 12px 14px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.08);
  transition: background-color var(--motion-hover) var(--ease-motion);
}
.todo-entry:hover {
  background: rgba(255, 255, 255, 0.14);
}
.todo-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.62);
}
.todo-num {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
}
.sprout {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  opacity: 0.9;
  filter: brightness(0) invert(1);
}
</style>
