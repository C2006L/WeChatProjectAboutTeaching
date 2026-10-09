/**
 * 教师工具箱 api（mock 优先）
 * 后端就绪后：将实现替换为 request 调用。
 *   GET /api/tools                 → fetchToolEntries
 *   GET /api/tools/family/scripts  → fetchFamilyScripts
 *   GET /api/tools/family/letter   → fetchFamilyLetter
 *   GET /api/tools/activities      → fetchActivities
 */
import { toolEntries } from '@/mock/tool';
import { familyScripts, familyLetter } from '@/mock/family';
import { activityList } from '@/mock/activities';
import type { ToolEntry, FamilyScript, FamilyLetter, Activity } from '@/types/mock';
// import { request } from '@/utils/request'; // 后端就绪后启用

export async function fetchToolEntries(): Promise<ToolEntry[]> {
  // return request<ToolEntry[]>({ url: '/tools' });
  return toolEntries;
}

export async function fetchFamilyScripts(): Promise<FamilyScript[]> {
  // return request<FamilyScript[]>({ url: '/tools/family/scripts' });
  return familyScripts;
}

export async function fetchFamilyLetter(): Promise<FamilyLetter> {
  // return request<FamilyLetter>({ url: '/tools/family/letter' });
  return familyLetter;
}

export async function fetchActivities(): Promise<Activity[]> {
  // return request<Activity[]>({ url: '/tools/activities' });
  return activityList;
}
