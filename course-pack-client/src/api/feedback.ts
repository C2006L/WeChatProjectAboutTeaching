/**
 * 我的提问工单 api（mock 实现，storage 持久化）
 * 链路：教师在「去提问」提交 → 本地工单置 PENDING；
 *       Web 管理端「问答与反馈」回复后状态变 REPLIED（后端在线时经接口同步）。
 * 后端就绪后映射：
 *   GET  /api/feedback/tickets          → fetchMyTickets
 *   POST /api/feedback/tickets          → submitTicket
 */
import { STORAGE_TICKETS } from '@/config';
import { ticketSeed } from '@/mock/feedback';
import type { FeedbackTicket } from '@/types/mock';

/** 读取本地工单（首次访问用 seed 初始化） */
function readAll(): FeedbackTicket[] {
  const raw = uni.getStorageSync(STORAGE_TICKETS);
  if (raw) {
    try {
      const list = JSON.parse(raw) as FeedbackTicket[];
      if (Array.isArray(list)) return list;
    } catch {
      // 数据损坏时回退 seed，避免页面白屏
    }
  }
  uni.setStorageSync(STORAGE_TICKETS, JSON.stringify(ticketSeed));
  return [...ticketSeed];
}

function writeAll(list: FeedbackTicket[]) {
  uni.setStorageSync(STORAGE_TICKETS, JSON.stringify(list));
}

/** 当前时间 yyyy-MM-dd HH:mm */
function nowText(): string {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
}

/** 我的提问列表（最新在前） */
export async function fetchMyTickets(): Promise<FeedbackTicket[]> {
  return readAll().sort((a, b) => (a.time < b.time ? 1 : -1));
}

/** 提交提问（成功后工单为 PENDING） */
export async function submitTicket(payload: {
  title: string;
  content: string;
  category: string;
}): Promise<FeedbackTicket> {
  const list = readAll();
  const ticket: FeedbackTicket = {
    id: (list.reduce((max, t) => Math.max(max, t.id), 0) || 0) + 1,
    title: payload.title,
    content: payload.content,
    category: payload.category,
    status: 'PENDING',
    time: nowText(),
  };
  list.push(ticket);
  writeAll(list);
  return ticket;
}
