<template>
  <div class="page">
    <!-- 顶部统计条：管理员视角 -->
    <div class="overview card-leaf">
      <span class="ov-item"><b>{{ overview.requiredRate }}%</b> 必修完成率</span>
      <span class="ov-item"><b>{{ overview.doneTeachers }}</b> 已完成教师</span>
      <span class="ov-item"><b>{{ overview.inprogressTeachers }}</b> 进行中教师</span>
      <span class="ov-item"><b>{{ overview.teamCount }}</b> 覆盖队伍</span>
    </div>

    <!-- 培训课程管理表 -->
    <div class="table-card card-leaf">
      <div class="card-head">
        <h3 class="card-title">培训课程管理</h3>
        <span class="card-hint">完成率达到 76% 即长成"小树"</span>
      </div>
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="title" label="课程名" min-width="220" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === '必修' ? 'primary' : 'info'" effect="light" size="small">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时长" width="100">
          <template #default="{ row }">{{ row.duration }} 分钟</template>
        </el-table-column>
        <el-table-column label="完成人数" width="110" align="center">
          <template #default="{ row }">
            <span class="finish-num">{{ row.completedCount }}/{{ row.assignedCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="完成率" min-width="190">
          <template #default="{ row }">
            <GrowthProgress :percent="rateOf(row as TrainingCourseRow)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row as TrainingCourseRow)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 未完成必修教师名单（对接教师管理数据） -->
    <div class="table-card card-leaf">
      <div class="card-head">
        <h3 class="card-title">未完成必修教师名单</h3>
        <router-link class="to-teacher" to="/teacher">去教师管理<ArrowIcon /></router-link>
      </div>
      <el-table v-if="unfinished.length" :data="unfinished" style="width: 100%">
        <el-table-column prop="name" label="教师" width="110" />
        <el-table-column prop="team" label="所属队伍" min-width="150" />
        <el-table-column label="必修进度" width="110" align="center">
          <template #default="{ row }">
            <span class="finish-num">{{ row.completedCount }}/{{ row.requiredCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="未完成课程" min-width="260">
          <template #default="{ row }">
            <el-tag
              v-for="m in row.missing"
              :key="m"
              size="small"
              effect="plain"
              class="missing-tag"
            >{{ m }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="all-done">所有教师均已完成必修课程</div>
    </div>

    <!-- 编辑课程对话框 -->
    <el-dialog v-model="editVisible" title="编辑培训课程" width="440px">
      <el-form :model="editForm" label-width="72px">
        <el-form-item label="课程名">
          <el-input v-model="editForm.title" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="editForm.type">
            <el-radio value="必修">必修</el-radio>
            <el-radio value="选修">选修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="时长">
          <el-input-number v-model="editForm.duration" :min="10" :max="180" :step="5" />
          <span class="unit">分钟</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import ArrowIcon from '@/components/common/ArrowIcon.vue';
import GrowthProgress from '@/components/training/GrowthProgress.vue';
import { fetchTrainingCourses, fetchTrainingOverview } from '@/api/training';
import { fetchUnfinishedRequired } from '@/api/teacher';
import type {
  TrainingCourseRow,
  TrainingOverview,
  TeacherUnfinishedRow,
} from '@/types/dashboard';

const overview = ref<TrainingOverview>({ requiredRate: 0, doneTeachers: 0, inprogressTeachers: 0, teamCount: 0 });
const courses = ref<TrainingCourseRow[]>([]);
const unfinished = ref<TeacherUnfinishedRow[]>([]);

function rateOf(row: TrainingCourseRow): number {
  if (!row.assignedCount) return 0;
  return Math.round((row.completedCount / row.assignedCount) * 100);
}

/* ---- 编辑课程（mock 直改，后端就绪后换 PUT /api/admin/training/:id） ---- */
const editVisible = ref(false);
const editId = ref<number>(0);
const editForm = reactive({ title: '', type: '必修' as '必修' | '选修', duration: 45 });

function openEdit(row: TrainingCourseRow) {
  editId.value = row.id;
  editForm.title = row.title;
  editForm.type = row.type;
  editForm.duration = row.duration;
  editVisible.value = true;
}

function saveEdit() {
  if (!editForm.title.trim()) {
    ElMessage.error('课程名不能为空');
    return;
  }
  const row = courses.value.find((c) => c.id === editId.value);
  if (row) {
    row.title = editForm.title.trim();
    row.type = editForm.type;
    row.duration = editForm.duration;
  }
  editVisible.value = false;
  ElMessage.success('课程信息已保存');
}

onMounted(async () => {
  const [ov, cs, uf] = await Promise.all([
    fetchTrainingOverview(),
    fetchTrainingCourses(),
    fetchUnfinishedRequired(),
  ]);
  overview.value = ov;
  courses.value = cs;
  unfinished.value = uf;
});
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-l);
  padding-bottom: var(--space-l);
}
/* 顶部统计条：燕麦底 */
.overview {
  display: flex;
  gap: var(--space-l);
  padding: 16px 20px;
  background: var(--bg-oat);
  border: 1px solid var(--border-warm);
  font-size: var(--font-size-s);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  color: var(--text-regular);
}
.overview b {
  font-size: var(--font-size-l);
  color: var(--brand-primary-dark);
  font-weight: 650;
  margin-right: 4px;
}
/* 卡片容器 */
.table-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: 8px;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* 表格行分隔线/表头线统一墨绿低透明度（禁中性灰） */
  --el-table-border-color: var(--divider-soft);
}
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px 8px;
}
.card-title {
  margin: 0;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.card-hint {
  font-size: 12px;
  color: var(--text-secondary);
}
.to-teacher {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  color: var(--brand-primary);
  text-decoration: none;
  font-weight: 600;
}
.to-teacher:hover {
  color: var(--brand-primary-dark);
}
.finish-num {
  font-variant-numeric: tabular-nums;
  color: var(--text-regular);
  font-weight: 600;
}
.missing-tag {
  margin: 2px 6px 2px 0;
  color: var(--text-regular);
}
.all-done {
  padding: 32px 0 24px;
  text-align: center;
  font-size: var(--font-size-s);
  color: var(--text-secondary);
}
.unit {
  margin-left: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
