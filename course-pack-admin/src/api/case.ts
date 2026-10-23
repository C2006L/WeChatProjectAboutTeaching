/**
 * 案例管理 api（批次J）
 * - mock 模式：走本地 teachingCases
 * - 真实模式：
 *   GET  /api/admin/cases            → fetchCases（含四环节 reviews）
 *   POST /api/admin/cases/{id}/featured → toggleFeatured（服务端写时间线，返回切换后的值）
 */
import { teachingCases } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import { useMock } from './use-mock';
import { request } from './request';
import type { CaseItem, AuditStage, AuditComment } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

/** 后端 CaseAdminVO */
interface CaseAdminVOJson {
  id: number;
  title: string;
  uploader?: string;
  team?: string;
  uploadedAt: string;
  status: string;
  featured?: boolean;
  summary?: string;
  stages?: { key: string; label: string; done?: boolean }[];
  comments?: { stageKey: string; stageLabel: string; content: string; reviewer?: string; time: string }[];
}

const STAGE_SEQUENCE: AuditStage['key'][] = ['education', 'psychology', 'sociology', 'principal'];

function normalizeStageKey(raw: string): AuditStage['key'] {
  const s = raw.toLowerCase();
  const hit = STAGE_SEQUENCE.find((k) => s.startsWith(k));
  return hit ?? 'education';
}

function voToItem(vo: CaseAdminVOJson): CaseItem {
  return {
    id: vo.id,
    title: vo.title,
    uploader: vo.uploader || '教师',
    team: vo.team || '未分配',
    uploadedAt: vo.uploadedAt,
    status: vo.status === 'APPROVED' ? 'APPROVED' : 'PENDING',
    featured: !!vo.featured,
    summary: vo.summary || '',
    stages: (vo.stages || []).map((s) => ({
      key: normalizeStageKey(s.key),
      label: s.label,
      done: !!s.done,
    })) as CaseItem['stages'],
    comments: (vo.comments || []).map((c) => ({
      stageKey: normalizeStageKey(c.stageKey),
      stageLabel: c.stageLabel,
      content: c.content,
      reviewer: c.reviewer || '',
      time: c.time,
    })) as AuditComment[],
  };
}

export async function fetchCases(): Promise<CaseItem[]> {
  if (useMock()) {
    await delay();
    return teachingCases;
  }
  const vos = await request<CaseAdminVOJson[]>({ url: '/admin/cases' });
  return (vos || []).map(voToItem);
}

/** 标/取消精选，写入最新动态时间线；返回切换后的值 */
export async function toggleFeatured(id: number): Promise<boolean> {
  if (useMock()) {
    const c = teachingCases.find((x) => x.id === id);
    if (!c) throw new Error(`案例不存在：id=${id}`);
    c.featured = !c.featured;
    activityStore.push(
      c.featured ? `已将案例「${c.title}」标记为精选` : `已取消案例「${c.title}」的精选标记`,
      'course',
    );
    return c.featured;
  }
  const res = await request<{ featured: boolean }>({ url: `/admin/cases/${id}/featured`, method: 'POST' });
  return !!res?.featured;
}
