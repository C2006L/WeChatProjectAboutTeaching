# 第五部分：角色权限系统与 Web 教师工作台 — 实施计划

## Context（背景）

第五部分为角色权限系统与教师工作台。经探查，**后端认证层已在历史批次完整实现**（`course-pack-server`）：
- 统一 `/api/auth/login`（bcrypt），JWT 7 天携带 `role/status`（`JwtUtil`）
- `AuthInterceptor` + `@RequireRole` 双层校验、状态机拦截 DISABLED/REJECTED（`auth/` 包）
- `User` 实体含 `role/status/isSeed/auditField/rejectReason`
- `DataSeeder` 演示账号 teacher01-04 / auditor01-02 / admin01（密码 123456）
- 教师草稿同步接口 `DraftController`（`/api/tools/prepare/draft`）

**主要缺口在 Web admin 前端**与**课程审核后端**。本计划聚焦补齐这些。

## 范围界定（决策）

- 本轮以 **Web admin 端**为主。
- **补齐课程审核后端接口**（AUDITOR 审核中心需要按 `audit_field` 过滤），admin 的 AuditView 原为 mock，改接后端。
- client 小程序的登录接入与状态锁页**留到下一阶段**（逐页交付纪律）。

## 一、auth 接入后端（admin）

**文件**：`course-pack-admin/src/store/auth.ts`
- `login(username,password)` 改为调 `POST /api/auth/login`（走 `api/request.ts` 自动带 token），将返回的 `LoginResponse`（token + userVO 含 role/status/isSeed/auditField/name）存入 state + `localStorage`。
- 新增状态：`user`、`role`、`status`、`isSeed`；`isLoggedIn` 基于 token。
- `eleven` 增补 `getUser` 反序列化。
- 登录失败抛业务异常（沿用 request 封装的 ElMessage 提示）。

## 二、路由角色分流（admin）

**文件**：`course-pack-admin/src/router/index.ts` + 新路由

```
beforeEach: 解析 token → auth.setUser 恢复
1. 未登录 → /login
2. 已登录访问 /login 或 /dashboard →
   - ADMIN  → /dashboard（全模块）
   - AUDITOR → /audit
   - TEACHER ACTIVE  → /teacher/workbench
   - TEACHER PENDING → /teacher/status?mode=pending
   - TEACHER REJECTED → /teacher/status?mode=rejected
   - TEACHER DISABLED → /teacher/status?mode=disabled
3. 各角色路径白名单校验（前端导航守卫）
```

新增路由：
- `/teacher/workbench` → `views/teacher/TeacherWorkbenchView.vue`
- `/teacher/status` → `views/teacher/TeacherStatusView.vue`（`?mode=` 控制 PENDING 锁定页 / REJECTED 驳回原因+重新提交 / DISABLED 提示页）

TEACHER 访问其余 `/admin` 系路径 → 重定向工作台。

## 三、Sidebar 按角色渲染（admin）

**文件**：`course-pack-admin/src/layout/MainLayout.vue`
- `menuRoutes` 改为计算属性：ADMIN → 全 11 模块；AUDITOR → 仅审核中心；TEACHER → 不进入 MainLayout（走工作台，独立布局）。
- Header 用户名/头像读取 `auth.user`，显示当前角色。
- 守卫在 TEACHER 时阻止进入 MainLayout 容器。

## 四、课程审核后端（server）

**新增**：`entity/CourseAudit.java`、`mapper/CourseAuditMapper.java`、`service/CourseAuditService.java`、`controller/audit/CourseAuditController.java`

- 表 `course_audit`：id、course_id、title、audit_field(EDU/PSY/SOC)、applicant、submit_time、auditor_id、status(PENDING/APPROVED/REJECTED)、progress、reject_reason、created_at。
- `CourseAuditController` 标注 `@RequireRole(UserRole.AUDITOR)`：
  - `GET /api/audit/queue`：仅返回 `audit_field = 当前审核员.auditField` 的 PENDING 队列
  - `POST /api/audit/{id}/approve` / `reject {reason}`
  - 写 `TimelineService` 动态时间线（可追溯）
- `schema.sql` 追加建表语句；`DataSeeder` 播种少量课程审核样例（含不同 audit_field）。

## 五、admin 审核中心改造（admin）

**文件**：`course-pack-admin/src/views/audit/AuditView.vue`、`course-pack-admin/src/api/audit.ts`
- 由内部 mock 改为调后端 `/api/audit/queue` + approve/reject。
- 保持现有双栏 UI（左列表/右详情 + AuditStepper 拟物流程条）不变，仅换数据源。
- 审核通过/驳回后实时写时间线（后端处理，前端刷新队列）。

## 六、Web 教师工作台（admin 新增）

**入口**：`views/teacher/TeacherWorkbenchView.vue`，复用 AdminLayout 布局（全宽内容区 1280px）。
四模块 Tab：

1. **课程学习**：复用 `views/course/CourseView.vue` 的课程卡片组件（Bento 网格、封面色、主题标签），按钮文案由"查看详情 →"改为 **"去备课 →"**，跳转备课助手。数据源复用 `api/course.ts` mock。
2. **培训学习**：复用 `views/training/TrainingView.vue` 的培训列表/进度条（GrowthProgress 拟物条），展示当前教师必修培训进度。
3. **备课助手**：左侧大纲 + 右侧编辑器。调后端 `/api/tools/prepare/draft`（读写同一张 LessonDraft 表，与小程序端续写数据互通）。大纲从草稿 `outline` 字段渲染。
4. **我的案例**：仅当 `auth.isSeed===1` 显示。复用 `views/case/CaseView.vue` 的案例卡片，展示当前教师上传案例。

TEACHER_PENDING/REJECTED/DISABLED 由 `TeacherStatusView` 处理（见路由），不进入工作台。

## 验证（Verification）

1. 后端：`mvn -pl course-pack-server compile`（或现有 mvn wrapper）通过；本地 MySQL 执行 `schema.sql` 后启动，`/api/auth/login` 用 admin01/123456 返回 JWT，再用 JWT 访问 `/api/audit/queue`。
2. admin：`npm run type-check` 零错误；`npm run dev` 后：
   - admin01 → 全模块 sidebar
   - auditor01（PSY）→ 仅审核中心，且队列只含 PSY 课程
   - teacher04（ACTIVE+is_seed）→ 教师工作台四模块
   - teacher02（PENDING）→ 审核中锁页；teacher03（REJECTED）→ 驳回原因页
3. 前端控制台零 error/warning。
4. 工作台备课草稿：保存后调 `/api/tools/prepare/draft`，再在小程序端备课页确认同一草稿可见（数据同表）。

## 交付形态

本阶段仅 web-admin（1440×900）与后端接口；不截图，用户自验收。client 小程序端登录接入与锁页留待下一阶段。