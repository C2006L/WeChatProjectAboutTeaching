<template>
  <div class="page">
    <div class="layout">
      <!-- 左：待审核列表 -->
      <div class="list-panel card-leaf">
        <div class="list-head">
          <span class="lh-title">待审核课程</span>
          <span class="lh-count">{{ audits.length }} 条</span>
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
        </div>
      </div>

      <!-- 右：审核详情 -->
      <div v-if="active" class="detail-panel card-leaf">
        <div class="detail-title">{{ active.title }}</div>
        <div class="detail-meta">
          <span>申请人：{{ active.applicant }}</span>
          <span>提交时间：{{ active.submittedAt }}</span>
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
          <el-button type="primary" class="approve-btn" @click="handleApprove">通过审核</el-button>
          <el-button class="reject-btn" @click="handleReject">驳回</el-button>
        </div>
      </div>
      <div v-else class="detail-empty">
        <EmptyState description="当前没有待审核课程" hint="左侧列表出现新课程后可在此处理" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { fetchAuditList, approveAudit, rejectAudit } from '@/api/audit';
import type { AuditItem } from '@/types/dashboard';
import AuditStepper from '@/components/audit/AuditStepper.vue';
/* V3：四环节植物小图标复用共享组件，删除本文件内的重复实现 */
import StageIcon from '@/components/audit/StageIcon.vue';
import EmptyState from '@/components/common/EmptyState.vue';

const audits = ref<AuditItem[]>([]);
const active = ref<AuditItem | null>(null);

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
.layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: var(--space-l);
  align-items: start;
}
/* 左列表 */
.list-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  overflow: hidden;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.list-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--divider-soft);
}
.lh-title {
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
.lh-count {
  font-size: 13px;
  color: var(--text-secondary);
}
.audit-item {
  padding: 14px 20px;
  border-bottom: 1px solid var(--divider-soft);
  cursor: pointer;
}
.audit-item:last-child {
  border-bottom: none;
}
.audit-item.active {
  background: var(--brand-primary-light);
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
  font-family: var(--font-serif);
  font-size: 15px;
  font-weight: 650;
  color: var(--brand-deep);
}
/* 右详情 */
.detail-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
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
  margin-top: var(--space-l);
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
  margin-top: var(--space-l);
  display: flex;
  gap: var(--space-m);
}
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
</style>