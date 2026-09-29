import type { UserInfo } from '@/store/auth';

/**
 * 演示账号表（密码统一 123456，README 账号表同构）
 * 后端不可达（网络错误/5xx）时由 auth store 回退使用；
 * 后端在线时以 /auth/login 真实鉴权为准。
 */
export interface MockAccount {
  username: string;
  password: string;
  user: UserInfo;
}

export const MOCK_PASSWORD = '123456';

export const mockAccounts: MockAccount[] = [
  {
    username: 'admin01',
    password: MOCK_PASSWORD,
    user: { id: 1, username: 'admin01', name: '管理员', role: 'ADMIN', status: 'ACTIVE', isSeed: 0 },
  },
  {
    username: 'auditor01',
    password: MOCK_PASSWORD,
    user: { id: 2, username: 'auditor01', name: '陈教授', role: 'AUDITOR', status: 'ACTIVE', isSeed: 0, auditField: 'PSY' },
  },
  {
    username: 'auditor02',
    password: MOCK_PASSWORD,
    user: { id: 3, username: 'auditor02', name: '周教授', role: 'AUDITOR', status: 'ACTIVE', isSeed: 0, auditField: 'EDU' },
  },
  {
    username: 'teacher01',
    password: MOCK_PASSWORD,
    user: { id: 4, username: 'teacher01', name: '李老师', role: 'TEACHER', status: 'ACTIVE', isSeed: 0 },
  },
  {
    username: 'teacher02',
    password: MOCK_PASSWORD,
    user: { id: 5, username: 'teacher02', name: '王老师', role: 'TEACHER', status: 'PENDING', isSeed: 0 },
  },
  {
    username: 'teacher03',
    password: MOCK_PASSWORD,
    user: {
      id: 6, username: 'teacher03', name: '赵老师', role: 'TEACHER', status: 'REJECTED', isSeed: 0,
      rejectReason: '支教证明材料不清晰',
    },
  },
  {
    username: 'teacher04',
    password: MOCK_PASSWORD,
    user: { id: 7, username: 'teacher04', name: '张老师', role: 'TEACHER', status: 'ACTIVE', isSeed: 1 },
  },
];

/** mock 校验：命中返回 token+user，否则 null */
export function mockLogin(username: string, password: string): { token: string; user: UserInfo } | null {
  const acc = mockAccounts.find((a) => a.username === username && a.password === password);
  if (!acc) return null;
  return { token: `mock-token-${acc.username}`, user: { ...acc.user } };
}
