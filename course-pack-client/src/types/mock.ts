/**
 * 伴芽 · Mock 数据类型定义
 * 字段结构与后端接口规范对齐，后端就绪后页面零改动。
 */

export type CourseTheme = 'body' | 'family' | 'respect' | 'growth' | 'general';
export type CourseVersion = '精简版' | '标准版';

export interface TeachingStep {
  index: number;
  title: string;
  desc: string;
  duration: number;
}

export interface CourseMaterials {
  plan: number;
  slides: number;
  toolkit: number;
  video: number;
}

export interface CourseSummary {
  id: number;
  title: string;
  theme: CourseTheme;
  themeLabel: string;
  grade: string;
  duration: number;
  version: CourseVersion;
  summary: string;
  coverColor: string;
  materials: CourseMaterials;
}

export interface CourseDetail extends CourseSummary {
  objectives: string[];
  values: string[];
  sensitiveQa: number[];
  steps: TeachingStep[];
}

export interface QA {
  id: number;
  question: string;
  category: '身体与安全' | '人际关系' | '成长变化';
  scene: string;
  response: string;
  tips: { type: 'do' | 'caution'; content: string }[];
}

export type TrainingStatus = 'done' | 'current' | 'lock';
export type TrainingType = 'required' | 'elective';

export interface Training {
  id: number;
  title: string;
  type: TrainingType;
  duration: number;
  progress: number;
  status: TrainingStatus;
  videoUrl: string;
  introduction?: string;
  outline?: string[];
  materials?: { name: string; type: string }[];
}

export type UserRole = 'TEACHER' | 'SEED_TEACHER' | 'AUDITOR' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'PENDING' | 'REJECTED' | 'DISABLED';

export interface UserProfile {
  id: number;
  name: string;
  avatar: string;
  org: string;
  team: string;
  badge: string;
  stage: string;
  recommend: string;
  trainingProgress: { completed: number; total: number };
  /** V3：是否种子教师（荣誉徽章） */
  is_seed?: number;
  /** V3：角色 */
  role?: UserRole;
  /** V3：账号状态 */
  status?: UserStatus;
  /** V3：审核领域（AUDITOR 用） */
  audit_field?: string;
}

export interface QuickEntry {
  icon: string;
  label: string;
  path: string;
  image: string;
  /** 方砖底：米 / 浅绿 交替（B 轨线性图标衬底） */
  tile: 'm' | 'g';
  /** 参考插画资产名（manifest 中的 key），可空 */
  illu?: string;
  /** 半行灰色元信息（V2 首页宫格） */
  meta: string;
}

/** 首页：继续学习条 */
export interface ContinueLearning {
  courseName: string;
  completed: number;
  total: number;
  percent: number;
  path: string;
}

/** 首页：最近备课草稿（与 web 备课助手同 storage key） */
export interface HomeDraft {
  id: number;
  courseName: string;
  editTime: string;
  path: string;
}

/** 首页：平台动态一条 */
export interface PlatformNews {
  id: number;
  text: string;
  time: string;
}

export interface HomeData {
  greeting: string;
  dateText: string;
  userName: string;
  stage: {
    title: string;
    day: number;
    recommend: string;
  };
  quickEntries: QuickEntry[];
  todayRecommend: CourseSummary;
  /** 继续学习条 */
  continueLearning: ContinueLearning;
  /** 最近备课草稿（0-2 条） */
  recentDrafts: HomeDraft[];
  /** 平台动态（2-3 条） */
  platformNews: PlatformNews[];
}

export interface MineMenuItem {
  icon: string;
  label: string;
  /** 入口状态或用途说明，帮助用户在进入前判断内容 */
  meta?: string;
  /** 为空表示 V1 占位（toast 提示） */
  path: string;
}

export interface ToolEntry {
  icon: string;
  title: string;
  desc: string;
  /** 为空表示 V1 占位入口（toast 提示） */
  path: string;
  /** 图片图标路径（优先） */
  image?: string;
  /** emoji 图标（image 为空时使用） */
  emoji?: string;
  /** V3：图标方砖底（米 / 浅绿交替） */
  tile?: 'm' | 'g';
}

export interface PrepareResult {
  grade: string;
  theme: string;
  duration: number;
  recommendedCourses: number[];
  teachingFlow: string[];
  materialChecklist: string[];
  attentionNotes: string[];
}

/** 家校沟通：一个沟通话题的话术 */
export interface FamilyScript {
  id: number;
  topic: string;          // 话题标题（如：孩子在校表现）
  scene: string;          // 场景说明
  suggestion: string;     // 沟通建议话术
  tips: string[];         // 需要注意的点
}

/** 家校沟通：致家长的一封信 */
export interface FamilyLetter {
  subject: string;
  paragraphs: string[];
  signature: string;
  date: string;
}

/** 课堂活动库：单个活动 */
export type ActivityType = '破冰' | '主题' | '收尾';

export interface Activity {
  id: number;
  title: string;
  type: ActivityType;
  minutes: number;
  grade: string;
  desc: string;
}

/** 问答工单：教师提问 → 管理员回复（对齐 Web 端问答与反馈模块） */
export type TicketStatus = 'PENDING' | 'REPLIED';

export interface TicketReply {
  content: string;
  admin: string;
  time: string;
}

export interface FeedbackTicket {
  id: number;
  title: string;
  content: string;
  /** 问题分类：身体与安全 / 人际关系 / 成长变化 / 其他 */
  category: string;
  status: TicketStatus;
  /** 提问时间 yyyy-MM-dd HH:mm */
  time: string;
  reply?: TicketReply;
}

/** 教学案例：教师提交后进入审核，审核通过后进入公共案例库 */
export type TeachingCaseStatus = 'DRAFT' | 'PENDING' | 'APPROVED' | 'REJECTED';

export interface TeachingCase {
  id: number;
  title: string;
  grade: '小学1-3年级' | '小学4-6年级';
  theme: '身体与安全' | '家庭与合作' | '人际与尊重' | '成长与变化' | '综合主题';
  courseTitle: string;
  summary: string;
  content: string;
  reflection: string;
  author: string;
  team: string;
  status: TeachingCaseStatus;
  featured: boolean;
  isMine: boolean;
  createdAt: string;
  updatedAt: string;
  reviewComment?: string;
}

export type TeachingCasePayload = Pick<
  TeachingCase,
  'title' | 'grade' | 'theme' | 'courseTitle' | 'summary' | 'content' | 'reflection'
>;
