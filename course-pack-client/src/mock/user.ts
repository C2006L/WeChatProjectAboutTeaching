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

/** 我的内容 */
export const mineContentMenus: MineMenuItem[] = [
  { icon: 'book', label: '学习记录', meta: '6 节已完成', path: '/pages/training/index' },
  { icon: 'star', label: '收藏课程', meta: '随时继续备课', path: '/pages/course/favorites' },
  { icon: 'case', label: '我的案例', meta: '查看草稿与审核进度', path: '/subpkg/case/list?tab=mine' },
];

/** 我的服务（设置随功能上线补充） */
export const mineServiceMenus: MineMenuItem[] = [
  { icon: 'question', label: '我的问题', meta: '查看提问与回复', path: '/subpkg/tools/qa?tab=tickets' },
  { icon: 'help', label: '帮助与反馈', meta: '使用帮助与意见反馈', path: '/pages/mine/help' },
  { icon: 'about', label: '关于伴芽小站', meta: '了解项目与团队', path: '/pages/mine/about' },
];
