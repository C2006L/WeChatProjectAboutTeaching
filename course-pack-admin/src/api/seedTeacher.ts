/**
 * 种子教师荣誉 api（批次J）
 * - mock 模式：本地 seedHonor 与 teachingCases 按 uploader 名字联动计算
 * - 真实模式：GET /api/admin/seed-teachers（caseCount/approvedCount/featuredCount 服务端聚合）
 */
import { seedHonor, teachingCases } from '@/mock/seed';
import { useMock } from './use-mock';
import { request } from './request';
import type { SeedHonorRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export interface SeedHonorWithCount extends SeedHonorRow {
  caseCount: number;      // 上传案例总数（全部状态，与案例管理列表一致）
  approvedCount: number;  // 其中已通过四环节审核数
  featuredCount: number;  // 精选数
}

export async function fetchSeedHonors(): Promise<SeedHonorWithCount[]> {
  if (useMock()) {
    await delay();
    return seedHonor.map((h) => {
      const mine = teachingCases.filter((c) => c.uploader === h.name);
      return {
        ...h,
        caseCount: mine.length,
        approvedCount: mine.filter((c) => c.status === 'APPROVED').length,
        featuredCount: mine.filter((c) => c.featured).length,
      };
    });
  }
  return request<SeedHonorWithCount[]>({ url: '/admin/seed-teachers' });
}
