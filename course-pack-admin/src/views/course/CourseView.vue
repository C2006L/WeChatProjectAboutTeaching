<template>
  <div class="page">
    <div class="page-head">
      <div>
        <span class="eyebrow">内容资产</span>
        <h2>课程库</h2>
        <p>统一维护课程内容、适用学段、材料完整度和发布状态。</p>
      </div>
      <el-button type="primary" size="large" @click="openCreate">新建课程</el-button>
    </div>

    <div class="overview-strip">
      <div class="overview-item"><span>课程总数</span><strong>{{ all.length }}</strong></div>
      <div class="overview-item"><span>已发布</span><strong>{{ publishedCount }}</strong></div>
      <div class="overview-item"><span>教学材料</span><strong>{{ materialCount }}</strong></div>
      <div class="overview-note">建议优先补齐材料不完整的课程，再提交内容审核。</div>
    </div>

    <div class="work-panel">
      <div class="filter-bar">
        <div class="filter-main">
          <el-input v-model="query.keyword" placeholder="搜索课程名称或简介" clearable class="f-keyword" @input="applyFilter" />
          <el-select v-model="query.grade" placeholder="全部学段" clearable class="f-grade" @change="applyFilter">
            <el-option label="小学1-3年级" value="小学1-3年级" />
            <el-option label="小学4-6年级" value="小学4-6年级" />
          </el-select>
          <el-select v-model="query.themeLabel" placeholder="全部主题" clearable class="f-theme" @change="applyFilter">
            <el-option v-for="t in themeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </div>
        <el-segmented v-model="query.version" :options="versionOptions" class="f-version" @change="applyFilter" />
      </div>

      <el-table v-loading="loading" :data="paged" row-key="id" class="course-table">
        <el-table-column label="课程" min-width="310">
          <template #default="{ row }">
            <div class="course-cell" @click="openDetail()">
              <span class="course-mark" :style="{ background: themeColor(row.themeLabel) }"></span>
              <div class="course-copy">
                <strong>{{ row.title }}</strong>
                <span>{{ row.summary }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="主题" width="126">
          <template #default="{ row }">
            <span class="theme-tag" :style="{ background: themeSoft(row.themeLabel), color: themeColor(row.themeLabel) }">{{ row.themeLabel }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="适用学段" width="130" />
        <el-table-column prop="version" label="版本" width="90" />
        <el-table-column label="课时" width="78"><template #default="{ row }">{{ row.duration }} 分</template></el-table-column>
        <el-table-column label="材料" width="78" align="center">
          <template #default="{ row }"><span class="material-count">{{ materialTotal(row) }}/4</span></template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }"><span class="status-dot" :class="row.status ? 'online' : 'offline'">{{ row.status ? '已发布' : '未发布' }}</span></template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="最近更新" width="112" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link @click="openDetail()">详情</el-button>
            <el-button link type="danger" @click="confirmDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer">
        <span>共 {{ filtered.length }} 门课程</span>
        <el-pagination layout="prev, pager, next" :total="filtered.length" :page-size="pageSize" :current-page="page" @current-change="(p: number) => (page = p)" />
      </div>
    </div>

    <!-- 新增/编辑弹框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px" destroy-on-close>
      <el-form ref="formRef" :model="form" label-width="90px">
        <el-form-item label="课程名称" required>
          <el-input v-model="form.title" placeholder="如：我的身体，我做主" />
        </el-form-item>
        <el-form-item label="主题" required>
          <el-select v-model="form.themeLabel" placeholder="选择主题">
            <el-option v-for="t in themeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="学段" required>
          <el-select v-model="form.grade">
            <el-option label="小学1-3年级" value="小学1-3年级" />
            <el-option label="小学4-6年级" value="小学4-6年级" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本" required>
          <el-radio-group v-model="form.version">
            <el-radio value="精简版">精简版</el-radio>
            <el-radio value="标准版">标准版</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="时长(分)">
          <el-input-number v-model="form.duration" :min="20" :max="90" :step="5" />
        </el-form-item>
        <el-form-item label="一句话简介">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="一句话介绍课程价值" />
        </el-form-item>
        <el-form-item label="上架状态">
          <el-switch v-model="form.status" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { fetchCourseList, createCourse, updateCourse, deleteCourse } from '@/api/course';
import type { CourseAdminItem } from '@/types/dashboard';

const themeOptions = ['身体与安全', '家庭与合作', '人际与尊重', '成长与变化', '综合主题'];
const versionOptions = [
  { label: '全部', value: '' },
  { label: '精简版', value: '精简版' },
  { label: '标准版', value: '标准版' },
];

/** 主题色（V3：引用全局 token，禁组件内硬编码 hex） */
const themeColorMap: Record<string, string> = {
  身体与安全: 'var(--theme-body)',
  家庭与合作: 'var(--theme-family)',
  人际与尊重: 'var(--theme-respect)',
  成长与变化: 'var(--theme-growth)',
  综合主题: 'var(--theme-general)',
};

function themeColor(theme: string): string {
  return themeColorMap[theme] ?? 'var(--theme-body)';
}
/** 主题 8% 淡底（V3：引用 --theme-*-soft，禁用 hex+14 拼接） */
function themeSoft(theme: string): string {
  return `var(--theme-${themeKeyEn[theme] ?? 'body'}-soft)`;
}

const all = ref<CourseAdminItem[]>([]);
const loading = ref(false);
const query = reactive({ keyword: '', version: '', grade: '', themeLabel: '' });

/* 分页 */
const page = ref(1);
const pageSize = 6;

const filtered = computed(() =>
  all.value.filter((c) => {
    if (query.keyword && !c.title.includes(query.keyword)) return false;
    if (query.version && c.version !== query.version) return false;
    if (query.grade && c.grade !== query.grade) return false;
    if (query.themeLabel && c.themeLabel !== query.themeLabel) return false;
    return true;
  }),
);
const paged = computed(() =>
  filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize),
);
const publishedCount = computed(() => all.value.filter((item) => item.status).length);
const materialCount = computed(() => all.value.reduce((sum, item) => sum + materialTotal(item), 0));

function materialTotal(row: Record<string, any>) {
  const materials = row.materials as Record<string, number>;
  return Object.values(materials).filter((count) => count > 0).length;
}

function applyFilter() {
  page.value = 1;
}

/* 弹框 */
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const editingId = ref<number | null>(null);
const dialogTitle = computed(() => (editingId.value === null ? '新增课程' : '编辑课程'));

const form = reactive({
  title: '',
  themeLabel: '身体与安全',
  grade: '小学1-3年级',
  version: '精简版' as '精简版' | '标准版',
  duration: 30,
  summary: '',
  status: true,
});

/* 查看详情 / 详情：V2 演示入口 */
function openDetail() {
  ElMessage.info('课程详情页开发中');
}

async function loadAll() {
  loading.value = true;
  try {
    all.value = await fetchCourseList();
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, {
    title: '',
    themeLabel: '身体与安全',
    grade: '小学1-3年级',
    version: '精简版',
    duration: 30,
    summary: '',
    status: true,
  });
  dialogVisible.value = true;
}

function openEdit(source: Record<string, any>) {
  const row = source as CourseAdminItem;
  editingId.value = row.id;
  Object.assign(form, {
    title: row.title,
    themeLabel: row.themeLabel,
    grade: row.grade,
    version: row.version,
    duration: row.duration,
    summary: row.summary,
    status: row.status,
  });
  dialogVisible.value = true;
}

const themeKeyEn: Record<string, string> = {
  身体与安全: 'body',
  家庭与合作: 'family',
  人际与尊重: 'respect',
  成长与变化: 'growth',
  综合主题: 'general',
};

async function onSubmit() {
  if (!form.title.trim()) {
    ElMessage.warning('请输入课程名称');
    return;
  }
  const item: CourseAdminItem = {
    id: editingId.value ?? all.value.length + 1,
    title: form.title,
    theme: themeKeyEn[form.themeLabel] ?? 'body',
    themeLabel: form.themeLabel,
    grade: form.grade,
    duration: form.duration,
    version: form.version,
    summary: form.summary,
    status: form.status,
    materials: { plan: 1, slides: 1, toolkit: 1, video: 1 },
    updatedAt: new Date().toISOString().slice(0, 10),
  };

  if (editingId.value === null) {
    await createCourse(item);
    all.value.push(item);
    ElMessage.success('新增成功（演示）');
  } else {
    await updateCourse(item);
    const idx = all.value.findIndex((c) => c.id === editingId.value);
    if (idx >= 0) all.value[idx] = item;
    ElMessage.success('保存成功（演示）');
  }
  dialogVisible.value = false;
}

async function confirmDelete(source: Record<string, any>) {
  const row = source as CourseAdminItem;
  await ElMessageBox.confirm(`确定删除课程《${row.title}》吗？`, '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  });
  await deleteCourse(row.id);
  all.value = all.value.filter((c) => c.id !== row.id);
  ElMessage.success('删除成功（演示）');
}

loadAll();
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding-bottom: var(--space-l);
}
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}
.eyebrow { font-size: 11px; font-weight: 600; color: var(--brand-primary); letter-spacing: .12em; }
.page-head h2 { margin: 4px 0 0; font-size: 22px; color: var(--text-primary); }
.page-head p { margin: 5px 0 0; font-size: 13px; color: var(--text-secondary); }
.overview-strip {
  display: grid;
  grid-template-columns: repeat(3, 150px) 1fr;
  align-items: stretch;
  overflow: hidden;
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  background: var(--bg-card);
}
.overview-item { padding: 16px 20px; border-right: 1px solid var(--divider-soft); }
.overview-item span { display: block; font-size: 12px; color: var(--text-secondary); }
.overview-item strong { display: block; margin-top: 5px; font-size: 24px; color: var(--brand-ink); }
.overview-note { display: flex; align-items: center; justify-content: flex-end; padding: 16px 22px; font-size: 13px; color: var(--text-secondary); }
.work-panel { overflow: hidden; border: 1px solid var(--card-border); border-radius: var(--radius-card); background: var(--bg-card); }
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-m);
  padding: 14px 16px;
  border-bottom: 1px solid var(--divider-soft);
  flex-wrap: wrap;
}
.filter-main { display: flex; align-items: center; gap: 10px; }
.course-table { width: 100%; }
.course-cell { display: flex; align-items: center; gap: 12px; cursor: pointer; }
.course-mark { flex-shrink: 0; width: 8px; height: 38px; border-radius: 5px; }
.course-copy { min-width: 0; display: flex; flex-direction: column; gap: 3px; }
.course-copy strong { overflow: hidden; color: var(--text-primary); font-size: 14px; text-overflow: ellipsis; white-space: nowrap; }
.course-copy span { overflow: hidden; color: var(--text-secondary); font-size: 12px; text-overflow: ellipsis; white-space: nowrap; }
.theme-tag { display: inline-flex; padding: 3px 8px; border-radius: 5px; font-size: 12px; }
.material-count { color: var(--brand-primary-dark); font-weight: 600; }
.status-dot { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; }
.status-dot::before { content: ''; width: 7px; height: 7px; border-radius: 50%; }
.status-dot.online { color: var(--brand-primary-dark); }
.status-dot.online::before { background: var(--succ); }
.status-dot.offline { color: var(--text-secondary); }
.status-dot.offline::before { background: var(--text-disabled); }
.table-footer { display: flex; align-items: center; justify-content: space-between; min-height: 58px; padding: 0 16px; border-top: 1px solid var(--divider-soft); font-size: 12px; color: var(--text-secondary); }
.course-table :deep(.el-table__header th) { height: 46px; background: #F7F9F6; color: var(--text-secondary); font-size: 12px; font-weight: 600; }
.course-table :deep(.el-table__row td) { padding: 11px 0; }
.course-table :deep(.el-table__inner-wrapper::before) { display: none; }
.course-table :deep(.el-button + .el-button) { margin-left: 6px; }
.course-table :deep(.el-table__body tr:hover > td) { background: rgba(var(--rgb-brand-primary), .035); }
.f-keyword { width: 280px; }
.f-version { flex-shrink: 0; }
.f-grade, .f-theme { width: 150px; }
@media (max-width: 1100px) {
  .overview-strip { grid-template-columns: repeat(3, 1fr); }
  .overview-note { display: none; }
}
</style>
