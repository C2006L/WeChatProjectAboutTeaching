<template>
  <div class="todo-list">
    <h4 class="title">待办事项</h4>
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
  padding: var(--space-m);
  background: var(--bg-card);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
}
.title {
  margin: 0 0 var(--space-s);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-s) var(--space-xs);
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
