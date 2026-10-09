<template>
  <view class="prepare-form">
    <view class="form-section">
      <text class="section-label">授课年级</text>
      <view class="option-row">
        <view
          v-for="g in options.grades"
          :key="g"
          class="option"
          :class="{ active: grade === g }"
          @tap="grade = g"
        >
          {{ g }}
        </view>
      </view>
    </view>

    <view class="form-section">
      <text class="section-label">课程主题</text>
      <view class="option-row">
        <view
          v-for="t in options.themes"
          :key="t"
          class="option"
          :class="{ active: theme === t }"
          @tap="theme = t"
        >
          {{ t }}
        </view>
      </view>
    </view>

    <view class="form-section">
      <text class="section-label">课时长度</text>
      <view class="option-row">
        <view
          v-for="d in options.durations"
          :key="d"
          class="option"
          :class="{ active: duration === d }"
          @tap="duration = d"
        >
          {{ d }}分钟
        </view>
      </view>
    </view>

    <view class="btn-generate" :class="{ disabled: !ready }" @tap="onGenerate">
      <text class="btn-text">生成备课清单</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import type { PrepareOptions } from '@/api/prepare';

interface Props {
  options: PrepareOptions;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'generate', payload: { grade: string; theme: string; duration: number }): void;
}>();

const grade = ref<string>(props.options.grades[0] ?? '');
const theme = ref<string>(props.options.themes[0] ?? '');
const duration = ref<number>(props.options.durations[0] ?? 30);

const ready = computed(() => grade.value && theme.value && duration.value);

function onGenerate() {
  if (!ready.value) {
    uni.showToast({ title: '请先完成选择', icon: 'none' });
    return;
  }
  emit('generate', { grade: grade.value, theme: theme.value, duration: duration.value });
}
</script>

<style scoped lang="scss">
.prepare-form {
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  padding: $space-m;
}

.form-section {
  &:not(:first-child) {
    margin-top: $space-m;
  }
}

.section-label {
  display: block;
  font-size: $font-size-xs;
  color: $text-secondary;
  margin-bottom: $space-s;
}

.option-row {
  display: flex;
  flex-wrap: wrap;
  gap: $space-s;
}

.option {
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
  background: $bg-page;
  font-size: $font-size-xs;
  color: $text-regular;

  &.active {
    background: $brand-primary;
    color: #FFFFFF;
    font-weight: $font-weight-bold;
  }
}

.btn-generate {
  margin-top: $space-l;
  height: 88rpx;
  border-radius: 44rpx;
  background: $brand-primary;
  display: flex;
  align-items: center;
  justify-content: center;

  &.disabled {
    background: $text-disabled;
  }
}

.btn-text {
  color: #FFFFFF;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
}
</style>
