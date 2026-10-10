<template>
  <view class="page">
    <view class="notice">
      <text class="notice-title">先保护隐私，再分享经验</text>
      <text class="notice-text">请使用化名或概括性描述，不填写儿童姓名、联系方式、家庭住址和清晰正脸照片。</text>
    </view>

    <view class="form-card">
      <text class="field-label">案例标题 <text class="required">*</text></text>
      <input v-model="form.title" class="input" maxlength="40" placeholder="一句话概括这次课堂实践" />

      <text class="field-label">适用年级 <text class="required">*</text></text>
      <view class="chip-row">
        <view v-for="grade in grades" :key="grade" class="chip" :class="{ active: form.grade === grade }" @tap="form.grade = grade">{{ grade }}</view>
      </view>

      <text class="field-label">案例主题 <text class="required">*</text></text>
      <view class="chip-row">
        <view v-for="theme in themes" :key="theme" class="chip" :class="{ active: form.theme === theme }" @tap="form.theme = theme">{{ theme }}</view>
      </view>

      <text class="field-label">关联课程 <text class="required">*</text></text>
      <input v-model="form.courseTitle" class="input" maxlength="40" placeholder="例如：我的身体，我做主" />

      <text class="field-label">案例摘要 <text class="required">*</text></text>
      <view class="textarea-wrap short">
        <textarea v-model="form.summary" class="textarea short-textarea" maxlength="160" placeholder="说明课堂目标、方法和主要效果" />
        <text class="word-count">{{ form.summary.length }}/160</text>
      </view>

      <text class="field-label">课堂实施 <text class="required">*</text></text>
      <view class="textarea-wrap">
        <textarea v-model="form.content" class="textarea" maxlength="1200" placeholder="记录教学准备、实施步骤、学生参与和现场调整。不要写入可识别儿童身份的信息。" />
        <text class="word-count">{{ form.content.length }}/1200</text>
      </view>

      <text class="field-label">教学反思 <text class="required">*</text></text>
      <view class="textarea-wrap reflection">
        <textarea v-model="form.reflection" class="textarea reflection-textarea" maxlength="600" placeholder="哪些做法有效？下次会如何调整？有哪些需要其他教师注意的边界？" />
        <text class="word-count">{{ form.reflection.length }}/600</text>
      </view>
    </view>

    <!-- cover-view 才能稳定覆盖微信小程序原生 textarea，普通 view 即使提高 z-index 也会被穿透 -->
    <cover-view class="bottom-bar">
      <cover-view class="draft-btn pressable" :class="{ disabled: saving }" hover-class="hv-press" @tap="saveDraft">保存草稿</cover-view>
      <cover-view class="submit-btn pressable" :class="{ disabled: saving }" hover-class="hv-press" @tap="submitCase">
        {{ saving ? '处理中…' : '提交审核' }}
      </cover-view>
    </cover-view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { fetchTeachingCaseDetail, saveTeachingCaseDraft, submitTeachingCase } from '@/api/case';
import type { TeachingCasePayload } from '@/types/mock';

const grades: TeachingCasePayload['grade'][] = ['小学1-3年级', '小学4-6年级'];
const themes: TeachingCasePayload['theme'][] = ['身体与安全', '家庭与合作', '人际与尊重', '成长与变化', '综合主题'];
const caseId = ref<number>();
const saving = ref(false);
const form = reactive<TeachingCasePayload>({
  title: '',
  grade: '小学1-3年级',
  theme: '身体与安全',
  courseTitle: '',
  summary: '',
  content: '',
  reflection: '',
});

onLoad(async (options) => {
  const id = Number(options?.id) || undefined;
  if (!id) return;
  const existing = await fetchTeachingCaseDetail(id);
  if (!existing || !existing.isMine) return;
  caseId.value = id;
  Object.assign(form, {
    title: existing.title,
    grade: existing.grade,
    theme: existing.theme,
    courseTitle: existing.courseTitle,
    summary: existing.summary,
    content: existing.content,
    reflection: existing.reflection,
  });
  uni.setNavigationBarTitle({ title: '编辑案例' });
});

function payload(): TeachingCasePayload {
  return {
    title: form.title.trim(),
    grade: form.grade,
    theme: form.theme,
    courseTitle: form.courseTitle.trim(),
    summary: form.summary.trim(),
    content: form.content.trim(),
    reflection: form.reflection.trim(),
  };
}

function validate(data: TeachingCasePayload): boolean {
  if (!data.title) return showRequired('请填写案例标题');
  if (!data.courseTitle) return showRequired('请填写关联课程');
  if (!data.summary) return showRequired('请填写案例摘要');
  if (!data.content) return showRequired('请填写课堂实施过程');
  if (!data.reflection) return showRequired('请填写教学反思');
  if (data.content.length < 30) return showRequired('课堂实施至少填写 30 个字');
  return true;
}

function showRequired(message: string): false {
  uni.showToast({ title: message, icon: 'none' });
  return false;
}

async function saveDraft() {
  if (saving.value) return;
  const data = payload();
  if (!data.title) {
    showRequired('至少填写案例标题后再保存');
    return;
  }
  saving.value = true;
  try {
    const saved = await saveTeachingCaseDraft(data, caseId.value);
    caseId.value = saved.id;
    uni.showToast({ title: '草稿已保存', icon: 'success' });
    setTimeout(() => uni.redirectTo({ url: `/subpkg/case/detail?id=${saved.id}` }), 500);
  } finally {
    saving.value = false;
  }
}

async function submitCase() {
  if (saving.value) return;
  const data = payload();
  if (!validate(data)) return;
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '确认提交审核？',
      content: '提交后暂时不能编辑，审核人员会检查内容与隐私风险。',
      confirmText: '提交审核',
      success: (result) => resolve(result.confirm),
      fail: () => resolve(false),
    });
  });
  if (!confirmed) return;
  saving.value = true;
  try {
    const saved = await submitTeachingCase(data, caseId.value);
    uni.showToast({ title: '已提交审核', icon: 'success' });
    setTimeout(() => uni.redirectTo({ url: `/subpkg/case/detail?id=${saved.id}` }), 500);
  } finally {
    saving.value = false;
  }
}
</script>

<style scoped lang="scss">
.page { min-height: 100vh; padding: $space-m $space-m calc(env(safe-area-inset-bottom) + 176rpx); background: $bg-page; }
.notice { padding: $space-m; border-radius: $radius-card; background: $brand-primary-light; display: flex; flex-direction: column; gap: 6rpx; }
.notice-title { font-size: $font-size-s; font-weight: $font-weight-bold; color: $brand-primary-dark; }
.notice-text { font-size: 22rpx; line-height: 1.6; color: $text-regular; }
.form-card { margin-top: $space-m; padding: $space-m; border: $border-card; border-radius: $radius-card; border-top-left-radius: $radius-card-tl; background: $bg-card; display: flex; flex-direction: column; }
.field-label { margin: $space-m 0 $space-s; font-size: $font-size-s; font-weight: $font-weight-bold; color: $text-primary; }
.field-label:first-child { margin-top: 0; }
.required { color: $danger; }
.input { height: 80rpx; padding: 0 $space-s; border-radius: $radius-s; background: $bg-page; font-size: $font-size-s; color: $text-primary; }
.chip-row { display: flex; flex-wrap: wrap; gap: $space-s; }
.chip { min-height: 56rpx; padding: 0 22rpx; display: flex; align-items: center; border-radius: 28rpx; background: $bg-oat; font-size: 22rpx; color: $text-regular; }
.chip.active { background: $brand-primary-light; color: $brand-primary-dark; font-weight: $font-weight-bold; }
.textarea-wrap { position: relative; padding: $space-s; border-radius: $radius-s; background: $bg-page; }
.textarea { width: 100%; height: 320rpx; font-size: $font-size-s; line-height: 1.7; color: $text-primary; }
.short-textarea { height: 180rpx; }
.reflection { background: $brand-primary-light; }
.reflection-textarea { height: 240rpx; }
.word-count { display: block; text-align: right; font-size: 20rpx; color: $text-secondary; }
.bottom-bar { position: fixed; z-index: 100; left: 0; right: 0; bottom: 0; padding: 18rpx $space-m calc(env(safe-area-inset-bottom) + 18rpx); border-top: 1rpx solid rgba(31, 52, 49, .08); background: $bg-card; box-shadow: 0 -12rpx 32rpx rgba(31, 52, 49, .10); display: flex; gap: $space-s; }
.draft-btn, .submit-btn { height: 88rpx; border-radius: 44rpx; display: flex; align-items: center; justify-content: center; font-size: $font-size-m; font-weight: $font-weight-bold; }
.draft-btn { width: 220rpx; border: 2rpx solid rgba(35, 140, 130, .72); color: $brand-primary; background: #F8FCFB; }
.submit-btn { flex: 1; color: #fff; background: $brand-primary; box-shadow: 0 8rpx 18rpx rgba(35, 140, 130, .18); }
.disabled { opacity: .6; }
</style>
