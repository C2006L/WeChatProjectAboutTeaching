/**
 * 案例管理 api（mock 优先）
 * 后端就绪后映射：
 *  GET  /api/admin/cases              → fetchCases
 *  PUT  /api/admin/cases/:id/featured → toggleFeatured
 */
import { teachingCases } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import type { CaseItem } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchCases(): Promise<CaseItem[]> {
  await delay();
  return teachingCases;
}

/** 标/取消精选，写入最新动态时间线 */
export async function toggleFeatured(id: number): Promise<boolean> {
  const c = teachingCases.find((x) => x.id === id);
  if (!c) throw new Error(`案例不存在：id=${id}`);
  c.featured = !c.featured;
  activityStore.push(
    c.featured ? `已将案例「${c.title}」标记为精选` : `已取消案例「${c.title}」的精选标记`,
    'course',
  );
  return c.featured;
}
