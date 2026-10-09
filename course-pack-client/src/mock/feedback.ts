/**
 * 问答工单 seed（我的提问首屏演示数据：已回复 + 待回复各一）
 * 仅在本地 storage 首次为空时写入；教师新提交的工单以 storage 为准。
 */
import type { FeedbackTicket } from '@/types/mock';

export const ticketSeed: FeedbackTicket[] = [
  {
    id: 1,
    title: '4-6年级孩子拒绝身体接触的话术尺度如何把握？',
    content: '在讲《我的身体，我做主》时，有学生现场问“长辈强行抱我怎么办”，想确认课上回应的尺度和课后跟进方式。',
    category: '身体与安全',
    status: 'REPLIED',
    time: '2026-09-08 19:24',
    reply: {
      content: '课上先肯定孩子“拒绝是对的”，不展开细节；课后单独告诉孩子可以信任的求助对象（家长/老师/12355），并同步给班主任。教案第3环节的话术卡可直接使用。',
      admin: '项目管理员',
      time: '2026-09-09 09:12',
    },
  },
  {
    id: 2,
    title: '小组活动中学生不愿参与如何引导？',
    content: '班里有两名比较内向的学生，破冰环节一直坐着不参与，请问有没有低压力的参与方式？',
    category: '人际关系',
    status: 'PENDING',
    time: '2026-09-11 16:40',
  },
];
