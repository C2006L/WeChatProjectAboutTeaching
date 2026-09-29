# 代码规范（大纲雏形）

## 后端（course-pack-server）

- 分层：`controller` → `service` → `mapper` → `entity/dto/vo`

- 统一返回：`Result<T>` / `PageResult<T>`

- 异常处理：抛 `BusinessException`，全局处理器兜底

- 错误码：登记到 `ErrorCode` + `error-code.csv`

- 待补充：命名约定、MapStruct 映射规范、参数校验（Validation）

## 前端（course-pack-client / admin）

- 语言：TypeScript（严格模式）

- 请求：统一封装 request（拦截器：注入 token、错误统一处理）

- 设计 Token：引用 `course-pack-docs/01-设计/tokens.md`，禁止硬编码色值

- 目录分层：`src/api` `src/views|pages` `src/components` `src/store` `src/utils`

- 待补充：组件规范、命名约定、接口对时字段约定

