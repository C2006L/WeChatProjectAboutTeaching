import type { TrainingCourseRow } from '@/types/dashboard';

/**
 * 培训课程管理 mock（管理员视角：必修 5 + 选修 3，课程名/时长与小程序端一致）
 * 必修课 completedCount 由 api 层按 teacherTraining 记录实时汇总（此处恒 0），
 * 保证与"未完成必修教师名单"数字一致；选修课无学习记录，直接给静态值。
 */
export const trainingCourses: TrainingCourseRow[] = [
  { id: 1, title: '支教工作流程与安全守则', type: '必修', duration: 45, assignedCount: 4, completedCount: 0 },
  { id: 2, title: '儿童心理与沟通基础', type: '必修', duration: 60, assignedCount: 4, completedCount: 0 },
  { id: 3, title: '课程教学法实操', type: '必修', duration: 50, assignedCount: 4, completedCount: 0 },
  { id: 4, title: '生命教育核心理念', type: '必修', duration: 40, assignedCount: 4, completedCount: 0 },
  { id: 5, title: '家校沟通技巧', type: '必修', duration: 45, assignedCount: 4, completedCount: 0 },
  { id: 6, title: '乡村学情分析与因材施教', type: '选修', duration: 40, assignedCount: 3, completedCount: 1 },
  { id: 7, title: '课堂活动组织与破冰', type: '选修', duration: 35, assignedCount: 2, completedCount: 1 },
  { id: 8, title: '反霸凌与自我保护课程', type: '选修', duration: 45, assignedCount: 2, completedCount: 0 },
];
