# 课程包资源站 — 后端架构与实操指南 (v2)

> 本版本在 v1 基础上，根据"API 文档缺失、SSE 小程序兼容、视频防盗链、缓存穿透、连接池、对象映射、统一错误码"等评审意见进行了增补和优化。

***

## 一、项目概述

"课程包资源站"是一个面向支教老师的微信小程序 + Web 管理后台的双端项目。后端采用 Spring Boot 2.7.x，负责所有业务逻辑、数据持久化、文件存储代理、AI 对话代理。

**v2 核心升级点**：

* 引入 **Knife4j** 作为 API 文档中心，前后端契约先行

* 引入 **MapStruct** 做高性能对象映射 (Entity ↔ DTO/VO)

* 引入 **Hutool** 工具库减少重复造轮子

* 引入 **统一错误码体系**，前端可基于错误码做差异化 Toast

* 补充 **JWT Refresh Token** 静默续期机制

* 补充 **小程序 SSE 兼容方案** (`enableChunked` + `onChunkReceived`)

* 补充 **视频防盗链方案** (OSS 签名 URL / 阿里云 VOD PlayAuth)

* 补充 **Redis 缓存注解** 防穿透/击穿

* 补充 **HikariCP 连接池** 显式配置，应对 AI 长耗时调用

* 补充 **Nginx SSE 专用配置**（关闭 proxy\_buffering）

***

## 二、整体架构图

```
┌─────────────────────────┐     ┌──────────────────────────────┐
│   微信小程序端 (C端)      │     │   Vue3 Web 管理后台 (B端)       │
│   WXML + WXSS + JS      │     │   Vue3 + Element Plus + Axios │
│   + Vant Weapp          │     │                                │
│   + Flyio + Day.js      │     │                                │
│   + MobX (AI 状态)       │     │                                │
└───────────┬─────────────┘     └──────────────┬───────────────┘
            │  HTTPS (微信强制)                  │  HTTPS
            ▼                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Nginx (443 端口)                               │
│  - SSL 终止 (Let's Encrypt / 阿里云免费证书，TLS 1.2+)            │
│  - 反向代理 → Spring Boot (8080)                                  │
│  - 静态资源缓存 (OSS 回源)                                        │
│  - 限流保护                                                       │
│  - ★ /api/wx/ai/chat 单独配置 proxy_buffering off (SSE 必须)     │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│               Spring Boot 2.7.x (内嵌 Tomcat, 8080)              │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              表现层 (Controller) + Knife4j 文档            │   │
│  │  /api/wx/*       小程序接口 (JWT 鉴权 + 接口签名)           │   │
│  │  /api/admin/*    管理后台接口 (Session + 验证码登录)        │   │
│  │  /api/public/*   公共接口 (无需登录)                        │   │
│  │  /doc.html       Knife4j API 文档入口                       │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              业务层 (Service) + @Cacheable 缓存            │   │
│  │  CoursePackageService  TrainingVideoService               │   │
│  │  FaqService            AiChatService                      │   │
│  │  UserService           CommunityService                   │   │
│  │  CaseLibraryService    OssService                         │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │         持久层 (Mapper) + MapStruct 对象转换               │   │
│  │  MyBatis-Plus BaseMapper + 自定义 SQL                     │   │
│  └──────────────────────────┬───────────────────────────────┘   │
│                             │                                    │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              通用层 (common) + 统一错误码                   │   │
│  │  Result<T>  PageResult  BusinessException  ErrorCode       │   │
│  │  GlobalExceptionHandler  JwtTokenUtil  SignUtil            │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────┼───────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
   │   MySQL 8.0  │   │    Redis     │   │  阿里云 OSS  │
   │  业务数据     │   │  AI上下文     │   │  + VOD 视频  │
   │  HikariCP    │   │  Session     │   │  签名URL     │
   │  显式配置     │   │  缓存/限流    │   │  防盗链      │
   └──────────────┘   └──────────────┘   └──────────────┘
```

***

## 三、项目结构 (v2 推荐)

```
course-pack-server/
├── pom.xml
├── src/main/java/com/coursepack/
│   ├── CoursePackApplication.java
│   │
│   ├── config/                                  # 配置类
│   │   ├── WebMvcConfig.java                   # CORS、拦截器注册
│   │   ├── MyBatisPlusConfig.java              # 分页插件
│   │   ├── RedisConfig.java                    # Redis 序列化 + CacheManager
│   │   ├── OssConfig.java                      # 阿里云 OSS 配置
│   │   ├── AiConfig.java                       # DeepSeek/通义千问 配置
│   │   ├── HikariConfig.java                   # ★ 显式连接池配置
│   │   ├── Knife4jConfig.java                  # ★ API 文档配置
│   │   └── MapStructConfig.java                # ★ MapStruct 配置
│   │
│   ├── controller/                              # 接口层 (Swagger 注解)
│   │   ├── wx/                                 # 小程序端接口
│   │   │   ├── WxLoginController.java
│   │   │   ├── WxCourseController.java
│   │   │   ├── WxVideoController.java
│   │   │   ├── WxAiChatController.java
│   │   │   ├── WxFaqController.java
│   │   │   ├── WxCommunityController.java
│   │   │   ├── WxCaseController.java
│   │   │   └── WxUserController.java
│   │   │
│   │   ├── admin/                              # 管理后台接口
│   │   │   └── ...
│   │   │
│   │   └── common/
│   │       └── OssCallbackController.java
│   │
│   ├── service/                                 # 业务层 (接口)
│   ├── service/impl/                            # 业务层 (实现，含 @Cacheable)
│   ├── mapper/                                  # MyBatis-Plus Mapper
│   ├── entity/                                  # 数据库实体
│   │
│   ├── dto/
│   │   ├── request/
│   │   └── response/                           # VO
│   │
│   ├── converter/                               # ★ MapStruct 转换器
│   │   ├── CourseConverter.java
│   │   ├── VideoConverter.java
│   │   ├── UserConverter.java
│   │   └── CaseConverter.java
│   │
│   ├── security/                                # 安全相关
│   │   ├── JwtTokenUtil.java                   # JWT (含 Refresh Token)
│   │   ├── WxJwtInterceptor.java               # 小程序 JWT 拦截器
│   │   ├── AdminAuthInterceptor.java           # 后台 Session 拦截器
│   │   └── SignInterceptor.java                # ★ 接口签名校验拦截器
│   │
│   ├── common/                                  # 通用模块
│   │   ├── Result.java                         # 统一响应体
│   │   ├── PageResult.java                     # 分页响应
│   │   ├── BusinessException.java              # 业务异常
│   │   ├── ErrorCode.java                      # ★ 统一错误码枚举
│   │   └── GlobalExceptionHandler.java         # 全局异常处理
│   │
│   └── util/
│       ├── WeChatUtil.java
│       ├── OssUtil.java
│       └── SignUtil.java                       # ★ 接口签名工具
│
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-prod.yml
    ├── error-code.csv                           # ★ 错误码字典 (前后端共享)
    └── mapper/
```

***

## 四、数据库设计 (核心表)

> 表结构与 v1 一致，详见 v1 文档第四节。补充分类字段说明：
>
> * `t_user.role`: 0=普通教师, 1=种子教师
>
> * `t_course_package.type`: 0=精简版, 1=标准版
>
> * `t_course_resource.resource_type`: 0=教案, 1=PPT, 2=视频, 3=教具清单
>
> * `t_training_video.module`: 0=必修, 1=选修

***

## 五、核心接口设计

> 接口表与 v1 一致，所有接口在 Knife4j 文档中可在线调试。错误响应统一返回：
>
> ```json
> { "code": 10001, "message": "参数校验失败", "data": null }
> ```

***

## 六、关键技术点 (v2 重点更新)

### 6.1 微信登录 + JWT (含 Refresh Token 静默续期)

**v1 问题**：JWT 过期后用户会被踢回登录页，体验差。

**v2 方案**：双 Token 机制

* `accessToken`: 有效期 2 小时，放 Header 鉴权

* `refreshToken`: 有效期 30 天，存小程序本地 storage

* 小程序检测到 401 时，自动用 refreshToken 调 `/api/wx/refresh` 换新 accessToken，无感续期

```
小程序端                          Spring Boot                      微信服务端
   │── wx.login() ──────────────────────────────────────────────────>│
   │<── code ────────────────────────────────────────────────────────│
   │── POST /api/wx/login {code}──>│                                │
   │                                │── code2session(code) ─────────>│
   │                                │<── {openid, session_key} ──────│
   │<── {accessToken(2h),           │                                │
   │     refreshToken(30d)} ───────│                                │
   │                                │                                │
   │── 请求带 accessToken ─────────>│                                │
   │<── 401 (token 过期) ──────────│                                │
   │                                │                                │
   │── POST /api/wx/refresh         │                                │
   │   {refreshToken} ─────────────>│ 校验 refreshToken 有效性        │
   │<── 新 accessToken ────────────│                                │
   │                                │                                │
   │── 用新 token 重发原请求 ───────>│                                │
```

### 6.2 AI 对话 SSE (小程序兼容方案) ★ 重点

**v1 问题**：小程序原生不支持 `EventSource`，`wx.request` 是一次性返回。

**v2 解决方案**：

* 后端保持 SSE 接口不变 (`text/event-stream`)

* 前端使用 `wx.request` + `enableChunked: true` + `onChunkReceived` 手动解析

**前端伪代码**：

```javascript
// 小程序端模拟 SSE
const requestTask = wx.request({
  url: 'https://your-domain/api/wx/ai/chat',
  enableChunked: true,  // 关键：开启分块传输
  method: 'POST',
  header: { 'Authorization': 'Bearer ' + token },
  data: { message: '如何脱敏教学？' },
  success(res) { /* 完整回调 */ }
});

requestTask.onChunkReceived((res) => {
  // 手动解析 ArrayBuffer 为字符串，拆分 SSE 数据包
  // 数据格式: "data: {json}\n\n"
  let text = ab2str(res.data);
  let lines = text.split('\n');
  lines.forEach(line => {
    if (line.startsWith('data:')) {
      let payload = JSON.parse(line.substring(5).trim());
      // 追加到聊天界面 (打字机效果)
      this.appendChatMessage(payload.content);
    }
  });
});
```

**Nginx 必须配置 (否则 SSE 失效)**：

```nginx
location /api/wx/ai/chat {
    proxy_pass http://127.0.0.1:8080;
    proxy_buffering off;           # ★ 关键：关闭缓冲
    proxy_cache off;
    proxy_set_header Connection ''; # 清除 Connection 头
    proxy_http_version 1.1;
    chunked_transfer_encoding on;
    proxy_read_timeout 300s;        # AI 响应慢，超时调长
}
```

### 6.3 文件上传 (OSS 直传方案)

与 v1 一致，后端签名 + 客户端直传。补充：

* 小程序端需对接阿里云 OSS 上传 SDK，处理上传进度回调和断点续传

* 建议封装 `ossUpload.js` 工具类

### 6.4 视频播放 + 防盗链 ★ 新增

**v1 问题**：直接返回 OSS URL 容易被盗链下载。

**v2 方案**：

| 场景      | 方案                  | 说明                   |
| ------- | ------------------- | -------------------- |
| 普通培训视频  | OSS 签名 URL (5 分钟有效) | 简单，适合内部教学视频          |
| 敏感/付费视频 | 阿里云 VOD + PlayAuth  | URL 完全隐藏，小程序用 VID 播放 |

**本项目建议**：MVP 阶段先用 OSS 签名 URL，后续如有付费需求再迁移到 VOD。

### 6.5 鉴权方案 (v2 增强)

| 端    | 方案                               | 补充                                     |
| ---- | -------------------------------- | -------------------------------------- |
| 小程序  | JWT (accessToken + refreshToken) | ★ 增加静默续期                               |
| 管理后台 | Session + Cookie                 | 传统 session                             |
| 所有接口 | ★ 接口签名校验                         | 防止反编译抓包恶意调用 (timestamp + nonce + sign) |

### 6.6 Redis 缓存防穿透 ★ 新增

**v1 问题**：课程包列表高并发访问可能压垮数据库。

**v2 方案**：Service 层使用 Spring Cache 注解

```java
@Cacheable(value = "courses", key = "'list:' + #type + ':' + #grade", unless = "#result == null")
public List<CourseListVO> listCourses(Integer type, Integer grade) { ... }

@CacheEvict(value = "courses", allEntries = true)  // 后台编辑后清缓存
public void updateCourse(CourseUpdateRequest req) { ... }
```

* 缓存空值防穿透 (`unless = "#result == null"` 配合 `@Cacheable` 返回空集合)

* 使用 Redisson 做互斥锁防击穿 (可选)

### 6.7 HikariCP 连接池显式配置 ★ 新增

**v1 问题**：AI 调用大模型耗时长 (5-30 秒)，可能耗尽连接池。

**v2 配置** (application.yml)：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20          # 最大连接数
      minimum-idle: 5                # 最小空闲连接
      connection-timeout: 30000      # 连接超时 30s
      idle-timeout: 600000           # 空闲超时 10min
      max-lifetime: 1800000          # 连接最大生命周期 30min
      leak-detection-threshold: 60000 # 连接泄漏检测 60s
```

**注意**：AI 对话接口内部不持有数据库连接 (在调用大模型前释放)，避免连接占用。

***

## 七、实操步骤 (v2 调整)

### 第 1 步：环境准备 (同 v1)

补充：申请阿里云 OSS、微信小程序 AppID、DeepSeek/通义千问 API Key。

### 第 2 步：初始化 Spring Boot 项目 ★ 调整

* Spring Initializr 生成骨架

* **立刻引入 Knife4j**，先把 API 文档框架搭起来

* 配置 pom.xml (含 v2 新增依赖)

### 第 3 步：★ 先定义 API 契约 (新增步骤)

* 在 Controller 上写 Swagger 注解 (`@Api`, `@ApiOperation`, `@ApiModelProperty`)

* 定义 Request/Response DTO，即使方法体是空实现

* 前端 (或自己) 可以直接打开 `http://localhost:8080/doc.html` 看接口文档联调

### 第 4 步：搭建基础框架

* `Result<T>` + `ErrorCode` 枚举 + `GlobalExceptionHandler`

* `JwtTokenUtil` (含 refreshToken) + 拦截器

* `MyBatisPlusConfig` 分页插件

* `MapStruct` 配置 + 转换器

* `WebMvcConfig` CORS + 拦截器链 (JWT → Sign)

### 第 5 步：建表 & 生成 Entity/Mapper (同 v1)

### 第 6 步：按模块开发 (v2 推荐顺序)

1. **微信登录 + JWT + refreshToken** — 跑通双端连接，验证鉴权链路
2. **★ 课程包管理 (后台) + OSS 上传** — 核心业务价值，先有数据
3. **课程包浏览 (小程序) + 下载** — 配套 + 下载记录
4. **培训视频 + 签名 URL 防盗链** — 视频模块
5. **常见问题 FAQ** — 简单 CRUD，热身
6. **★ AI 智能问答 (SSE)** — 技术难点攻坚，最后做
7. **教学案例库** — CRUD
8. **种子教师交流区** — 帖子列表 + 发帖
9. **个人中心** — 下载记录 + 种子教师认证

### 第 7 步：部署 (同 v1，补充 Nginx SSE 配置)

* Nginx 必须对 `/api/wx/ai/chat` 单独配置 `proxy_buffering off`

***

## 八、pom.xml 核心依赖 (v2 完整版)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<properties>
    <java.version>11</java.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <knife4j.version>3.0.3</knife4j.version>
    <hutool.version>5.8.26</hutool.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <jjwt.version>0.12.5</jjwt.version>
    <aliyun-oss.version>3.17.4</aliyun-oss.version>
</properties>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- ★ Knife4j API 文档 (Swagger 增强 UI) -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-spring-boot-starter</artifactId>
        <version>${knife4j.version}</version>
    </dependency>

    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>${mybatis-plus.version}</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- ★ Hutool 工具包 (加密/日期/HTTP/JSON) -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>${hutool.version}</version>
    </dependency>

    <!-- ★ MapStruct 对象映射 -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
        <scope>provided</scope>
    </dependency>

    <!-- 阿里云 OSS -->
    <dependency>
        <groupId>com.aliyun.oss</groupId>
        <artifactId>aliyun-sdk-oss</artifactId>
        <version>${aliyun-oss.version}</version>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>${jjwt.version}</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>${jjwt.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>${jjwt.version}</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok (注意：MapStruct 注解处理器需在 Lombok 之后) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- ★ 热部署 (开发阶段) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- 测试 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>11</source>
                <target>11</target>
                <annotationProcessorPaths>
                    <!-- ★ Lombok 必须在 MapStruct 之前 -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

***

## 九、★ 统一错误码体系 (v2 新增)

`ErrorCode` 枚举定义所有业务错误码，前端基于此做差异化提示。

| 错误码区间       | 模块    | 示例                                                     |
| ----------- | ----- | ------------------------------------------------------ |
| 10000-10099 | 通用错误  | 10001 参数校验失败 / 10002 资源不存在 / 10003 未登录                 |
| 20000-20099 | 用户/登录 | 20001 微信 code 无效 / 20002 refreshToken 过期 / 20003 用户被禁用 |
| 30000-30099 | 课程包   | 30001 课程包已下架 / 30002 资源不存在                             |
| 40000-40099 | 视频模块  | 40001 视频未上架                                            |
| 50000-50099 | AI 对话 | 50001 限流 (每分钟超 10 次) / 50002 大模型调用失败                   |
| 60000-60099 | 社区/案例 | 60001 帖子不存在 / 60002 无发帖权限                              |
| 90000-90099 | 系统错误  | 90001 服务器内部错误 / 90002 OSS 上传失败                         |

**ErrorCode.java 示例**：

```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    PARAM_INVALID(10001, "参数校验失败"),
    NOT_LOGIN(10003, "请先登录"),
    WX_CODE_INVALID(20001, "微信 code 无效"),
    REFRESH_TOKEN_EXPIRED(20002, "请重新登录"),
    AI_RATE_LIMIT(50001, "提问太频繁，请稍后再试"),
    AI_CALL_FAILED(50002, "AI 服务开小差了"),
    SYSTEM_ERROR(90001, "系统繁忙");

    private final int code;
    private final String message;
}
```

***

## 十、★ 小程序前端技术栈推荐 (v2 新增)

| 类别      | 推荐组件                   | 作用                                               |
| ------- | ---------------------- | ------------------------------------------------ |
| UI 组件库  | **Vant Weapp**         | Uploader/Skeleton/Tab/Dialog，与 Element Plus 风格接近 |
| 状态管理    | **MobX-miniprogram**   | 复杂状态 (如 AI 对话) 用 MobX；简单状态用 globalData           |
| 网络请求库   | **Flyio** 或 MinRequest | 统一处理 JWT 注入、refreshToken 续期、全局错误、Loading         |
| 日期处理    | **Day.js**             | 2KB，替代 Moment.js，格式化时间                           |
| SSE 客户端 | **自封装 SSEClient.js**   | 基于 `wx.request enableChunked`                    |

**目录结构建议 (与后端模块对齐)**：

```
miniprogram/
├── pages/
│   ├── course/        # 对应 WxCourseController
│   ├── video/         # 对应 WxVideoController
│   ├── ai/            # 对应 WxAiChatController
│   ├── faq/           # 对应 WxFaqController
│   ├── community/     # 对应 WxCommunityController
│   ├── case/          # 对应 WxCaseController
│   └── user/          # 对应 WxUserController
├── utils/
│   ├── request.js     # Flyio 封装
│   ├── sse-client.js  # SSE 客户端
│   ├── auth.js        # JWT + refreshToken 管理
│   └── oss-upload.js  # OSS 直传
└── store/             # MobX store
```

***

## 十一、★ Nginx 关键配置 (v2 新增)

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate     /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    ssl_protocols       TLSv1.2 TLSv1.3;

    # 普通接口
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    # ★ AI 对话 SSE 接口 (单独配置)
    location /api/wx/ai/chat {
        proxy_pass http://127.0.0.1:8080;
        proxy_buffering off;            # 关键：关闭缓冲
        proxy_cache off;
        proxy_set_header Connection '';
        proxy_http_version 1.1;
        chunked_transfer_encoding on;
        proxy_read_timeout 300s;        # AI 响应慢，超时调长
    }

    # Knife4j 文档 (生产环境建议关闭或加 IP 白名单)
    location /doc.html {
        allow 1.2.3.4;                  # 你的办公 IP
        deny all;
        proxy_pass http://127.0.0.1:8080;
    }
}
```

***

## 十二、关键注意事项 (v2 汇总)

1. **微信小程序必须 HTTPS** — Nginx 必须配置 SSL 证书 (TLS 1.2+)
2. **文件绝不存本地** — 所有文件走 OSS，服务器只存 URL
3. **AI 对话用 SSE** — 后端 SSE + 前端 `enableChunked` 解析，Nginx 必须 `proxy_buffering off`
4. **接口防刷** — Redis 限流 (AI 每人每分钟 10 次) + 接口签名校验 (防反编译抓包)
5. **OSS 文件分类目录** — `{type}/{date}/{uuid}.{ext}` 组织
6. **敏感配置外置** — 数据库密码、API Key 用环境变量
7. **先做后台再做小程序** — 后台先造数据，小程序端才能调试
8. ★ **API 文档先行** — Knife4j 接口先定义，前后端契约一致
9. ★ **JWT 双 Token** — accessToken + refreshToken 静默续期
10. ★ **视频防盗链** — OSS 签名 URL (5 分钟有效)，敏感内容用 VOD
11. ★ **Redis 缓存** — `@Cacheable` 防穿透，后台编辑后 `@CacheEvict` 清缓存
12. ★ **连接池显式配置** — HikariCP `maximum-pool-size: 20`，AI 调用前释放 DB 连接
13. ★ **统一错误码** — `ErrorCode` 枚举，前端基于 code 做差异化 Toast

***

## 十三、可选的增强项 (非 MVP)

* ElasticSearch 全文搜索

* RocketMQ 视频转码回调

* Docker 容器化部署

* CI/CD (GitHub Actions)

* 阿里云 VOD 视频点播 (替代 OSS 直传视频)

* Redisson 分布式锁 (防缓存击穿)

* Nacos 配置中心 (多环境管理)

***

## 附录：v1 → v2 变更对照表

| 变更点       | v1        | v2                                         | 章节          |
| --------- | --------- | ------------------------------------------ | ----------- |
| API 文档    | 仅接口表      | **Knife4j 3.0.3** 在线文档                     | 八、依赖        |
| 对象映射      | 未明确       | **MapStruct 1.5.5.Final**                  | 三、结构 / 八、依赖 |
| 工具类       | 自写        | **Hutool 5.8.26**                          | 八、依赖        |
| 热部署       | 无         | **spring-boot-devtools**                   | 八、依赖        |
| 错误码       | 仅异常类      | **ErrorCode 枚举 + 区间规范**                    | 九、新增章节      |
| JWT       | 单 token   | **accessToken + refreshToken**             | 六、6.1       |
| SSE 前端    | 未提        | **enableChunked + onChunkReceived** 伪代码    | 六、6.2       |
| Nginx SSE | 未提        | **proxy\_buffering off** 专用配置              | 六、6.2 / 十一  |
| 视频防盗链     | 仅 URL     | **OSS 签名 URL / VOD PlayAuth**              | 六、6.4       |
| 缓存        | 未提        | **@Cacheable / @CacheEvict**               | 六、6.6       |
| 连接池       | 默认        | **HikariCP 显式配置**                          | 六、6.7       |
| 接口安全      | 仅限流       | **限流 + 签名校验**                              | 六、6.5       |
| 前端栈       | 仅 WXML/JS | **Vant + Flyio + MobX + Day.js**           | 十、新增章节      |
| 开发顺序      | 登录→课程→AI  | **先 Knife4j 契约 → 登录 → 课程包+OSS → ... → AI** | 七、第 3/6 步   |

