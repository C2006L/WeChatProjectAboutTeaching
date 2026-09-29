/**
 * 培训管理 api（mock 优先，管理员视角）
 * 后端就绪后：GET /api/admin/training/overview、GET /api/admin/training/courses
 * 必修课完成人数由 teacherTraining 记录实时汇总，保证与未完成名单一致
 */
import { trainingCourses } from '@/mock/training';
import { teacherList, teacherTraining } from '@/mock/teacher';
import type { TrainingCourseRow, TrainingOverview } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

export async function fetchTrainingCourses(): Promise<TrainingCourseRow[]> {
  await delay();
  return trainingCourses.map((c) => {
    if (c.type !== '必修') return { ...c };
    const completedCount = teacherTraining.filter((r) => r.completed.includes(c.id)).length;
    return { ...c, completedCount };
  });
}

export async function fetchTrainingOverview(): Promise<TrainingOverview> {
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
