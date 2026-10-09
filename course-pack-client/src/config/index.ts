/**
 * 全局运行时配置
 * BASE_URL 按端条件编译：
 *   - 小程序/App：必须是已在微信后台配置的 HTTPS 合法域名（硬约束：TLS 1.2+，禁 HTTP）；
 *     开发期可在微信开发者工具勾选「不校验合法域名」临时联调 http://localhost。
 *   - H5：默认走本机后端，可用 VITE_API_BASE_URL 覆盖。
 * 上线前必须替换 MP_BASE_URL 为已备案的 HTTPS 接口域名。
 */
let baseUrl = 'http://localhost:8080/api';
// #ifdef H5
baseUrl = (import.meta.env.VITE_API_BASE_URL as string) || 'http://localhost:8080/api';
// #endif
// #ifndef H5
baseUrl = 'https://api.banya.example.com/api'; // TODO(上线): 替换为已备案 HTTPS 接口域名
// #endif
export const BASE_URL = baseUrl;

/** 统一 storage key（请求层与登录页共用，避免多源读写不一致） */
export const STORAGE_TOKEN = 'token';        // JWT（request 注入 Bearer）
export const STORAGE_LOGIN_FLAG = 'banya_logged'; // 小程序演示登录态标记（'1' = 已登录）
export const STORAGE_TICKETS = 'banya_feedback_tickets'; // 我的提问工单（提问链路闭环）
export const STORAGE_FAV = 'banya_fav_courses';          // 收藏的课程 id 列表
export const STORAGE_GUIDE_SEEN = 'banya_guide_seen';    // 新手引导已展示标记（'1'）

/** 请求超时（ms）：支教场景弱网，10s 比默认 60s 更快暴露失败 */
export const REQUEST_TIMEOUT = 10000;
