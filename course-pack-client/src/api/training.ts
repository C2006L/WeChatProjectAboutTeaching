/**
 * 培训模块 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET /api/trainings       → fetchTrainingList
 *   GET /api/trainings/{id}  → fetchTrainingById
 */
import { trainingList, getTrainingById } from '@/mock/training';
import type { Training } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export async function fetchTrainingList(): Promise<Training[]> {
  // return request<Training[]>({ url: '/trainings' });
  return trainingList;
}

export async function fetchTrainingById(id: number): Promise<Training | null> {
  // return request<Training>({ url: `/trainings/${id}` });
  return getTrainingById(id) ?? null;
}
