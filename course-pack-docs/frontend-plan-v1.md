# 前端工程规划 V1

> 目标：让 `course-pack-client`（小程序端）与 `course-pack-admin`（Web 管理端）严格对齐已确认的 V1 打样图，建立可复用的组件体系与可切换的 Mock 数据层。
> 范围：本轮仅输出规划与规范文档，不写入业务代码；确认后按批次实施。

---

## 一、目标状态定义

### 1.1 小程序端 9 屏状态

| 屏名 | 对应路由/文件路径 | 当前状态 | 与打样图差距 |
|---|---|---|---|
| 首页 | `pages/home/home.vue`（新） | 未建 | 需重建：问候语、支教阶段 Hero 卡、四宫格快速入口、今日推荐 |
| 课程列表 | `pages/course/list.vue`（新） | 未建 | 需重建：精简版/标准版切换、学段筛选、主题筛选、课程卡片 |
| 课程详情 | `pages/course/detail.vue`（新，原 `pages/detail/detail.vue` 迁移） | 已建但差距大 | 当前为旧“课程包”详情，需改为生命教育课程详情：教学目标/价值总结/敏感问题/教学流程步骤条/材料下载 |
| 培训首页 | `pages/training/index.vue`（新） | 未建 | 需重建：培训完成度、必修/选修模块列表 |
| 培训视频播放 | `pages/training/play.vue`（新） | 未建 | 需新建：视频区、课程介绍/大纲/资料下载 Tab |
| 教师工具箱 | `pages/tool/index.vue`（新） | 未建 | 需新建：敏感问题应答、备课助手、家校沟通、课堂活动库四宫格入口 |
| 敏感问题应答 | `pages/tool/qa.vue`（新） | 未建 | 需新建：搜索、热门问题分类、问题列表、去提问入口 |
| 备课助手 | `pages/tool/prepare.vue`（新） | 未建 | 需新建：年级/主题/时长选择、生成备课清单 |
| 我的页面 | `pages/mine/mine.vue` | 已建但差距大 | 当前只有登录/退出，需重建：用户信息、学习进度、我的内容四宫格、我的服务四宫格 |

**说明**：原 `pages/index/index.vue` 中的旧首页内容废弃，V1 用 `pages/home/home.vue` 作为新首页；原 `pages/detail/detail.vue` 迁移为 `pages/course/detail.vue`。

### 1.2 Web 管理端 11 项状态

| 菜单项 | 对应路由 | 当前状态 | V1 目标 |
|---|---|---|---|
| 数据看板 | `/dashboard` | 已建但差距大 | 必须对齐打样：欢迎横幅、5 个统计卡、折线图、饼图、活跃支教队表、右侧快捷入口/待办/动态 |
| 课程管理 | `/course`（原 `/packages` 迁移） | 已建但差距大 | 字段需对齐生命教育课程（学段/主题/版本/材料），不是旧“课程包” |
| 培训管理 | `/training` | 未建 | V1 仅做占位页（EmptyState） |
| 案例管理 | `/case` | 未建 | V1 仅做占位页 |
| 种子教师 | `/seed-teacher` | 未建 | V1 仅做占位页 |
| 支教队伍 | `/team` | 未建 | V1 仅做占位页 |
| 教师管理 | `/teacher` | 未建 | V1 仅做占位页 |
| 问答与反馈 | `/feedback` | 未建 | V1 仅做占位页 |
| 审核中心 | `/audit` | 未建 | V1 仅做占位页 |
| 资源库 | `/resource` | 未建 | V1 仅做占位页 |
| 系统设置 | `/setting` | 未建 | V1 仅做占位页 |

---

## 二、页面与路由映射表

### 2.1 小程序端

| 页面 | 路径 | navigationBarTitleText | 是否 TabBar 页 | 依赖组件 | 依赖 mock 数据 |
|---|---|---|---|---|---|
| 首页 | `pages/home/home` | 首页 | 是 | GreetingHeader、StageHeroCard、QuickEntryGrid、CourseCard | `mock/home.ts`、`mock/user.ts` |
| 课程列表 | `pages/course/list` | 课程 | 是 | FilterBar、CourseCard | `mock/course.ts` |
| 课程详情 | `pages/course/detail` | 课程详情 | 否 | CourseDetailHeader、MaterialIconRow、StepFlow、QAListItem | `mock/course.ts` |
| 培训首页 | `pages/training/index` | 培训 | 是 | TrainingProgressCard、ChecklistItem | `mock/training.ts` |
| 培训视频播放 | `pages/training/play` | 核心理念与科学框架 | 否 | VideoPlayer、TabSegment | `mock/training.ts` |
| 教师工具箱 | `pages/tool/index` | 教师工具箱 | 是 | ToolCard | `mock/tool.ts` |
| 敏感问题应答 | `pages/tool/qa` | 敏感问题应答 | 否 | SearchBar、QAListItem、CategoryTabs | `mock/qa.ts` |
| 备课助手 | `pages/tool/prepare` | 备课助手 | 否 | OptionButtonGroup、PrepareStepForm | `mock/prepare.ts` |
| 我的页面 | `pages/mine/mine` | 我的 | 是 | ProfileHeader、Badge、MenuGrid | `mock/user.ts` |

### 2.2 Web 管理端

| 页面 | 路径 | 页面标题 | 完整实现/占位 | 依赖组件 | 依赖 mock 数据 |
|---|---|---|---|---|---|
| 数据看板 | `/dashboard` | 数据看板 | 完整实现 | WelcomeBanner、StatCard、LineChartCard、PieChartCard、TeamTable、TodoList、ActivityFeed、SideBrandCard | `mock/dashboard.ts` |
| 课程管理 | `/course` | 课程管理 | 完整实现 | SearchBar、FilterSelect、CourseTable、CourseFormDialog | `mock/course-admin.ts` |
| 培训管理 | `/training` | 培训管理 | 占位 | EmptyState | - |
| 案例管理 | `/case` | 案例管理 | 占位 | EmptyState | - |
| 种子教师 | `/seed-teacher` | 种子教师 | 占位 | EmptyState | - |
| 支教队伍 | `/team` | 支教队伍 | 占位 | EmptyState | - |
| 教师管理 | `/teacher` | 教师管理 | 占位 | EmptyState | - |
| 问答与反馈 | `/feedback` | 问答与反馈 | 占位 | EmptyState | - |
| 审核中心 | `/audit` | 审核中心 | 占位 | EmptyState | - |
| 资源库 | `/resource` | 资源库 | 占位 | EmptyState | - |
| 系统设置 | `/setting` | 系统设置 | 占位 | EmptyState | - |

---

## 三、组件拆解清单

### 3.1 小程序端组件

| 组件名 | Props 定义 | 使用页面 | 对应 Token |
|---|---|---|---|
| `GreetingHeader` | `userName: string; greeting: string;` | 首页 | `--text-primary`, `--font-size-l`, `--font-weight-bold` |
| `StageHeroCard` | `stage: string; day: number; recommend: string;` | 首页 | `--brand-primary-light`, `--brand-primary`, `--radius-card`, `--shadow-card` |
| `QuickEntryGrid` | `items: {icon, label, path}[]` | 首页 | `--bg-card`, `--brand-primary`, `--text-primary`, `--radius-card` |
| `CourseCard` | `course: CourseSummary;` | 首页、课程列表 | `--bg-card`, `--radius-card`, `--shadow-card`, `--theme-*` |
| `CourseDetailHeader` | `title: string; tags: string[]; grade: string; duration: string; version: string; summary: string;` | 课程详情 | `--text-primary`, `--text-secondary`, `--brand-primary-light`, `--font-size-l` |
| `MaterialIconRow` | `materials: {type, name, count}[]` | 课程详情 | `--brand-primary`, `--text-regular`, `--space-m` |
| `StepFlow` | `steps: {index, title, desc, duration}[]` | 课程详情 | `--brand-primary`, `--border`, `--text-primary`, `--text-secondary` |
| `TrainingProgressCard` | `percent: number; completed: number; total: number;` | 培训首页 | `--brand-primary`, `--brand-primary-light`, `--radius-card`, `--shadow-card` |
| `ChecklistItem` | `index: number; title: string; status: 'done' \| 'lock' \| 'current';` | 培训首页 | `--succ`, `--warn`, `--text-disabled`, `--text-primary` |
| `ToolCard` | `icon: string; title: string; desc: string; path: string;` | 教师工具箱 | `--bg-card`, `--radius-card`, `--shadow-card`, `--brand-primary` |
| `QAListItem` | `question: string; category: string;` | 敏感问题应答、课程详情 | `--bg-card`, `--border`, `--text-primary`, `--text-secondary` |
| `PrepareStepForm` | `options: PrepareOptions; onGenerate: () => void;` | 备课助手 | `--brand-primary`, `--brand-primary-light`, `--radius-s`, `--text-primary` |
| `ProfileHeader` | `avatar: string; name: string; org: string; team: string; badge: string;` | 我的页面 | `--bg-card`, `--radius-card`, `--text-primary`, `--text-secondary` |
| `Badge` | `text: string;` | 我的页面、课程详情 | `--brand-secondary`, `--brand-secondary-light`, `--radius-s` |
| `MenuGrid` | `items: {icon, label, path}[]; title?: string;` | 我的页面 | `--bg-card`, `--radius-card`, `--text-primary`, `--text-secondary` |

### 3.2 Web 管理端组件

| 组件名 | Props 定义 | 使用页面 | 对应 Token |
|---|---|---|---|
| `WelcomeBanner` | `userName: string; date: string;` | 数据看板 | `--brand-primary-light`, `--brand-primary`, `--radius-card`, `--shadow-card` |
| `StatCard` | `label: string; value: string; unit: string; trend: string; icon: string;` | 数据看板 | `--bg-card`, `--radius-card`, `--shadow-card`, `--font-size-xxl`, `--text-secondary` |
| `LineChartCard` | `title: string; data: ChartData;` | 数据看板 | `--bg-card`, `--radius-card`, `--brand-primary`, `--theme-respect` |
| `PieChartCard` | `title: string; data: PieData[]; total: number;` | 数据看板 | `--bg-card`, `--radius-card`, `--theme-body`, `--theme-family`, `--theme-respect`, `--theme-growth`, `--theme-general` |
| `TodoList` | `items: {title, count, path}[]` | 数据看板 | `--bg-card`, `--radius-card`, `--text-primary`, `--text-secondary`, `--warn` |
| `ActivityFeed` | `items: {title, time, type}[]` | 数据看板 | `--bg-card`, `--radius-card`, `--text-secondary`, `--border` |
| `TeamTable` | `teams: Team[]` | 数据看板 | `--bg-card`, `--radius-card`, `--border`, `--brand-primary` |
| `SideBrandCard` | `slogan: string;` | 数据看板（侧边栏底部） | `--brand-primary-light`, `--brand-primary`, `--radius-card` |

---

## 四、Mock 数据内容规范（重点章）

> 原则：所有 Mock 数据必须真实反映伴芽生命教育业务，**严禁出现 Vue、Spring、TypeScript 等编程类课程**。数据集中放在 `src/mock/` 目录，字段结构与接口规范文档对齐，后端就绪后只改 request 层、页面零改动。

### 4.1 目录约定

```
course-pack-client/src/mock/
├── index.ts          # 统一导出
├── user.ts           # 用户信息 + 支教阶段
├── home.ts           # 首页推荐、快速入口
├── course.ts         # 课程列表 + 课程详情
├── training.ts       # 培训课程 + 视频播放
├── qa.ts             # 敏感问题应答
├── prepare.ts        # 备课助手选项与生成规则
└── tool.ts           # 教师工具箱入口

course-pack-admin/src/mock/
├── index.ts
├── dashboard.ts      # 看板统计、图表、待办、动态、队伍
└── course-admin.ts   # 管理端课程列表
```

### 4.2 课程包（Course）数据规范

统一 8 门课程，每门课程字段如下：

```ts
interface Course {
  id: number;
  title: string;              // 课程名
  subtitle?: string;          // 副标题（可选）
  theme: 'body' | 'family' | 'respect' | 'growth' | 'general';
  themeLabel: string;         // 身体与安全 / 家庭与合作 / 人际与尊重 / 成长与变化 / 综合主题
  grade: string;              // 小学1-3年级 / 小学4-6年级
  duration: number;           // 分钟
  version: '精简版' | '标准版';
  summary: string;            // 一句话简介
  coverColor: string;         // 主题色插画占位，使用 theme 对应 Token
  materials: {
    plan: number;             // 教案数量
    slides: number;           // 课件数量
    toolkit: number;          // 教具清单数量
    video: number;            // 课程视频数量
  };
  objectives: string[];       // 教学目标
  values: string[];           // 价值总结
  sensitiveQa: number[];      // 关联的敏感问题 id 列表
  steps: TeachingStep[];      // 教学流程
}

interface TeachingStep {
  index: number;
  title: string;
  desc: string;
  duration: number;           // 分钟
}
```

#### 8 门课程清单

| id | 课程名 | theme | 学段 | 时长 | 版本 | 一句话简介 |
|---|---|---|---|---|---|---|
| 1 | 我的身体，我做主 | body | 小学4-6年级 | 40 | 精简版 | 认识自己的身体，学会表达和保护自己的界限，尊重自己也尊重他人。 |
| 2 | 我们的家庭，不一样也很棒 | family | 小学1-3年级 | 30 | 精简版 | 了解家庭的多样性，学习欣赏不同家庭结构中的爱与支持。 |
| 3 | 说话有力量，尊重每个人 | respect | 小学4-6年级 | 40 | 精简版 | 学习有效沟通与倾听，理解尊重他人边界的重要性。 |
| 4 | 成长的我，很特别 | growth | 小学1-3年级 | 30 | 精简版 | 认识成长中的变化，建立积极的自我认同。 |
| 5 | 我会表达拒绝 | body | 小学4-6年级 | 45 | 标准版 | 学会在不舒服的情境中清晰说“不”，并寻求可信赖成人的帮助。 |
| 6 | 我们的朋友，我们的约定 | respect | 小学1-3年级 | 35 | 标准版 | 学习友谊中的规则与边界，共建安全的同伴关系。 |
| 7 | 变化中的我与我 | growth | 小学4-6年级 | 40 | 标准版 | 理解青春期前的身心变化，以科学态度迎接成长。 |
| 8 | 安全小卫士 | general | 小学1-3年级 | 30 | 标准版 | 识别日常安全风险，掌握基本自我保护方法。 |

#### 示例：课程 1《我的身体，我做主》完整字段

```ts
{
  id: 1,
  title: '我的身体，我做主',
  theme: 'body',
  themeLabel: '身体与安全',
  grade: '小学4-6年级',
  duration: 40,
  version: '精简版',
  summary: '认识自己的身体，学会表达和保护自己的界限，尊重自己也尊重他人。',
  coverColor: '--theme-body',
  materials: { plan: 1, slides: 1, toolkit: 1, video: 1 },
  objectives: [
    '认识身体隐私部位与身体界限',
    '学会用“我愿意/我不愿意”表达感受',
    '知道遇到不舒服接触时可以告诉信任的成人'
  ],
  values: [
    '每个孩子都有权利保护自己的身体',
    '尊重他人从尊重身体边界开始'
  ],
  sensitiveQa: [1, 2, 3],
  steps: [
    { index: 1, title: '热身活动', desc: '认识身体的不同部位', duration: 10 },
    { index: 2, title: '情景讨论', desc: '哪些行为让人不舒服？', duration: 10 },
    { index: 3, title: '小组活动', desc: '我的身体界限小卡片', duration: 10 },
    { index: 4, title: '分享交流', desc: '我们可以怎么做？', duration: 5 },
    { index: 5, title: '价值总结与升华', desc: '每个人都有身体界限，我们要学会尊重', duration: 5 }
  ]
}
```

### 4.3 敏感问题应答库（Q&A）

至少 10 条，字段结构：

```ts
interface QA {
  id: number;
  question: string;           // 问题
  category: '身体与安全' | '人际关系' | '成长变化';
  scene: string;              // 情景描述
  response: string;           // 推荐回应
  tips: Tip[];                // 教师提示
}

interface Tip {
  type: 'do' | 'caution';     // ✓ 或 ⚠
  content: string;
}
```

| id | 问题 | category | 推荐回应 | 教师提示 |
|---|---|---|---|---|
| 1 | 老师，男生和女生有什么不一样？ | 身体与安全 | 男生和女生在身体上有些不同，这些都是正常的成长现象。重要的是我们都要尊重彼此的身体和隐私。 | ✓ 用科学、平和的语气回答；✓ 强调“尊重隐私”；⚠ 避免让孩子感到羞耻或好奇被批评 |
| 2 | 老师，为什么有人有胸，有人没有？ | 成长变化 | 这是身体发育的一部分，每个人发育的时间不同，都是正常的。 | ✓ 解释发育的个体差异；⚠ 不要嘲笑或比较任何孩子 |
| 3 | 老师，别人摸我这里，我不舒服，怎么办？ | 身体与安全 | 如果有人让你不舒服，你可以大声说“不要这样”，然后马上离开，告诉老师或家长。 | ✓ 明确告诉孩子可以说“不”；✓ 强调告诉信任的成人；✓ 肯定孩子的感受 |
| 4 | 老师，我可以不告诉父母吗？ | 人际关系 | 有些事情如果你感到害怕或担心，告诉父母或老师会帮助你。我们会一起保护你。 | ✓ 表达支持；⚠ 不要承诺替孩子保密涉及安全的事情 |
| 5 | 老师，谈恋爱是什么？ | 成长变化 | 谈恋爱是成年人之间一种亲密的交往方式，你们现在的主要任务是学习和交朋友。 | ✓ 回答简洁；⚠ 不要贬低或嘲笑孩子的好奇 |
| 6 | 老师，为什么有的家庭只有爸爸（妈妈）？ | 家庭与合作 | 每个家庭都不一样，有的家庭有爸爸妈妈，有的只有爸爸或妈妈，有的和爷爷奶奶一起生活。爱我们的家人就是最重要的。 | ✓ 强调家庭多样性；✓ 避免负面评价任何家庭结构 |
| 7 | 老师，有人给我起外号，我不喜欢 | 人际关系 | 你可以告诉对方“请不要这样叫我，我不喜欢”。如果对方继续，你可以来找老师。 | ✓ 教孩子表达感受；✓ 提供求助渠道 |
| 8 | 老师，我可以一个人去厕所吗？ | 身体与安全 | 当然可以。但如果在不熟悉的地方，最好和小伙伴一起去，互相照应。 | ✓ 鼓励孩子独立；✓ 提醒安全场景 |
| 9 | 老师，我为什么长得比别人矮？ | 成长变化 | 每个人的成长速度不一样，有的先长高，有的后长高。只要健康饮食、多运动、睡好觉，身体就会健康成长。 | ✓ 消除焦虑；⚠ 不要承诺具体长高时间 |
| 10 | 老师，如果有人让我看不好的图片怎么办？ | 身体与安全 | 不好的图片不是我们这个年龄该看的，你可以拒绝，并且告诉老师或家长。 | ✓ 明确拒绝；✓ 强调报告；⚠ 不要追问孩子看到了什么细节 |

### 4.4 培训课程数据

```ts
interface Training {
  id: number;
  title: string;
  type: 'required' | 'elective';
  duration: number;           // 分钟
  progress: number;           // 0-100
  status: 'done' | 'current' | 'lock';
  videoUrl: string;           // mock 占位
}
```

必修 5 节：
1. 破冰与价值共识（20 分钟，已完成）
2. 核心理念与科学框架（30 分钟，已完成）—— 对应培训视频播放页
3. 课程包使用方法（25 分钟，已完成）
4. 授课技巧与课堂应对（35 分钟，当前学习）
5. 价值引导与伦理风险控制（30 分钟，未解锁）

选修 2 节：
1. 积极的社会学视角赋能（30 分钟，未解锁）
2. 乡村儿童心理特点与沟通（35 分钟，未解锁）

### 4.5 备课助手生成规则

```ts
interface PrepareOption {
  grades: Array<'1-3年级' | '4-6年级'>;
  themes: Array<'身体与安全' | '家庭与合作' | '人际与尊重' | '成长与变化'>;
  durations: Array<20 | 30 | 40 | 60>;
}

interface PrepareResult {
  grade: string;
  theme: string;
  duration: number;
  recommendedCourses: number[];   // 课程 id
  teachingFlow: string[];         // 教学流程建议
  materialChecklist: string[];    // 材料清单
  attentionNotes: string[];       // 注意事项
}
```

生成映射规则：
- 年级 + 主题 → 过滤出匹配的课程（见 4.2 课程清单）
- 时长 → 推荐最接近且不超过该时长的课程版本
- 教学流程默认 5 步：热身活动 → 主题导入 → 主体活动 → 分享讨论 → 总结延伸
- 材料清单默认：教案 1 份、课件 1 份、教具清单 1 份、课程视频 1 段
- 注意事项：根据主题自动追加一条安全/伦理提示

### 4.6 用户信息

```ts
interface UserProfile {
  id: number;
  name: string;                 // 李老师
  avatar: string;               // 默认头像
  org: string;                  // 山东师范大学
  team: string;                 // ××支教队
  badge: string;                // 种子教师
  stage: string;                // 短期支教 · 第5天
  recommend: string;            // 推荐：小学4-6年级课程
  trainingProgress: { completed: number; total: number };
}
```

### 4.7 Web 看板数据规范

```ts
interface DashboardData {
  welcome: { userName: string; date: string };
  stats: [
    { label: '服务教师总数', value: 356, unit: '人', trend: '较上月 ↑ 12.4%' },
    { label: '课程学习次数', value: 1284, unit: '次', trend: '较上月 ↑ 8.2%' },
    { label: '课程下载次数', value: 863, unit: '次', trend: '较上月 ↑ 11.3%' },
    { label: '培训完成率', value: 78.6, unit: '%', trend: '较上月 ↑ 4.5%' },
    { label: '覆盖支教队伍', value: 32, unit: '支', trend: '较上月 ↑ 3 支' }
  ];
  growthTrend: {
    months: ['2025-03', '2025-04', '2025-05', '2025-06', '2025-07', '2025-08'];
    teachers: [120, 145, 180, 220, 280, 320];
    studies: [420, 560, 720, 900, 1100, 1284];
  };
  courseThemeDistribution: [
    { theme: '身体与安全', value: 410, percent: 32, color: '#4F8F83' },
    { theme: '家庭与合作', value: 295, percent: 23, color: '#D99A62' },
    { theme: '人际与尊重', value: 231, percent: 18, color: '#6B9BD0' },
    { theme: '成长与变化', value: 192, percent: 15, color: '#7BB57D' },
    { theme: '综合主题', value: 156, percent: 12, color: '#A78BCB' }
  ];
  activeTeams: [
    { name: '山科大希望之光队', org: '山东科技大学', area: '临沂市 沂水县', teachers: 18, usage: 36, completion: 82 },
    { name: '青岛大学麦田队', org: '青岛大学', area: '菏泽市 单县', teachers: 22, usage: 48, completion: 76 },
    { name: '曲师大知行队', org: '曲阜师范大学', area: '济宁市 金乡县', teachers: 16, usage: 31, completion: 68 },
    { name: '山师大蒲公英队', org: '山东师范大学', area: '德州市 平原县', teachers: 20, usage: 42, completion: 80 },
    { name: '聊城大学星火队', org: '聊城大学', area: '聊城市 东阿县', teachers: 15, usage: 28, completion: 70 }
  ];
  todos: [
    { title: '待审核课程', count: 6 },
    { title: '待审核案例', count: 12 },
    { title: '教师反馈', count: 8 },
    { title: '问答待回复', count: 15 }
  ];
  activities: [
    { title: '新课程《我会表达拒绝》已发布', time: '2小时前', type: 'course' },
    { title: '山东科技大学支教队 完成培训', time: '5小时前', type: 'team' },
    { title: '新增教师注册：王老师（临沂支教队）', time: '1天前', type: 'user' },
    { title: '课程《我们的家庭》被收藏 23 次', time: '1天前', type: 'favorite' }
  ];
}
```

---

## 五、设计 Token 落地核查表

| 视觉元素 | 应引用 Token | 当前是否生效 | 备注 |
|---|---|---|---|
| 小程序页面背景 | `--bg-page: #F7F6F1` | 否（当前 #F6F7F8） | 必须全局替换 |
| 小程序导航栏背景 | `--bg-nav: #F7F6F1` | 否（当前绿色 #2F9E6C） | 必须为米白 |
| 小程序导航栏标题 | `--text-primary: #233C36` | 否 | 禁止绿色标题 |
| tabBar 选中色 | `--tab-active: #4F8F83` | 否（当前 #2F9E6C） | 必须替换 |
| tabBar 未选中色 | `--tab-inactive: #86909C` | 是 | 保持 |
| tabBar 背景 | `--bg-card: #FFFFFF` | 是 | 保持 |
| 卡片背景 | `--bg-card: #FFFFFF` | 是 | 保持 |
| 卡片圆角 | `--radius-card: 14px` | 否（当前 8px/16px） | 统一 14px |
| 卡片阴影 | `--shadow-card: rgba(35,60,54,0.06)` | 否 | 必须替换 |
| 主按钮背景 | `--brand-primary: #4F8F83` | 否 | 替换亮绿色 |
| 主按钮按压 | `--brand-primary-dark: #3A6E64` | 否 | 同步替换 |
| 标签浅底 | `--brand-primary-light: #E6F0EE` | 否 | 同步替换 |
| 暖橙强调 | `--brand-secondary: #D99A62` | 否 | 收藏/提醒/勋章 |
| 暖橙浅底 | `--brand-secondary-light: #F8E9DA` | 否 | 提示卡片背景 |
| 主题色-身体安全 | `--theme-body: #4F8F83` | 否 | 新课程主题色 |
| 主题色-家庭合作 | `--theme-family: #D99A62` | 否 | 新课程主题色 |
| 主题色-人际尊重 | `--theme-respect: #6B9BD0` | 否 | 新课程主题色 |
| 主题色-成长变化 | `--theme-growth: #7BB57D` | 否 | 新课程主题色 |
| 主题色-综合 | `--theme-general: #A78BCB` | 否 | 新课程主题色 |
| 大标题字号 | `--font-size-xl: 24px` | 是 | 保持 |
| 数据看板大数字 | `--font-size-xxl: 32px` | 否 | Web 端新增 |
| Web 侧边栏选中背景 | `--side-active-bg: #4F8F83` | 否 | 替换 Element Plus 默认蓝 |
| Web 侧边栏选中文字 | `--side-active-text: #FFFFFF` | 否 | 同步 |

---

## 六、小程序导航与信息架构

### 6.1 TabBar 五项完整配置

```json
{
  "color": "#86909C",
  "selectedColor": "#4F8F83",
  "backgroundColor": "#FFFFFF",
  "borderStyle": "white",
  "list": [
    { "pagePath": "pages/home/home", "text": "首页" },
    { "pagePath": "pages/course/list", "text": "课程" },
    { "pagePath": "pages/training/index", "text": "培训" },
    { "pagePath": "pages/tool/index", "text": "工具" },
    { "pagePath": "pages/mine/mine", "text": "我的" }
  ]
}
```

### 6.2 页面跳转关系（文字版）

```
首页
 ├─ 点击快速入口「找课程」 → 课程列表
 ├─ 点击快速入口「学培训」 → 培训首页
 ├─ 点击快速入口「备课工具」 → 教师工具箱
 ├─ 点击快速入口「问答支持」 → 敏感问题应答
 ├─ 点击今日推荐课程卡片 → 课程详情
 └─ 点击支教阶段 Hero 卡 → 课程列表（按推荐学段过滤）

课程列表
 ├─ 点击课程卡片 → 课程详情
 └─ 筛选/搜索 → 本地过滤课程列表

课程详情
 ├─ 点击「开始备课」 → 备课助手（携带课程主题/年级）
 └─ 点击敏感问题 → 敏感问题应答（携带问题 id）

培训首页
 ├─ 点击必修/选修课程 → 培训视频播放
 └─ 点击「继续学习」 → 培训视频播放（当前课程）

教师工具箱
 ├─ 敏感问题应答 → 敏感问题应答页
 ├─ 备课助手 → 备课助手页
 ├─ 家校沟通 → 占位提示（V1 仅入口）
 └─ 课堂活动库 → 占位提示（V1 仅入口）

我的页面
 ├─ 学习记录 → 培训首页
 ├─ 收藏课程 → 课程列表（过滤收藏）
 ├─ 下载记录 / 我的案例 / 我的问题 → 占位提示
 └─ 设置 → 占位提示
```

### 6.3 NavigationBar 样式

全局配置（`pages.json`）：

```json
"globalStyle": {
  "navigationBarTextStyle": "black",
  "navigationBarTitleText": "伴芽",
  "navigationBarBackgroundColor": "#F7F6F1",
  "backgroundColor": "#F7F6F1"
}
```

要求：
- 所有页面导航栏背景为米白 `#F7F6F1`
- 标题文字为深色 `#233C36`
- 禁止使用绿色导航栏背景
- 返回按钮保持系统默认黑色

---

## 七、V1 实施顺序

### 批次 ①：Token 修复 + 导航/tabBar
**涉及页面**：全局
**涉及组件**：无新增
**工作内容**：
1. 更新 `course-pack-client/src/uni.scss` 中全部 Token 为 V1 值
2. 更新 `course-pack-client/src/pages.json`：tabBar 五项、全局 navigationBar 米白
3. 更新 `course-pack-admin/src/styles/index.css`：覆盖 Element Plus 主题色为 `#4F8F83`
4. 调整 `course-pack-admin` 侧边栏样式为选中绿色
**验收标准**：
- [ ] 小程序运行后页面背景为 `#F7F6F1`
- [ ] 小程序导航栏为米白 + 黑色文字
- [ ] tabBar 五项可见，选中色为 `#4F8F83`
- [ ] Web 端侧边栏选中项为绿色背景

### 批次 ②：首页
**涉及页面**：`pages/home/home.vue`
**涉及组件**：GreetingHeader、StageHeroCard、QuickEntryGrid、CourseCard
**工作内容**：
1. 新建首页，替换原 `pages/index/index.vue`
2. 实现问候语、支教阶段 Hero 卡、四宫格快速入口、今日推荐
3. 课程卡片使用主题色占位插画
**验收标准**：
- [ ] 首页显示“早上好，李老师”及日期
- [ ] Hero 卡显示“短期支教 · 第5天”及推荐学段
- [ ] 四宫格入口为：找课程、学培训、备课工具、问答支持
- [ ] 今日推荐区展示 1 条伴芽生命教育课程
- [ ] 不出现任何编程类课程

### 批次 ③：课程列表 + 课程详情
**涉及页面**：`pages/course/list.vue`、`pages/course/detail.vue`
**涉及组件**：FilterBar、CourseCard、CourseDetailHeader、MaterialIconRow、StepFlow
**工作内容**：
1. 新建课程列表页，支持精简版/标准版切换、学段/主题筛选
2. 迁移并重建课程详情页，对齐教学目标/价值总结/敏感问题/教学流程/材料下载
3. Mock 数据替换为 4.2 中 8 门课程
**验收标准**：
- [ ] 课程列表展示 8 门伴芽课程
- [ ] 筛选可正常工作
- [ ] 课程详情页展示教学流程步骤条
- [ ] 材料图标行展示教案/课件/教具清单/课程视频数量
- [ ] 敏感问题模块关联到对应 QA

### 批次 ④：培训两页 + 工具箱四页
**涉及页面**：`pages/training/index.vue`、`pages/training/play.vue`、`pages/tool/index.vue`、`pages/tool/qa.vue`、`pages/tool/prepare.vue`
**涉及组件**：TrainingProgressCard、ChecklistItem、ToolCard、QAListItem、PrepareStepForm
**工作内容**：
1. 培训首页：完成度、必修/选修列表
2. 培训视频播放：视频区、课程介绍/大纲/资料下载 Tab；`videoUrl` 为空时渲染静态封面（`--brand-primary-light` 背景 + 主题插画 + 播放按钮），与打样图 P5 一致
3. 教师工具箱：四宫格入口
4. 敏感问题应答：搜索、分类、问题列表
5. 备课助手：年级/主题/时长选择 + 生成清单
**验收标准**：
- [ ] 培训完成度显示 75%（6/8）
- [ ] 必修 5 节 + 选修 2 节展示正确
- [ ] 敏感问题库至少 10 条且含教师提示
- [ ] 备课助手可按选择生成备课清单
- [ ] 工具箱入口不含编程相关内容

### 批次 ⑤：我的页面 + Web 看板对齐打样
**涉及页面**：`pages/mine/mine.vue`、`/dashboard`
**涉及组件**：ProfileHeader、Badge、MenuGrid、WelcomeBanner、StatCard、LineChartCard、PieChartCard、TeamTable、TodoList、ActivityFeed、SideBrandCard
**工作内容**：
1. 重建我的页面：用户信息、学习进度、我的内容、我的服务
2. Web 数据看板完全对齐打样图；图表用 ECharts **按需引入**（仅 `LineChart`、`PieChart` 及必需组件），并在 vite `manualChunks` 中独立分包
3. Web 课程管理页字段对齐生命教育课程
4. 其余 9 个 Web 菜单用 EmptyState 占位
**验收标准**：
- [ ] 我的页面显示李老师/山东师范大学/支教队/种子教师
- [ ] Web 看板显示 5 个统计数字与趋势
- [ ] 折线图展示近 6 个月教师数与学习次数
- [ ] 饼图展示课程主题分布
- [ ] 活跃支教队表格与打样一致
- [ ] 其余 9 个菜单可点击进入 EmptyState 页

---

## 八、验收清单

### 全局设计验收
- [ ] 所有颜色引用自 `tokens.md`，无硬编码色值
- [ ] 小程序页面背景为 `#F7F6F1`
- [ ] 小程序导航栏为米白背景 + 深色文字
- [ ] tabBar 五项完整，选中色 `#4F8F83`
- [ ] 卡片圆角统一为 `14px`，阴影使用 `rgba(35,60,54,0.06)`
- [ ] Web 侧边栏选中项为绿色背景白色文字

### 内容验收
- [ ] Mock 数据中不出现任何编程类课程（Vue、Spring、TypeScript 等）
- [ ] 8 门课程均为伴芽生命教育课程内容
- [ ] 敏感问题库至少 10 条，覆盖身体安全/人际关系/成长变化
- [ ] 用户信息为“李老师 / 山东师范大学 / 支教队 / 种子教师”
- [ ] Web 看板数据与 4.7 规范一致

### 功能验收
- [ ] 小程序 9 屏均可访问
- [ ] 课程筛选、搜索正常工作
- [ ] 备课助手可按选项生成清单
- [ ] 培训进度与视频页可切换
- [ ] Web 看板图表正常渲染
- [ ] Web 11 个菜单均可点击

### 代码验收
- [ ] Mock 数据集中在 `src/mock/` 目录
- [ ] 页面不直接写 mock 数据，统一通过 api 层调用
- [ ] 后端就绪后只需替换 request 层，页面零改动
- [ ] 组件 Props 类型定义完整
- [ ] 文件路径与工程现有结构一致，无冗余目录

---

## 附录：相关文件索引

- 设计 Token：[course-pack-docs/01-设计/tokens.md](file:///d:/WeChatProject/course-pack-docs/01-设计/tokens.md)
- 接口规范：[course-pack-docs/02-接口/接口规范.md](file:///d:/WeChatProject/course-pack-docs/02-接口/接口规范.md)
- 产品需求概述：[course-pack-docs/00-产品/产品需求概述.md](file:///d:/WeChatProject/course-pack-docs/00-产品/产品需求概述.md)
- 原型图：[course-pack-docs/01-设计/原型图.md](file:///d:/WeChatProject/course-pack-docs/01-设计/原型图.md)
- 小程序端工程：[course-pack-client/](file:///d:/WeChatProject/course-pack-client)
- Web 管理端工程：[course-pack-admin/](file:///d:/WeChatProject/course-pack-admin)
