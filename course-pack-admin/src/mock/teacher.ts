import type { TeacherRow } from '@/types/dashboard';

/**
 * 教师管理 mock（对接种子账号 teacher01-04）
 * 后端就绪后由 GET /api/admin/teachers 返回同构数据
 */
export const teacherList: TeacherRow[] = [
  {
    id: 1,
    account: 'teacher01',
    name: '王春霞',
    team: '山科大希望之光队',
    registeredAt: '2026-07-02',
    status: 'ACTIVE',
    isSeed: true,
  },
  {
    id: 2,
    account: 'teacher02',
    name: '李有田',
    team: '青岛大学麦田队',
    registeredAt: '2026-08-11',
    status: 'PENDING',
    isSeed: false,
  },
  {
    id: 3,
    account: 'teacher03',
    name: '张远航',
    team: '曲师大知行队',
    registeredAt: '2026-08-19',
    status: 'REJECTED',
    rejectReason: '身份与支教资质材料不完整',
    isSeed: false,
  },
  {
    id: 4,
    account: 'teacher04',
    name: '陈雪梅',
    team: '山师大蒲公英队',
    registeredAt: '2026-06-28',
    status: 'DISABLED',
    isSeed: false,
  },
];

/** 教师必修学习记录（completed = 已完成必修课程 id），驱动培训页统计与未完成名单 */
export const teacherTraining: { teacherId: number; completed: number[] }[] = [
  { teacherId: 1, completed: [1, 2, 3, 4, 5] }, // 全部完成
  { teacherId: 2, completed: [1, 2] },
  { teacherId: 3, completed: [1, 4] },
  { teacherId: 4, completed: [1, 2] },
];
