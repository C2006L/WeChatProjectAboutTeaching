/**
 * 教学案例 API（本地持久化实现）。
 * 后端就绪后保持函数签名不变，将内部替换为 request 调用即可。
 */
import { teachingCaseSeed } from '@/mock/case';
import type { TeachingCase, TeachingCasePayload, TeachingCaseStatus } from '@/types/mock';

const STORAGE_KEY = 'banya_teaching_cases';

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

export async function fetchTeachingCases(): Promise<TeachingCase[]> {
  return readAll()
    .filter((item) => item.status === 'APPROVED')
    .sort((a, b) => (a.updatedAt < b.updatedAt ? 1 : -1));
}

export async function fetchMyTeachingCases(): Promise<TeachingCase[]> {
  return readAll()
    .filter((item) => item.isMine)
    .sort((a, b) => (a.updatedAt < b.updatedAt ? 1 : -1));
}

export async function fetchTeachingCaseDetail(id: number): Promise<TeachingCase | null> {
  return readAll().find((item) => item.id === id) ?? null;
}

async function persistCase(
  payload: TeachingCasePayload,
  status: TeachingCaseStatus,
  id?: number,
): Promise<TeachingCase> {
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

export function saveTeachingCaseDraft(payload: TeachingCasePayload, id?: number) {
  return persistCase(payload, 'DRAFT', id);
}

export function submitTeachingCase(payload: TeachingCasePayload, id?: number) {
  return persistCase(payload, 'PENDING', id);
}
