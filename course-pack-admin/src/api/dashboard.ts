/**
 * 数据看板 api
 * - mock 模式：全量走本地 mock
 * - 真实模式：统计/图表域后端未建（批次H），沿用 mock 基底，
 *   仅"最新动态"接真实 GET /api/admin/timeline（后端已就绪，批次G），失败回退 mock 动态。
 */
import { dashboardData } from '@/mock/dashboard';
import { useMock } from './use-mock';
import { request } from './request';
import type { DashboardData, ActivityItem } from '@/types/dashboard';

/** 后端 TimelineLogVO */
interface TimelineLogJson {
  id: number;
  action: string;
  content: string;
  operatorId?: number | null;
  createdAt: string;
}

function actionToType(action: string): ActivityItem['type'] {
  if (action.startsWith('TEACHER')) return 'user';
  if (action.startsWith('AUDIT') || action.startsWith('COURSE')) return 'course';
  return 'feedback';
}

export async function fetchDashboard(): Promise<DashboardData> {
  if (useMock()) {
    return dashboardData;
  }
  const base: DashboardData = { ...dashboardData };
  try {
    const logs = await request<TimelineLogJson[]>({ url: '/admin/timeline?limit=10' });
    base.activities = logs.map((l) => ({
      title: l.content,
      time: (l.createdAt || '').slice(5, 16),
      type: actionToType(l.action || ''),
    }));
  } catch {
    // 时间线接口异常时保留 mock 动态，不阻塞看板渲染
  }
  return base;
}
