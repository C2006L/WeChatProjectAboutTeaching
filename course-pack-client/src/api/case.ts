/**
 * 教学案例 API（批次J）
 * - mock 模式：本地持久化实现（storage 种子）
 * - 真实模式：
 *   GET  /api/cases       → fetchTeachingCases（公共库 APPROVED）
 *   GET  /api/cases/mine  → fetchMyTeachingCases
 *   GET  /api/cases/{id}  → fetchTeachingCaseDetail
 *   POST /api/cases       → saveDraft(action=DRAFT) / submit(action=PENDING)
 *   PUT  /api/cases/{id}  → 更新（本人案例）
 */
import { teachingCaseSeed } from '@/mock/case';
import type { TeachingCase, TeachingCasePayload, TeachingCaseStatus } from '@/types/mock';
import { useMock } from './use-mock';
import { request } from '@/utils/request';

const STORAGE_KEY = 'banya_teaching_cases';

/** 后端 CaseVO */
interface CaseVOJson {
  id: number;
  title: string;
  grade: string;
  theme: string;
  courseTitle?: string;
  summary?: string;
  content?: string;
  reflection?: string;
  author?: string;
  team?: string;
  status: string;
  featured?: boolean;
  isMine?: boolean;
  createdAt: string;
  updatedAt: string;
  reviewComment?: string;
}

function voToCase(vo: CaseVOJson): TeachingCase {
  return {
    id: vo.id,
    title: vo.title,
    grade: vo.grade === '小学1-3年级' ? '小学1-3年级' : '小学4-6年级',
    theme: vo.theme as TeachingCase['theme'],
    courseTitle: vo.courseTitle || '',
    summary: vo.summary || '',
    content: vo.content || '',
    reflection: vo.reflection || '',
    author: vo.author || '教师',
    team: vo.team || '未分配',
    status: vo.status as TeachingCaseStatus,
    featured: !!vo.featured,
    isMine: !!vo.isMine,
    createdAt: vo.createdAt,
    updatedAt: vo.updatedAt,
    reviewComment: vo.reviewComment,
  };
}

function cloneSeed(): TeachingCase[] {
  return teachingCaseSeed.map((item) => ({ ...item }));
}

function readAll(): TeachingCase[] {
  const raw = uni.getStorageSync(STORAGE_KEY);
  if (raw) {
    try {
      const list = JSON.parse(raw) as TeachingCase[];
      if (Array.isArray(list)) return list;
    } catch {
      // 本地数据损坏时回退种子数据，避免案例库白屏。
    }
  }
  const initial = cloneSeed();
  writeAll(initial);
  return initial;
}

function writeAll(list: TeachingCase[]) {
  uni.setStorageSync(STORAGE_KEY, JSON.stringify(list));
}

function today(): string {
  const date = new Date();
  const pad = (value: number) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

/** 公共案例库 */
export async function fetchTeachingCases(): Promise<TeachingCase[]> {
  if (useMock()) {
    return readAll()
      .filter((item) => item.status === 'APPROVED')
      .sort((a, b) => (a.updatedAt < b.updatedAt ? 1 : -1));
  }
  const vos = await request<CaseVOJson[]>({ url: '/cases' });
  return (vos || []).map(voToCase);
}

/** 我的案例 */
export async function fetchMyTeachingCases(): Promise<TeachingCase[]> {
  if (useMock()) {
    return readAll()
      .filter((item) => item.isMine)
      .sort((a, b) => (a.updatedAt < b.updatedAt ? 1 : -1));
  }
  const vos = await request<CaseVOJson[]>({ url: '/cases/mine' });
  return (vos || []).map(voToCase);
}

/** 详情（已通过或本人可见） */
export async function fetchTeachingCaseDetail(id: number): Promise<TeachingCase | null> {
  if (useMock()) {
    return readAll().find((item) => item.id === id) ?? null;
  }
  try {
    const vo = await request<CaseVOJson>({ url: `/cases/${id}` });
    return voToCase(vo);
  } catch {
    return null;
  }
}

async function persistCase(
  payload: TeachingCasePayload,
  status: TeachingCaseStatus,
  id?: number,
): Promise<TeachingCase> {
  if (useMock()) {
    const list = readAll();
    const now = today();
    const existingIndex = id ? list.findIndex((item) => item.id === id && item.isMine) : -1;
    const existing = existingIndex >= 0 ? list[existingIndex] : null;
    const item: TeachingCase = {
      id: existing?.id ?? Math.max(0, ...list.map((entry) => entry.id)) + 1,
      ...payload,
      author: existing?.author ?? '李老师',
      team: existing?.team ?? '第3支教队',
      status,
      featured: existing?.featured ?? false,
      isMine: true,
      createdAt: existing?.createdAt ?? now,
      updatedAt: now,
      reviewComment: status === 'DRAFT' ? existing?.reviewComment : undefined,
    };
    if (existingIndex >= 0) list.splice(existingIndex, 1, item);
    else list.push(item);
    writeAll(list);
    return item;
  }
  const body = {
    title: payload.title,
    grade: payload.grade,
    theme: payload.theme,
    courseTitle: payload.courseTitle,
    summary: payload.summary,
    content: payload.content,
    reflection: payload.reflection,
    action: status,
  };
  const vo = id
    ? await request<CaseVOJson>({ url: `/cases/${id}`, method: 'PUT', data: body })
    : await request<CaseVOJson>({ url: '/cases', method: 'POST', data: body });
  return voToCase(vo);
}

export function saveTeachingCaseDraft(payload: TeachingCasePayload, id?: number) {
  return persistCase(payload, 'DRAFT', id);
}

export function submitTeachingCase(payload: TeachingCasePayload, id?: number) {
  return persistCase(payload, 'PENDING', id);
}
