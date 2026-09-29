# Phase 1 实施计划：骨架代码与统一规范

> 阶段定位：搭建项目基础设施，建立代码规范，为后续业务开发提供统一标准

---

## 一、阶段目标与核心任务

### 1.1 阶段目标

| 目标类型 | 具体目标 | 衡量标准 |
|---------|---------|---------|
| 架构目标 | 建立清晰的项目分层结构 | Controller / Service / Mapper / Entity / DTO 包结构完整 |
| 规范目标 | 统一响应格式、错误码、异常处理 | 所有接口返回 `Result<T>` 格式，错误码覆盖 7 大模块 |
| 开发效率 | API 文档自动化，前后端契约先行 | Knife4j 可访问，Swagger 注解覆盖所有接口 |
| 代码质量 | 编译通过，无警告，符合阿里巴巴 Java 规范 | IDEA 无黄色警告，`mvn compile` 成功 |

### 1.2 核心任务清单

```
Phase 1 任务树
├── 1.1 Result<T> 统一响应体
│   ├── 定义泛型响应结构 (code, message, data)
│   ├── 提供成功/失败静态工厂方法
│   └── 编写单元测试验证序列化
│
├── 1.2 ErrorCode 错误码枚举
│   ├── 定义 7 个错误码区间 (10000-90099)
│   ├── 创建 ErrorCode 枚举类
│   └── 编写错误码字典 CSV 供前端使用
│
├── 1.3 BusinessException 业务异常
│   ├── 继承 RuntimeException
│   ├── 持有 ErrorCode 字段
│   └── 提供多个构造器重载
│
├── 1.4 GlobalExceptionHandler 全局异常处理
│   ├── 捕获 BusinessException → 返回业务错误码
│   ├── 捕获 MethodArgumentNotValidException → 参数校验错误
│   ├── 捕获 Exception → 兜底 500 错误
│   └── 记录异常日志 (SLF4J)
│
├── 1.5 Knife4j API 文档配置
│   ├── 创建 Knife4jConfig.java
│   ├── 配置扫描 Controller 包路径
│   ├── 设置文档标题/描述/版本
│   └── 测试 /doc.html 可访问
│
├── 1.6 MyBatisPlusConfig 分页插件
│   ├── 注册 PaginationInnerInterceptor
│   ├── 配置数据库类型 MySQL
│   └── 创建 PageResult 分页响应体
│
└── 1.7 WebMvcConfig 跨域配置
    ├── 配置 CORS 允许来源
    ├── 注册 JWT 拦截器 (占位，Phase 2 实现)
    └── 配置静态资源映射
```

---

## 二、关键时间节点安排

### 2.1 时间规划（单人开发，按实际可投入时间估算）

假设每天可投入 **3-4 小时** 开发时间：

| 时间段 | 任务 | 预计工时 | 累计 |
|-------|------|---------|------|
| Day 1 上午 | 项目包结构调整 + Result/ErrorCode/BusinessException | 2h | 2h |
| Day 1 下午 | GlobalExceptionHandler + Knife4j 配置 | 2h | 4h |
| Day 2 上午 | MyBatisPlusConfig + PageResult | 1.5h | 5.5h |
| Day 2 下午 | WebMvcConfig CORS + 测试验证 | 1.5h | 7h |
| Day 3 | 缓冲时间（调试问题 + 整理文档） | 2h | 9h |

**预计总工时**：9 小时  
**建议完成时间**：2-3 天

### 2.2 里程碑节点

| 节点 | 完成标志 | 验收方式 |
|------|---------|---------|
| M1 | 代码框架搭建完成 | IDEA 项目结构清晰，包名正确 |
| M2 | 统一响应体可用 | 启动项目，返回 `{"code": 0, "message": "success", "data": null}` |
| M3 | API 文档可访问 | 浏览器打开 `http://localhost:8080/doc.html` 显示 Swagger UI |
| M4 | Phase 1 完成 | 所有任务通过单元测试，可进入 Phase 2 |

---

## 三、参与人员及职责分工

### 3.1 团队组成（大创项目标准）

| 角色 | 人数 | 职责 | 时间占比 |
|------|-----|------|---------|
| **全栈开发者（你）** | 1 | 后端编码、架构设计、文档编写、测试 | 90% |
| **指导老师** | 1 | 技术方向指导、阶段性验收 | 5% |
| **队友（如有）** | 0-1 | 前端小程序开发（Phase 2 并行） | 5% |

### 3.2 具体职责分解

```
┌─────────────────────────────────────────────────────────────┐
│                    你的职责（Phase 1）                        │
├─────────────────────────────────────────────────────────────┤
│  代码开发                                                     │
│  ├── 编写 Result/ErrorCode/BusinessException                 │
│  ├── 编写 GlobalExceptionHandler                            │
│  ├── 配置 Knife4j/MyBatis-Plus/CORS                         │
│  └── 编写单元测试                                            │
│                                                              │
│  文档输出                                                     │
│  ├── 更新架构文档（补充实际实现细节）                          │
│  └── 记录踩坑笔记（遇到的问题及解决方案）                      │
│                                                              │
│  质量保障                                                     │
│  ├── 代码自审（符合阿里巴巴规范）                              │
│  └── 提交 Git 前 `mvn clean compile` 验证                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 四、预期交付成果

### 4.1 代码产出

| 类/文件 | 路径 | 功能 |
|--------|------|------|
| `Result.java` | `common/Result.java` | 统一响应体 |
| `PageResult.java` | `common/PageResult.java` | 分页响应体 |
| `ErrorCode.java` | `common/ErrorCode.java` | 错误码枚举 |
| `BusinessException.java` | `common/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler.java` | `common/GlobalExceptionHandler.java` | 全局异常处理 |
| `Knife4jConfig.java` | `config/Knife4jConfig.java` | API 文档配置 |
| `MyBatisPlusConfig.java` | `config/MyBatisPlusConfig.java` | 分页插件配置 |
| `WebMvcConfig.java` | `config/WebMvcConfig.java` | CORS + 拦截器注册 |

### 4.2 文档产出

| 文档 | 内容 |
|------|------|
| `error-code.csv` | 错误码字典（前后端共享） |
| Phase 1 完成记录 | 更新到 `project_memory.md` |

### 4.3 可验证成果

```bash
# 1. 项目可启动
mvn spring-boot:run
# 访问 http://localhost:8080/doc.html → 显示 API 文档

# 2. 测试统一响应
curl http://localhost:8080/api/public/test
# 返回：{"code": 0, "message": "success", "data": "Phase 1 完成"}

# 3. 测试异常处理
curl http://localhost:8080/api/public/error
# 返回：{"code": 10001, "message": "参数校验失败", "data": null}
```

---

## 五、技术实现路径与方法

### 5.1 Result<T> 统一响应体

**设计思路**：

```java
/**
 * 统一响应体，所有 Controller 返回值都包装为此类
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;      // 状态码：0=成功，其他=失败
    private String message;    // 提示信息
    private T data;            // 业务数据

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    // 成功响应（有数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    // 失败响应（使用错误码）
    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    // 失败响应（自定义消息）
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
```

**为什么用泛型**：
- 不同接口返回不同类型数据（UserVO、CourseVO 等）
- 编译期类型安全，避免强转
- 前端统一解析 `response.data`

---

### 5.2 ErrorCode 错误码枚举

**设计思路**：

```java
/**
 * 统一错误码定义
 * 规则：按模块分区，便于定位问题
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 通用错误 10000-10099
    SUCCESS(0, "成功"),
    PARAM_INVALID(10001, "参数校验失败"),
    RESOURCE_NOT_FOUND(10002, "资源不存在"),
    NOT_LOGIN(10003, "请先登录"),
    NO_PERMISSION(10004, "无操作权限"),

    // 用户/登录错误 20000-20099
    WX_CODE_INVALID(20001, "微信授权码无效"),
    WX_LOGIN_FAILED(20002, "微信登录失败"),
    TOKEN_EXPIRED(20003, "登录已过期"),
    REFRESH_TOKEN_INVALID(20004, "请重新登录"),

    // 课程包错误 30000-30099
    COURSE_NOT_FOUND(30001, "课程包不存在"),
    COURSE_OFFLINE(30002, "课程包已下架"),

    // 视频错误 40000-40099
    VIDEO_NOT_FOUND(40001, "视频不存在"),

    // AI 对话错误 50000-50099
    AI_RATE_LIMIT(50001, "提问太频繁，请稍后再试"),
    AI_CALL_FAILED(50002, "AI 服务开小差了"),

    // 社区/案例错误 60000-60099
    POST_NOT_FOUND(60001, "帖子不存在"),

    // 系统错误 90000-90099
    SYSTEM_ERROR(90001, "系统繁忙，请稍后重试"),
    OSS_UPLOAD_FAILED(90002, "文件上传失败");

    private final Integer code;
    private final String message;
}
```

**为什么用枚举**：
- 编译期校验，避免魔法数字
- IDE 自动补全，降低出错率
- 便于全局搜索和维护

---

### 5.3 GlobalExceptionHandler 全局异常处理

**设计思路**：

```java
/**
 * 全局异常处理器
 * 捕获所有未处理异常，统一返回 Result 格式
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getErrorCode());
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    // 兜底异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }
}
```

**为什么用 @RestControllerAdvice**：
- AOP 思想，不用每个 Controller 写 try-catch
- 统一异常处理逻辑，代码简洁
- 避免敏感信息泄露（生产环境不返回堆栈）

---

### 5.4 Knife4j 配置

**设计思路**：

```java
/**
 * Knife4j API 文档配置
 * 访问地址: http://localhost:8080/doc.html
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.coursepack.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("课程包资源站 API 文档")
                .description("支教老师培训平台后端接口")
                .version("v1.0.0")
                .contact(new Contact("C2006L", "https://gitee.com/C2006L", ""))
                .build();
    }
}
```

**为什么用 Knife4j 而非原生 Swagger**：
- 中文 UI，更友好
- 支持离线文档导出
- 支持接口排序、搜索

---

### 5.5 MyBatisPlusConfig 分页插件

**设计思路**：

```java
/**
 * MyBatis-Plus 配置
 */
@Configuration
@MapperScan("com.coursepack.mapper")
public class MyBatisPlusConfig {

    /**
     * 分页插件
     * 必须配置，否则分页查询不生效
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

---

### 5.6 WebMvcConfig CORS 配置

**设计思路**：

```java
/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * CORS 跨域配置
     * 允许小程序和本地开发前端访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 六、质量标准与验收条件

### 6.1 代码质量标准

| 检查项 | 标准 | 工具 |
|-------|------|------|
| 编译通过 | `mvn clean compile` 无错误 | Maven |
| 无警告 | IDEA 无黄色警告（安全警告除外） | IDEA |
| 代码规范 | 符合阿里巴巴 Java 规范 | IDEA 插件 |
| 单元测试 | 核心类测试覆盖率 > 80% | JUnit |

### 6.2 功能验收条件

| 功能 | 验收方式 | 通过标准 |
|------|---------|---------|
| 统一响应体 | 启动项目，调用任意接口 | 返回 `{"code": x, "message": "xxx", "data": xxx}` |
| 错误码体系 | 抛出 BusinessException | 返回对应错误码和消息 |
| API 文档 | 浏览器访问 `/doc.html` | 显示接口列表，可展开调试 |
| 分页插件 | 调用分页查询接口 | 返回 `PageResult` 格式 |
| CORS | 小程序/前端发起请求 | 不报跨域错误 |

### 6.3 验收检查清单

```markdown
## Phase 1 验收清单

### 代码结构
- [ ] 包名已改为 `com.coursepack`
- [ ] 目录结构符合规范（config/common/controller/service/mapper/entity/dto）

### 核心类
- [ ] Result.java 编写完成，含 success/fail 方法
- [ ] ErrorCode.java 定义至少 15 个错误码
- [ ] BusinessException.java 继承 RuntimeException
- [ ] GlobalExceptionHandler.java 捕获 3 种异常类型

### 配置类
- [ ] Knife4jConfig.java 配置完成
- [ ] MyBatisPlusConfig.java 配置分页插件
- [ ] WebMvcConfig.java 配置 CORS

### 测试验证
- [ ] 项目可启动（无报错）
- [ ] /doc.html 可访问
- [ ] 测试接口返回统一格式
- [ ] 抛异常返回正确错误码

### 文档
- [ ] error-code.csv 导出
- [ ] project_memory.md 更新
```

---

## 七、风险及应对策略

### 7.1 技术风险

| 风险 | 可能性 | 影响 | 应对策略 |
|------|-------|------|---------|
| Knife4j 版本与 Spring Boot 2.7 不兼容 | 中 | 高 | 使用 3.0.3 版本，已验证兼容 |
| MyBatis-Plus 分页不生效 | 中 | 中 | 确保注入 MybatisPlusInterceptor |
| CORS 配置不生效 | 低 | 高 | 检查是否配置了 `allowedOriginPatterns` |
| IDEA 编译报错 | 中 | 中 | 先 `mvn clean compile`，再 Reload Maven |

### 7.2 进度风险

| 风险 | 可能性 | 影响 | 应对策略 |
|------|-------|------|---------|
| 理解泛型/枚举有困难 | 中 | 中 | 查阅代码示例，先模仿后理解 |
| 配置类写错导致启动失败 | 中 | 高 | 每写完一个配置就启动验证 |
| 时间估计不足 | 低 | 中 | 预留 1 天缓冲时间 |

### 7.3 应对预案

```
如果遇到问题：
1. 先看控制台报错信息，搜索关键错误
2. 检查配置是否遗漏（@Configuration、@Bean）
3. 对比示例代码，逐步注释排查
4. 记录问题和解决方案到笔记
```

---

## 八、与其他项目阶段的衔接关系

### 8.1 Phase 1 在整体架构中的位置

```
Phase 0: 环境准备（MySQL/Redis/Maven）
    ↓
Phase 1: 骨架代码 ←── 你现在在这里
    ↓
Phase 2: 鉴权链路（JWT + 微信登录）
    ↓
Phase 3: 核心业务（课程包 + OSS）
    ↓
Phase 4-7: 其他模块
```

### 8.2 对后续阶段的影响

| Phase 1 成果 | 后续阶段依赖 |
|-------------|-------------|
| `Result<T>` | 所有 Controller 返回值都基于此 |
| `ErrorCode` | Phase 2 鉴权失败返回对应错误码 |
| `GlobalExceptionHandler` | 捕获所有阶段的异常 |
| `Knife4j` | Phase 2-7 所有接口都需要 Swagger 注解 |
| `MyBatisPlusConfig` | Phase 2-7 所有分页查询都依赖此配置 |
| `WebMvcConfig` | Phase 2 需要在此注册 JWT 拦截器 |

### 8.3 Phase 1 完成后的状态

```
完成 Phase 1 后，你将拥有：

✓ 一个可启动的 Spring Boot 项目
✓ 统一的响应格式和错误码体系
✓ 可访问的 API 文档页面
✓ 分页查询能力
✓ 跨域访问支持

接下来进入 Phase 2：
→ 创建 WxLoginController
→ 编写 JwtTokenUtil
→ 实现 /api/wx/login 接口
→ 在 WebMvcConfig 注册拦截器
```

---

## 九、行动指令

### 立即执行的任务

1. **修正项目包名**：`com.example.coursepackserver` → `com.coursepack`
2. **创建包结构**：config / common / controller / service / mapper / entity / dto
3. **创建第一个类**：`Result.java`
4. **启动验证**：确保项目可以跑起来

### 需要你确认的信息

1. MySQL root 密码是什么？（或者用什么工具管理 MySQL）
2. Redis 安装了吗？（Windows 需要单独安装）
3. 有没有 Navicat 或其他数据库管理工具？

确认后我立即开始帮你搭建代码。