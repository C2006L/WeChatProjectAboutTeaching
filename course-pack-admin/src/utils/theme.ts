/**
 * 设计 Token 运行时读取（V3）
 *
 * ECharts 走 Canvas 渲染，拿不到 DOM 的样式继承，无法像 CSS 那样直接写 var(--x)，
 * 因此统一在这里把 :root 上的 token 解析成实际色值后再交给图表配置，
 * 避免图表里再出现散落的 hex / rgba 字面量。
 *
 * 调用时机：须在组件挂载后（此时 index.css 已注入），本项目的图表均在 onMounted 里渲染。
 */

/** 读取 CSS 变量的计算值；变量缺失时回退 fallback，避免图表出现空色 */
export function cssVar(name: string, fallback: string): string {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim();
  return value || fallback;
}

/** 取 token 的 RGB 分量并按需合成透明度（图表渐变等场景） */
export function cssRgba(rgbToken: string, alpha: number, fallbackRgb: string): string {
  return `rgba(${cssVar(rgbToken, fallbackRgb)}, ${alpha})`;
}