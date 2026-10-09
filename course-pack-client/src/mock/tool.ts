import type { ToolEntry } from '@/types/mock';

/**
 * 教师工具箱四宫格入口（V2：2×2 宫格，线稿 icon）。
 * icon 为语义 key，由 ToolCard 通过 CSS background-data-uri 绘制线稿 SVG（禁用 emoji）。
 */
export const toolEntries: ToolEntry[] = [
  {
    icon: 'qa',
    title: '敏感问题应答',
    desc: '课堂敏感问题情景应对指南',
    path: '/subpkg/tools/qa',
    tile: 'm',
  },
  {
    icon: 'prepare',
    title: '备课助手',
    desc: '快速生成备课清单',
    path: '/subpkg/tools/prepare',
    tile: 'g',
  },
  {
    icon: 'family',
    title: '家校沟通',
    desc: '家长沟通话题与话术（含《致家长的一封信》）',
    path: '/subpkg/tools/family',
    tile: 'm',
  },
  {
    icon: 'activity',
    title: '课堂活动库',
    desc: '破冰与主题活动集',
    path: '/subpkg/tools/activities',
    tile: 'g',
  },
];