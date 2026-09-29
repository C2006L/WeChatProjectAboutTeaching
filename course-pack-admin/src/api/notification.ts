/**
 * 待办聚合（Header 铃铛通知中心的数据源）
 * 全部取自现有 mock 单例，各模块操作后计数实时变化；
 * 后端就绪后映射：GET /api/notifications/todos
 */
import { teacherList, teacherTraining } from '@/mock/teacher';
import { feedbackTickets, teachingCases } from '@/mock/seed';
import { auditList } from '@/mock/audit';
import { trainingCourses } from '@/mock/training';
import type { TodoItem } from '@/types/dashboard';
import type { UserInfo } from '@/store/auth';

const delay = (ms = 120) => new Promise((r) => setTimeout(r, ms));

/** 按角色聚合待办：ADMIN 三类待处理 / AUDITOR 待我审核 / TEACHER 必修未完成 */
export async function fetchTodos(user: UserInfo | null): Promise<TodoItem[]> {
  await delay();
  if (!user) return [];

  if (user.role === 'AUDITOR') {
    const count = auditList.filter(
      (a) => a.auditField === user.auditField && a.progress < 100,
    ).length;
    return [{ title: '待我审核的课程', count, path: '/audit' }];
  }

  if (user.role === 'TEACHER') {
    const teacher = teacherList.find((t) => t.account === user.username);
    const required = trainingCourses.filter((c) => c.type === '必修').map((c) => c.id);
    const done = teacher
      ? teacherTraining.find((r) => r.teacherId === teacher.id)?.completed ?? []
      : [];
    return [
      {
        title: '未完成的必修培训',
        count: required.filter((id) => !done.includes(id)).length,
        path: '/teacher/workbench',
      },
    ];
  }

  return [
    {
      title: '待审核教师',
      count: teacherList.filter((t) => t.status === 'PENDING').length,
      path: '/teacher',
    },
    {
      title: '待回复工单',
      count: feedbackTickets.filter((t) => t.status === 'PENDING').length,
      path: '/feedback',
    },
    {
      title: '待审案例',
      count: teachingCases.filter((c) => c.status === 'PENDING').length,
      path: '/case',
    },
  ];
}