/**
 * 培训管理 api（批次J）
 * - mock 模式：走本地 mock + teacherTraining 聚合
 * - 真实模式：
 *   GET /api/admin/training/courses   → fetchTrainingCourses
 *   GET /api/admin/training/overview  → fetchTrainingOverview
 * 覆盖队伍数在后端队伍域（批次J4）建立前为 0，前端沿用 0 展示。
 */
import { trainingCourses } from '@/mock/training';
import { teacherList, teacherTraining } from '@/mock/teacher';
import { useMock } from './use-mock';
import { request } from './request';
import type { TrainingCourseRow, TrainingOverview } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchTrainingCourses(): Promise<TrainingCourseRow[]> {
  if (useMock()) {
    await delay();
    return trainingCourses.map((c) => {
      if (c.type !== '必修') return { ...c };
      const completedCount = teacherTraining.filter((r) => r.completed.includes(c.id)).length;
      return { ...c, completedCount };
    });
  }
  return request<TrainingCourseRow[]>({ url: '/admin/training/courses' });
}

export async function fetchTrainingOverview(): Promise<TrainingOverview> {
  if (useMock()) {
    await delay();
    const requiredIds = trainingCourses.filter((c) => c.type === '必修').map((c) => c.id);
    const totalRequired = requiredIds.length * teacherTraining.length;
    const doneRequired = teacherTraining.reduce(
      (sum, r) => sum + r.completed.filter((id) => requiredIds.includes(id)).length,
      0,
    );
    const doneSet = teacherTraining.filter((r) => requiredIds.every((id) => r.completed.includes(id)));
    const inprogressSet = teacherTraining.filter(
      (r) => !doneSet.includes(r) && r.completed.some((id) => requiredIds.includes(id)),
    );
    const teams = new Set(
      teacherTraining
        .filter((r) => r.completed.length > 0)
        .map((r) => teacherList.find((t) => t.id === r.teacherId)?.team)
        .filter(Boolean),
    );
    return {
      requiredRate: totalRequired ? Math.round((doneRequired / totalRequired) * 100) : 0,
      doneTeachers: doneSet.length,
      inprogressTeachers: inprogressSet.length,
      teamCount: teams.size,
    };
  }
  return request<TrainingOverview>({ url: '/admin/training/overview' });
}
