/** 伴芽 · Web 管理端数据类型（对齐接口规范文档） */

export interface WelcomeInfo {
  userName: string;
  date: string;
  pendingReview: number;   // 今天待审核课程数
  todoCount: number;       // 全部待办数
}

export interface StatItem {
  label: string;
  value: number;
  unit: string;
  trend: string;
  icon: string;
}

export interface GrowthTrend {
  months: string[];
  teachers: number[];
  studies: number[];
}

export interface ThemeSlice {
  theme: string;
  value: number;
  percent: number;
}

export interface TeamRow {
  name: string;
  org: string;
  area: string;
  teachers: number;
  usage: number;
  completion: number;
}

export interface TodoItem {
  title: string;
  count: number;
  path: string;
}

export interface ActivityItem {
  title: string;
  time: string;
  type: 'course' | 'team' | 'user' | 'favorite' | 'feedback';
}

export interface DashboardData {
  welcome: WelcomeInfo;
  stats: StatItem[];
  growthTrend: GrowthTrend;
  courseThemeDistribution: ThemeSlice[];
  activeTeams: TeamRow[];
  todos: TodoItem[];
  activities: ActivityItem[];
}

/** 管理端课程条目（生命教育课程字段） */
export interface CourseAdminItem {
  id: number;
  title: string;
  theme: string;
  themeLabel: string;
  grade: string;
  duration: number;
  version: '精简版' | '标准版';
  summary: string;
  status: boolean;
  materials: { plan: number; slides: number; toolkit: number; video: number };
  updatedAt: string;
}

/** 培训课程管理行（V1.1 管理员视角） */
export interface TrainingCourseRow {
  id: number;
  title: string;
  type: '必修' | '选修';
  duration: number;          // 分钟
  completedCount: number;    // 已完成教师数
  assignedCount: number;     // 应完成教师数
}

/** 培训管理顶部统计（管理员视角，禁止教师个人学习进度） */
export interface TrainingOverview {
  requiredRate: number;      // 必修完成率 %
  doneTeachers: number;      // 已完成教师数（必修全通过）
  inprogressTeachers: number;// 进行中教师数
  teamCount: number;         // 覆盖队伍数
}

/** 教师状态（对齐接口规范：PENDING/ACTIVE/REJECTED/DISABLED） */
export type TeacherStatus = 'PENDING' | 'ACTIVE' | 'REJECTED' | 'DISABLED';

/** 教师管理行 */
export interface TeacherRow {
  id: number;
  account: string;           // 登录账号 teacher01-04
  name: string;
  team: string;              // 所属队伍
  registeredAt: string;      // 注册时间
  status: TeacherStatus;
  isSeed: boolean;           // 种子教师 is_seed=1
  rejectReason?: string;     // 驳回原因（REJECTED 时展示）
}

/** 未完成必修教师名单行（对接教师管理数据） */
export interface TeacherUnfinishedRow {
  teacherId: number;
  name: string;
  team: string;
  status: TeacherStatus;
  completedCount: number;    // 已完成必修数
  requiredCount: number;     // 必修总数
  missing: string[];         // 未完成的必修课程名
}

/** ===== 批次 E：问答 / 案例 / 队伍 / 资源 / 种子荣誉 ===== */

/** 问答工单状态 */
export type TicketStatus = 'PENDING' | 'REPLIED';

/** 管理员回复 */
export interface TicketReply {
  content: string;
  admin: string;
  time: string;
}

/** 问答工单（教师提问 → 管理员回复，列表流，非聊天） */
export interface FeedbackTicket {
  id: number;
  teacher: string;           // 提问教师
  team: string;              // 所属队伍
  time: string;              // 提问时间
  status: TicketStatus;
  title: string;             // 工单标题
  content: string;           // 问题描述
  reply?: TicketReply;
}

/** 教学案例（复用四环节审核流转） */
export interface CaseItem {
  id: number;
  title: string;
  uploader: string;          // 上传人
  team: string;              // 所属队伍
  uploadedAt: string;
  status: 'APPROVED' | 'PENDING';
  featured: boolean;         // 精选
  summary: string;
  stages: AuditStage[];
  comments: AuditComment[];
}

/** 队伍成员（含必修进度） */
export interface TeamMember {
  name: string;
  role: '队长' | '成员';
  requiredDone: number;
  requiredTotal: number;
}

/** 支教队伍（完整版行，completion 由成员进度推导） */
export interface TeamDetailRow {
  id: number;
  name: string;
  org: string;
  area: string;
  usage: number;             // 资源使用次数
  members: TeamMember[];
}

/** 资源类型 */
export type ResourceKind = '教案模板' | 'PPT模板' | '评估量表' | '绘本清单';

/** 资源条目 */
export interface ResourceItem {
  id: number;
  title: string;
  kind: ResourceKind;
  downloads: number;         // 下载次数
  size: string;
  updatedAt: string;
}

/** 种子教师荣誉（上传案例数与案例管理联动计算） */
export interface SeedHonorRow {
  id: number;
  name: string;
  team: string;
  certifiedAt: string;
}

/** 审核环节（四环节 → 植物隐喻） */
export type AuditStageKey = 'education' | 'psychology' | 'sociology' | 'principal';

export interface AuditStage {
  key: AuditStageKey;
  label: string;
  done: boolean;
}

export interface AuditComment {
  stageKey: AuditStageKey;
  stageLabel: string;
  content: string;
  reviewer: string;
  time: string;
}

/** 一条课程审核 */
export interface AuditItem {
  id: number;
  title: string;
  applicant: string;
  submittedAt: string;
  progress: number;              // 整体进度百分比
  stages: AuditStage[];
  comments: AuditComment[];
  /** 审核领域：PSY 心理 / EDU 教育 / SOC 社会（AUDITOR 按此字段过滤队列） */
  auditField?: string;
}
