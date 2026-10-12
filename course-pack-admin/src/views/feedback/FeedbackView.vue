<template>
  <div class="page">
    <!-- 副标题 -->
    <p class="page-sub">教师对平台的疑问与建议 · 工单回复后同步写入最新动态</p>

    <div class="layout">
      <!-- 左：工单列表 -->
      <div class="list-panel card-leaf">
        <div class="filter-bar">
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
        <div class="list-scroll">
          <div
            v-for="t in filtered"
            :key="t.id"
            class="ticket-item"
            :class="{ active: t.id === active?.id }"
            @click="active = t"
          >
            <div class="item-head">
              <span class="item-title">{{ t.title }}</span>
              <span class="status-dot" :class="t.status === 'PENDING' ? 'pending' : 'replied'">
                {{ t.status === 'PENDING' ? '待回复' : '已回复' }}
              </span>
            </div>
            <span class="item-meta">{{ t.teacher }} · {{ t.team }}</span>
            <span class="item-time">{{ t.time }}</span>
          </div>
        </div>
      </div>

      <!-- 右：详情 + 回复框 -->
      <div v-if="active" class="detail-panel card-leaf">
        <div class="detail-head">
          <h3 class="detail-title">{{ active.title }}</h3>
          <span class="status-dot" :class="active.status === 'PENDING' ? 'pending' : 'replied'">
            {{ active.status === 'PENDING' ? '待回复' : '已回复' }}
          </span>
        </div>
        <div class="detail-meta">
          <span>提问教师：{{ active.teacher }}</span>
          <span>所属队伍：{{ active.team }}</span>
          <span>提问时间：{{ active.time }}</span>
        </div>

        <!-- 提问内容 -->
        <div class="question-box card-leaf-sm">
          <p class="question-content">{{ active.content }}</p>
        </div>

        <!-- 已回复：回复卡片 -->
        <div v-if="active.status === 'REPLIED' && active.reply" class="reply-card card-leaf-sm">
          <div class="reply-head">
            <span class="reply-badge">回复</span>
            <span class="reply-meta">{{ active.reply.admin }} · {{ active.reply.time }}</span>
          </div>
          <p class="reply-content">{{ active.reply.content }}</p>
        </div>

        <!-- 待回复：回复框 -->
        <div v-else class="reply-editor">
          <h4 class="sub-title">回复教师</h4>
          <el-input
            v-model="replyText"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入回复内容，提交后工单状态将变为「已回复」并通知教师…"
          />
          <div class="reply-actions">
            <el-button type="primary" :disabled="!replyText.trim()" @click="onReply">提交回复</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { fetchTickets, replyTicket } from '@/api/feedback';
import type { FeedbackTicket } from '@/types/dashboard';

/** 筛选：全部 / 待回复 / 已回复 */
const filters = [
  { key: 'ALL', label: '全部' },
  { key: 'PENDING', label: '待回复' },
  { key: 'REPLIED', label: '已回复' },
] as const;

type FilterKey = (typeof filters)[number]['key'];
const current = ref<FilterKey>('ALL');
const tickets = ref<FeedbackTicket[]>([]);
const active = ref<FeedbackTicket | null>(null);
const replyText = ref('');

const filtered = computed(() =>
  current.value === 'ALL' ? tickets.value : tickets.value.filter((t) => t.status === current.value),
);

function countOf(key: FilterKey): number {
  return key === 'ALL' ? tickets.value.length : tickets.value.filter((t) => t.status === key).length;
}

async function load() {
  tickets.value = await fetchTickets();
  if (tickets.value.length) active.value = tickets.value[0];
}

async function onReply() {
  if (!active.value || !replyText.value.trim()) return;
  await replyTicket(active.value.id, replyText.value.trim());
  ElMessage.success('回复成功，已通知教师并写入最新动态');
  replyText.value = '';
}

onMounted(load);
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--space-m);
  padding-bottom: var(--space-l);
}
.page-sub {
  margin: 0;
  font-size: var(--font-size-s);
  color: var(--text-secondary);
}
.layout {
  display: grid;
  grid-template-columns: 380px 1fr;
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
.filter-bar {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--divider-soft);
}
.filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 30px;
  padding: 0 12px;
  border-radius: 15px;
  font-size: 13px;
  color: var(--text-regular);
  background: var(--bg-oat);
  cursor: pointer;
  transition: all 0.15s ease;
}
.filter-tab.active {
  background: var(--brand-primary);
  color: var(--on-brand);
  font-weight: 600;
}
.count {
  font-size: 12px;
  opacity: 0.75;
  font-variant-numeric: tabular-nums;
}
.list-scroll {
  max-height: 560px;
  overflow-y: auto;
}
.ticket-item {
  display: block;
  padding: 14px 16px;
  border-bottom: 1px solid var(--divider-soft);
  cursor: pointer;
  transition: background 0.15s ease;
}
.ticket-item:last-child {
  border-bottom: none;
}
.ticket-item:hover {
  background: var(--bg-page);
}
.ticket-item.active {
  background: var(--brand-primary-light);
}
.item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}
.item-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}
.item-time {
  display: block;
  margin-top: 2px;
  font-size: 13px;
  color: var(--text-secondary);
}
/* 状态徽章：待回复暖橙 / 已回复墨绿 */
.status-dot {
  flex-shrink: 0;
  padding: 1px 10px;
  height: 22px;
  line-height: 20px;
  border-radius: 11px;
  font-size: 12px;
  white-space: nowrap;
}
.status-dot.pending {
  color: var(--warn-text);
  background: var(--brand-secondary-light);
}
.status-dot.replied {
  color: var(--brand-primary-dark);
  background: var(--brand-primary-light);
}
/* 右详情 */
.detail-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: var(--space-l);
  min-height: 420px;
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.detail-head {
  display: flex;
  align-items: center;
  gap: var(--space-m);
}
.detail-title {
  margin: 0;
  font-size: var(--font-size-l);
  font-weight: 600;
  color: var(--text-primary);
}
.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-l);
  margin-top: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}
.question-box {
  margin-top: var(--space-l);
  padding: var(--space-m);
  background: var(--bg-page);
  border: 1px solid var(--border-warm);
  /* 圆角由全局 .card-leaf-sm 叶片裁角提供（V3） */
}
.question-content {
  margin: 0;
  font-size: 15px;
  color: var(--text-regular);
  line-height: 1.8;
}
.sub-title {
  margin: 0 0 var(--space-s);
  font-size: var(--font-size-m);
  font-weight: 600;
  color: var(--text-primary);
}
/* 回复卡片（已回复） */
.reply-card {
  margin-top: var(--space-l);
  padding: var(--space-m);
  background: var(--brand-primary-light);
  border: 1px solid var(--border-brand);
  /* 圆角由全局 .card-leaf-sm 叶片裁角提供（V3） */
}
.reply-head {
  display: flex;
  align-items: center;
  gap: 10px;
}
.reply-badge {
  padding: 1px 10px;
  height: 22px;
  line-height: 20px;
  border-radius: 11px;
  font-size: 12px;
  font-weight: 600;
  color: var(--on-brand);
  background: var(--brand-primary);
}
.reply-meta {
  font-size: 13px;
  color: var(--text-secondary);
}
.reply-content {
  margin: 10px 0 0;
  font-size: 15px;
  color: var(--text-regular);
  line-height: 1.8;
}
/* 回复编辑（待回复） */
.reply-editor {
  margin-top: var(--space-l);
}
.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--space-m);
}
@media (max-width: 1100px) {
  .layout { grid-template-columns: 1fr; }
  .list-scroll { max-height: 320px; }
  .detail-panel { min-height: 360px; }
}
@media (max-width: 700px) {
  .filter-bar { flex-wrap: wrap; }
  .detail-panel { padding: var(--space-m); }
  .detail-head { align-items: flex-start; flex-direction: column; gap: 8px; }
  .detail-meta { gap: 6px 18px; }
}
</style>
