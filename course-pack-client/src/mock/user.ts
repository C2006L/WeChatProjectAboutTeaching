import type { UserProfile, MineMenuItem } from '@/types/mock';

export const currentUser: UserProfile = {
  id: 1,
  name: '李老师',
  avatar: '',
  org: '山东师范大学',
  team: '第3支教队',
  badge: '种子教师',
  stage: '短期支教',
  recommend: '小学4-6年级',
  trainingProgress: { completed: 6, total: 8 },
  is_seed: 1,
  role: 'SEED_TEACHER',
  status: 'ACTIVE',
};

/** 我的内容（仅保留已落地入口；下载记录/我的案例随 P1 业务闭环回归） */
export const mineContentMenus: MineMenuItem[] = [
  { icon: 'book', label: '学习记录', path: '/pages/training/index' },
  { icon: 'star', label: '收藏课程', path: '/pages/course/favorites' },
];

/** 我的服务（设置随功能上线补充） */
export const mineServiceMenus: MineMenuItem[] = [
  { icon: 'question', label: '我的问题', path: '/subpkg/tools/qa?tab=tickets' },
  { icon: 'help', label: '帮助与反馈', path: '/pages/mine/help' },
  { icon: 'about', label: '关于伴芽小站', path: '/pages/mine/about' },
];
