<template>
  <div class="status-page">
    <div class="card card-leaf-lg">
      <!-- 状态图标（墨绿单色线条） -->
      <div class="ico-wrap" :class="mode">
        <view-scene :kind="mode" />
      </div>
      <h2 class="title">{{ title }}</h2>
      <p class="desc">{{ desc }}</p>

      <!-- REJECTED：展示驳回原因 + 重新提交 -->
      <div v-if="mode === 'rejected'" class="reject-box card-leaf">
        <div class="reason-row">
          <span class="reason-label">驳回原因</span>
          <span class="reason-text">{{ auth.user?.rejectReason || '未填写' }}</span>
        </div>
        <el-button type="primary" class="resubmit" @click="resubmit">重新提交材料</el-button>
      </div>

      <el-button v-else @click="logout" class="back">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineComponent, h } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/store/auth';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const mode = computed<'pending' | 'rejected' | 'disabled'>(() => {
  const m = String(route.query.mode || '');
  return (['pending', 'rejected', 'disabled'].includes(m) ? m : 'pending') as
    | 'pending'
    | 'rejected'
    | 'disabled';
});

const title = computed(() =>
  mode.value === 'pending' ? '账号审核中' : mode.value === 'rejected' ? '审核未通过' : '账号已停用',
);

const desc = computed(() => {
  if (mode.value === 'pending') return '你的申请正在审核，请耐心等待。通过后自动进入教师工作台。';
  if (mode.value === 'rejected') return '很遗憾，你的申请未通过审核。可补充材料后重新提交。';
  return '该账号已被停用，无法继续使用，请联系管理员。';
});

/** 状态场景插画（线性单色，替代彩色 emoji） */
const ViewScene = defineComponent({
  props: { kind: { type: String as () => 'pending' | 'rejected' | 'disabled', required: true } },
  setup(props) {
    return () =>
      h(
        'svg',
        { viewBox: '0 0 80 80', fill: 'none', style: 'width:96px;height:96px' },
        props.kind === 'pending'
          ? [
              h('path', { d: 'M40 62V30', style: { stroke: 'var(--line-brand)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round' }),
              h('path', { d: 'M40 32c-9 0-15-5-15-12 9 0 15 5 15 12Z', style: { stroke: 'var(--line-brand)' }, 'stroke-width': 2.5, 'stroke-linejoin': 'round' }),
              h('path', { d: 'M40 32c9-1 15-5 15-13-9 1-15 6-15 13Z', style: { stroke: 'var(--line-brand)' }, 'stroke-width': 2.5, 'stroke-linejoin': 'round' }),
              h('path', { d: 'M28 62c2-9 6-13 12-13s10 4 12 13', style: { stroke: 'var(--line-brand)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round' }),
            ]
          : props.kind === 'rejected'
            ? [
                h('circle', { cx: 40, cy: 36, r: 15, style: { stroke: 'var(--brand-secondary)' }, 'stroke-width': 2.5 }),
                h('path', { d: 'M70 66l-6-4 6 4Zm0 0v-7', style: { stroke: 'var(--brand-secondary)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round' }),
                h('circle', { cx: 40, cy: 36, r: 0.1, style: { fill: 'var(--brand-secondary)' } }),
                h('path', { d: 'M40 30v7', style: { stroke: 'var(--brand-secondary)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round' }),
                h('circle', { cx: 40, cy: 42, r: 1.6, style: { fill: 'var(--brand-secondary)' } }),
              ]
            : [
                h('path', { d: 'M40 18v30M40 20c-7 0-11-4-11-10 7 0 11 4 11 10ZM40 20c7 0 11-4 11-10-7 0-11 4-11 10ZM40 48h0', style: { stroke: 'var(--text-weak)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
                h('path', { d: 'M22 62h36', style: { stroke: 'var(--text-weak)' }, 'stroke-width': 2.5, 'stroke-linecap': 'round' }),
              ],
      );
  },
});

function resubmit() {
  // 仅演示：提示重新提交成功（真实流程需后端提交接口）
  ElMessage.success('已提交，请等待重新审核');
  auth.setUser({ ...(auth.user as NonNullable<typeof auth.user>), status: 'PENDING' });
  router.replace('/teacher/status?mode=pending');
}

function logout() {
  auth.logout();
  router.push('/login');
}
</script>

<style scoped>
.status-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-page);
}
.card {
  width: 460px;
  background: var(--bg-card);
  border: 1px solid var(--border-warm);
  padding: 48px 40px;
  text-align: center;
  /* 圆角由全局 .card-leaf-lg 叶片裁角提供（V3） */
}
.ico-wrap {
  width: 96px;
  height: 96px;
  margin: 0 auto;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ico-wrap.pending {
  background: rgba(var(--rgb-brand-primary), 0.1);
}
.ico-wrap.rejected {
  background: rgba(var(--rgb-brand-secondary), 0.12);
}
.ico-wrap.disabled {
  background: rgba(var(--rgb-text-weak), 0.15);
}
.title {
  margin: 20px 0 8px;
  font-family: var(--font-serif);
  font-size: var(--font-size-xl);
  color: var(--text-primary);
}
.desc {
  margin: 0;
  font-size: var(--font-size-s);
  color: var(--text-regular);
  line-height: 1.7;
}
.reject-box {
  margin-top: 24px;
  text-align: left;
  border: 1px solid var(--border-warm);
  padding: 16px;
  background: var(--bg-oat);
  /* 圆角由全局 .card-leaf 叶片裁角提供（V3） */
}
.reason-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}
.reason-label {
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}
.reason-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
}
.resubmit {
  width: 100%;
  margin-top: 16px;
  border-color: transparent;
  background: var(--brand-primary);
}
.back {
  margin-top: 24px;
}
</style>