import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/auth';
import MainLayout from '@/layout/MainLayout.vue';

/** ADMIN 全量 11 个菜单 */
const ADMIN_MENU = [
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

/** TEACHER 工作台专属菜单 */
export const TEACHER_MENU = [
  { path: '/teacher/workbench', title: '教师工作台', children: [] as { key: string; label: string }[] },
];

/** 各角色可访问的顶级路径白名单（前端导航守卫，不含工作台内部 tab） */
const ROLE_PATHS: Record<string, string[]> = {
  ADMIN: ADMIN_MENU.map((m) => m.path),
  AUDITOR: ['/audit'],
  TEACHER: ['/teacher/workbench', '/teacher/status'],
};

/** 路径 → 视图文件名：/seed-teacher → SeedTeacherView */
function viewName(path: string): string {
  return (
    path
      .slice(1)
      .split('-')
      .map((s) => s.charAt(0).toUpperCase() + s.slice(1))
      .join('') + 'View'
  );
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/LoginView.vue'),
      meta: { public: true, title: '登录' },
    },
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: '', redirect: '/dashboard' },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: '数据看板' },
        },
        ...ADMIN_MENU.filter((m) => m.path !== '/dashboard' && m.path !== '/setting')
          .map((m) => ({
            path: m.path.slice(1),
            name: m.path.slice(1),
            component: () => import(`@/views/${m.path.slice(1)}/${viewName(m.path)}.vue`),
            meta: { title: m.title },
          })),
        {
          path: 'setting',
          name: 'setting',
          component: () => import('@/views/setting/SettingView.vue'),
          meta: { title: '系统设置' },
        },
        // TEACHER 工作台相关
        {
          path: 'teacher/workbench',
          name: 'teacher-workbench',
          component: () => import('@/views/teacher/TeacherWorkbenchView.vue'),
          meta: { title: '教师工作台' },
        },
        {
          path: 'teacher/status',
          name: 'teacher-status',
          component: () => import('@/views/teacher/TeacherStatusView.vue'),
          meta: { title: '账号状态' },
        },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
});

/** 登录后按角色 + 状态分流的默认落地路径 */
function homeByRole(role: string, status: string): string {
  if (role === 'AUDITOR') return '/audit';
  if (role === 'TEACHER') {
    if (status === 'ACTIVE') return '/teacher/workbench';
    return '/teacher/status';
  }
  return '/dashboard';
}

// 登录守卫：认证 + 角色路径白名单 + 落地分流
router.beforeEach((to) => {
  const auth = useAuthStore();

  // 公开页（登录）
  if (to.meta.public) {
    // 已登录访问 /login → 按角色落地
    if (auth.isLoggedIn) {
      return homeByRole(auth.role, auth.status);
    }
    return true;
  }

  // 未登录 → 登录页
  if (!auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.path } };
  }

  const role = auth.role;

  // 落地分流：TEACHER 未 ACTIVE 但尝试进工作台 → 状态页
  if (role === 'TEACHER' && auth.status !== 'ACTIVE') {
    return '/teacher/status';
  }

  // 路径白名单校验
  const allowed = ROLE_PATHS[role] || [];
  if (allowed.length && !allowed.includes(to.path)) {
    return homeByRole(role, auth.status);
  }

  // 已登录访问根/默认(空 path 已 redirect dashboard)；TEACHER 命中断言则不适用，稳一遍
  return true;
});

router.afterEach((to) => {
  document.title = to.meta.title ? `${String(to.meta.title)} · 伴芽小站` : '伴芽小站工作台';
});

export default router;
