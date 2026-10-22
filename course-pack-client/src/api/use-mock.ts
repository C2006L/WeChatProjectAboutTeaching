/**
 * 统一 Mock 切换开关（对齐接口规范 §5）
 * 环境变量 VITE_USE_MOCK：'true' 走 mock；'false' 走真实 request；
 * 未配置时默认 mock（开发环境后端未就绪前保持全站可用）。
 * 构建发行版时务必设置 VITE_USE_MOCK=false（或直接在 .env 文件固定）。
 */
const RAW = (import.meta.env.VITE_USE_MOCK as string | undefined)?.trim();

export function useMock(): boolean {
  if (RAW === undefined || RAW === '') return true;
  return RAW === 'true';
}
