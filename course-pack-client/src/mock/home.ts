import { currentUser } from './user';
import { courseList } from './course';
import type { HomeData } from '@/types/mock';

function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 12) return '早上好';
  if (hour < 18) return '下午好';
  return '晚上好';
}

function getDateText(): string {
  const now = new Date();
  const week = ['日', '一', '二', '三', '四', '五', '六'][now.getDay()];
  return `${now.getMonth() + 1}月${now.getDate()}日 星期${week}`;
}

/** 最近备课草稿：与 web 备课助手共用同一种 storage key（双端同步演示入口） */
const DRAFT_STORAGE_KEY = 'banya_present_drafts';

export const homeData: HomeData = {
  greeting: getGreeting(),
  dateText: getDateText(),
  userName: currentUser.name,
  stage: {
    title: currentUser.stage,
    day: 5,
    recommend: currentUser.recommend,
  },
  quickEntries: [
    {
      icon: 'icon-course', label: '找课程', path: '/pages/course/list',
      image: '/static/course.png', tile: 'm',
      meta: '精选 24 门课', illu: 'illu-course',
    },
    {
      icon: 'icon-training', label: '学培训', path: '/pages/training/index',
      image: '/static/training.png', tile: 'g',
      meta: '必修 6 节课', illu: 'illu-training',
    },
    {
      icon: 'icon-tool', label: '备课工具', path: '/pages/tool/index',
      image: '/static/prepare.png', tile: 'm',
      meta: '开到就能备', illu: 'illu-prepare',
    },
    {
      icon: 'icon-qa', label: '问答支持', path: '/subpkg/tools/qa',
      image: '/static/qa.png', tile: 'g',
      meta: '随时帮你答', illu: 'illu-qa',
    },
  ],
  todayRecommend: courseList[0],
  continueLearning: {
    courseName: '儿童心理急救',
    completed: currentUser.trainingProgress.completed,
    total: currentUser.trainingProgress.total,
    percent: Math.round(
      (currentUser.trainingProgress.completed / currentUser.trainingProgress.total) * 100,
    ),
    path: '/pages/training/index',
  },
  recentDrafts: [
    {
      id: 1,
      courseName: '我的身体，我做主',
      editTime: '昨天 21:40',
      path: '/subpkg/tools/prepare',
    },
    {
      id: 2,
      courseName: '安全出行小达人',
      editTime: '3 天前',
      path: '/subpkg/tools/prepare',
    },
  ],
  platformNews: [
    { id: 1, text: '新增课程《儿童心理急救》推荐给小学阶段', time: '2 小时前' },
    { id: 2, text: '本周培训：留守儿童心理照护专题已更新', time: '昨天' },
    { id: 3, text: '家长沟通话术库新增 3 个场景', time: '3 天前' },
  ],
};

export { DRAFT_STORAGE_KEY };