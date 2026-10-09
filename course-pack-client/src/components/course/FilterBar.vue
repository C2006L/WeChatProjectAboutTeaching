<template>
  <view class="filter-panel">
    <!-- 搜索 -->
    <view class="search-box">
      <view class="search-ico"></view>
      <input
        class="search-input"
        :value="filter.keyword"
        placeholder="搜索课程名称"
        confirm-type="search"
        @input="onKeywordInput"
      />
    </view>

    <view v-for="group in filterGroups" :key="group.key" class="filter-row">
      <text class="filter-label">{{ group.label }}</text>
      <scroll-view scroll-x :show-scrollbar="false" class="chip-scroll">
        <view class="chip-row">
          <view
            v-for="option in group.options"
            :key="option"
            class="chip"
            :class="{ active: filter[group.key] === option }"
            @tap="tapChip({ key: group.key, label: option })"
          >{{ option }}</view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { CourseFilter } from '@/api/course';

interface Props {
  filter: CourseFilter;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'change', filter: CourseFilter): void }>();

const filterGroups: { key: 'version' | 'grade' | 'theme'; label: string; options: string[] }[] = [
  { key: 'version', label: '版本', options: ['全部', '精简版', '标准版'] },
  { key: 'grade', label: '年级', options: ['全部', '1-3年级', '4-6年级'] },
  { key: 'theme', label: '主题', options: ['全部', '身体与安全', '家庭与合作', '人际与尊重', '成长与变化', '综合主题'] },
];

function tapChip(c: { key: 'version' | 'grade' | 'theme'; label: string }) {
  const patch: Partial<CourseFilter> = { [c.key]: c.label };
  emit('change', { ...props.filter, ...patch });
}

function onKeywordInput(e: Event) {
  const inputEvent = e as Event & { detail?: { value?: string } };
  const target = e.target as HTMLInputElement | null;
  const value = inputEvent.detail?.value ?? target?.value ?? '';
  emit('change', { ...props.filter, keyword: value });
}
</script>

<style scoped lang="scss">
.filter-panel {
  position: sticky;
  top: 0;
  z-index: 5;
  padding: 20rpx $space-m 18rpx;
  background: rgba(245, 247, 243, 0.96);
  border-bottom: 1rpx solid rgba(31, 52, 49, 0.09);
}

.search-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 0 22rpx;
  height: 76rpx;
  border: $border-card;
  border-radius: $radius-card;
  background: $bg-card;
}

.search-ico {
  flex-shrink: 0;
  width: 34rpx;
  height: 34rpx;
  /* 线性搜索图标（data-uri，小程序不支持 svg 标签） */
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none'%3E%3Ccircle cx='11' cy='11' r='6.5' stroke='%2336524D' stroke-width='1.5'/%3E%3Cpath d='m16 16 4.5 4.5' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round'/%3E%3C/svg%3E") center / contain no-repeat;
}

.search-input {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}

.chip-scroll {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
}

.filter-row {
  display: flex;
  align-items: center;
  margin-top: 16rpx;
}
.filter-label {
  flex-shrink: 0;
  width: 72rpx;
  font-size: 22rpx;
  color: $text-secondary;
}

.chip-row {
  display: inline-flex;
  gap: 8rpx;
}

.chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 50rpx;
  padding: 0 18rpx;
  border-radius: 8rpx;
  font-size: $font-size-xs;
  color: $text-regular;
  background: transparent;

  &.active {
    background: $brand-primary-light;
    color: $brand-primary-dark;
    font-weight: $font-weight-bold;
  }
}
</style>
