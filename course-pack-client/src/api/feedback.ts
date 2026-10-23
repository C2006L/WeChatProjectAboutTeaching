/**
 * 我的提问工单 api（批次J）
 * - mock 模式：storage 持久化（本地 seed 初始化）
 * - 真实模式：
 *   GET  /api/feedback/tickets → fetchMyTickets（最新在前）
 *   POST /api/feedback/tickets → submitTicket（PENDING）
 */
import { STORAGE_TICKETS } from '@/config';
import { ticketSeed } from '@/mock/feedback';
import type { FeedbackTicket } from '@/types/mock';
import { useMock } from './use-mock';
import { request } from '@/utils/request';

/** 读取本地工单（mock 模式：首次访问用 seed 初始化） */
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

/** 后端 TicketVO */
interface TicketVOJson {
  id: number;
  teacher?: string;
  time: string;
  status: string;
  title: string;
  content: string;
  category?: string;
}

/** 我的提问列表（最新在前） */
export async function fetchMyTickets(): Promise<FeedbackTicket[]> {
  if (useMock()) {
    return readAll().sort((a, b) => (a.time < b.time ? 1 : -1));
  }
  const vos = await request<TicketVOJson[]>({ url: '/feedback/tickets' });
  return (vos || []).map((t) => ({
    id: t.id,
    title: t.title,
    content: t.content,
    category: t.category || '其他',
    status: t.status === 'REPLIED' ? 'REPLIED' : 'PENDING',
    time: t.time,
  }));
}

/** 提交提问（成功后工单为 PENDING） */
export async function submitTicket(payload: {
  title: string;
  content: string;
  category: string;
}): Promise<FeedbackTicket> {
  if (useMock()) {
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
  const vo = await request<TicketVOJson>({
    url: '/feedback/tickets',
    method: 'POST',
    data: { title: payload.title, content: payload.content, category: payload.category },
  });
  return {
    id: vo.id,
    title: vo.title,
    content: vo.content,
    category: vo.category || '其他',
    status: 'PENDING',
    time: vo.time,
  };
}
