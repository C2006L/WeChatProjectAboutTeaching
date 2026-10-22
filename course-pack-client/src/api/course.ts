/**
 * 课程模块 api（批次H）
 * - mock 模式：走本地 courseList
 * - 真实模式：
 *   GET  /api/courses          → fetchCourses（一次取满，页面本地筛选）
 *   GET  /api/courses/{id}     → fetchCourseDetail
 *   POST /api/courses/{id}/favorite → toggleFavorite（收藏状态本地即时切换，服务端只负责持久化）
 * 服务端VO与页面类型对齐：cover → coverColor（全局 token 色值由 CSS 变量承接）。
 */
import { courseList, getCourseById } from '@/mock/course';
import type { CourseSummary, CourseDetail } from '@/types/mock';
import { useMock } from './use-mock';
import { isFav } from '@/utils/favorite';
import { request } from '@/utils/request';

/** 后端 CourseVO */
interface CourseVOJson {
  id: number;
  title: string;
  theme: string;
  themeLabel: string;
  grade: string;
  duration: number;
  version: string;
  summary: string;
  cover?: string | null;
  materials: { plan: number; slides: number; toolkit: number; video: number };
  objectives?: string[];
  values?: string[];
  sensitiveQa?: number[];
  steps?: { index: number; title: string; desc: string; duration: number }[];
}

function voToSummary(vo: CourseVOJson): CourseSummary {
  return {
    id: vo.id,
    title: vo.title,
    theme: vo.theme as CourseSummary['theme'],
    themeLabel: vo.themeLabel,
    grade: vo.grade,
    duration: vo.duration,
    version: vo.version === '标准版' ? '标准版' : '精简版',
    summary: vo.summary,
    coverColor: vo.cover || '#4F8F83',
    materials: vo.materials || { plan: 0, slides: 0, toolkit: 0, video: 0 },
  };
}

export interface CourseFilter {
  keyword: string;
  version: '全部' | '精简版' | '标准版';
  grade: '全部' | '1-3年级' | '4-6年级';
  theme: string;
}

export const defaultCourseFilter: CourseFilter = {
  keyword: '',
  version: '全部',
  grade: '全部',
  theme: '全部',
};

/** 课程列表（V1 全量返回，前端筛选） */
export async function fetchCourses(): Promise<CourseSummary[]> {
  if (useMock()) {
    return courseList;
  }
  const page = await request<{ records: CourseVOJson[] }>({ url: '/courses?page=1&size=100' });
  return (page?.records || []).map(voToSummary);
}

/** 课程详情 */
export async function fetchCourseDetail(id: number): Promise<CourseDetail | null> {
  if (useMock()) {
    return getCourseById(id) ?? null;
  }
  try {
    const vo = await request<CourseVOJson>({ url: `/courses/${id}` });
    return {
      ...voToSummary(vo),
      objectives: vo.objectives || [],
      values: vo.values || [],
      sensitiveQa: vo.sensitiveQa || [],
      steps: vo.steps || [],
    };
  } catch {
    return null;
  }
}

/** 切换收藏（服务端持久化；具体收藏状态以后端 /api/user/favorites 为准） */
export function toggleFavorite(id: number): Promise<void> {
  return request({ url: `/courses/${id}/favorite`, method: 'POST', silent: true }).then(
    () => undefined,
  ) as Promise<void>;
}

/** 我的收藏课程列表（详情聚合，收藏页转 id 用） */
export async function fetchFavoriteCourses(): Promise<CourseSummary[]> {
  if (useMock()) {
    return courseList.filter((c) => isFav(c.id));
  }
  const vos = await request<CourseVOJson[]>({ url: '/user/favorites' });
  return (vos || []).map(voToSummary);
}
