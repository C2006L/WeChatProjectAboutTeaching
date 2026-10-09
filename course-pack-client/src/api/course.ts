/**
 * 课程模块 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用，页面零改动。
 *   GET /api/courses        → fetchCourses
 *   GET /api/courses/{id}   → fetchCourseDetail
 */
import { courseList, getCourseById } from '@/mock/course';
import type { CourseSummary, CourseDetail } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

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
  // return request<CourseSummary[]>({ url: '/courses' });
  return courseList;
}

/** 课程详情 */
export async function fetchCourseDetail(id: number): Promise<CourseDetail | null> {
  // return request<CourseDetail>({ url: `/courses/${id}` });
  return getCourseById(id) ?? null;
}
