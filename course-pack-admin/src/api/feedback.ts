/**
 * 问答与反馈 api（mock 优先，工单形态：教师提问 → 管理员回复，列表流）
 * 后端就绪后映射：
 *  GET  /api/admin/feedback/tickets        → fetchTickets
 *  POST /api/admin/feedback/tickets/:id/reply → replyTicket
 */
import { feedbackTickets } from '@/mock/seed';
import { activityStore } from '@/store/activity';
import type { FeedbackTicket } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchTickets(): Promise<FeedbackTicket[]> {
  await delay();
  return feedbackTickets;
}

/** 回复工单：PENDING → REPLIED，并写入最新动态时间线 */
export async function replyTicket(id: number, content: string): Promise<void> {
  const t = feedbackTickets.find((x) => x.id === id);
  if (!t) throw new Error(`工单不存在：id=${id}`);
  t.status = 'REPLIED';
  t.reply = {
    content,
    admin: '项目管理员',
    time: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-'),
  };
  activityStore.push(`已回复教师「${t.teacher}」的问答工单：${t.title}`, 'feedback');
}
