/**
 * 种子教师荣誉 api（mock 优先，上传案例数与案例管理数据联动计算）
 * 后端就绪后映射：
 *  GET /api/admin/seed-teachers → fetchSeedHonors（含 caseCount 聚合字段）
 */
import { seedHonor, teachingCases } from '@/mock/seed';
import type { SeedHonorRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export interface SeedHonorWithCount extends SeedHonorRow {
  caseCount: number;      // 上传案例总数（全部状态，与案例管理列表一致）
  approvedCount: number;  // 其中已通过四环节审核数
  featuredCount: number;  // 精选数
}

export async function fetchSeedHonors(): Promise<SeedHonorWithCount[]> {
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
