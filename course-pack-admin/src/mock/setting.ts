import { mockAccounts, MOCK_PASSWORD } from '@/mock/auth';
import type { UserInfo } from '@/store/auth';

/**
 * 系统设置 · mock 数据层
 * 演示账号直接复用 mock/auth.ts 单一数据源（与登录回退、README 账号表同构）；
 * 平台基本信息走 localStorage 持久化，演示"保存生效"闭环。
 */

export interface PlatformInfo {
  name: string;
  subtitle: string;
  contactEmail: string;
}

const STORAGE_KEY = 'setting:platform';

export const DEFAULT_PLATFORM_INFO: PlatformInfo = {
  name: '伴芽课程包资源站',
  subtitle: '短期支教 · 伴芽课程包一站式备课与培训平台',
  contactEmail: 'support@banya-demo.cn',
};

export function loadPlatformInfo(): PlatformInfo {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return { ...DEFAULT_PLATFORM_INFO };
  try {
    return { ...DEFAULT_PLATFORM_INFO, ...(JSON.parse(raw) as Partial<PlatformInfo>) };
  } catch {
    localStorage.removeItem(STORAGE_KEY);
    return { ...DEFAULT_PLATFORM_INFO };
  }
}

export function savePlatformInfo(info: PlatformInfo): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(info));
}

/** 审核领域映射（与后端 AuditField 枚举同构） */
export const AUDIT_FIELD_LABEL: Record<string, string> = {
  PSY: '心理与安全',
  EDU: '教育与课程',
};

/** 演示账号行（复用登录 mock 单一数据源，取 user 字段） */
export type DemoAccount = UserInfo;

export const demoAccounts: DemoAccount[] = mockAccounts.map((a) => ({ ...a.user }));
export { MOCK_PASSWORD };
