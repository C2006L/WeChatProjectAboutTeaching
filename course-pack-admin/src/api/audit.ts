/**
 * 审核中心 api
 * - mock 模式：内存队列 + 拟物化四环节，AUDITOR 按账号 auditField 过滤，ADMIN 全量
 * - 真实模式：GET /api/audit/queue（后端已就绪，批次G；后端按审核员 audit_field 过滤，管理员全量）
 *   POST /api/audit/:id/approve | :id/reject
 * 真实队列 VO 无阶段明细，四环节由 auditField 推导（本领域为待完成环节，其余视为已完成）。
 */
import { auditList } from '@/mock/audit';
import { activityStore } from '@/store/activity';
import { useAuthStore } from '@/store/auth';
import { useMock } from './use-mock';
import { request } from './request';
import type { AuditItem, AuditStage } from '@/types/dashboard';

/** ===== 真实接口类型（对齐后端 CourseAuditVO） ===== */
interface CourseAuditVOJson {
  id: number;
  courseId: number;
  title: string;
  auditField: string;   // EDU | PSY | SOC
  applicant?: string | null;
  submitTime?: string | null;
  progress: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  rejectReason?: string | null;
}

const STAGE_BY_FIELD: Record<string, { key: AuditStage['key']; label: string }> = {
  EDU: { key: 'education', label: '教育学审核' },
  PSY: { key: 'psychology', label: '儿童心理学' },
  SOC: { key: 'sociology', label: '社会学审核' },
};

const ALL_STAGES: { key: AuditStage['key']; label: string }[] = [
  { key: 'education', label: '教育学审核' },
  { key: 'psychology', label: '儿童心理学' },
  { key: 'sociology', label: '社会学审核' },
  { key: 'principal', label: '项目负责人' },
];

function synthesizeStages(field: string): AuditStage[] {
  const own = STAGE_BY_FIELD[field];
  return ALL_STAGES.map((s) => ({
    ...s,
    // 本领域为当前待完成环节；无领域信息（理论不出现）则全部待完成
    done: own ? s.key !== own.key : false,
  }));
}

function voToItem(vo: CourseAuditVOJson): AuditItem {
  const item: AuditItem = {
    id: vo.id,
    title: vo.title,
    applicant: vo.applicant || '—',
    submittedAt: (vo.submitTime || '').slice(0, 16),
    progress: vo.progress,
    stages: synthesizeStages(vo.auditField),
    comments: vo.rejectReason
      ? [
          {
            stageKey: STAGE_BY_FIELD[vo.auditField]?.key ?? 'education',
            stageLabel: STAGE_BY_FIELD[vo.auditField]?.label ?? '审核意见',
            content: vo.rejectReason,
            reviewer: '平台',
            time: (vo.submitTime || '').slice(0, 16),
          },
        ]
      : [],
  };
  if (vo.auditField) item.auditField = vo.auditField;
  return item;
}

export async function fetchAuditList(): Promise<AuditItem[]> {
  if (!useMock()) {
    const vos = await request<CourseAuditVOJson[]>({ url: '/audit/queue' });
    return vos.map(voToItem);
  }
  const auth = useAuthStore();
  // 仅审核员按领域过滤；管理员及无领域账号查看全量
  const field = auth.role === 'AUDITOR' ? auth.user?.auditField : undefined;
  const source = field ? auditList.filter((a) => !a.auditField || a.auditField === field) : auditList;
  return source.map((a) => ({ ...a, stages: a.stages.map((s) => ({ ...s })), comments: [...a.comments] }));
}

/** 通过课程审核：移出队列 + 写时间线 */
export async function approveAudit(id: number): Promise<void> {
  if (!useMock()) {
    await request({ url: `/audit/${id}/approve`, method: 'POST' });
    return;
  }
  const idx = auditList.findIndex((a) => a.id === id);
  if (idx === -1) return;
  const [item] = auditList.splice(idx, 1);
  activityStore.push(`课程《${item.title}》审核通过`, 'course');
}

/** 驳回课程审核（附原因）：移出队列 + 写时间线 */
export async function rejectAudit(id: number, reason: string): Promise<void> {
  if (!useMock()) {
    await request({ url: `/audit/${id}/reject`, method: 'POST', data: { reason } });
    return;
  }
  const idx = auditList.findIndex((a) => a.id === id);
  if (idx === -1) return;
  const [item] = auditList.splice(idx, 1);
  activityStore.push(`课程《${item.title}》被驳回：${reason}`, 'course');
}
