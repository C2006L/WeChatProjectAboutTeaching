/**
 * 教师管理 api
 * - mock 模式：操作直接变更 mock 单例并写入最新动态时间线
 * - 真实模式：GET /api/admin/teachers（后端已就绪，批次G）
 *   POST /api/admin/teachers/:id/approve|reject|disable|seed
 *   POST /api/admin/teachers/:id/seed → markSeedTeacher
 */
import { teacherList, teacherTraining } from '@/mock/teacher';
import { trainingCourses } from '@/mock/training';
import { activityStore } from '@/store/activity';
import { useMock } from './use-mock';
import { request } from './request';
import type { TeacherRow, TeacherUnfinishedRow } from '@/types/dashboard';

const delay = (ms = 160) => new Promise((r) => setTimeout(r, ms));

function mustFind(id: number): TeacherRow {
  const t = teacherList.find((x) => x.id === id);
  if (!t) throw new Error(`教师不存在：id=${id}`);
  return t;
}

/** ===== 真实接口类型（对齐后端 UserVO / PageResult） ===== */
interface UserVOJson {
  id: number;
  username: string;
  name: string;
  role: string;
  status: 'PENDING' | 'ACTIVE' | 'REJECTED' | 'DISABLED';
  isSeed: number;
  auditField?: string | null;
  rejectReason?: string | null;
  /** yyyy-MM-dd HH:mm:ss（Jackson 全局格式） */
  createdAt?: string;
}

interface PageResultJson<T> {
  total: number;
  current: number;
  size: number;
  pages: number;
  records: T[];
}

function voToRow(vo: UserVOJson): TeacherRow {
  const row: TeacherRow = {
    id: vo.id,
    account: vo.username,
    name: vo.name,
    // 队伍管理域尚未建表（批次J），当前强一致字段缺省
    team: '未分配',
    registeredAt: vo.createdAt ? vo.createdAt.slice(0, 10) : '—',
    status: vo.status,
    isSeed: vo.isSeed === 1,
  };
  if (vo.status === 'REJECTED' && vo.rejectReason) row.rejectReason = vo.rejectReason;
  return row;
}

export async function fetchTeachers(): Promise<TeacherRow[]> {
  if (useMock()) {
    await delay();
    return teacherList;
  }
  const page = await request<PageResultJson<UserVOJson>>({
    url: '/admin/teachers?size=100',
  });
  return page.records.map(voToRow);
}

/** 通过：PENDING/REJECTED/DISABLED → ACTIVE */
export async function approveTeacher(id: number): Promise<void> {
  if (useMock()) {
    const t = mustFind(id);
    t.status = 'ACTIVE';
    delete t.rejectReason;
    activityStore.push(`教师「${t.name}」已通过审核（${t.account}）`, 'user');
    return;
  }
  await request({ url: `/admin/teachers/${id}/approve`, method: 'POST' });
}

/** 驳回：→ REJECTED，须填写原因 */
export async function rejectTeacher(id: number, reason: string): Promise<void> {
  if (useMock()) {
    const t = mustFind(id);
    t.status = 'REJECTED';
    t.rejectReason = reason;
    activityStore.push(`教师「${t.name}」审核被驳回：${reason}`, 'user');
    return;
  }
  await request({ url: `/admin/teachers/${id}/reject`, method: 'POST', data: { reason } });
}

/** 停用：ACTIVE → DISABLED */
export async function disableTeacher(id: number): Promise<void> {
  if (useMock()) {
    const t = mustFind(id);
    t.status = 'DISABLED';
    activityStore.push(`教师「${t.name}」账号已停用（${t.team}）`, 'user');
    return;
  }
  await request({ url: `/admin/teachers/${id}/disable`, method: 'POST' });
}

/** 标记种子教师：is_seed=1（后端支持，批次G） */
export async function markSeedTeacher(id: number): Promise<void> {
  if (useMock()) {
    const t = mustFind(id);
    t.isSeed = true;
    activityStore.push(`教师「${t.name}」已被标记为种子教师`, 'user');
    return;
  }
  await request({ url: `/admin/teachers/${id}/seed`, method: 'POST' });
}

/**
 * 未完成必修教师名单
 * 依赖培训域数据（teacherTraining），后端培训域未建（批次J），
 * 真实模式暂返回空列表，页面走 EmptyState。
 */
export async function fetchUnfinishedRequired(): Promise<TeacherUnfinishedRow[]> {
  if (useMock()) {
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
  return [];
}
