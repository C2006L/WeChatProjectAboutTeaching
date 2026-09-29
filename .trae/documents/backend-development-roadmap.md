# 后端开发路线图（Phase 2 起）

> 定位：在 Phase 1 骨架与既有四个模块之上，把剩余业务接口全部补齐，使前端 api 层可"零改动"切换真实接口。
> 配套文档：`接口规范.md`（契约）、`course-package-backend-architecture-plan.md`（技术方案 v2）、`phase1-implementation-plan.md`（已完成）。
> 使用方式：按里程碑顺序施工，每完成一项立即用该里程碑的"验收"自测，不留债。

---

## 〇、现状基线

| 类别 | 状态 | 说明 |
|---|---|---|
| 通用层 | 已完成 | `Result` / `PageResult` / `ErrorCode` / `BusinessException` / `GlobalExceptionHandler` / 三个枚举 |
| 鉴权层 | 已完成 | `JwtUtil` / `AuthInterceptor` / `AuthContext` / `@RequireRole` |
| 配置层 | 已完成 | `WebMvcConfig`(CORS+拦截器) / `MyBatisPlusConfig` / `Knife4jConfig` / `DataSeeder` |
| 业务模块 | 已完成 4 个 | 统一认证、教师管理、审核中心、备课草稿 |
| 数据表 | 已完成 4 张 | `users` / `lesson_draft` / `timeline_log` / `course_audit` |
| 业务模块 | **待实现** | 课程、培训、案例、队伍、看板、问答反馈、资源库、首页聚合、用户中心、工具内容、OSS、限流 |

**重写策略**：既有四个模块保留为"写法模板"。每重写一个模块前，先读对应旧实现 → 自己写 → 验收通过 → 替换旧代码。
理由：旧实现沉淀了本项目全部约定（构造器注入、`@RequireRole`、`Result` 包装、时间线写入、MapStruct 转换），照抄结构能少踩坑。

---

## 一、技术栈定稿

| 层 | 选型 | 版本 | 说明 |
|---|---|---|---|
| 框架 | Spring Boot | 2.7.18 | 不升级（2.7.x 与 Knife4j 3.0.3 组合已验证；已在 pom 覆盖 Tomcat/Jackson/SnakeYAML/Netty CVE） |
| JDK | Java | 21 | 与本机一致 |
| 构建 | Maven | 3.8+ | 注解处理器顺序：Lombok → MapStruct（不可颠倒） |
| 持久层 | MyBatis-Plus | 3.5.5 | `BaseMapper` 免写单表 SQL；分页插件已注册；逻辑删除已开（`deleted`） |
| 数据库 | MySQL | 8.0 | 库 `course_pack`，字符集 `utf8mb4` |
| 缓存/限流 | Redis | — | Lettuce；用于 AI 限流（10 次/人/分钟）与热点缓存 |
| 接口文档 | Knife4j | 3.0.3 | `/doc.html`，契约先行 |
| 对象映射 | MapStruct | 1.5.5.Final | Entity → VO，禁止手写循环 set |
| 工具库 | Hutool | 5.8.26 | `BCrypt`（密码）、日期、集合 |
| 令牌 | jjwt | 0.12.5 | HS256，密钥 ≥32 字节 |
| 文件存储 | 阿里云 OSS | 3.17.4 | 服务器不落盘，只存 URL |
| 连接池 | HikariCP | 内置 | 已显式配置（maximum-pool-size 20） |
| 测试 | JUnit 5 + MockMvc | 内置 | `spring-boot-starter-test` |

**需新建的基础设施**（M0 里程碑）：

| 类/文件 | 作用 |
|---|---|
| `config/RedisConfig.java` | `RedisTemplate` 使用 Jackson 序列化（默认 JDK 序列化不可读） |
| `config/MyMetaObjectHandler.java` | `created_at` / `updated_at` 自动填充，省掉每个 Service 手写时间 |
| `config/OssConfig.java` + `service/OssService.java` | OSS 客户端与签名 URL（5 分钟有效） |
| `resources/error-code.csv` | 错误码字典导出，前端差异化 Toast 依据（Phase 1 遗留项） |
| `application-dev.yml` / `application-prod.yml` | 拆分环境；DB 密码、JWT secret、OSS Key 全部走环境变量（现状为明文硬编码，属安全债） |

---

## 二、核心模块拆解与依赖顺序

| 序 | 模块 | 关键产出 | 依赖 | 规模 |
|---|---|---|---|---|
| M0 | 基础设施补完 | RedisConfig / 自动填充 / 配置外置 / error-code.csv / 测试骨架 | — | S |
| M1 | 课程模块 | `course` `course_material` `course_step` `course_favorite` + 管理端 CRUD + 小程序读取 | M0 | L |
| M2 | 培训模块 | `training` `training_progress` + 进度上报 + 管理端必修配置 | M1 | M |
| M3 | 案例 + 审核扩展 | `case_library` + 教师提交/管理端审核/精选 + 审核队列按领域过滤 | M1 | M |
| M4 | 队伍 + 看板 | `team` + `users.team_id` + 聚合统计接口 | M1-M3 | M |
| M5 | 问答反馈工单 | `feedback_ticket` + 提问/我的问题/管理端回复 | M0 | S |
| M6 | 资源库 + OSS | `resource` `download_record` + 上传签名/下载计数 | M0 | M |
| M7 | 工具内容 + 备课 | `qa` `family_topic` `activity` + `/api/prepare/generate` | M0 | M |
| M8 | 首页聚合 + 用户中心 | `/api/home` + profile/favorites/downloads/cases/questions | M1-M7 | M |
| M9 | 加固与联调 | 限流、缓存、测试覆盖、7 账号矩阵回归、双端联调 | 全部 | M |
| M10 | 可选攻坚 | AI 对话 SSE + Redis 限流、微信登录、通知 | M9 | L |

> 顺序设计理由：课程是"数据母体"（培训、案例、资源、收藏、下载都挂在课程/用户上），必须先做；看板是最上层聚合，必须最后做；AI 与微信登录属技术攻坚，且非演示主链路，放最后。

---

## 三、接口设计

### 3.1 全局约定

- 前缀：统一 `/api`；按角色分区：`/api/auth`（公共）、`/api/admin/**`（ADMIN）、`/api/audit/**`（AUDITOR）、其余为登录用户接口
- 响应：一律 `Result<T>{code,message,data}`；分页一律 `PageResult<T>{list,total,pageNum,pageSize}`
- 失败：抛 `BusinessException(ErrorCode.XXX)`，由 `GlobalExceptionHandler` 统一转 `Result`，Controller 内不写 try-catch
- 文档：每个 Controller 加 `@Api(tags=...)`，每个方法加 `@ApiOperation`，DTO 加 `@ApiModelProperty`（Knife4j 即契约）

### 3.2 两处必须先和解的契约漂移（在 M0 内完成）

| 漂移点 | 实现现状 | 文档现状 | 建议裁决 |
|---|---|---|---|
| 登录路径 | `/api/auth/login` | `/api/admin/auth/login` | **以实现为准**：两端共用一套账号，路径保持 `/api/auth/login`；更新 `接口规范.md` |
| 分页参数名 | `page` / `size`（教师管理接口） | `pageNum` / `pageSize` | **以文档为准**：统一为 `pageNum`/`pageSize`，改教师管理接口，同步前端 api 层 |

### 3.3 端点清单（按模块）

**认证（两端共用）**

| 方法 | 路径 | 角色 | 状态 |
|---|---|---|---|
| POST | `/api/auth/login` | 公开 | 已实现 |
| GET | `/api/auth/me` | 登录 | 已实现 |

**小程序端（教师）**

| 方法 | 路径 | 说明 | 里程碑 |
|---|---|---|---|
| GET | `/api/home` | 首页七段式聚合数据 | M8 |
| GET | `/api/user/profile` | 我的页用户卡 | M8 |
| GET | `/api/user/favorites` `/downloads` `/cases` `/questions` | 我的内容四入口 | M8 |
| GET | `/api/courses` | 列表（keyword/theme/grade/version/pageNum/pageSize） | M1 |
| GET | `/api/courses/{id}` | 详情（含步骤、目标、价值观、关联敏感问题） | M1 |
| GET | `/api/courses/{id}/materials` | 课程材料（教案/PPT/教具/视频） | M1 |
| POST | `/api/courses/{id}/favorite` | 收藏/取消 | M1 |
| GET | `/api/trainings` `/api/trainings/{id}` | 培训列表/详情 | M2 |
| POST | `/api/trainings/{id}/progress` | 进度上报（驱动"生长线"状态） | M2 |
| GET | `/api/qa` `/api/qa/{id}` | 敏感问题应答 | M7 |
| GET | `/api/tools/family` `/api/tools/activities` | 家校话术 / 活动库 | M7 |
| POST | `/api/prepare/generate` | 备课清单生成 | M7 |
| GET/POST | `/api/tools/prepare/draft` | 草稿读写（双端互通） | 已实现 |
| POST | `/api/feedback` | 教师提问（工单） | M5 |

**管理端（ADMIN）**

| 方法 | 路径 | 说明 | 里程碑 |
|---|---|---|---|
| GET | `/api/admin/dashboard` | 看板聚合（统计/趋势/主题分布/队伍排行/待办/动态） | M4 |
| GET/POST/PUT/DELETE | `/api/admin/courses[/{id}]` | 课程 CRUD + 上下架 | M1 |
| GET/PUT | `/api/admin/trainings[/{id}]` | 培训管理（必修/选修、时长） | M2 |
| GET/POST | `/api/admin/cases` `/{id}/feature` | 案例列表 + 精选标记 | M3 |
| GET | `/api/admin/teams[/{id}]` | 队伍列表/详情（含教师名单、培训进度） | M4 |
| GET | `/api/admin/teachers` + 操作 | 教师管理（已实现，需改分页参数名） | 已实现 |
| GET/POST | `/api/admin/feedback[/{id}/reply]` | 工单列表 + 回复 | M5 |
| GET/POST | `/api/admin/resources` + 上传签名 | 资源库 | M6 |
| GET | `/api/admin/seed-teachers` | 种子教师荣誉墙 | M3 |

**审核员（AUDITOR）**

| 方法 | 路径 | 说明 | 状态 |
|---|---|---|---|
| GET | `/api/audit/queue` | 按 `audit_field` 过滤的待审队列 | 已实现 |
| POST | `/api/audit/{id}/approve` `/{id}/reject` | 审核操作 | 已实现 |

---

## 四、数据库表规划

### 4.1 通用约定

- 主键：`id BIGINT AUTO_INCREMENT`；外键列建索引；状态/类型列建索引
- 审计列：`created_at` `updated_at DATETIME`（由 `MyMetaObjectHandler` 自动填充）
- 逻辑删除：`deleted TINYINT DEFAULT 0`（MyBatis-Plus 全局已配置，查询自动过滤）
- 字符集 `utf8mb4`；避免物理外键（用应用层保证引用完整性）

### 4.2 已有表（4）

`users`（账号/角色/状态/is_seed/audit_field）、`lesson_draft`（备课草稿）、`timeline_log`（系统动态）、`course_audit`（课程审核）

### 4.3 新增表清单

| 表 | 用途 | 关键字段要点 |
|---|---|---|
| `course` | 课程主表 | title / theme(5 主题枚举) / grade / duration / version / summary / cover_color(品牌色) / status(0下架1上架) / objectives / values / sensitive_qa_ids |
| `course_material` | 课程材料 | course_id / type(PLAN/SLIDES/TOOLKIT/VIDEO) / name / url / size / sort |
| `course_step` | 课程环节 | course_id / idx / title / desc / duration |
| `course_favorite` | 收藏 | user_id + course_id 唯一索引（幂等收藏） |
| `training` | 培训课程 | title / type(REQUIRED/ELECTIVE) / duration / video_url / introduction / outline / sort |
| `training_progress` | 学习进度 | user_id + training_id 唯一索引 / progress / status(DONE/CURRENT/LOCK) |
| `case_library` | 教学案例 | title / author_id / team_id / content / audit_field / status(PENDING/APPROVED/FEATURED/REJECTED) |
| `team` | 支教队伍 | name / org / area / leader / 统计列（教师数可聚合） |
| `feedback_ticket` | 问答工单 | user_id / title / content / status(PENDING/REPLIED) / reply / replied_by / replied_at |
| `resource` | 资源库 | type(教案/PPT/评估量表/绘本清单) / name / url / size / download_count / uploader_id |
| `download_record` | 下载记录 | user_id + resource_id / created_at（驱动"我的下载"） |
| `qa` | 敏感问题 | question / category / scene / response / tips(JSON) / sort |
| `family_topic` | 家校话术 | title / scene / script / sort |
| `activity` | 课堂活动 | title / type / content / materials / duration |

> `users` 表需扩展 `team_id`（队伍归属），否则队伍模块无法落数据。

---

## 五、认证与授权实现方案

### 5.1 链路（既有模式，重写时保持一致）

```
请求 → AuthInterceptor.preHandle
       ├─ 取 Authorization: Bearer <token>
       ├─ JwtUtil.parse() 验签+过期 → 失败抛 TOKEN_EXPIRED(20003)
       ├─ 查 users 表 → 无此人抛 NOT_LOGIN(10003)
       ├─ 状态兜底：DISABLED/REJECTED → USER_DISABLED(20005)
       ├─ 写入 AuthContext（ThreadLocal：id/username/name/role/status/isSeed/auditField）
       └─ @RequireRole 校验（方法注解优先，其次类注解）→ 不符抛 NO_PERMISSION(10004)
   请求结束 → AuthContext.clear()（防线程复用串号）
```

### 5.2 角色与状态矩阵

| 角色 | 可访问 | 数据过滤 |
|---|---|---|
| ADMIN | `/api/admin/**` 全量 | — |
| AUDITOR | `/api/audit/**` | 按 `users.audit_field` 过滤队列（PSY 心理与安全 / EDU 教育与课程） |
| TEACHER | 小程序端 + `/api/tools/**` | 仅本人数据（`AuthContext.userId()`） |

| 状态 | 登录 | 已登录请求 |
|---|---|---|
| ACTIVE | 放行 | 放行 |
| PENDING | 拒绝（20006） | 拒绝 |
| REJECTED | 拒绝（20007） | 拒绝 |
| DISABLED | 拒绝（20005） | 拒绝（即便 token 未过期） |

### 5.3 新接口如何加锁

- 类级：`@RequireRole(UserRole.ADMIN)` 打在 Controller 上（同类统一权限时）
- 方法级：打在方法上，优先级高于类级
- 原则：**前端隐藏不算锁，后端拒绝才是锁**；新增接口必须显式声明角色，不要依赖默认放行

### 5.4 待加固项

| 项 | 说明 |
|---|---|
| 密钥外置 | JWT secret、DB 密码、OSS Key 走环境变量（当前明文在 yml 里） |
| 限流 | Redis 计数器：AI 会话 10 次/人/分钟（`ErrorCode.AI_RATE_LIMIT` 50001 已备好） |
| 审计日志 | 管理端写操作统一走 `TimelineService`，保证"系统最新动态"可追溯 |
| 登出 | 当前无状态 JWT 无服务端登出；如需强制失效可加 Redis 黑名单（演示阶段可不做） |

---

## 六、里程碑与排期

> 工时按"单人开发、含自测"粗估，仅作排期参考；实际按每天可投入时长换算。

| 里程碑 | 内容 | 验收标准 | 粗估 |
|---|---|---|---|
| M0 | 基础设施补完 + 契约漂移和解 | RedisConfig 可取写；created_at 自动填充生效；`mvn test` 通过；错误码 CSV 导出 | 3-4h |
| M1 | 课程模块（5 表 + 管理端 CRUD + 小程序读取） | Knife4j 新增课程 → 小程序列表可见 → 收藏幂等；上下架状态生效 | 8-10h |
| M2 | 培训模块（2 表 + 进度上报） | 上报进度后"生长线"三态正确（完成/进行中/未解锁） | 5-6h |
| M3 | 案例 + 审核扩展 | 教师提交 → 对应领域审核员可见 → 通过/驳回/精选状态流转正确 | 6-8h |
| M4 | 队伍 + 看板聚合 | 看板数字与库内数据一致；队伍详情含教师名单与进度 | 6-8h |
| M5 | 问答反馈工单 | 提问 → 管理端待回复 → 回复后教师端可见 | 4-5h |
| M6 | 资源库 + OSS | 上传走 OSS（服务器不落盘）；下载计数 +1；我的下载有记录 | 6-8h |
| M7 | 工具内容 + 备课生成 | QA/家校/活动库三页有数据；备课生成返回四段结构 | 5-6h |
| M8 | 首页聚合 + 用户中心 | 首页七段式全部由 `/api/home` 驱动；四个"我的"入口有数据 | 5-6h |
| M9 | 加固与联调 | 限流生效；7 账号矩阵回归通过；双端关 mock 联调无报错 | 6-8h |
| M10 | 可选攻坚（AI SSE / 微信登录） | `wx.request enableChunked` 能收到流式分片 | 10h+ |

---

## 七、测试策略

| 层级 | 工具 | 覆盖内容 |
|---|---|---|
| 单元测试 | JUnit 5 + Mockito | Service 逻辑：登录状态机（4 状态分支）、权限判定、状态流转 |
| 集成测试 | MockMvc + `@SpringBootTest` + `@Transactional` | Controller 全链路：参数校验、`Result` 包装、异常转码 |
| 鉴权矩阵 | MockMvc | 未登录→10003、角色不符→10004、状态拦截→20005/20007（**每个新 Controller 必测**） |
| 契约验证 | Knife4j 手测 + curl 脚本 | 每个里程碑产出一份 curl 清单，POST 操作后查库确认落库 |
| 联调回归 | 前端关 mock | 7 账号（admin01/auditor01-02/teacher01-04）矩阵走一遍主链路 |

质量门槛：`mvn clean test` 全绿；IDEA 无警告；每个里程碑结束执行 `mvn clean package`。

---

## 八、与前端集成点

### 8.1 管理端（course-pack-admin）

- `src/api/request.ts`：`baseURL: '/api'`，请求头注入 `Authorization: Bearer <localStorage.adminToken>`，响应解包 `{code,message,data}`，`code !== 0` 弹错并 reject
- **待和解**：前端把 `code === 401` 当作登录失效（清 token 跳 `/login`），而后端错误码是 10003/20003 → 二选一：后端鉴权失败改为返回 HTTP 401，或前端判定改为 10003/20003（建议前者，语义更标准）
- 分页参数：前端按 `pageNum`/`pageSize` 传参（与 3.2 裁决一致）
- 登录兜底：后端不可达时 auth store 回退本地 mock 账号；后端在线则以真实接口为准
- mock 开关：`VITE_USE_MOCK=true|false`，后端接口就绪后按模块逐个关闭

### 8.2 小程序端（course-pack-client）

- `src/api/` 下 8 个模块（home/course/training/qa/tool/prepare/user/feedback）已按接口规范封装，后端就绪后逐个切真实请求
- 按项目约束：**登录与草稿同步走真实接口**（已就绪），其余保持 mock 直至对应里程碑完成
- 草稿双端互通：同一 API `/api/tools/prepare/draft`（Web 备课助手 ⇄ 小程序续写）

### 8.3 数据一致性约定（三方同构）

| 数据 | 三处必须一致 |
|---|---|
| 演示账号 | `DataSeeder` ⇄ 管理端 `mock/auth.ts` ⇄ README 账号表（7 账号，密码 123456） |
| 系统动态 | `timeline_log` ⇄ 管理端"最新动态" ⇄ 小程序首页"平台动态"（同源同序） |
| 主题色 | 后端 `course.cover_color` 只能取品牌五色（#4F8F83/#D99A62/#8FB7C5/#5D9B78/#A8B5A0） |
| 错误码 | 后端 `ErrorCode` ⇄ `error-code.csv` ⇄ 前端提示文案 |

---

## 九、下一步（本周可执行）

1. 前端收尾 → 单次提交推送 GitHub（见对话中的推送方案）
2. M0：基础设施补完 + 两处契约漂移和解（先做这个，后续所有模块都受益）
3. M1：课程模块，按"建表 → Entity → Mapper → Service → Controller → Knife4j 自测"六步走