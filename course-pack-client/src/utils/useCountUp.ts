import { ref, watch, type Ref, type ComputedRef } from 'vue';

/**
 * 数字滚动 count-up（动效规范：600ms 统一缓动，ease-out cubic）。
 * 仅驱动文本更新，不动布局动画（性能纪律）。
 */
export function useCountUp(source: Ref<number> | ComputedRef<number>, duration = 600) {
  const display = ref(0);
  let timer: ReturnType<typeof setInterval> | null = null;

  watch(
    source,
    (target) => {
      if (timer) clearInterval(timer);
      const start = Date.now();
      timer = setInterval(() => {
        const t = Math.min(1, (Date.now() - start) / duration);
        const eased = 1 - Math.pow(1 - t, 3);
        display.value = Math.round(target * eased);
        if (t >= 1 && timer) {
          clearInterval(timer);
          timer = null;
        }
      }, 16);
    },
    { immediate: true },
  );

  return display;
}
