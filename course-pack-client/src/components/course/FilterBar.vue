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

    <view class="filter-triggers">
      <view
        v-for="group in filterGroups"
        :key="group.key"
        class="filter-trigger pressable"
        :class="{ active: expanded === group.key || filter[group.key] !== '全部' }"
        hover-class="hv-press"
        @tap="toggleGroup(group.key)"
      >
        <text>{{ group.label }}</text>
        <text class="trigger-value">{{ filter[group.key] }}</text>
        <view class="trigger-arrow" :class="{ open: expanded === group.key }"></view>
      </view>
    </view>

    <view v-if="activeGroup" class="option-panel">
      <text class="option-label">{{ activeGroup.label }}</text>
      <view class="chip-row">
        <view
          v-for="option in activeGroup.options"
          :key="option"
          class="chip"
          :class="{ active: filter[activeGroup.key] === option }"
          @tap="tapChip({ key: activeGroup.key, label: option })"
        >{{ option }}</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
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
const expanded = ref<'version' | 'grade' | 'theme' | ''>('');
const activeGroup = computed(() => filterGroups.find((group) => group.key === expanded.value));

function toggleGroup(key: 'version' | 'grade' | 'theme') {
  expanded.value = expanded.value === key ? '' : key;
}

function tapChip(c: { key: 'version' | 'grade' | 'theme'; label: string }) {
  const patch: Partial<CourseFilter> = { [c.key]: c.label };
  emit('change', { ...props.filter, ...patch });
  expanded.value = '';
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
  padding: 0 $space-m;
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
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none'%3E%3Ccircle cx='11' cy='11' r='6.5' stroke='%23253C37' stroke-width='1.5'/%3E%3Cpath d='m16 16 4.5 4.5' stroke='%23253C37' stroke-width='1.5' stroke-linecap='round'/%3E%3C/svg%3E") center / contain no-repeat;
}

.search-input {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}

.filter-triggers { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10rpx; margin-top: 12rpx; }
.filter-trigger { min-width: 0; height: 58rpx; padding: 0 14rpx; display: flex; align-items: center; gap: 5rpx; border-radius: 29rpx; border: $border-card; background: $bg-card; font-size: 23rpx; color: $text-secondary; }
.filter-trigger.active { border-color: rgba(35,140,130,.2); background: $brand-primary-light; color: $brand-primary-dark; }
.trigger-value { flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-weight: 600; }
.trigger-arrow { width: 20rpx; height: 20rpx; background: center / contain no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237B8986' stroke-width='1.7' stroke-linecap='round'%3E%3Cpath d='m6 9 6 6 6-6'/%3E%3C/svg%3E"); transition: transform $motion-fast $motion-ease; }
.trigger-arrow.open { transform: rotate(180deg); }
.option-panel { margin-top: 14rpx; padding: 20rpx; border: $border-card; border-radius: $radius-card; background: $bg-card; }
.option-label { display: block; margin-bottom: 12rpx; font-size: 24rpx; font-weight: 600; color: $text-primary; }

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 54rpx;
  padding: 0 20rpx;
  border-radius: 27rpx;
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
