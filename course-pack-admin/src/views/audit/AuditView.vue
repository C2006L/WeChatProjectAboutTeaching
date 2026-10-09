<template>
  <div class="page">
    <div class="audit-summary card-leaf">
      <div>
        <span class="summary-kicker">审核工作台</span>
        <h2>先处理阻塞发布的内容</h2>
        <p>按提交时间检查课程内容，并查看各专业环节留下的意见。</p>
      </div>
      <div class="summary-metrics">
        <div><strong>{{ audits.length }}</strong><span>待处理</span></div>
        <div><strong>{{ completedStageCount }}</strong><span>已完成环节</span></div>
        <div class="urgent"><strong>{{ urgentCount }}</strong><span>超过 3 天</span></div>
      </div>
    </div>

    <div class="layout">
      <!-- 左：待审核列表 -->
      <div class="list-panel">
        <div class="list-head">
          <div><span class="lh-title">审核队列</span><span class="lh-sub">按提交时间排序</span></div>
          <span class="lh-count">{{ audits.length }}</span>
        </div>
        <div
          v-for="a in audits"
          :key="a.id"
          class="audit-item"
          :class="{ active: a.id === active?.id }"
          @click="select(a)"
        >
          <span class="item-title">{{ a.title }}</span>
          <span class="item-meta">{{ a.applicant }}</span>
          <div class="item-foot">
            <span class="item-time">{{ a.submittedAt }}</span>
            <span class="item-progress">{{ a.progress }}%</span>
          </div>
          <div class="mini-progress"><i :style="{ width: `${a.progress}%` }"></i></div>
        </div>
      </div>

      <!-- 右：审核详情 -->
      <div v-if="active" class="detail-panel">
        <div class="detail-head">
          <div>
            <span class="detail-kicker">课程审核</span>
            <div class="detail-title">{{ active.title }}</div>
            <div class="detail-meta">
              <span>申请人：{{ active.applicant }}</span>
              <span>提交时间：{{ active.submittedAt }}</span>
            </div>
          </div>
          <span class="review-status">审核中 · {{ active.progress }}%</span>
        </div>

        <!-- 拟物化流程条 -->
        <div class="stepper-box">
          <h4 class="sub-title">审核进度</h4>
          <AuditStepper :stages="active.stages" :progress="active.progress" />
        </div>

        <!-- 审核意见区：每条前缀环节小图标 + 环节名 -->
        <div class="comment-box">
          <h4 class="sub-title">审核意见</h4>
          <div
            v-for="(c, i) in active.comments"
            :key="i"
            class="comment"
          >
            <StageIcon :stage="c.stageKey" :done="true" />
            <div class="comment-body">
              <div class="comment-head">
                <span class="stage-name">{{ c.stageLabel }}</span>
                <span class="reviewer">{{ c.reviewer }} · {{ c.time }}</span>
              </div>
              <p class="comment-content">{{ c.content }}</p>
            </div>
          </div>
          <EmptyState v-if="!active.comments.length" compact description="暂无审核意见" />
        </div>

        <!-- 操作区：通过 / 驳回 -->
        <div class="action-box">
          <div class="action-copy">
            <strong>完成本次决策</strong>
            <span>通过后课程将进入发布流程；驳回时需要填写明确原因。</span>
          </div>
          <div class="action-buttons">
            <el-button class="reject-btn" @click="handleReject">退回修改</el-button>
            <el-button type="primary" class="approve-btn" @click="handleApprove">通过审核</el-button>
          </div>
        </div>
      </div>
      <div v-else class="detail-empty">
        <EmptyState description="当前没有待审核课程" hint="左侧列表出现新课程后可在此处理" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { fetchAuditList, approveAudit, rejectAudit } from '@/api/audit';
import type { AuditItem } from '@/types/dashboard';
import AuditStepper from '@/components/audit/AuditStepper.vue';
/* V3：四环节植物小图标复用共享组件，删除本文件内的重复实现 */
import StageIcon from '@/components/audit/StageIcon.vue';
import EmptyState from '@/components/common/EmptyState.vue';

const audits = ref<AuditItem[]>([]);
const active = ref<AuditItem | null>(null);
const completedStageCount = computed(() => audits.value.reduce((sum, item) => sum + item.stages.filter((stage) => stage.done).length, 0));
const urgentCount = computed(() => audits.value.filter((item) => item.progress < 50).length);

onMounted(load);

async function load() {
  audits.value = await fetchAuditList();
  active.value = audits.value.length ? audits.value[0] : null;
}

function select(a: AuditItem) {
  active.value = a;
}

async function handleApprove() {
  if (!active.value) return;
  try {
    await approveAudit(active.value.id);
    ElMessage.success('已通过');
    await load();
  } catch {
    /* message 已在 request 处理 */
  }
}

async function handleReject() {
  if (!active.value) return;
  try {
    const { value } = await ElMessageBox.prompt('请输入驳回原因', '驳回课程', {
      confirmButtonText: '确认驳回',
      cancelButtonText: '取消',
      inputPattern: /^[\s\S]+$/,
      inputErrorMessage: '驳回原因不能为空',
    });
    await rejectAudit(active.value.id, value);
    ElMessage.success('已驳回');
    await load();
  } catch {
    /* 取消或失败 */
  }
}
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 18px; }
.audit-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  padding: 24px 28px;
  background: var(--brand-ink);
  color: #fff;
}
.summary-kicker { font-size: 11px; font-weight: 600; color: rgba(255,255,255,.58); letter-spacing: .12em; }
.audit-summary h2 { margin: 5px 0 0; font-size: 22px; }
.audit-summary p { margin: 6px 0 0; font-size: 13px; color: rgba(255,255,255,.68); }
.summary-metrics { display: grid; grid-template-columns: repeat(3, 110px); flex-shrink: 0; }
.summary-metrics > div { padding: 4px 18px; border-left: 1px solid rgba(255,255,255,.14); }
.summary-metrics strong, .summary-metrics span { display: block; }
.summary-metrics strong { font-size: 25px; line-height: 1.2; }
.summary-metrics span { margin-top: 5px; font-size: 11px; color: rgba(255,255,255,.58); }
.summary-metrics .urgent strong { color: #FFAD78; }
.layout {
  display: grid;
  grid-template-columns: 310px minmax(0, 1fr);
  gap: 16px;
  align-items: stretch;
}
/* 左列表 */
.list-panel {
  background: var(--bg-card);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  overflow: hidden;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 66px;
  padding: 12px 18px;
  border-bottom: 1px solid var(--divider-soft);
}
.lh-title {
  display: block;
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.lh-sub { display: block; margin-top: 2px; font-size: 11px; color: var(--text-secondary); }
.lh-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  height: 24px;
  border-radius: 12px;
  background: var(--brand-primary-light);
  font-size: 12px;
  font-weight: 700;
  color: var(--brand-primary-dark);
}
.audit-item {
  position: relative;
  padding: 15px 18px 13px;
  border-bottom: 1px solid var(--divider-soft);
  cursor: pointer;
}
.audit-item:last-child {
  border-bottom: none;
}
.audit-item.active {
  background: #EDF6F3;
}
.audit-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 12px;
  bottom: 12px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: var(--brand-primary);
}
.item-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.item-meta {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}
.item-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
}
.item-time {
  font-size: 13px;
  color: var(--text-secondary);
}
.item-progress {
  font-size: 13px;
  font-weight: 700;
  color: var(--brand-deep);
}
.mini-progress { height: 3px; margin-top: 9px; overflow: hidden; border-radius: 3px; background: var(--brand-primary-light); }
.mini-progress i { display: block; height: 100%; border-radius: inherit; background: var(--brand-primary); }
/* 右详情 */
.detail-panel {
  background: var(--bg-card);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  padding: 0;
  overflow: hidden;
}
.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 24px 26px 20px;
  border-bottom: 1px solid var(--divider-soft);
}
.detail-kicker { display: block; margin-bottom: 5px; font-size: 11px; font-weight: 600; color: var(--brand-primary); letter-spacing: .12em; }
.review-status { flex-shrink: 0; padding: 5px 9px; border-radius: 5px; background: var(--brand-primary-light); font-size: 12px; font-weight: 600; color: var(--brand-primary-dark); }
.detail-title {
  font-size: var(--font-size-l);
  font-weight: 600;
  color: var(--text-primary);
}
.detail-meta {
  display: flex;
  gap: var(--space-l);
  margin-top: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}
.sub-title {
  margin: 0 0 var(--space-m);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.stepper-box,
.comment-box {
  margin: 0;
  padding: 22px 26px;
}
.stepper-box {
  background: #F8FAF7;
  border-bottom: 1px solid var(--divider-soft);
}
.comment-box {
  padding-bottom: 8px;
}
.comment {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--divider-soft);
}
.comment:last-child {
  border-bottom: none;
}
.comment-body {
  min-width: 0;
}
.comment-head {
  display: flex;
  align-items: center;
  gap: var(--space-m);
}
.stage-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--brand-primary-dark);
}
.reviewer {
  font-size: 13px;
  color: var(--text-secondary);
}
.comment-content {
  margin: 6px 0 0;
  font-size: 15px;
  color: var(--text-regular);
  line-height: 1.7;
}
/* 操作区 */
.action-box {
  position: sticky;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 16px 26px;
  border-top: 1px solid var(--divider-soft);
  background: rgba(255,255,255,.96);
}
.action-copy { min-width: 0; display: flex; flex-direction: column; }
.action-copy strong { font-size: 13px; color: var(--text-primary); }
.action-copy span { margin-top: 2px; font-size: 11px; color: var(--text-secondary); }
.action-buttons { flex-shrink: 0; display: flex; gap: 8px; }
.action-buttons .el-button + .el-button { margin-left: 0; }
.approve-btn {
  --el-button-bg-color: var(--brand-primary);
  --el-button-border-color: var(--brand-primary);
  --el-button-hover-bg-color: var(--brand-primary-dark);
  --el-button-hover-border-color: var(--brand-primary-dark);
}
.reject-btn {
  --el-button-text-color: var(--brand-primary-dark);
  --el-button-border-color: rgba(var(--rgb-brand-ink), 0.3);
  --el-button-hover-bg-color: transparent;
}
.detail-empty {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}
@media (max-width: 1100px) {
  .audit-summary { align-items: flex-start; }
  .summary-metrics { grid-template-columns: repeat(3, 88px); }
  .layout { grid-template-columns: 270px minmax(0, 1fr); }
}
</style>
