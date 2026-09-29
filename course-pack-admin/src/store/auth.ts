import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { request } from '@/api/request';
import { mockLogin } from '@/mock/auth';
import { ElMessage } from 'element-plus/es/components/message/index';

export interface UserInfo {
  id: number;
  username: string;
  name: string;
  role: 'TEACHER' | 'AUDITOR' | 'ADMIN';
  status: 'PENDING' | 'ACTIVE' | 'REJECTED' | 'DISABLED';
  isSeed: number;
  auditField?: string;
  rejectReason?: string;
}

const USER_KEY = 'adminUser';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('adminToken') || '');
  const user = ref<UserInfo | null>(readUser());
  const isLoggedIn = computed(() => !!token.value && !!user.value);
  const role = computed(() => user.value?.role || '');
  const status = computed(() => user.value?.status || '');
  const isSeed = computed(() => Number(user.value?.isSeed || 0) === 1);

  function readUser(): UserInfo | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserInfo;
    } catch {
      localStorage.removeItem(USER_KEY);
      return null;
    }
  }

  async function login(username: string, password: string): Promise<void> {
    let session: { token: string; user: UserInfo } | null = null;
    try {
      session = await request<{ token: string; user: UserInfo }>({
        url: '/auth/login',
        method: 'post',
        data: { username, password },
        skipErrorToast: true, // 登录失败自行处理提示，避免与 mock 兜底重复弹窗
      });
    } catch (e) {
      // 仅当后端不可达（网络错误无 response / 5xx）时回退本地演示账号；
      // 业务错误（reject 出的 body 无 response 字段，如密码错误）与 4xx 不回退。
      const err = e as { response?: { status?: number } };
      const isHttpError = typeof e === 'object' && e !== null && 'response' in (e as Record<string, unknown>);
      const status = err?.response?.status;
      const unreachable = isHttpError && (status === undefined || status >= 500);
      if (!unreachable) throw e;
      session = mockLogin(username, password);
      if (!session) {
        ElMessage.error('账号或密码错误');
        throw e;
      }
      ElMessage.info('后端未启动，已使用演示账号登录');
    }
    token.value = session.token;
    user.value = session.user;
    localStorage.setItem('adminToken', session.token);
    localStorage.setItem(USER_KEY, JSON.stringify(session.user));
  }

  function setUser(u: UserInfo | null) {
    user.value = u;
    if (u) {
      localStorage.setItem(USER_KEY, JSON.stringify(u));
    } else {
      localStorage.removeItem(USER_KEY);
    }
  }

  function logout() {
    token.value = '';
    user.value = null;
    localStorage.removeItem('adminToken');
    localStorage.removeItem(USER_KEY);
  }

  return { token, user, isLoggedIn, role, status, isSeed, login, setUser, logout };
});