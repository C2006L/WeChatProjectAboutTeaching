/**
 * 审核中心 api（mock 实现）
 * 数据源：src/mock/audit.ts（与全站其他模块一致，走本地 mock）；
 * AUDITOR 队列按登录账号 auditField 过滤（auditor01=PSY / auditor02=EDU），ADMIN 全量；
 * 通过/驳回操作从内存队列移除并写入 activityStore 时间线，看板"最新动态"实时联动。
 * 后端联调切换：恢复 request({ url: '/audit/queue' ... }) 系列（见 git 历史）。
 */
import { auditList } from '@/mock/audit';
import { activityStore } from '@/store/activity';
import { useAuthStore } from '@/store/auth';
import type { AuditItem } from '@/types/dashboard';

export async function fetchAuditList(): Promise<AuditItem[]> {
  const auth = useAuthStore();
  // 仅审核员按领域过滤；管理员及无领域账号查看全量
  const field = auth.role === 'AUDITOR' ? auth.user?.auditField : undefined;
  const source = field ? auditList.filter((a) => !a.auditField || a.auditField === field) : auditList;
  return source.map((a) => ({ ...a, stages: a.stages.map((s) => ({ ...s })), comments: [...a.comments] }));
}

/** 通过课程审核：移出队列 + 写时间线 */
export async function approveAudit(id: number): Promise<void> {
  const idx = auditList.findIndex((a) => a.id === id);
  if (idx === -1) return;
  const [item] = auditList.splice(idx, 1);
  activityStore.push(`课程《${item.title}》审核通过`, 'course');
}

/** 驳回课程审核（附原因）：移出队列 + 写时间线 */
export async function rejectAudit(id: number, reason: string): Promise<void> {
  const idx = auditList.findIndex((a) => a.id === id);
  if (idx === -1) return;
  const [item] = auditList.splice(idx, 1);
  activityStore.push(`课程《${item.title}》被驳回：${reason}`, 'course');
}
