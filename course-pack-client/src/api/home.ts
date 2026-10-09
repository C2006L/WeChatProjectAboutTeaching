/**
 * 首页 api（mock 优先）
 * 后端就绪后：替换为 request 调用。
 *   GET /api/home → fetchHomeData
 */
import { homeData } from '@/mock/home';
import type { HomeData } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export async function fetchHomeData(): Promise<HomeData> {
  // return request<HomeData>({ url: '/home' });
  return homeData;
}
