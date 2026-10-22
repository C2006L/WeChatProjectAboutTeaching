/**
 * 课程收藏状态（批次H）
 * mock 模式：storage 持久化；真实模式（已登录）：服务端为准 —
 * 初始化从 GET /api/user/favorites 同步，切换时 POST /api/courses/{id}/favorite。
 * 未登录真实环境回退本地 storage，保持旧体验。
 * 模块级响应式状态：收藏列表/详情/标记共享同一份 favIds，收起再入时全局即时刷新。
 */
import { ref } from 'vue';
import { STORAGE_FAV, STORAGE_TOKEN } from '@/config';
import { request } from '@/utils/request';

function readStored(): number[] {
  const raw = uni.getStorageSync(STORAGE_FAV);
  if (!raw) return [];
  try {
    const arr = JSON.parse(raw);
    return Array.isArray(arr) ? arr.filter((n) => typeof n === 'number') : [];
  } catch {
    return [];
  }
}

const favIds = ref<number[]>(readStored());

function persist() {
  uni.setStorageSync(STORAGE_FAV, JSON.stringify(favIds.value));
}

function loggedIn(): boolean {
  return !!uni.getStorageSync(STORAGE_TOKEN);
}

/** 同步收藏状态：真实模式且已登录时以服务端为准 */
export async function syncFavorites(): Promise<void> {
  if (!loggedIn()) return;
  try {
    const vos = await request<{ id: number }[]>({ url: '/user/favorites', silent: true });
    favIds.value = (vos || []).map((v) => v.id);
    persist();
  } catch {
    /* 同步失败保留本地状态 */
  }
}

/** 响应式收藏 id 列表（computed 直接使用） */
export function useFavIds() {
  return favIds;
}

export function isFav(id: number): boolean {
  return favIds.value.includes(id);
}

/** 切换收藏：本地即时切换，已登录时同步服务端（失败回滚本地状态） */
export function toggleFav(id: number): boolean {
  const before = [...favIds.value];
  const next = favIds.value.includes(id);
  favIds.value = next
    ? favIds.value.filter((x) => x !== id)
    : [...favIds.value, id];
  persist();
  if (loggedIn()) {
    request({ url: `/courses/${id}/favorite`, method: 'POST', silent: true }).catch(() => {
      favIds.value = before;
      persist();
    });
  }
  return !next;
}
