/**
 * 备课助手 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET /api/prepare/options                          → fetchPrepareOptions
 *   POST /api/prepare/generate {grade,theme,duration} → generatePrepareList
 */
import { prepareOptionMap, generatePrepareList } from '@/mock/prepare';
import type { PrepareResult } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export interface PrepareOptions {
  grades: readonly string[];
  themes: readonly string[];
  durations: readonly number[];
}

export async function fetchPrepareOptions(): Promise<PrepareOptions> {
  // return request<PrepareOptions>({ url: '/prepare/options' });
  return prepareOptionMap;
}

export async function generatePrepare(
  grade: string,
  theme: string,
  duration: number,
): Promise<PrepareResult> {
  // return request<PrepareResult>({ url: '/prepare/generate', method: 'POST', data: { grade, theme, duration } });
  return generatePrepareList(grade, theme, duration);
}
