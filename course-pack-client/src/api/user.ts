/**
 * 用户模块 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET /api/user        → fetchCurrentUser
 *   GET /api/user/menus  → fetchMineMenus
 */
import { currentUser, mineContentMenus, mineServiceMenus } from '@/mock/user';
import type { UserProfile, MineMenuItem } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export async function fetchCurrentUser(): Promise<UserProfile> {
  // return request<UserProfile>({ url: '/user' });
  return currentUser;
}

export async function fetchMineMenus(): Promise<{
  content: MineMenuItem[];
  service: MineMenuItem[];
}> {
  // return request({ url: '/user/menus' });
  return { content: mineContentMenus, service: mineServiceMenus };
}
