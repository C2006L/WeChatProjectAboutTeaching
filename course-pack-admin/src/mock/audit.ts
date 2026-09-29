import type { AuditItem } from '@/types/dashboard';

/** 审核中心 mock（V2 拟物化进度：种子→发芽→长叶→小树） */
export const auditList: AuditItem[] = [
  {
    id: 1,
    auditField: 'PSY',
    title: '我的身体，我做主（标准版）',
    applicant: '李芳 · 山东师范大学',
    submittedAt: '2026-09-03 14:20',
    progress: 75,
    stages: [
      { key: 'education', label: '教育学审核', done: true },
      { key: 'psychology', label: '儿童心理学', done: true },
      { key: 'sociology', label: '社会学审核', done: true },
      { key: 'principal', label: '项目负责人', done: false },
    ],
    comments: [
      {
        stageKey: 'education',
        stageLabel: '教育学审核',
        content: '教学目标清晰，活动设计与生命教育主题契合，建议补充课堂时间分配说明。',
        reviewer: '王敏',
        time: '2026-09-03 15:02',
      },
      {
        stageKey: 'psychology',
        stageLabel: '儿童心理学',
        content: '身体界限教学符合该学段认知发展水平，措辞温和，无不适内容。',
        reviewer: '赵倩',
        time: '2026-09-04 09:31',
      },
      {
        stageKey: 'sociology',
        stageLabel: '社会学审核',
        content: '内容尊重多元家庭与社会文化背景，价值观导向正确。',
        reviewer: '陈然',
        time: '2026-09-04 16:47',
      },
    ],
  },
  {
    id: 2,
    auditField: 'EDU',
    title: '我们的朋友，我们的约定（精简版）',
    applicant: '刘洋 · 曲阜师范大学',
    submittedAt: '2026-09-05 10:05',
    progress: 50,
    stages: [
      { key: 'education', label: '教育学审核', done: true },
      { key: 'psychology', label: '儿童心理学', done: true },
      { key: 'sociology', label: '社会学审核', done: false },
      { key: 'principal', label: '项目负责人', done: false },
    ],
    comments: [
      {
        stageKey: 'education',
        stageLabel: '教育学审核',
        content: '环节衔接流畅，案例贴近乡村小学校园生活。',
        reviewer: '王敏',
        time: '2026-09-05 10:40',
      },
      {
        stageKey: 'psychology',
        stageLabel: '儿童心理学',
        content: '同伴边界教学可补充冲突处理的具体话术指引。',
        reviewer: '赵倩',
        time: '2026-09-05 11:12',
      },
    ],
  },
  {
    id: 3,
    auditField: 'PSY',
    title: '安全小卫士（标准版）',
    applicant: '孙悦 · 临沂大学',
    submittedAt: '2026-09-06 09:00',
    progress: 25,
    stages: [
      { key: 'education', label: '教育学审核', done: true },
      { key: 'psychology', label: '儿童心理学', done: false },
      { key: 'sociology', label: '社会学审核', done: false },
      { key: 'principal', label: '项目负责人', done: false },
    ],
    comments: [
      {
        stageKey: 'education',
        stageLabel: '教育学审核',
        content: '安全情景演练设计合理，器材清单与场地要求已说明。',
        reviewer: '王敏',
        time: '2026-09-06 09:35',
      },
    ],
  },
];