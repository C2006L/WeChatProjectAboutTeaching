import { ref, watch, type Ref, type ComputedRef } from 'vue';

/**
 * 数字滚动 count-up（动效规范：800ms，ease-out cubic）。
 * rAF 驱动仅更新文本，不做布局动画（性能纪律：只动 transform/opacity）。
 */
export function useCountUp(source: Ref<number> | ComputedRef<number>, duration = 800) {
  const display = ref(0);
  let raf = 0;

  watch(
    source,
    (target) => {
      cancelAnimationFrame(raf);
      const from = display.value;
      const start = performance.now();
      const tick = (now: number) => {
        const t = Math.min(1, (now - start) / duration);
        const eased = 1 - Math.pow(1 - t, 3);
        display.value = Math.round(from + (target - from) * eased);
        if (t < 1) raf = requestAnimationFrame(tick);
      };
      raf = requestAnimationFrame(tick);
    },
    { immediate: true },
  );

  return display;
}
