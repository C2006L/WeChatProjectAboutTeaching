/**
 * 课程管理 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET    /api/admin/courses        → fetchCourseList
 *   POST   /api/admin/courses        → createCourse
 *   PUT    /api/admin/courses/{id}   → updateCourse
 *   DELETE /api/admin/courses/{id}   → deleteCourse
 */
import { courseAdminList } from '@/mock/course-admin';
import type { CourseAdminItem } from '@/types/dashboard';
// import { request } from '@/api/request'; // 后端就绪后启用

export async function fetchCourseList(): Promise<CourseAdminItem[]> {
  // return request<CourseAdminItem[]>({ url: '/admin/courses' });
  return courseAdminList;
}

export async function createCourse(item: CourseAdminItem): Promise<CourseAdminItem> {
  // return request<CourseAdminItem>({ url: '/admin/courses', method: 'POST', data: item });
  return item;
}

export async function updateCourse(item: CourseAdminItem): Promise<CourseAdminItem> {
  // return request<CourseAdminItem>({ url: `/admin/courses/${item.id}`, method: 'PUT', data: item });
  return item;
}

export async function deleteCourse(id: number): Promise<void> {
  // return request({ url: `/admin/courses/${id}`, method: 'DELETE' });
  void id;
}
