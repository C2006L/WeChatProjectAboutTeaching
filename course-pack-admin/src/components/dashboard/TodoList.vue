<template>
  <div class="todo-list card-leaf">
    <div class="list-head">
      <h4 class="title">当前未处理</h4>
      <span class="hint">当前存量 · 点击处理</span>
    </div>
    <div
      v-for="item in items"
      :key="item.title"
      class="todo-item"
      @click="$router.push(item.path)"
    >
      <span class="todo-title">{{ item.title }}</span>
      <span class="todo-count" :class="{ urgent: item.count >= 10 }">{{ item.count }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TodoItem } from '@/types/dashboard';

interface Props {
  items: TodoItem[];
}

defineProps<Props>();
</script>

<style scoped>
.todo-list {
  min-height: 174px;
  padding: 20px 22px;
  background: var(--bg-card);
  border: 1px solid var(--card-border);
}
.list-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}
.title {
  margin: 0 0 8px;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.hint {
  font-size: 13px;
  color: var(--text-secondary);
}
.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px var(--space-xs);
  border-bottom: 1px solid var(--border);
  cursor: pointer;
}
.todo-item:last-child {
  border-bottom: none;
}
.todo-item:hover .todo-title {
  color: var(--brand-primary);
}
.todo-title {
  font-size: var(--font-size-s);
  color: var(--text-regular);
}
.todo-count {
  min-width: 28px;
  height: 20px;
  padding: 0 8px;
  border-radius: 10px;
  background: var(--brand-primary-light);
  color: var(--brand-primary);
  font-size: var(--font-size-xs);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.todo-count.urgent {
  background: var(--brand-secondary-light);
  color: var(--warn);
}
</style>
