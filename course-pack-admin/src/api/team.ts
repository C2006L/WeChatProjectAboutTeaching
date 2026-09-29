/**
 * 支教队伍 api（mock 优先）
 * 后端就绪后映射：
 *  GET /api/admin/teams → fetchTeams
 */
import { teams } from '@/mock/seed';
import type { TeamDetailRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

/** 队伍必修完成率 = 成员必修进度的平均值（与培训数据口径一致） */
export function teamCompletion(t: TeamDetailRow): number {
  if (!t.members.length) return 0;
  const sum = t.members.reduce((acc, m) => acc + m.requiredDone / m.requiredTotal, 0);
  return Math.round((sum / t.members.length) * 100);
}

export async function fetchTeams(): Promise<TeamDetailRow[]> {
  await delay();
  return teams;
}
