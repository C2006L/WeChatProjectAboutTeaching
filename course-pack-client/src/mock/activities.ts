import type { Activity } from '@/types/mock';

/** 课堂活动库 mock（V2：破冰 4 / 主题 4 / 收尾 2，共 10 条） */
export const activityList: Activity[] = [
  { id: 1, title: '名字连连看', type: '破冰', minutes: 5, grade: '1-3年级', desc: '围圈进行，每人说出自己的名字并做一个动作，其余同学复述前一人的名字和动作。' },
  { id: 2, title: '情绪温度计', type: '破冰', minutes: 8, grade: '1-6年级', desc: '每人用手势或颜色表示自己今天的心情，简单说明原因，建立课堂情感连接。' },
  { id: 3, title: '你好，我来自', type: '破冰', minutes: 6, grade: '4-6年级', desc: '两人一组互相介绍家乡与喜好，再向全班介绍对方，练习倾听与表达。' },
  { id: 4, title: '节奏接龙', type: '破冰', minutes: 7, grade: '1-4年级', desc: '用拍手节奏传递接龙，一人发起节奏，大家跟随并逐一轮流加入新动作。' },
  { id: 5, title: '身体红绿灯', type: '主题', minutes: 12, grade: '1-3年级', desc: '认识身体的“安全区域”，用红绿灯卡片区分可接受与不可接受的触碰，学会说“不”。' },
  { id: 6, title: '情绪的绘本', type: '主题', minutes: 15, grade: '1-4年级', desc: '共读情绪主题绘本，讨论角色感受，并画出自己的“心情小怪兽”。' },
  { id: 7, title: '尊重小剧场', type: '主题', minutes: 15, grade: '4-6年级', desc: '分角色表演人际冲突场景，尝试用尊重的方式化解，再讨论不同处理方式的结果。' },
  { id: 8, title: '家庭大树', type: '主题', minutes: 12, grade: '1-6年级', desc: '画出自己的“家庭大树”，贴出家人们的特点，理解并欣赏不同的家庭与爱。' },
  { id: 9, title: '优点圆环', type: '收尾', minutes: 8, grade: '1-6年级', desc: '围成圈，轮流说出相邻同学的一个优点，感受被看见与被肯定的温暖。' },
  { id: 10, title: '我可以带走的一句话', type: '收尾', minutes: 6, grade: '1-6年级', desc: '每位同学说一句今天最想记住的话，作为整节课的收束与呼应。' },
];