/**
 * 课程管理 api（批次H）
 * - mock 模式：走本地 courseAdminList
 * - 真实模式：
 *   GET    /api/admin/courses?page=&size=   → fetchCourseList（一次取满，前端本地筛选）
 *   POST   /api/admin/courses               → createCourse
 *   PUT    /api/admin/courses/{id}          → updateCourse
 *   DELETE /api/admin/courses/{id}          → deleteCourse
 * 管理端表单仅覆盖标题/主题/学段/版本/时长/简介/上架，其余域
 * （目标/价值/流程/材料数量）按缺省模板提交，详情编辑批次K 再开放。
 */
import { courseAdminList } from '@/mock/course-admin';
import { useMock } from './use-mock';
import { request } from './request';
import type { CourseAdminItem } from '@/types/dashboard';

/** 后端 CourseVO（管理端列表形态） */
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
  /** 材料数量统计 {plan,slides,toolkit,video} */
  materials: { plan: number; slides: number; toolkit: number; video: number };
  status: number; // 0 下架 | 1 上架
  /** yyyy-MM-dd HH:mm:ss（Jackson 全局格式） */
  updatedAt: string;
}

/** 后端 CourseUpsertRequest */
interface CourseUpsertJson {
  title: string;
  theme: string;
  grade: string;
  duration: number;
  version: string;
  summary: string;
  cover?: string;
  status?: number;
  objectives: string[];
  values: string[];
  steps: { index: number; title: string; desc: string; duration: number }[];
  materials: CourseAdminItem['materials'];
}

function voToItem(vo: CourseVOJson): CourseAdminItem {
  return {
    id: vo.id,
    title: vo.title,
    theme: vo.theme,
    themeLabel: vo.themeLabel,
    grade: vo.grade,
    duration: vo.duration,
    version: vo.version === '标准版' ? '标准版' : '精简版',
    summary: vo.summary,
    status: vo.status === 1,
    materials: vo.materials || { plan: 0, slides: 0, toolkit: 0, video: 0 },
    updatedAt: (vo.updatedAt || '').slice(0, 16),
  };
}

function itemToUpsert(item: CourseAdminItem): CourseUpsertJson {
  return {
    title: item.title,
    theme: item.theme,
    grade: item.grade,
    duration: item.duration,
    version: item.version,
    summary: item.summary,
    status: item.status ? 1 : 0,
    objectives: ['暂无'],
    values: ['暂无'],
    steps: [{ index: 1, title: '课程学习', desc: item.summary, duration: item.duration }],
    materials: item.materials,
  };
}

export async function fetchCourseList(): Promise<CourseAdminItem[]> {
  if (useMock()) {
    return courseAdminList;
  }
  const page = await request<{ records: CourseVOJson[] }>({
    url: '/admin/courses?page=1&size=100',
  });
  return (page?.records || []).map(voToItem);
}

export async function createCourse(item: CourseAdminItem): Promise<CourseAdminItem> {
  if (useMock()) {
    return item;
  }
  const vo = await request<CourseVOJson>({
    url: '/admin/courses',
    method: 'POST',
    data: itemToUpsert(item),
  });
  return voToItem(vo);
}

export async function updateCourse(item: CourseAdminItem): Promise<CourseAdminItem> {
  if (useMock()) {
    return item;
  }
  const vo = await request<CourseVOJson>({
    url: `/admin/courses/${item.id}`,
    method: 'PUT',
    data: itemToUpsert(item),
  });
  return voToItem(vo);
}

export async function deleteCourse(id: number): Promise<void> {
  if (useMock()) {
    return;
  }
  await request({ url: `/admin/courses/${id}`, method: 'DELETE' });
}
