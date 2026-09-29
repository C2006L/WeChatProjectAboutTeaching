<template>
  <div class="layout">
    <!-- 左侧固定 Sidebar：燕麦底 -->
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-badge">芽</span>
        <span class="logo-text">伴芽</span>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu :default-active="activeMenu" router class="menu">
          <el-menu-item v-for="m in menuRoutes" :key="m.path" :index="m.path">
            <el-icon><component :is="menuIcons[m.path]" /></el-icon>
            <span>{{ m.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>

      <div class="version">V1.0 · 伴芽项目组</div>
    </aside>

    <!-- 右侧：Header + 内容区 -->
    <div class="main-wrap">
      <header class="header">
        <h1 class="page-title">{{ currentTitle }}</h1>
        <div class="header-actions">
          <button type="button" class="bell" aria-label="待办事项" @click="bellOpen = true">
            <el-badge :value="todoCount" :hidden="todoCount === 0">
              <el-icon :size="20" class="bell-icon"><Bell /></el-icon>
            </el-badge>
          </button>
          <el-dropdown @command="onCommand">
            <span class="user">
              <el-avatar :size="32" class="user-avatar">{{ (auth.user?.name || '用').slice(0, 1) }}</el-avatar>
              <span class="user-name">{{ auth.user?.name || '未登录' }} · {{ userRoleLabel }}</span>
              <el-icon class="caret"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 铃铛：右侧滑出待办抽屉（计数取自 api/notification 实时聚合） -->
      <el-drawer
        v-model="bellOpen"
        direction="rtl"
        size="360px"
        :with-header="false"
        class="bell-drawer"
      >
        <div class="drawer-head">
          <span class="drawer-title">待办事项</span>
          <span class="drawer-sub">{{ todoCount ? `共 ${todoCount} 项待处理` : '暂无待办' }}</span>
        </div>
        <ul class="todo-rows">
          <li v-for="t in todos" :key="t.title" @click="goTodo(t.path)">
            <span class="todo-name">{{ t.title }}</span>
            <span class="todo-num" :class="{ zero: t.count === 0 }">{{ t.count }}</span>
          </li>
        </ul>
      </el-drawer>

      <main class="content">
        <!-- 路由切换：内容区淡入 180ms（token 见 index.css .route-fade-*） -->
        <router-view v-slot="{ Component }">
          <transition name="route-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, type Component } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  DataAnalysis,
  Files,
  VideoCamera,
  Collection,
  Medal,
  Flag,
  User,
  ChatDotRound,
  Checked,
  FolderOpened,
  Setting,
  Bell,
  ArrowDown,
} from '@element-plus/icons-vue';
import { useAuthStore } from '@/store/auth';
import { fetchTodos } from '@/api/notification';
import type { TodoItem } from '@/types/dashboard';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const activeMenu = computed(() => route.path);
const currentTitle = computed(() => (route.meta.title as string) || '伴芽');

/** 按角色渲染 Sidebar 菜单（ADMIN 全量 / AUDITOR 仅审核中心 / TEACHER 工作台） */
const AD_MENU = [
  { path: '/dashboard', title: '数据看板' },
  { path: '/course', title: '课程管理' },
  { path: '/training', title: '培训管理' },
  { path: '/case', title: '案例管理' },
  { path: '/seed-teacher', title: '种子教师' },
  { path: '/team', title: '支教队伍' },
  { path: '/teacher', title: '教师管理' },
  { path: '/feedback', title: '问答与反馈' },
  { path: '/audit', title: '审核中心' },
  { path: '/resource', title: '资源库' },
  { path: '/setting', title: '系统设置' },
];

const menuRoutes = computed(() => {
  if (auth.role === 'AUDITOR') return [{ path: '/audit', title: '审核中心' }];
  if (auth.role === 'TEACHER') return [{ path: '/teacher/workbench', title: '教师工作台' }];
  return AD_MENU;
});

const userRoleLabel = computed(() => {
  const label: Record<string, string> = {
    ADMIN: '系统管理员',
    AUDITOR: '审核员',
    TEACHER: auth.isSeed ? '种子教师' : '普通教师',
  };
  return label[auth.role] || '用户';
});

const menuIcons: Record<string, Component> = {
  '/dashboard': DataAnalysis,
  '/course': Files,
  '/training': VideoCamera,
  '/case': Collection,
  '/seed-teacher': Medal,
  '/team': Flag,
  '/teacher': User,
  '/feedback': ChatDotRound,
  '/audit': Checked,
  '/resource': FolderOpened,
  '/setting': Setting,
  '/teacher/workbench': Flag,
};

function onCommand(cmd: string) {
  if (cmd === 'logout') {
    auth.logout();
    router.push('/login');
  }
}

/* ---- 待办抽屉 ---- */
const bellOpen = ref(false);
const todos = ref<TodoItem[]>([]);
const todoCount = computed(() => todos.value.reduce((sum, t) => sum + t.count, 0));

watch(
  () => auth.user,
  async (u) => {
    todos.value = await fetchTodos(u);
  },
  { immediate: true },
);

function goTodo(path: string) {
  bellOpen.value = false;
  router.push(path);
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100%;
}

/* ===== Sidebar ===== */
.sidebar {
  width: var(--side-width);
  background: var(--side-bg);
  border-right: 1px solid var(--border-warm);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 64px;
  padding: 0 20px;
  flex-shrink: 0;
}
.logo-badge {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: var(--brand-primary);
  color: var(--on-brand);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.logo-text {
  font-family: var(--font-serif);   /* V2：Logo 衬线 */
  font-size: var(--font-size-m);
  font-weight: 650;
  color: var(--side-text);
}
.menu-scroll {
  flex: 1;
}
.menu {
  border-right: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(var(--rgb-brand-primary), 0.08);
  --el-menu-text-color: var(--side-text);
}
.menu :deep(.el-menu-item) {
  height: 46px;
  line-height: 46px;
  margin: 2px 10px;
  border-radius: 10px;
}
.menu :deep(.el-menu-item.is-active) {
  background-color: var(--side-active-bg);
  color: var(--side-active-text);
  font-weight: 600;
}
.version {
  flex-shrink: 0;
  padding: 16px 20px;
  font-size: 13px;
  color: var(--text-secondary);
  border-top: 1px solid var(--divider-soft);
}

/* ===== 主区域 ===== */
.main-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 32px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--divider-soft);
  flex-shrink: 0;
}
.page-title {
  margin: 0;
  font-family: var(--font-serif);   /* V2：页面 H1 衬线 */
  font-size: var(--font-size-xl);
  font-weight: 650;
  color: var(--text-primary);
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
/* 铃铛：裸按钮，悬停淡绿底 */
.bell {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  padding: 0;
  border: none;
  border-radius: 10px;
  background: transparent;
  cursor: pointer;
  transition: background-color var(--motion-hover) var(--ease-motion);
}
.bell:hover {
  background: rgba(var(--rgb-brand-primary), 0.1);
}
.bell-icon {
  display: block;
  color: var(--text-regular);
}

/* 待办抽屉 */
.bell-drawer :deep(.el-drawer__body) {
  padding: var(--space-l);
}
.drawer-head {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-bottom: var(--space-m);
  border-bottom: 1px solid var(--divider-soft);
}
.drawer-title {
  font-family: var(--font-serif);
  font-size: var(--font-size-m);
  font-weight: 650;
  color: var(--text-primary);
}
.drawer-sub {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}
.todo-rows {
  list-style: none;
  margin: 0;
  padding: 0;
}
.todo-rows li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-s);
  padding: 14px 4px;
  border-bottom: 1px solid var(--divider-soft);
  cursor: pointer;
}
.todo-rows li:last-child {
  border-bottom: none;
}
.todo-name {
  font-size: var(--font-size-s);
  color: var(--text-regular);
}
.todo-rows li:hover .todo-name {
  color: var(--brand-primary);
}
.todo-num {
  min-width: 26px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: var(--brand-primary-light);
  color: var(--brand-primary-dark);
  font-size: var(--font-size-xs);
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.todo-num.zero {
  background: var(--bg-oat);
  color: var(--text-secondary);
}
.user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.user-avatar {
  background: var(--brand-primary);
}
.user-name {
  font-size: var(--font-size-s);
  color: var(--text-regular);
}
.caret {
  font-size: 12px;
  color: var(--text-secondary);
}

/* ===== 内容区：1280px 居中 ===== */
.content {
  flex: 1;
  overflow: auto;
  /* 留白节奏（V3）：上 40px / 左右 32px / 下 40px，页面级留白用 --space-3xl 语义档 */
  padding: var(--space-2xl) var(--space-xl);
}
.content > * {
  max-width: 1280px;
  margin: 0 auto;
}
</style>