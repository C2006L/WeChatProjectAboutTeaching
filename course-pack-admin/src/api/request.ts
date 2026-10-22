import axios, { type AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus/es/components/message/index';

/** 扩展 axios 配置：静默请求（失败不弹全局 toast，由调用方自行处理） */
declare module 'axios' {
  interface AxiosRequestConfig {
    skipErrorToast?: boolean;
  }
}

export interface ApiResult<T = unknown> {
  code: number;
  message: string;
  data: T;
}

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截：注入 token
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截：统一处理业务码
http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult;
    if (body.code === 0) {
      return response;
    }
    const silent = response.config.skipErrorToast;
    if (!silent) ElMessage.error(body.message || '请求失败');
    if (body.code === 401) {
      localStorage.removeItem('adminToken');
      window.location.href = '/login';
    }
    return Promise.reject(body);
  },
  (error) => {
    const silent = error.config?.skipErrorToast;
    if (!silent) ElMessage.error(error?.message || '网络异常');
    return Promise.reject(error);
  },
);

export function request<T = unknown>(config: AxiosRequestConfig): Promise<T> {
  return http.request(config).then((res) => (res.data as ApiResult<T>).data);
}