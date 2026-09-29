<template>
  <div class="page">
    <!-- 顶部操作条：新建课程主按钮（右上） -->
    <div class="page-head">
      <div></div>
      <el-button type="primary" size="large" @click="openCreate">+ 新建课程</el-button>
    </div>

    <!-- 筛选区：燕麦底圆角条卡 -->
    <div class="filter-bar card-leaf">
      <el-input
        v-model="query.keyword"
        placeholder="搜索课程名称"
        clearable
        class="f-keyword"
        @input="applyFilter"
      />
      <el-segmented
        v-model="query.version"
        :options="versionOptions"
        class="f-version"
      />
      <el-select v-model="query.grade" placeholder="全部学段" clearable class="f-grade" @change="applyFilter">
        <el-option label="小学1-3年级" value="小学1-3年级" />
        <el-option label="小学4-6年级" value="小学4-6年级" />
      </el-select>
      <el-select v-model="query.themeLabel" placeholder="全部主题" clearable class="f-theme" @change="applyFilter">
        <el-option v-for="t in themeOptions" :key="t" :label="t" :value="t" />
      </el-select>
    </div>

    <!-- Bento 三列卡片网格 -->
    <div v-loading="loading" class="grid">
      <div v-for="c in paged" :key="c.id" class="course-card card-float card-leaf" @click="openDetail()">
        <!-- 封面区：主题色纯色底 + 白色线条小芽 + 版本角标 -->
        <div class="cover" :style="{ background: themeColor(c.themeLabel) }">
          <svg class="cover-sprout" viewBox="0 0 80 80" fill="none" aria-hidden="true">
            <path d="M40 64V34" style="stroke: var(--on-brand)" stroke-width="2.5" stroke-linecap="round" />
            <path d="M40 36c-10 0-17-6-17-14 10 0 17 6 17 14Z" style="stroke: var(--on-brand)" stroke-width="2.5" stroke-linejoin="round" />
            <path d="M40 36c10-1 17-6 17-15-10-1-17 5-17 15Z" style="stroke: var(--on-brand)" stroke-width="2.5" stroke-linejoin="round" />
          </svg>
          <span class="version-badge">{{ c.version }}</span>
        </div>
        <!-- 内容区 -->
        <div class="body">
          <span class="theme-tag" :style="{ background: themeSoft(c.themeLabel), color: themeColor(c.themeLabel) }">
            {{ c.themeLabel }}
          </span>
          <h3 class="title">{{ c.title }}</h3>
          <p class="summary">{{ c.summary }}</p>
          <div class="meta-row">
            <span class="meta">{{ c.grade }} · {{ c.duration }}分钟</span>
            <span class="go-btn">查看详情<ArrowIcon /></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页器 -->
    <div class="pager">
      <el-pagination
        layout="prev, pager, next, total"
        :total="filtered.length"
        :page-size="pageSize"
        :current-page="page"
        @current-change="(p: number) => (page = p)"
      />
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
import ArrowIcon from '@/components/common/ArrowIcon.vue';
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

function openEdit(row: CourseAdminItem) {
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

async function confirmDelete(row: CourseAdminItem) {
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
  gap: var(--space-l);
  padding-bottom: var(--space-l);
}
.page-head {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
/* 筛选区：燕麦底圆角条卡 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--space-m);
  padding: 16px 20px;
  background: var(--bg-oat);
  border: 1px solid var(--border-warm);
  flex-wrap: wrap;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.f-keyword {
  width: 240px;
}
.f-version {
  flex-shrink: 0;
}
.f-grade,
.f-theme {
  width: 150px;
}
/* Bento 三列网格 */
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-l);
}
.course-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  overflow: hidden;
  cursor: pointer;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
  /* hover 上浮统一走全局 .card-float（180ms 缓动 token） */
}
.cover {
  position: relative;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-sprout {
  width: 72px;
  height: 72px;
  opacity: 0.9;
}
.version-badge {
  position: absolute;
  right: 12px;
  bottom: 12px;
  padding: 2px 10px;
  border-radius: 999px;
  background: rgba(var(--rgb-on-brand), 0.88);
  color: var(--text-regular);
  font-size: 13px;
}
.body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.theme-tag {
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 13px;
}
.title {
  margin: 0;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
}
.summary {
  margin: 0;
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 45px;
}
.meta-row {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 4px;
}
.meta {
  font-size: 13px;
  color: var(--text-weak);
}
.go-btn {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: var(--brand-primary);
}
.pager {
  display: flex;
  justify-content: center;
  padding: 8px 0;
}
@media (max-width: 1100px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>