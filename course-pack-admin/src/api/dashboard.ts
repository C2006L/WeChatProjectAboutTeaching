/**
 * 数据看板 api（批次H）
 * - mock 模式：全量走本地 mock
 * - 真实模式：GET /api/admin/dashboard 返回后端可聚合部分（待办/教师数/课程数/
 *   收藏数/教师增长趋势/主题分布/最新动态），覆盖 mock 基底；
 *   队伍/培训/案例域批次J 前沿用 mock 兜底，接口异常时整体回退 mock。
 */
import { dashboardData } from '@/mock/dashboard';
import { useMock } from './use-mock';
import { request } from './request';
import type { DashboardData, ActivityItem } from '@/types/dashboard';

/** 后端 DashboardVO 聚合 */
interface DashboardVOJson {
  pendingAudits: number;
  pendingTeachers: number;
  todoCount: number;
  teacherCount: number;
  courseOnlineCount: number;
  favoriteCount: number;
  growth: { month: string; teachers: number }[];
  themes: { theme: string; value: number; percent: number }[];
  activities: TimelineLogJson[];
}

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

function applyDashboardVO(base: DashboardData, vo: DashboardVOJson): DashboardData {
  const next: DashboardData = {
    ...base,
    welcome: {
      ...base.welcome,
      pendingReview: vo.pendingAudits ?? base.welcome.pendingReview,
      todoCount: vo.todoCount ?? base.welcome.todoCount,
    },
    todos: [
      { title: '累计待审核课程', count: vo.pendingAudits ?? 0, path: '/audit' },
      { title: '累计待审核教师', count: vo.pendingTeachers ?? 0, path: '/teachers' },
      base.todos[2],
      base.todos[3],
    ],
    activities: (vo.activities || []).map((l) => ({
      title: l.content,
      time: (l.createdAt || '').slice(5, 16),
      type: actionToType(l.action || ''),
    })),
  };

  const stats = base.stats.map((s) => ({ ...s }));
  const teacherStat = stats.find((s) => s.label.includes('教师'));
  if (teacherStat && vo.teacherCount != null) teacherStat.value = vo.teacherCount;
  next.stats = stats;

  if (vo.growth && vo.growth.length > 0) {
    next.growthTrend = {
      months: vo.growth.map((g) => g.month),
      teachers: vo.growth.map((g) => g.teachers),
      studies: base.growthTrend.studies, // 学习次数量批次J 前沿用 mock
    };
  }
  if (vo.themes && vo.themes.length > 0) {
    next.courseThemeDistribution = vo.themes.map((t) => ({
      theme: t.theme,
      value: t.value,
      percent: t.percent,
    }));
  }
  return next;
}

export async function fetchDashboard(): Promise<DashboardData> {
  if (useMock()) {
    return dashboardData;
  }
  try {
    const vo = await request<DashboardVOJson>({ url: '/admin/dashboard' });
    return applyDashboardVO(dashboardData, vo);
  } catch {
    // 聚合接口异常时整体回退 mock，不阻塞看板渲染
    return dashboardData;
  }
}
