/**
 * 支教队伍 api（批次J）
 * - mock 模式：走本地 teams
 * - 真实模式：GET /api/admin/teams（成员必修进度服务端实时聚合，user 关联行走 teacher_training）
 */
import { teams } from '@/mock/seed';
import { useMock } from './use-mock';
import { request } from './request';
import type { TeamDetailRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

/** 队伍必修完成率 = 成员必修进度的平均值（与培训数据口径一致） */
export function teamCompletion(t: TeamDetailRow): number {
  if (!t.members.length) return 0;
  const sum = t.members.reduce((acc, m) => acc + m.requiredDone / m.requiredTotal, 0);
  return Math.round((sum / t.members.length) * 100);
}

export async function fetchTeams(): Promise<TeamDetailRow[]> {
  if (useMock()) {
    await delay();
    return teams;
  }
  return request<TeamDetailRow[]>({ url: '/admin/teams' });
}
