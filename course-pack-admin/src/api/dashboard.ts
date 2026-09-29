/**
 * 数据看板 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET /api/admin/dashboard → fetchDashboard
 */
import { dashboardData } from '@/mock/dashboard';
import type { DashboardData } from '@/types/dashboard';
// import { request } from '@/api/request'; // 后端就绪后启用

export async function fetchDashboard(): Promise<DashboardData> {
  // return request<DashboardData>({ url: '/admin/dashboard' });
  return dashboardData;
}
