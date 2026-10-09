/**
 * 敏感问题 api（mock 优先）
 * 后端就绪后：替换为 request 调用。
 *   GET /api/qa?ids=      → fetchQaByIds
 *   GET /api/qa           → fetchQaList
 */
import { qaList, getQaById } from '@/mock/qa';
import type { QA } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export async function fetchQaList(): Promise<QA[]> {
  // return request<QA[]>({ url: '/qa' });
  return qaList;
}

export async function fetchQaByIds(ids: number[]): Promise<QA[]> {
  // return request<QA[]>({ url: '/qa', data: { ids } });
  return qaList.filter((q) => ids.includes(q.id));
}

export async function fetchQaById(id: number): Promise<QA | null> {
  return getQaById(id) ?? null;
}
