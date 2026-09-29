import type {
  FeedbackTicket,
  CaseItem,
  TeamDetailRow,
  ResourceItem,
  SeedHonorRow,
  AuditStage,
} from '@/types/dashboard';

/**
 * 批次 E 统一 mock seed（问答工单 / 教学案例 / 支教队伍 / 资源库 / 种子教师荣誉）
 * 数值对齐 course-pack-docs/02-接口/接口规范.md；后端就绪后按同构结构替换
 */

/** 四环节工厂（done 数决定进度） */
function makeStages(doneCount: number): AuditStage[] {
  const all = [
    { key: 'education', label: '教育审核' },
    { key: 'psychology', label: '心理审核' },
    { key: 'sociology', label: '社会审核' },
    { key: 'principal', label: '校长终审' },
  ] as const;
  return all.map((s, i) => ({ key: s.key, label: s.label, done: i < doneCount }));
}

/* ── 1. 教师工单 ×6（3 待回复 + 3 已回复） ── */
export const feedbackTickets: FeedbackTicket[] = [
  {
    id: 1,
    teacher: '王春霞',
    team: '山科大希望之光队',
    time: '2026-09-05 14:20',
    status: 'PENDING',
    title: '课件下载后解压失败',
    content: '《我会表达拒绝》的课件包下载后解压提示文件损坏，换了两次网络都一样，麻烦帮忙看下资源包。',
  },
  {
    id: 2,
    teacher: '李有田',
    team: '青岛大学麦田队',
    time: '2026-09-05 09:05',
    status: 'PENDING',
    title: '建议补充低年级绘本资源',
    content: '我们服务的是小学 1-3 年级，现有绘本清单整体偏高年级，希望平台补充低龄段书单和配套导读。',
  },
  {
    id: 3,
    teacher: '张远航',
    team: '曲师大知行队',
    time: '2026-09-04 18:42',
    status: 'PENDING',
    title: '必修课学习进度未保存',
    content: '昨晚学完《家校沟通技巧》并完成打卡，今天培训页进度还是 64%，学习记录疑似没有上传成功。',
  },
  {
    id: 4,
    teacher: '陈雪梅',
    team: '山师大蒲公英队',
    time: '2026-09-03 10:15',
    status: 'REPLIED',
    title: '课程视频无法播放',
    content: '《生命教育核心理念》第 3 节视频一直转圈，安卓手机和电脑都试过，无法播放。',
    reply: {
      content: '已处理：该节视频源已完成更换，请退出后重新进入学习页，即可正常播放。给您带来不便深表歉意。',
      admin: '项目管理员',
      time: '2026-09-03 15:30',
    },
  },
  {
    id: 5,
    teacher: '周雨桐',
    team: '青岛大学麦田队',
    time: '2026-09-02 16:08',
    status: 'REPLIED',
    title: '如何申请成为种子教师？',
    content: '听说种子教师可以优先参与课程共创，想了解申请条件和认证流程。',
    reply: {
      content: '种子教师由管理员在教师管理页标记认证，通常要求：完成全部必修培训 + 至少上传 1 个通过审核的教学案例。您目前的进度已接近，保持活跃即可。',
      admin: '项目管理员',
      time: '2026-09-02 17:12',
    },
  },
  {
    id: 6,
    teacher: '刘志强',
    team: '曲师大知行队',
    time: '2026-09-01 11:26',
    status: 'REPLIED',
    title: '希望开放案例交流区',
    content: '教学案例目前只能看不能评论，希望增加交流区，方便队伍之间互相取经。',
    reply: {
      content: '建议已收录进需求池，排期在批次 F 评估。感谢您的反馈，种子教师共创群也会同步这个消息。',
      admin: '项目管理员',
      time: '2026-09-01 14:05',
    },
  },
];

/* ── 2. 教学案例 ×8（6 已通过 + 2 待审） ── */
export const teachingCases: CaseItem[] = [
  {
    id: 1,
    title: '《我的身体我做主》课堂实录与反思',
    uploader: '王春霞',
    team: '山科大希望之光队',
    uploadedAt: '2026-08-28',
    status: 'APPROVED',
    featured: true,
    summary: '四年级 40 分钟完整课堂实录，含"身体红绿灯"游戏的分步组织方法与三种突发情况应对。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '教学目标清晰，环节完整，适合直接复用。', reviewer: '李文静（教育顾问）', time: '2026-08-29 10:20' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '身体保护表述科学，无恐吓性语言。', reviewer: '王雅雯（心理督导）', time: '2026-08-29 16:45' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '内容符合乡村课堂实际，材料成本低。', reviewer: '郑海燕（社工督导）', time: '2026-08-30 09:10' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过，建议作为示范案例推广。', reviewer: '陈明德（校长）', time: '2026-08-30 15:00' },
    ],
  },
  {
    id: 2,
    title: '《我们的家庭》绘本共读教案改编',
    uploader: '周雨桐',
    team: '青岛大学麦田队',
    uploadedAt: '2026-08-25',
    status: 'APPROVED',
    featured: true,
    summary: '把原绘本共读改编为三代家庭访谈任务单，学生带回家庭作业后再回课堂分享，附访谈提纲。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '家庭访谈设计巧妙，家校联动自然。', reviewer: '李文静（教育顾问）', time: '2026-08-26 11:00' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '注意了单亲/留守家庭的表述敏感性，处理得当。', reviewer: '王雅雯（心理督导）', time: '2026-08-26 17:30' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '任务单工作量适中，符合农村家庭实际。', reviewer: '郑海燕（社工督导）', time: '2026-08-27 09:40' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过。', reviewer: '陈明德（校长）', time: '2026-08-27 14:20' },
    ],
  },
  {
    id: 3,
    title: '反霸凌情景剧：三幕课堂设计',
    uploader: '刘志强',
    team: '曲师大知行队',
    uploadedAt: '2026-08-22',
    status: 'APPROVED',
    featured: false,
    summary: '旁观者视角的三幕情景剧剧本，含角色卡、舞台提示与演后引导话术，60 分钟完整流程。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '剧本冲突设计合理，引导话术专业。', reviewer: '李文静（教育顾问）', time: '2026-08-23 10:05' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '旁观者介入策略符合校园心理干预规范。', reviewer: '王雅雯（心理督导）', time: '2026-08-23 15:15' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '通过。', reviewer: '郑海燕（社工督导）', time: '2026-08-24 09:30' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过，建议配一段排练花絮。', reviewer: '陈明德（校长）', time: '2026-08-24 16:50' },
    ],
  },
  {
    id: 4,
    title: '《我会表达拒绝》低龄化改编实践',
    uploader: '孙晓梅',
    team: '聊城大学星火队',
    uploadedAt: '2026-08-20',
    status: 'APPROVED',
    featured: false,
    summary: '面向 1-2 年级把角色扮演简化为"点头/摇头"游戏，附方言版引导词，适合低龄段课堂。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '低龄化改编到位，游戏规则简单清晰。', reviewer: '李文静（教育顾问）', time: '2026-08-21 10:40' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '通过。', reviewer: '王雅雯（心理督导）', time: '2026-08-21 14:25' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '方言版引导词很接地气，值得推广。', reviewer: '郑海燕（社工督导）', time: '2026-08-22 09:15' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过。', reviewer: '陈明德（校长）', time: '2026-08-22 15:35' },
    ],
  },
  {
    id: 5,
    title: '家校沟通手册：沂水家访 12 例',
    uploader: '王春霞',
    team: '山科大希望之光队',
    uploadedAt: '2026-08-15',
    status: 'APPROVED',
    featured: false,
    summary: '12 个真实家访案例的沟通要点复盘，覆盖留守、隔代抚养、单亲三类家庭场景。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '案例真实，复盘有方法沉淀。', reviewer: '李文静（教育顾问）', time: '2026-08-16 10:10' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '家庭信息已充分脱敏。', reviewer: '王雅雯（心理督导）', time: '2026-08-16 15:20' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '通过。', reviewer: '郑海燕（社工督导）', time: '2026-08-17 09:00' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过，建议纳入新教师培训材料。', reviewer: '陈明德（校长）', time: '2026-08-17 14:45' },
    ],
  },
  {
    id: 6,
    title: '课堂破冰游戏 20 式（附物料清单）',
    uploader: '王春霞',
    team: '山科大希望之光队',
    uploadedAt: '2026-08-10',
    status: 'APPROVED',
    featured: false,
    summary: '按"场地/人数/物料"三维分类的 20 个破冰游戏速查表，每个附 3 分钟讲解脚本。',
    stages: makeStages(4),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '速查表实用，分类维度清晰。', reviewer: '李文静（教育顾问）', time: '2026-08-11 10:30' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '通过。', reviewer: '王雅雯（心理督导）', time: '2026-08-11 14:10' },
      { stageKey: 'sociology', stageLabel: '社会审核', content: '物料均为乡村常见用品，可落地。', reviewer: '郑海燕（社工督导）', time: '2026-08-12 09:20' },
      { stageKey: 'principal', stageLabel: '校长终审', content: '通过。', reviewer: '陈明德（校长）', time: '2026-08-12 15:00' },
    ],
  },
  {
    id: 7,
    title: '《生命的四季》主题班会全流程',
    uploader: '刘志强',
    team: '曲师大知行队',
    uploadedAt: '2026-09-03',
    status: 'PENDING',
    featured: false,
    summary: '以四季轮转类比生命周期的主题班会设计，含 4 个观察任务与生命教育绘本延伸书单。',
    stages: makeStages(2),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '结构完整，观察任务设计有层次。', reviewer: '李文静（教育顾问）', time: '2026-09-04 10:15' },
      { stageKey: 'psychology', stageLabel: '心理审核', content: '生命周期表述科学，建议补充哀伤情绪的引导预案。', reviewer: '王雅雯（心理督导）', time: '2026-09-04 16:30' },
    ],
  },
  {
    id: 8,
    title: '乡村小学性教育问答实录',
    uploader: '周雨桐',
    team: '青岛大学麦田队',
    uploadedAt: '2026-09-05',
    status: 'PENDING',
    featured: false,
    summary: '五年级课堂 23 个真实提问与应答实录，附"可回应/需转介"分类判断表。',
    stages: makeStages(1),
    comments: [
      { stageKey: 'education', stageLabel: '教育审核', content: '问答实录价值高，分类判断表实用，建议补充转介渠道清单。', reviewer: '李文静（教育顾问）', time: '2026-09-05 17:20' },
    ],
  },
];

/* ── 3. 支教队伍 ×6（看板 Top5 的完整版 + 沂蒙队） ── */
export const teams: TeamDetailRow[] = [
  {
    id: 1,
    name: '山科大希望之光队',
    org: '山东科技大学',
    area: '临沂市 沂水县',
    usage: 36,
    members: [
      { name: '王春霞', role: '队长', requiredDone: 5, requiredTotal: 5 },
      { name: '赵子豪', role: '成员', requiredDone: 5, requiredTotal: 5 },
      { name: '林晓彤', role: '成员', requiredDone: 4, requiredTotal: 5 },
    ],
  },
  {
    id: 2,
    name: '青岛大学麦田队',
    org: '青岛大学',
    area: '菏泽市 单县',
    usage: 48,
    members: [
      { name: '周雨桐', role: '队长', requiredDone: 5, requiredTotal: 5 },
      { name: '李有田', role: '成员', requiredDone: 2, requiredTotal: 5 },
      { name: '徐海生', role: '成员', requiredDone: 5, requiredTotal: 5 },
      { name: '马兰', role: '成员', requiredDone: 3, requiredTotal: 5 },
    ],
  },
  {
    id: 3,
    name: '曲师大知行队',
    org: '曲阜师范大学',
    area: '济宁市 金乡县',
    usage: 31,
    members: [
      { name: '刘志强', role: '队长', requiredDone: 5, requiredTotal: 5 },
      { name: '张远航', role: '成员', requiredDone: 2, requiredTotal: 5 },
      { name: '杜文静', role: '成员', requiredDone: 4, requiredTotal: 5 },
    ],
  },
  {
    id: 4,
    name: '山师大蒲公英队',
    org: '山东师范大学',
    area: '德州市 平原县',
    usage: 42,
    members: [
      { name: '孙晓晴', role: '队长', requiredDone: 5, requiredTotal: 5 },
      { name: '陈雪梅', role: '成员', requiredDone: 2, requiredTotal: 5 },
      { name: '高峰', role: '成员', requiredDone: 5, requiredTotal: 5 },
      { name: '白鹭', role: '成员', requiredDone: 4, requiredTotal: 5 },
    ],
  },
  {
    id: 5,
    name: '聊城大学星火队',
    org: '聊城大学',
    area: '聊城市 东阿县',
    usage: 28,
    members: [
      { name: '孙晓梅', role: '队长', requiredDone: 5, requiredTotal: 5 },
      { name: '郭栋', role: '成员', requiredDone: 3, requiredTotal: 5 },
      { name: '韩冰', role: '成员', requiredDone: 5, requiredTotal: 5 },
    ],
  },
  {
    id: 6,
    name: '临沂大学沂蒙队',
    org: '临沂大学',
    area: '临沂市 蒙阴县',
    usage: 19,
    members: [
      { name: '孟祥云', role: '队长', requiredDone: 4, requiredTotal: 5 },
      { name: '尹航', role: '成员', requiredDone: 2, requiredTotal: 5 },
      { name: '彭飞', role: '成员', requiredDone: 1, requiredTotal: 5 },
    ],
  },
];

/* ── 4. 资源条目 ×12（4 类型 × 3） ── */
export const resources: ResourceItem[] = [
  { id: 1, title: '生命教育课程教案模板（标准版）', kind: '教案模板', downloads: 342, size: '86 KB', updatedAt: '2026-08-20' },
  { id: 2, title: '班会课教案骨架（40 分钟版）', kind: '教案模板', downloads: 218, size: '52 KB', updatedAt: '2026-08-12' },
  { id: 3, title: '家访记录与沟通要点模板', kind: '教案模板', downloads: 156, size: '48 KB', updatedAt: '2026-07-30' },
  { id: 4, title: '课程封面与目录 PPT 模板', kind: 'PPT模板', downloads: 289, size: '3.2 MB', updatedAt: '2026-08-18' },
  { id: 5, title: '互动问答页 PPT 模板', kind: 'PPT模板', downloads: 173, size: '1.8 MB', updatedAt: '2026-08-08' },
  { id: 6, title: '结课展示 PPT 模板', kind: 'PPT模板', downloads: 98, size: '2.4 MB', updatedAt: '2026-07-22' },
  { id: 7, title: '课堂参与度观察量表', kind: '评估量表', downloads: 201, size: '64 KB', updatedAt: '2026-08-25' },
  { id: 8, title: '学生成长追踪量表（学期版）', kind: '评估量表', downloads: 187, size: '72 KB', updatedAt: '2026-08-15' },
  { id: 9, title: '教师授课自评量表', kind: '评估量表', downloads: 143, size: '45 KB', updatedAt: '2026-07-28' },
  { id: 10, title: '低龄段绘本清单（1-3 年级）', kind: '绘本清单', downloads: 265, size: '38 KB', updatedAt: '2026-08-30' },
  { id: 11, title: '情绪主题绘本书单（附导读）', kind: '绘本清单', downloads: 154, size: '42 KB', updatedAt: '2026-08-22' },
  { id: 12, title: '家庭主题绘本书单', kind: '绘本清单', downloads: 132, size: '36 KB', updatedAt: '2026-08-05' },
];

/* ── 5. 种子教师荣誉（上传案例数由 api 层与教学案例联动计算） ── */
export const seedHonor: SeedHonorRow[] = [
  { id: 1, name: '王春霞', team: '山科大希望之光队', certifiedAt: '2026-07-02' },
  { id: 2, name: '周雨桐', team: '青岛大学麦田队', certifiedAt: '2026-06-18' },
  { id: 3, name: '刘志强', team: '曲师大知行队', certifiedAt: '2026-05-30' },
  { id: 4, name: '孙晓梅', team: '聊城大学星火队', certifiedAt: '2026-04-12' },
];
