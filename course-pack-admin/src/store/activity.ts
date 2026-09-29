import { reactive } from 'vue';
import type { ActivityItem } from '@/types/dashboard';
import { dashboardData } from '@/mock/dashboard';

/**
 * 最新动态时间线（模块级单例）
 * 管理端各处操作结果（教师审核/停用/种子标记等）写入此处，
 * 数据看板"最新动态"卡片读取同一份，实现跨页联动。
 */
const MAX_ITEMS = 12;

export const activityStore = reactive({
  items: [...dashboardData.activities] as ActivityItem[],
  push(title: string, type: ActivityItem['type'] = 'user') {
    this.items.unshift({ title, time: '刚刚', type });
    if (this.items.length > MAX_ITEMS) this.items.length = MAX_ITEMS;
  },
});
