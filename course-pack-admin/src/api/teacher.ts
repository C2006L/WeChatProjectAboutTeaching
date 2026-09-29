/**
 * 教师管理 api（mock 优先，操作直接变更 mock 单例并写入最新动态时间线）
 * 后端就绪后映射：
 *  GET  /api/admin/teachers                 → fetchTeachers
 *  POST /api/admin/teachers/:id/approve     → approveTeacher
 *  POST /api/admin/teachers/:id/reject      → rejectTeacher
 *  POST /api/admin/teachers/:id/disable     → disableTeacher
 *  POST /api/admin/teachers/:id/seed        → markSeedTeacher
 */
import { teacherList, teacherTraining } from '@/mock/teacher';
import { trainingCourses } from '@/mock/training';
import { activityStore } from '@/store/activity';
import type { TeacherRow, TeacherUnfinishedRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

function mustFind(id: number): TeacherRow {
  const t = teacherList.find((x) => x.id === id);
  if (!t) throw new Error(`教师不存在：id=${id}`);
  return t;
}

export async function fetchTeachers(): Promise<TeacherRow[]> {
  await delay();
  return teacherList;
}

/** 通过：PENDING/REJECTED/DISABLED → ACTIVE */
export async function approveTeacher(id: number): Promise<void> {
  const t = mustFind(id);
  t.status = 'ACTIVE';
  t.rejectReason = undefined;
  activityStore.push(`教师「${t.name}」已通过审核（${t.account}）`, 'user');
}

/** 驳回：→ REJECTED，须填写原因 */
export async function rejectTeacher(id: number, reason: string): Promise<void> {
  const t = mustFind(id);
  t.status = 'REJECTED';
  t.rejectReason = reason;
  activityStore.push(`教师「${t.name}」审核被驳回：${reason}`, 'user');
}

/** 停用：ACTIVE → DISABLED */
export async function disableTeacher(id: number): Promise<void> {
  const t = mustFind(id);
  t.status = 'DISABLED';
  activityStore.push(`教师「${t.name}」账号已停用（${t.team}）`, 'user');
}

/** 标记种子教师：is_seed=1 */
export async function markSeedTeacher(id: number): Promise<void> {
  const t = mustFind(id);
  t.isSeed = true;
  activityStore.push(`教师「${t.name}」已被标记为种子教师`, 'user');
}

/** 未完成必修教师名单（对接教师管理数据与培训课程 mock） */
export async function fetchUnfinishedRequired(): Promise<TeacherUnfinishedRow[]> {
  await delay();
  const requiredIds = trainingCourses.filter((c) => c.type === '必修').map((c) => c.id);
  return teacherTraining
    .filter((r) => requiredIds.some((id) => !r.completed.includes(id)))
    .map((r) => {
      const t = mustFind(r.teacherId);
      const missing = trainingCourses
        .filter((c) => c.type === '必修' && !r.completed.includes(c.id))
        .map((c) => c.title);
      return {
        teacherId: t.id,
        name: t.name,
        team: t.team,
        status: t.status,
        completedCount: r.completed.filter((id) => requiredIds.includes(id)).length,
        requiredCount: requiredIds.length,
        missing,
      };
    });
}
