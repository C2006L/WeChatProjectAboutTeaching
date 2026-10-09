/**
 * 课程收藏（mock 本地实现，storage 持久化）
 * 模块级响应式状态：列表/详情/角标共享同一份 favIds，任一入口切换后全局即时更新。
 * 后端就绪后映射：GET/POST /api/user/favorites。
 */
import { ref } from 'vue';
import { STORAGE_FAV } from '@/config';

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

/** 响应式收藏 id 列表（供 computed 过滤使用） */
export function useFavIds() {
  return favIds;
}

export function isFav(id: number): boolean {
  return favIds.value.includes(id);
}

/** 切换收藏，返回切换后的状态（true=已收藏） */
export function toggleFav(id: number): boolean {
  const idx = favIds.value.indexOf(id);
  if (idx === -1) {
    favIds.value = [...favIds.value, id];
    persist();
    return true;
  }
  favIds.value = favIds.value.filter((x) => x !== id);
  persist();
  return false;
}
