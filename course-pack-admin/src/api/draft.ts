/**
 * 备课草稿 api（对接后端；与小程序端续写同一张 lesson_draft 表）
 *   GET  /api/tools/prepare/draft   → fetchDraft（当前教师最新草稿）
 *   POST /api/tools/prepare/draft   → saveDraft
 */
import { request } from '@/api/request';

export interface LessonDraft {
  id?: number;
  userId?: number;
  courseId?: number | null;
  title?: string;
  outline?: string;
  content?: string;
  /** V3：编辑状态前端回填 */
  draft?: { outline: string } | null;
}

export async function fetchDraft(): Promise<LessonDraft | null> {
  // 静默加载：草稿是锦上添花，后端不可达时返回 null（空白教案），不得阻塞教师工作台首屏
  try {
    const res = await request<LessonDraft | null>({
      url: '/tools/prepare/draft',
      method: 'get',
      skipErrorToast: true,
    });
    return res || null;
  } catch {
    return null;
  }
}

export async function saveDraft(data: LessonDraft): Promise<LessonDraft> {
  return request<LessonDraft>({ url: '/tools/prepare/draft', method: 'post', data });
}