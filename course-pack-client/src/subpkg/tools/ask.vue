<template>
  <view class="page">
    <view class="card">
      <!-- 问题分类 -->
      <text class="field-label">问题分类</text>
      <view class="chip-row">
        <view
          v-for="c in categories"
          :key="c"
          class="chip pressable"
          :class="{ active: form.category === c }"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="form.category = c"
        >
          <text class="chip-text">{{ c }}</text>
        </view>
      </view>

      <!-- 标题 -->
      <text class="field-label">问题标题</text>
      <input
        class="title-input"
        v-model="form.title"
        placeholder="一句话描述你的问题"
        maxlength="30"
      />

      <!-- 正文 -->
      <text class="field-label">详细描述</text>
      <view class="textarea-wrap">
        <textarea
          class="content-input"
          v-model="form.content"
          placeholder="补充场景、学段、孩子反应等信息，便于管理员给出可操作的建议……"
          maxlength="500"
        />
        <text class="word-count">{{ form.content.length }}/500</text>
      </view>
    </view>

    <text class="tip">提交后可在「问答支持 · 我的提问」中查看回复进度</text>

    <!-- 提交 -->
    <view class="submit-bar">
      <view
        class="submit-btn pressable"
        :class="{ disabled: submitting }"
        hover-class="hv-press"
        :hover-stay-time="80"
        @tap="onSubmit"
      >
        <text class="submit-text">{{ submitting ? '提交中…' : '提交提问' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { submitTicket } from '@/api/feedback';

const categories = ['身体与安全', '人际关系', '成长变化', '其他'] as const;

const form = reactive({
  category: '身体与安全',
  title: '',
  content: '',
});
const submitting = ref(false);

async function onSubmit() {
  if (submitting.value) return; // 防重复提交
  const title = form.title.trim();
  const content = form.content.trim();
  if (!title) {
    uni.showToast({ title: '请填写问题标题', icon: 'none' });
    return;
  }
  if (!content) {
    uni.showToast({ title: '请填写问题描述', icon: 'none' });
    return;
  }
  submitting.value = true;
  try {
    await submitTicket({ title, content, category: form.category });
    uni.showToast({ title: '提交成功', icon: 'success' });
    setTimeout(() => uni.navigateBack(), 600);
  } catch {
    uni.showToast({ title: '提交失败，请重试', icon: 'none' });
    submitting.value = false;
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc(env(safe-area-inset-bottom) + 140rpx);
  background: $bg-page;
}

.card {
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;
  padding: $card-padding;
  display: flex;
  flex-direction: column;
}

.field-label {
  margin-bottom: $space-s;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;

  &:not(:first-child) {
    margin-top: $space-m;
  }
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: $space-s;
}

.chip {
  padding: 12rpx 28rpx;
  border-radius: 32rpx;
  background: $bg-oat;
}

.chip.active {
  background: $brand-primary-light;
}

.chip-text {
  font-size: $font-size-xs;
  color: $text-regular;
}

.chip.active .chip-text {
  color: $brand-primary;
  font-weight: $font-weight-bold;
}

.title-input {
  height: 80rpx;
  padding: 0 $space-s;
  border-radius: $radius-s;
  background: $bg-page;
  font-size: $font-size-s;
  color: $text-primary;
}

.textarea-wrap {
  position: relative;
  border-radius: $radius-s;
  background: $bg-page;
  padding: $space-s;
}

.content-input {
  width: 100%;
  height: 280rpx;
  font-size: $font-size-s;
  color: $text-primary;
  line-height: 1.6;
}

.word-count {
  display: block;
  text-align: right;
  font-size: 22rpx;
  color: $text-secondary;
}

.tip {
  display: block;
  margin-top: $space-m;
  font-size: $font-size-xs;
  color: $text-secondary;
}

.submit-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $space-s $space-m calc(env(safe-area-inset-bottom) + $space-s);
  background: $bg-card;
  box-shadow: $shadow-float;
}

.submit-btn {
  height: 88rpx;
  border-radius: 44rpx;
  background: $brand-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  --press-shadow: none;
}

.submit-btn.disabled {
  opacity: 0.6;
}

.submit-text {
  color: #FFFFFF;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
}
</style>
