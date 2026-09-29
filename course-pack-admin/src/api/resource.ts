/**
 * 资源库 api（mock 优先）
 * 后端就绪后映射：
 *  GET  /api/admin/resources            → fetchResources
 *  POST /api/admin/resources/:id/download → downloadResource（次数 +1）
 */
import { resources } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import type { ResourceItem } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchResources(): Promise<ResourceItem[]> {
  await delay();
  return resources;
}

/** 下载资源：次数 +1，写入最新动态时间线 */
export async function downloadResource(id: number): Promise<void> {
  const r = resources.find((x) => x.id === id);
  if (!r) throw new Error(`资源不存在：id=${id}`);
  r.downloads += 1;
  activityStore.push(`已下载资源「${r.title}」`, 'course');
}
