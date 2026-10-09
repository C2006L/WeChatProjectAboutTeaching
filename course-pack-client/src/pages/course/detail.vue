<template>
  <view v-if="course" class="page">
    <CourseDetailHeader :course="course" />

    <view class="card">
      <text class="card-title">课程材料</text>
      <MaterialIconRow :materials="course.materials" />
    </view>

    <view class="card">
      <view class="tabs">
        <view
          v-for="t in tabs"
          :key="t.key"
          class="tab"
          :class="{ active: activeTab === t.key }"
          @tap="activeTab = t.key"
        >
          <text class="tab-text">{{ t.label }}</text>
        </view>
      </view>

      <!-- 教学流程 -->
      <StepFlow v-if="activeTab === 'flow'" :steps="course.steps" class="tab-panel" />

      <!-- 课程目标 -->
      <view v-else-if="activeTab === 'objectives'" class="tab-panel">
        <view v-for="(obj, i) in course.objectives" :key="i" class="list-row">
          <view class="list-icon ok"></view>
          <text class="list-text">{{ obj }}</text>
        </view>
      </view>

      <!-- 价值总结 -->
      <view v-else-if="activeTab === 'values'" class="tab-panel">
        <view v-for="(v, i) in course.values" :key="i" class="list-row">
          <view class="list-icon icon-value"></view>
          <text class="list-text">{{ v }}</text>
        </view>
      </view>

      <!-- 敏感问题 -->
      <view v-else class="tab-panel qa-panel">
        <QAListItem v-for="qa in relatedQa" :key="qa.id" :qa="qa" />
      </view>
    </view>

    <view class="bottom-bar">
      <view class="fav-side">
        <FavButton :course-id="course.id" :box="88" :icon="40" />
      </view>
      <view class="btn-start pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goPrepare">
        <text class="btn-text">开始备课</text>
      </view>
    </view>
  </view>

  <view v-else class="empty">
    <text class="empty-text">课程不存在或已下架</text>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import CourseDetailHeader from '@/components/course/CourseDetailHeader.vue';
import MaterialIconRow from '@/components/course/MaterialIconRow.vue';
import StepFlow from '@/components/course/StepFlow.vue';
import QAListItem from '@/components/qa/QAListItem.vue';
import FavButton from '@/components/common/FavButton.vue';
import { fetchCourseDetail } from '@/api/course';
import { fetchQaByIds } from '@/api/qa';
import type { CourseDetail, QA } from '@/types/mock';

const tabs = [
  { key: 'flow', label: '教学流程' },
  { key: 'objectives', label: '课程目标' },
  { key: 'values', label: '价值总结' },
  { key: 'qa', label: '敏感问题' },
] as const;

const activeTab = ref<(typeof tabs)[number]['key']>('flow');
const course = ref<CourseDetail | null>(null);
const relatedQa = ref<QA[]>([]);

onLoad(async (options) => {
  const id = Number(options?.id);
  if (!id) return;
  course.value = await fetchCourseDetail(id);
  if (course.value) {
    relatedQa.value = await fetchQaByIds(course.value.sensitiveQa);
  }
});

function goPrepare() {
  uni.navigateTo({ url: '/subpkg/tools/prepare' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc($tab-bar-height + 160rpx);
  background: $bg-page;
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.card {
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  padding: $space-m;
}

.card-title {
  display: block;
  margin-bottom: $space-m;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.tabs {
  display: flex;
  gap: $space-s;
  flex-wrap: wrap;
}

.tab {
  padding: 8rpx 24rpx;
  border-radius: 28rpx;
  background: $bg-page;
}

.tab.active {
  background: $brand-primary-light;
}

.tab-text {
  font-size: $font-size-xs;
  color: $text-regular;
}

.tab.active .tab-text {
  color: $brand-primary;
  font-weight: $font-weight-bold;
}

.tab-panel {
  margin-top: $space-m;
}

.list-row {
  display: flex;
  align-items: flex-start;
  gap: $space-s;
  margin-bottom: $space-s;

  &:last-child {
    margin-bottom: 0;
  }
}

/* 行首图标：1.5px 线性墨绿单色（对勾 / 小芽） */
.list-icon {
  flex-shrink: 0;
  width: 30rpx;
  height: 30rpx;
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;

  &.ok {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m5 12.5 4.5 4.5L19 7.5'/%3E%3C/svg%3E");
  }

  &.icon-value {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 20v-7'/%3E%3Cpath d='M12 14c-3.5 0-6-2-6-5.5C9.5 8.5 12 10.5 12 14Z'/%3E%3Cpath d='M12 14c3.5 0 6-2 6-5.5C14.5 8.5 12 10.5 12 14Z'/%3E%3C/svg%3E");
  }
}

.list-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-regular;
  line-height: 1.6;
}

.qa-panel {
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: $space-s $space-m calc(env(safe-area-inset-bottom) + $space-s);
  background: $bg-card;
  box-shadow: $shadow-float;
  display: flex;
  align-items: center;
  gap: $space-m;
}

.fav-side {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-start {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  background: $brand-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  --press-shadow: none;
}

.btn-text {
  color: #FFFFFF;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
}

.empty {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-page;
}

.empty-text {
  font-size: $font-size-s;
  color: $text-secondary;
}
</style>
