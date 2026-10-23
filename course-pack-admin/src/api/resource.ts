/**
 * 资源库 api（批次J）
 * - mock 模式：走本地 resources
 * - 真实模式：
 *   GET  /api/admin/resources            → fetchResources
 *   POST /api/admin/resources/:id/download → downloadResource（次数 +1，服务端写时间线）
 */
import { resources } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import { useMock } from './use-mock';
import { request } from './request';
import type { ResourceItem } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchResources(): Promise<ResourceItem[]> {
  if (useMock()) {
    await delay();
    return resources;
  }
  return request<ResourceItem[]>({ url: '/admin/resources' });
}

/** 下载资源：次数 +1（真实模式由服务端写最新动态） */
export async function downloadResource(id: number): Promise<void> {
  if (useMock()) {
    const r = resources.find((x) => x.id === id);
    if (!r) throw new Error(`资源不存在：id=${id}`);
    r.downloads += 1;
    activityStore.push(`已下载资源「${r.title}」`, 'course');
    return;
  }
  const latest = await request<ResourceItem>({ url: `/admin/resources/${id}/download`, method: 'POST' });
  activityStore.push(`已下载资源「${latest?.title ?? ''}」`, 'course');
}
