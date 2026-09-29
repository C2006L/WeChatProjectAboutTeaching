<template>
  <div class="wb">
    <el-tabs v-model="tab" class="wb-tabs card-leaf">
      <!-- ① 课程学习 -->
      <el-tab-pane label="课程学习" name="courses">
        <div v-loading="courseLoading" class="courses">
          <div
            v-for="(c, i) in courses"
            :key="c.id"
            class="course-card card-float card-leaf"
            :class="{ 'is-feature': i === 0 }"
          >
            <!-- 封面：主题色 8% 淡底 + 墨绿线性主题图标 + 版本描边胶囊 -->
            <div class="cover" :style="{ background: themeSoft(c.themeLabel) }">
              <svg
                class="cover-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <path :d="themeIcon(c.themeLabel)" />
              </svg>
              <span class="version-badge">{{ c.version }}</span>
            </div>
            <div class="body">
              <span class="theme-tag" :style="{ background: themeSoft(c.themeLabel), color: themeColor(c.themeLabel) }">
                {{ c.themeLabel }}
              </span>
              <h3 class="title">{{ c.title }}</h3>
              <p class="summary">{{ c.summary }}</p>
              <div class="meta-row">
                <span class="meta">{{ c.grade }} · {{ c.duration }}分钟</span>
                <span class="go-btn" @click="goPrepare(c)">去备课<ArrowIcon /></span>
              </div>
            </div>
          </div>
          <EmptyState v-if="!courseLoading && !courses.length" description="暂无课程" />
        </div>
      </el-tab-pane>

      <!-- ② 培训学习 -->
      <el-tab-pane label="培训学习" name="training">
        <div class="training-progress">
          <span class="tp-label">必修完成率</span>
          <span class="tp-num">{{ overview.requiredRate }}</span>
          <span class="tp-unit">%</span>
        </div>
        <el-table :data="trainingRows" class="t-table">
          <el-table-column prop="title" label="培训课程" min-width="180" />
          <el-table-column prop="type" label="类型" width="90">
            <template #default="{ row }">
              <el-tag size="small" :type="row.type === '必修' ? 'warning' : 'info'" effect="plain">
                {{ row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时长" width="100">
            <template #default="{ row }">{{ row.duration }} 分钟</template>
          </el-table-column>
          <el-table-column label="团队完成率" min-width="200">
            <template #default="{ row }">
              <GrowthProgress :percent="rateOf(row)" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- ③ 备课助手 -->
      <el-tab-pane label="备课助手" name="prepare">
        <div class="prepare-wrap">
          <div class="outline">
            <div class="outline-head">
              <span>课程大纲</span>
              <el-button size="small" text @click="saveNow">保存草稿</el-button>
            </div>
            <el-input
              v-model="draft.outline"
              type="textarea"
              :rows="14"
              placeholder="在左侧编写课程大纲，手机端续写可见"
            />
          </div>
          <div class="editor">
            <div class="editor-head">教案正文</div>
            <el-input
              v-model="draft.content"
              type="textarea"
              :rows="16"
              placeholder="编写教案正文……（与小程序端备课续写共用同一份草稿）"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- ④ 我的案例（仅种子教师） -->
      <el-tab-pane v-if="auth.isSeed" label="我的案例" name="cases">
        <div v-loading="caseLoading" class="cases">
          <div v-for="c in myCases" :key="c.id" class="case-card card-leaf">
            <div class="case-head">
              <span class="case-title">{{ c.title }}</span>
              <el-tag
                size="small"
                :type="c.status === 'APPROVED' ? 'success' : 'warning'"
                effect="plain"
              >{{ c.status === 'APPROVED' ? '已过审' : '待审核' }}</el-tag>
              <el-tag v-if="c.featured" size="small" type="danger" effect="plain">精选</el-tag>
            </div>
            <p class="case-summary">{{ c.summary }}</p>
            <div class="case-meta">{{ c.team }} · {{ c.uploadedAt }}</div>
          </div>
          <EmptyState v-if="!caseLoading && !myCases.length" description="暂无我的案例" hint="通过审核的案例会展示在这里" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import ArrowIcon from '@/components/common/ArrowIcon.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import GrowthProgress from '@/components/training/GrowthProgress.vue';
import { useAuthStore } from '@/store/auth';
import { fetchCourseList } from '@/api/course';
import { fetchTrainingCourses, fetchTrainingOverview } from '@/api/training';
import { fetchCases } from '@/api/case';
import { fetchDraft, saveDraft, type LessonDraft } from '@/api/draft';

const auth = useAuthStore();

/* ---- 课程学习（复用课程管理卡片，按钮改"去备课"） ---- */
const courses = ref<Awaited<ReturnType<typeof fetchCourseList>>>([]);
const courseLoading = ref(false);

/** 主题中英映射：色值与淡底一律走 token（禁止组件内手写色值字面量） */
const themeKeyMap: Record<string, string> = {
  身体与安全: 'body',
  家庭与合作: 'family',
  人际与尊重: 'respect',
  成长与变化: 'growth',
  综合主题: 'general',
};
function themeColor(t: string) {
  return `var(--theme-${themeKeyMap[t] ?? 'body'})`;
}
function themeSoft(t: string) {
  return `var(--theme-${themeKeyMap[t] ?? 'body'}-soft)`;
}
/** 五枚主题线性图标（24 格 1.5px 线稿）：盾牌 / 小屋 / 双人 / 小芽 / 四宫格 */
const themeIconMap: Record<string, string> = {
  body: 'M12 3.2 18.6 6v5.1c0 4.2-2.7 7.7-6.6 9.1-3.9-1.4-6.6-4.9-6.6-9.1V6L12 3.2Z',
  family: 'M4.2 10.6 12 4.2l7.8 6.4M6.4 9.6V20h11.2V9.6M10.4 20v-4.6h3.2V20',
  respect:
    'M9 11.2a3 3 0 1 0 0-6 3 3 0 0 0 0 6ZM3.6 19.8c0-3 2.4-4.9 5.4-4.9s5.4 1.9 5.4 4.9M16.2 5.6a2.6 2.6 0 0 1 0 5M17.6 15.2c1.8.6 2.8 2.2 2.8 4.4',
  growth:
    'M12 20.4v-6.8M12 13.6C12 9.9 9.4 7.6 5.6 7.6c0 3.7 2.6 6 6.4 6ZM12 11.4c0-3.2 2.2-5.2 5.6-5.2 0 3.2-2.2 5.2-5.6 5.2Z',
  general: 'M4.4 4.4h6.2v6.2H4.4zM13.4 4.4h6.2v6.2h-6.2zM4.4 13.4h6.2v6.2H4.4zM13.4 13.4h6.2v6.2h-6.2z',
};
function themeIcon(t: string) {
  return themeIconMap[themeKeyMap[t] ?? 'body'];
}
function goPrepare(c: { title: string }) {
  tab.value = 'prepare';
  draft.title = c.title;
}

/* ---- 培训学习 ---- */
const trainingRows = ref<Awaited<ReturnType<typeof fetchTrainingCourses>>>([]);
const overview = ref<Awaited<ReturnType<typeof fetchTrainingOverview>>>({
  requiredRate: 0,
  doneTeachers: 0,
  inprogressTeachers: 0,
  teamCount: 0,
});
function rateOf(row: { completedCount?: number; assignedCount?: number }) {
  if (!row.assignedCount) return 0;
  return Math.round(((row.completedCount || 0) / row.assignedCount) * 100);
}

/* ---- 备课助手（后端草稿同步） ---- */
const draft = reactive<LessonDraft>({ title: '', outline: '', content: '', courseId: null });
async function saveNow() {
  try {
    await saveDraft({ ...draft });
    ElMessage.success('草稿已保存（双端同步）');
  } catch {
    // 失败提示已由 request 拦截器统一弹出
  }
}

/* ---- 我的案例（仅种子） ---- */
const myCases = ref<Awaited<ReturnType<typeof fetchCases>>>([]);
const caseLoading = ref(false);

const tab = ref('courses');

onMounted(async () => {
  await Promise.all([
    (async () => {
      courseLoading.value = true;
      try {
        courses.value = await fetchCourseList();
      } finally {
        courseLoading.value = false;
      }
    })(),
    fetchTrainingCourses().then((r) => (trainingRows.value = r)),
    fetchTrainingOverview().then((r) => (overview.value = r)),
    (async () => {
      if (!auth.isSeed) return;
      caseLoading.value = true;
      try {
        const all = await fetchCases();
        myCases.value = all.filter((c) => c.uploader === auth.user?.name);
      } finally {
        caseLoading.value = false;
      }
    })(),
    (async () => {
      const d = await fetchDraft();
      if (d) {
        draft.title = d.title || '';
        draft.outline = d.outline || '';
        draft.content = d.content || '';
        draft.courseId = d.courseId;
      }
    })(),
  ]);
});
</script>

<style scoped>
.wb {
  background: transparent;
}
.wb-tabs {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-m) var(--space-l);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
/* 课程（Bento 卡片：首张横向主卡 + 其余 2 列次级，打破均匀节奏） */
.courses {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-l);
}
.course-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  overflow: hidden;
  /* 叶片裁角与 hover 上浮走全局 .card-leaf / .card-float */
}
.course-card.is-feature {
  grid-column: span 2;
  display: grid;
  grid-template-columns: 40% 1fr;
}
.cover {
  position: relative;
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--divider-soft);
}
.is-feature .cover {
  height: auto;
  min-height: 190px;
  border-bottom: none;
  border-right: 1px solid var(--divider-soft);
}
.cover-icon {
  width: 56px;
  height: 56px;
  color: var(--brand-primary-dark);
}
.is-feature .cover-icon {
  width: 64px;
  height: 64px;
}
.version-badge {
  position: absolute;
  right: 12px;
  bottom: 12px;
  padding: 2px 10px;
  border: 1px solid var(--line-brand-muted);
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--brand-primary-dark);
  font-size: 13px;
}
.body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.is-feature .body {
  padding: var(--space-l);
  justify-content: center;
}
.theme-tag {
  padding: 3px 10px;
  border-radius: var(--radius-leaf-sm);
  font-size: 13px;
}
.title {
  margin: 0;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.summary {
  margin: 0;
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}
.meta-row {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.meta {
  font-size: 13px;
  color: var(--text-secondary);
}
.go-btn {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: var(--brand-primary);
  cursor: pointer;
}
/* 窄屏降级：主卡与次级卡统一单列 */
@media (max-width: 1100px) {
  .courses {
    grid-template-columns: 1fr;
  }
  .course-card.is-feature {
    grid-template-columns: 1fr;
  }
  .is-feature .cover {
    min-height: 120px;
    border-right: none;
    border-bottom: 1px solid var(--divider-soft);
  }
}
/* 培训 */
.training-progress {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-bottom: var(--space-m);
}
.tp-label {
  font-size: var(--font-size-s);
  color: var(--text-regular);
}
.tp-num {
  font-family: var(--font-serif);
  font-size: 40px;
  font-weight: 650;
  color: var(--brand-primary-dark);
}
.tp-unit {
  font-size: var(--font-size-m);
  color: var(--brand-primary-dark);
}
.t-table {
  width: 100%;
}
/* 备课：左大纲 + 右编辑器 */
.prepare-wrap {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-l);
}
.outline-head,
.editor-head {
  margin-bottom: 8px;
  font-size: var(--font-size-s);
  font-weight: 600;
  color: var(--text-primary);
}
.outline-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
/* 我的案例 */
.cases {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-l);
}
.case-card {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-m);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.case-head {
  display: flex;
  align-items: center;
  gap: var(--space-s);
}
.case-title {
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.case-summary {
  margin: 10px 0;
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.6;
}
.case-meta {
  font-size: 13px;
  color: var(--text-secondary);
}
</style>