<template>
  <div class="page">
    <!-- 状态筛选条 -->
    <div class="filter-card card-leaf">
      <div
        v-for="f in filters"
        :key="f.key"
        class="filter-tab"
        :class="{ active: current === f.key }"
        @click="current = f.key"
      >
        {{ f.label }}
        <span class="count">{{ countOf(f.key) }}</span>
      </div>
    </div>

    <!-- 教师列表 -->
    <div class="table-card card-leaf">
      <el-table :data="filtered" style="width: 100%">
        <el-table-column label="姓名" min-width="160">
          <template #default="{ row }">
            <div class="name-cell">
              <span class="name">{{ row.name }}</span>
              <span class="account">{{ row.account }}</span>
              <span v-if="row.isSeed" class="seed-tag">
                <svg viewBox="0 0 24 24" fill="none" class="seed-ico" aria-hidden="true">
                  <path d="M12 20v-6.6" style="stroke: var(--line-brand)" stroke-width="2" stroke-linecap="round" />
                  <path d="M12 13.6c-2.8 0-4.5-1.5-4.5-3.9 2.4 0 4.5 1.5 4.5 3.9Z" style="stroke: var(--line-brand)" stroke-width="2" stroke-linejoin="round" />
                  <path d="M12 13.6c2.8-.3 4.5-1.8 4.5-4.1-2.4-.3-4.5 1.6-4.5 4.1Z" style="stroke: var(--line-brand)" stroke-width="2" stroke-linejoin="round" />
                </svg>
                种子教师
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="team" label="所属队伍" min-width="150" />
        <el-table-column prop="registeredAt" label="注册时间" width="120" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" effect="light" size="small">{{ statusLabel(row.status) }}</el-tag>
            <div v-if="row.status === 'REJECTED' && row.rejectReason" class="reason">原因：{{ row.rejectReason }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 'ACTIVE'"
              link
              type="success"
              size="small"
              @click="onApprove(row as TeacherRow)"
            >通过</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              link
              type="danger"
              size="small"
              @click="onReject(row as TeacherRow)"
            >驳回</el-button>
            <el-button
              v-if="row.status === 'ACTIVE'"
              link
              type="danger"
              size="small"
              @click="onDisable(row as TeacherRow)"
            >停用</el-button>
            <el-button
              v-if="!row.isSeed"
              link
              type="primary"
              size="small"
              @click="onMarkSeed(row as TeacherRow)"
            >标记种子教师</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  fetchTeachers,
  approveTeacher,
  rejectTeacher,
  disableTeacher,
  markSeedTeacher,
} from '@/api/teacher';
import type { TeacherRow, TeacherStatus } from '@/types/dashboard';

/** 筛选：全部 + 四种状态（对齐接口规范状态机） */
const filters = [
  { key: 'ALL', label: '全部' },
  { key: 'PENDING', label: '待审核' },
  { key: 'ACTIVE', label: '正常' },
  { key: 'REJECTED', label: '已驳回' },
  { key: 'DISABLED', label: '已停用' },
] as const;

type FilterKey = (typeof filters)[number]['key'];
const current = ref<FilterKey>('ALL');
const rows = ref<TeacherRow[]>([]);

const filtered = computed(() =>
  current.value === 'ALL' ? rows.value : rows.value.filter((r) => r.status === current.value),
);

function countOf(key: FilterKey): number {
  return key === 'ALL' ? rows.value.length : rows.value.filter((r) => r.status === key).length;
}

const STATUS_LABEL: Record<TeacherStatus, string> = {
  PENDING: '待审核',
  ACTIVE: '正常',
  REJECTED: '已驳回',
  DISABLED: '已停用',
};
const statusLabel = (s: TeacherStatus) => STATUS_LABEL[s];

const STATUS_TAG: Record<TeacherStatus, 'warning' | 'success' | 'danger' | 'info'> = {
  PENDING: 'warning',
  ACTIVE: 'success',
  REJECTED: 'danger',
  DISABLED: 'info',
};
const statusTag = (s: TeacherStatus) => STATUS_TAG[s];

async function load() {
  rows.value = await fetchTeachers();
}

async function onApprove(row: TeacherRow) {
  await approveTeacher(row.id);
  ElMessage.success(`已通过「${row.name}」的审核`);
}

async function onReject(row: TeacherRow) {
  const { value } = await ElMessageBox.prompt('请填写驳回原因（将通知到教师）', `驳回「${row.name}」`, {
    confirmButtonText: '确认驳回',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：身份与支教资质材料不完整',
    inputValidator: (v: string) => (v && v.trim() ? true : '驳回原因不能为空'),
  });
  await rejectTeacher(row.id, value.trim());
  ElMessage.warning(`已驳回「${row.name}」`);
}

async function onDisable(row: TeacherRow) {
  await ElMessageBox.confirm(`停用后「${row.name}」将无法登录小程序端，确定停用？`, '停用确认', {
    confirmButtonText: '停用',
    cancelButtonText: '取消',
    type: 'warning',
  });
  await disableTeacher(row.id);
  ElMessage.warning(`已停用「${row.name}」`);
}

async function onMarkSeed(row: TeacherRow) {
  await markSeedTeacher(row.id);
  ElMessage.success(`「${row.name}」已标记为种子教师`);
}

onMounted(load);
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-l);
  padding-bottom: var(--space-l);
}
/* 筛选条：燕麦底横条 + 胶囊 tab */
.filter-card {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  background: var(--bg-oat);
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border-radius: 18px;
  font-size: var(--font-size-s);
  color: var(--text-regular);
  background: var(--bg-card);
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.15s ease;
}
.filter-tab:hover {
  border-color: var(--brand-primary-light);
}
.filter-tab.active {
  background: var(--brand-primary);
  border-color: var(--brand-primary);
  color: var(--on-brand);
  font-weight: 600;
}
.count {
  font-size: 13px;
  color: inherit;
  opacity: 0.75;
  font-variant-numeric: tabular-nums;
}
/* 列表卡 */
.table-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: 8px;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* 表格行分隔线/表头线统一墨绿低透明度（禁中性灰） */
  --el-table-border-color: var(--divider-soft);
}
.name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.name {
  font-weight: 600;
  color: var(--text-primary);
}
.account {
  font-size: 12px;
  color: var(--text-secondary);
  font-family: monospace;
}
.seed-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 0 8px;
  height: 20px;
  border-radius: 10px;
  font-size: 12px;
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
.seed-ico {
  width: 12px;
  height: 12px;
}
.reason {
  margin-top: 4px;
  font-size: 12px;
  color: var(--danger);
  line-height: 1.4;
}
</style>
