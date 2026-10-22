/**
 * 首页 api
 * - mock 模式：全量本地数据
 * - 真实模式：用户名与"最近备课草稿"接真实（GET /api/auth/me、GET /api/tools/prepare/draft，批次G），
 *   "今日推荐"取课程列表第一门（GET /api/courses，批次H）；
 *   失败静默回退 mock 值，不阻塞首屏渲染。
 */
import { homeData } from '@/mock/home';
import type { HomeData } from '@/types/mock';
import { useMock } from './use-mock';
import { request } from '@/utils/request';
import { fetchCourses } from './course';

/** 后端 LessonDraft */
interface LessonDraftJson {
  id: number;
  courseId?: number | null;
  title?: string | null;
  updatedAt?: string | null;
}

function relativeTime(dateStr?: string | null): string {
  if (!dateStr) return '刚刚';
  const ts = new Date(dateStr.replace(' ', 'T')).getTime();
  if (Number.isNaN(ts)) return '刚刚';
  const diffMin = Math.floor((Date.now() - ts) / 60000);
  if (diffMin < 60) return `${Math.max(diffMin, 1)} 分钟前`;
  if (diffMin < 60 * 24) return `${Math.floor(diffMin / 60)} 小时前`;
  const days = Math.floor(diffMin / (60 * 24));
  return days <= 7 ? `${days} 天前` : dateStr.slice(0, 10);
}

export async function fetchHomeData(): Promise<HomeData> {
  if (useMock()) {
    return homeData;
  }
  const base: HomeData = { ...homeData, recentDrafts: [] };

  try {
    const me = await request<{ name: string; username: string }>({ url: '/auth/me', silent: true });
    if (me?.name) base.userName = me.name;
  } catch {
    /* 未登录/网络异常：保留 mock 用户名 */
  }

  try {
    const draft = await request<LessonDraftJson | null>({ url: '/tools/prepare/draft', silent: true });
    if (draft) {
      base.recentDrafts = [
        {
          id: draft.id,
          courseName: draft.title || '未命名草稿',
          editTime: relativeTime(draft.updatedAt),
          path: '/subpkg/tools/prepare',
        },
      ];
    }
  } catch {
    /* 草稿接口异常：显示空草稿态 */
  }

  try {
    const courses = await fetchCourses();
    if (courses.length) base.todayRecommend = courses[0];
  } catch {
    /* 课程列表异常：保留 mock 今日推荐 */
  }
  return base;
}
