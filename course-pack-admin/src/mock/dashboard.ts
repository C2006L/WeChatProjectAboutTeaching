import type { DashboardData } from '@/types/dashboard';

/** 数据看板 mock（数值与 course-pack-docs/frontend-plan-v1.md 4.7 规范一致） */
export const dashboardData: DashboardData = {
  welcome: {
    userName: '项目管理员',
    date: '2026年9月6日 星期日',
    pendingReview: 3,
    todoCount: 12,
  },
  stats: [
    { label: '服务教师总数', value: 356, unit: '人', trend: '较上月 ↑ 12.4%', icon: 'User' },
    { label: '课程学习次数', value: 1284, unit: '次', trend: '较上月 ↑ 8.2%', icon: 'Reading' },
    { label: '课程下载次数', value: 863, unit: '次', trend: '较上月 ↑ 11.3%', icon: 'Download' },
    { label: '培训完成率', value: 78.6, unit: '%', trend: '较上月 ↑ 4.5%', icon: 'Medal' },
  ],
  growthTrend: {
    months: ['2026-03', '2026-04', '2026-05', '2026-06', '2026-07', '2026-08'],
    teachers: [120, 145, 180, 220, 280, 320],
    studies: [420, 560, 720, 900, 1100, 1284],
  },
  courseThemeDistribution: [
    { theme: '身体与安全', value: 410, percent: 32 },
    { theme: '家庭与合作', value: 295, percent: 23 },
    { theme: '人际与尊重', value: 231, percent: 18 },
    { theme: '成长与变化', value: 192, percent: 15 },
    { theme: '综合主题', value: 156, percent: 12 },
  ],
  activeTeams: [
    { name: '山科大希望之光队', org: '山东科技大学', area: '临沂市 沂水县', teachers: 18, usage: 36, completion: 82 },
    { name: '青岛大学麦田队', org: '青岛大学', area: '菏泽市 单县', teachers: 22, usage: 48, completion: 76 },
    { name: '曲师大知行队', org: '曲阜师范大学', area: '济宁市 金乡县', teachers: 16, usage: 31, completion: 68 },
    { name: '山师大蒲公英队', org: '山东师范大学', area: '德州市 平原县', teachers: 20, usage: 42, completion: 80 },
    { name: '聊城大学星火队', org: '聊城大学', area: '聊城市 东阿县', teachers: 15, usage: 28, completion: 70 },
  ],
  todos: [
    { title: '待审核课程', count: 6, path: '/audit' },
    { title: '待审核案例', count: 12, path: '/audit' },
    { title: '教师反馈', count: 8, path: '/feedback' },
    { title: '问答待回复', count: 15, path: '/feedback' },
  ],
  activities: [
    { title: '新课程《我会表达拒绝》已发布', time: '2小时前', type: 'course' },
    { title: '山东科技大学支教队 完成培训', time: '5小时前', type: 'team' },
    { title: '新增教师注册：王老师（临沂支教队）', time: '1天前', type: 'user' },
    { title: '课程《我们的家庭》被收藏 23 次', time: '1天前', type: 'favorite' },
  ],
};
