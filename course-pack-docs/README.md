# 伴芽课程包资源站 · 共享文档库

> 产品 / 设计 / 接口 / 代码规范 一体文档，供 `course-pack-server`（后端）、`course-pack-client`（小程序端）、`course-pack-admin`（管理端）三方共同引用，避免重复与漂移。
> 平台定位：面向乡村支教教师的生命教育与安全成长课程赋能平台。

## Monorepo 目录结构

```
WeChatProject/
├── course-pack-server/        # 后端：Spring Boot 2.7.18 + JDK21（端口 8080）
├── course-pack-client/        # 伴芽小程序端：uni-app Vue3 + TS
├── course-pack-admin/         # 伴芽 Web 管理端：Vue3 + TS + Element Plus
└── course-pack-docs/          # 共享规范文档（本文档库）
```

## 文档目录

| 文件/目录 | 内容 | 状态 |
|---|---|---|
| [frontend-plan-v1.md](./frontend-plan-v1.md) | **前端工程规划 V1**：9 屏小程序 + Web 看板打样对齐、组件拆解、Mock 规范、实施顺序 | 已发布，待确认 |
| `00-产品/` | 产品需求概述、角色与核心流程 | V1 对齐 |
| `01-设计/` | 设计 Token（色彩/字体/间距）、原型图 | V1 打样对齐 |
| `02-接口/` | 接口规范、统一响应、鉴权约定 | V1 接口清单已补充 |
| `03-代码规范/` | 后端分层规范、前端工程规范 | 大纲雏形 |

## 即将建设

- [x] 完成前端工程规划 V1
- [x] 设计 Token 与 V1 打样图对齐
- [x] 产品需求与原型图 V1 对齐
- [x] 接口规范 V1 清单补充
- [ ] 按批次实施前端页面与组件
- [ ] 代码规范细化