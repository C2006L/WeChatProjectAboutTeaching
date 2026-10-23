/**
 * 培训模块 api（批次J）
 * - mock 模式：走本地 trainingList
 * - 真实模式：
 *   GET  /api/trainings          → fetchTrainingList（含个人进度，状态服务端推导）
 *   GET  /api/trainings/{id}     → fetchTrainingById
 *   POST /api/trainings/{id}/progress → reportProgress（播放页上报，静默失败）
 */
import { trainingList, getTrainingById } from '@/mock/training';
import type { Training } from '@/types/mock';
import { useMock } from './use-mock';
import { request } from '@/utils/request';

/** 后端 TrainingVO */
interface TrainingVOJson {
  id: number;
  title: string;
  type: string; // required | elective
  duration: number;
  progress: number;
  status: string; // done | current | lock
  videoUrl?: string | null;
  introduction?: string | null;
  outline?: string[];
  materials?: { name: string; type: string }[];
}

function voToTraining(vo: TrainingVOJson): Training {
  return {
    id: vo.id,
    title: vo.title,
    type: vo.type === 'elective' ? 'elective' : 'required',
    duration: vo.duration,
    progress: vo.progress ?? 0,
    status: vo.status === 'done' ? 'done' : vo.status === 'lock' ? 'lock' : 'current',
    videoUrl: vo.videoUrl || '',
    introduction: vo.introduction || '',
    outline: vo.outline || [],
    materials: vo.materials || [],
  };
}

export async function fetchTrainingList(): Promise<Training[]> {
  if (useMock()) {
    return trainingList;
  }
  const vos = await request<TrainingVOJson[]>({ url: '/trainings' });
  return (vos || []).map(voToTraining);
}

export async function fetchTrainingById(id: number): Promise<Training | null> {
  if (useMock()) {
    return getTrainingById(id) ?? null;
  }
  try {
    const vo = await request<TrainingVOJson>({ url: `/trainings/${id}` });
    return voToTraining(vo);
  } catch {
    return null;
  }
}

/** 上报学习进度（0-100），只增不减；失败静默 */
export async function reportProgress(id: number, progress: number): Promise<void> {
  if (useMock()) {
    return;
  }
  await request({ url: `/trainings/${id}/progress`, method: 'POST', data: { progress }, silent: true });
}
