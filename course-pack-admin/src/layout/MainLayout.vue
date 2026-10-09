<template>
  <div class="layout">
    <!-- 左侧固定 Sidebar：燕麦底 -->
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-badge" aria-hidden="true"><i></i></span>
        <span class="logo-copy">
          <span class="logo-text">伴芽小站</span>
          <span class="logo-sub">教育工作台</span>
        </span>
      </div>

      <el-scrollbar class="menu-scroll">
        <div v-for="group in menuGroups" :key="group.label" class="menu-group">
          <div class="menu-label">{{ group.label }}</div>
          <el-menu :default-active="activeMenu" router class="menu">
            <el-menu-item v-for="m in group.items" :key="m.path" :index="m.path">
              <el-icon><component :is="menuIcons[m.path]" /></el-icon>
              <span>{{ m.title }}</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-scrollbar>

      <div class="version">伴芽小站 · 项目运营端</div>
    </aside>

    <!-- 右侧：Header + 内容区 -->
    <div class="main-wrap">
      <header class="header">
        <div class="page-heading">
          <span class="page-kicker">伴芽小站 / 工作台</span>
          <div class="title-line">
            <h1 class="page-title">{{ currentTitle }}</h1>
            <span class="page-description">{{ currentDescription }}</span>
          </div>
        </div>
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
const PAGE_DESCRIPTIONS: Record<string, string> = {
  '/dashboard': '掌握项目进展，优先处理今天的工作',
  '/course': '维护课程内容、版本与发布状态',
  '/training': '查看必修进度与培训课程',
  '/case': '沉淀一线教学实践',
  '/seed-teacher': '维护种子教师认证与荣誉',
  '/team': '查看支教队伍及服务情况',
  '/teacher': '管理教师账号与学习状态',
  '/feedback': '回应教师问题与平台反馈',
  '/audit': '集中处理内容审核任务',
  '/resource': '维护可下载的标准化教学素材',
  '/setting': '配置平台与演示账号',
};
const currentDescription = computed(() => PAGE_DESCRIPTIONS[route.path] || '伴芽小站项目工作区');

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

const menuGroups = computed(() => {
  const visible = menuRoutes.value;
  if (auth.role !== 'ADMIN') return [{ label: '工作区', items: visible }];
  const paths = (items: string[]) => visible.filter((item) => items.includes(item.path));
  return [
    { label: '总览', items: paths(['/dashboard']) },
    { label: '内容运营', items: paths(['/course', '/training', '/case', '/resource']) },
    { label: '人员与组织', items: paths(['/seed-teacher', '/team', '/teacher']) },
    { label: '任务处理', items: paths(['/feedback', '/audit']) },
    { label: '平台', items: paths(['/setting']) },
  ];
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
  border-right: 0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 72px;
  padding: 0 20px;
  flex-shrink: 0;
}
.logo-badge {
  position: relative;
  width: 34px;
  height: 34px;
  border-radius: 15px 8px 8px 8px;
  background: #2EA69B;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.logo-badge::before,
.logo-badge::after {
  content: '';
  position: absolute;
  top: 9px;
  width: 10px;
  height: 7px;
  border: 2px solid #fff;
  border-bottom: 0;
}
.logo-badge::before {
  left: 7px;
  border-radius: 10px 2px 0 2px;
  transform: rotate(28deg);
}
.logo-badge::after {
  right: 7px;
  border-radius: 2px 10px 2px 0;
  transform: rotate(-28deg);
}
.logo-badge i {
  width: 2px;
  height: 12px;
  margin-top: 9px;
  border-radius: 2px;
  background: #fff;
}
.logo-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
}
.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  line-height: 1.25;
}
.logo-sub {
  margin-top: 2px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.52);
  letter-spacing: 0.08em;
}
.menu-scroll {
  flex: 1;
  padding: 8px 0 16px;
}
.menu-group + .menu-group {
  margin-top: 14px;
}
.menu-label {
  padding: 0 20px 6px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.36);
  letter-spacing: 0.12em;
}
.menu {
  border-right: none;
  background: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.07);
  --el-menu-text-color: rgba(255, 255, 255, 0.68);
}
.menu :deep(.el-menu-item) {
  height: 42px;
  line-height: 42px;
  margin: 1px 12px;
  border-radius: 8px;
}
.menu :deep(.el-menu-item.is-active) {
  position: relative;
  background-color: rgba(255, 255, 255, 0.12);
  color: #fff;
  font-weight: 600;
}
.menu :deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 11px;
  width: 3px;
  height: 20px;
  border-radius: 0 4px 4px 0;
  background: var(--brand-secondary);
}
.version {
  flex-shrink: 0;
  padding: 16px 20px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.42);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

/* ===== 主区域 ===== */
.main-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--bg-page);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 86px;
  padding: 18px 32px 12px;
  background: transparent;
  border-bottom: 0;
  flex-shrink: 0;
}
.page-heading {
  min-width: 0;
}
.page-kicker {
  display: block;
  margin-bottom: 4px;
  font-size: 11px;
  font-weight: 600;
  color: var(--brand-primary);
  letter-spacing: 0.1em;
}
.title-line {
  display: flex;
  align-items: baseline;
  gap: 14px;
}
.page-title {
  margin: 0;
  font-size: 23px;
  font-weight: 700;
  color: var(--text-primary);
}
.page-description {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: var(--text-secondary);
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
  padding: 8px 32px 40px;
}
.content > * {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
