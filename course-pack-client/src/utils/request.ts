/**
 * 统一请求封装（uni.request 包装）
 * 约定对齐 course-pack-docs/02-接口：统一响应 { code, message, data }
 * 能力：10s 超时；GET 网络失败自动重试 1 次（弱网抖动）；401 统一清登录态并跳登录。
 */
import { BASE_URL, REQUEST_TIMEOUT, STORAGE_TOKEN, STORAGE_LOGIN_FLAG } from '@/config';

export interface ApiResult<T = unknown> {
  code: number;
  message: string;
  data: T;
}

interface RequestOptions {
  url: string;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  data?: Record<string, unknown>;
  /** 是否静默（失败不弹 toast），默认 false */
  silent?: boolean;
}

/** 401 处理节流：并发请求只提示/跳转一次 */
let unauthorizedLock = false;

function handleUnauthorized() {
  uni.removeStorageSync(STORAGE_TOKEN);
  uni.removeStorageSync(STORAGE_LOGIN_FLAG);
  if (unauthorizedLock) return;
  unauthorizedLock = true;
  uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
  setTimeout(() => {
    unauthorizedLock = false;
    // 我的页承载登录入口（tabBar 页只能 switchTab）
    uni.switchTab({ url: '/pages/mine/mine' });
  }, 800);
}

/** 单次请求 */
function doRequest<T>(opts: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + opts.url,
      method: opts.method || 'GET',
      data: opts.data,
      timeout: REQUEST_TIMEOUT,
      header: {
        Authorization: uni.getStorageSync(STORAGE_TOKEN) ? `Bearer ${uni.getStorageSync(STORAGE_TOKEN)}` : '',
      },
      success: (res) => {
        if (res.statusCode === 401) {
          handleUnauthorized();
          reject({ code: 401, message: '登录已过期' });
          return;
        }
        const body = res.data as ApiResult<T>;
        if (body && body.code === 0) {
          resolve(body.data);
          return;
        }
        if (body?.code === 401) {
          handleUnauthorized();
        } else if (!opts.silent) {
          uni.showToast({ title: body?.message || '请求失败', icon: 'none' });
        }
        reject(body || { code: res.statusCode, message: '请求失败' });
      },
      fail: (err) => {
        // 网络层失败（断网/DNS/超时）交给上层决定是否重试
        reject({ __networkError: true, raw: err });
      },
    });
  });
}

export function request<T = unknown>(options: RequestOptions): Promise<T> {
  const method = options.method || 'GET';
  // 仅 GET 在网络失败时重试 1 次（POST/PUT/DELETE 非幂等，不自动重试）
  const maxAttempts = method === 'GET' ? 2 : 1;
  let attempt = 0;

  const attemptRequest = (): Promise<T> =>
    doRequest<T>(options).catch((err) => {
      if (err?.__networkError && attempt + 1 < maxAttempts) {
        attempt += 1;
        return attemptRequest();
      }
      if (err?.__networkError && !options.silent) {
        uni.showToast({ title: '网络异常，请检查网络后重试', icon: 'none' });
      }
      return Promise.reject(err);
    });

  return attemptRequest();
}
