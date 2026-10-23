/**
 * 问答与反馈 api（批次J）
 * - mock 模式：走本地 feedbackTickets
 * - 真实模式：
 *   GET  /api/admin/feedback/tickets          → fetchTickets（最新在前）
 *   POST /api/admin/feedback/tickets/:id/reply → replyTicket（写服务端时间线）
 */
import { feedbackTickets } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import { useMock } from './use-mock';
import { request } from './request';
import type { FeedbackTicket } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

/** 后端 TicketVO */
interface TicketVOJson {
  id: number;
  teacher?: string;
  team?: string;
  time: string;
  status: string;
  title: string;
  content: string;
  reply?: { content: string; admin?: string; time: string } | null;
}

function voToItem(vo: TicketVOJson): FeedbackTicket {
  const item: FeedbackTicket = {
    id: vo.id,
    teacher: vo.teacher || '教师',
    team: vo.team || '未分配',
    time: vo.time,
    status: vo.status === 'REPLIED' ? 'REPLIED' : 'PENDING',
    title: vo.title,
    content: vo.content,
  };
  if (vo.reply) {
    item.reply = {
      content: vo.reply.content,
      admin: vo.reply.admin || '项目管理员',
      time: vo.reply.time,
    };
  }
  return item;
}

export async function fetchTickets(): Promise<FeedbackTicket[]> {
  if (useMock()) {
    await delay();
    return feedbackTickets;
  }
  const vos = await request<TicketVOJson[]>({ url: '/admin/feedback/tickets' });
  return (vos || []).map(voToItem);
}

/** 回复工单：PENDING → REPLIED，并写入最新动态时间线 */
export async function replyTicket(id: number, content: string): Promise<void> {
  if (useMock()) {
    const t = feedbackTickets.find((x) => x.id === id);
    if (!t) throw new Error(`工单不存在：id=${id}`);
    t.status = 'REPLIED';
    t.reply = {
      content,
      admin: '项目管理员',
      time: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-'),
    };
    activityStore.push(`已回复教师「${t.teacher}」的问答工单：${t.title}`, 'feedback');
    return;
  }
  await request({ url: `/admin/feedback/tickets/${id}/reply`, method: 'POST', data: { content } });
}
