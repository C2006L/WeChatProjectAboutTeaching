/**
 * 用户模块 api
 * - mock 模式：本地用户与菜单
 * - 真实模式：GET /api/auth/me（后端已就绪，批次G）、POST /api/auth/wx-login（批次I）
 */
import { currentUser, mineContentMenus, mineServiceMenus } from '@/mock/user';
import type { UserProfile, MineMenuItem } from '@/types/mock';
import { useMock } from './use-mock';
import { request } from '@/utils/request';

/** 后端 UserVO */
interface UserVOJson {
  id: number;
  username: string;
  name: string;
  role: 'TEACHER' | 'AUDITOR' | 'ADMIN';
  status: 'ACTIVE' | 'PENDING' | 'REJECTED' | 'DISABLED';
  isSeed: number;
  auditField?: string | null;
  rejectReason?: string | null;
}

/** 管理端/审核员不落在小程序（登录入口已拦截），路由映射只处理 TEACHER 主干 */
function voToProfile(vo: UserVOJson): UserProfile {
  return {
    ...currentUser,
    id: vo.id,
    name: vo.name || vo.username,
    badge: vo.isSeed === 1 ? '种子教师' : currentUser.badge,
    is_seed: vo.isSeed,
    role: vo.role,
    status: vo.status,
    audit_field: vo.auditField || undefined,
  };
}

/** 仅已登录（有 token）时调用；未登录返回 mock 默认，不触发 401 跳转 */
export async function fetchCurrentUser(): Promise<UserProfile> {
  if (useMock()) {
    return currentUser;
  }
  try {
    const vo = await request<UserVOJson>({ url: '/auth/me', silent: true });
    return voToProfile(vo);
  } catch {
    return currentUser;
  }
}

export async function fetchMineMenus(): Promise<{
  content: MineMenuItem[];
  service: MineMenuItem[];
}> {
  return { content: mineContentMenus, service: mineServiceMenus };
}

/** 微信登录（真实模式）：code → JWT + 用户信息 */
export async function wxLogin(code: string): Promise<{ token: string; user: UserVOJson }> {
  const res = await request<{ token: string; user: UserVOJson }>({
    url: '/auth/wx-login',
    method: 'POST',
    data: { code },
  });
  return res;
}
