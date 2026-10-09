<template>
  <view class="page">
    <view class="head">
      <text class="head-title">备课助手</text>
      <text class="head-desc">三步选择，快速生成属于你的备课清单</text>
    </view>

    <!-- 选项表单 -->
    <PrepareStepForm v-if="options" :options="options" @generate="onGenerate" />

    <!-- 生成结果 -->
    <view v-if="result" class="result">
      <view class="result-head">
        <text class="result-title">备课清单已生成</text>
        <text class="result-meta">{{ result.grade }} · {{ result.theme }} · {{ result.duration }}分钟</text>
      </view>

      <!-- 推荐课程 -->
      <view class="result-card">
        <text class="card-title">推荐课程</text>
        <view
          v-for="c in recommendCourses"
          :key="c.id"
          class="course-row"
          @tap="goCourseDetail(c.id)"
        >
          <view class="course-cover" :style="{ background: c.coverColor }"></view>
          <view class="course-info">
            <text class="course-name">{{ c.title }}</text>
            <text class="course-meta">{{ c.themeLabel }} · {{ c.duration }}分钟 · {{ c.version }}</text>
          </view>
          <view class="course-arrow"></view>
        </view>
      </view>

      <!-- 教学流程 -->
      <view class="result-card">
        <text class="card-title">教学流程建议</text>
        <view v-for="(flow, i) in result.teachingFlow" :key="i" class="flow-row">
          <text class="flow-index">{{ i + 1 }}</text>
          <text class="flow-text">{{ flow }}</text>
        </view>
      </view>

      <!-- 材料清单 -->
      <view class="result-card">
        <text class="card-title">材料清单</text>
        <view v-for="(item, i) in result.materialChecklist" :key="i" class="check-row">
          <view class="check-icon"></view>
          <text class="check-text">{{ item }}</text>
        </view>
      </view>

      <!-- 注意事项 -->
      <view class="result-card notice">
        <text class="card-title">注意事项</text>
        <view v-for="(note, i) in result.attentionNotes" :key="i" class="note-row">
          <view class="note-icon"></view>
          <text class="note-text">{{ note }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import PrepareStepForm from '@/components/tool/PrepareStepForm.vue';
import { fetchPrepareOptions, generatePrepare } from '@/api/prepare';
import type { PrepareOptions } from '@/api/prepare';
import { fetchCourses } from '@/api/course';
import type { PrepareResult, CourseSummary } from '@/types/mock';

const options = ref<PrepareOptions | null>(null);
const result = ref<PrepareResult | null>(null);
const courses = ref<CourseSummary[]>([]);
const recommendCourses = ref<CourseSummary[]>([]);

onLoad(async () => {
  options.value = await fetchPrepareOptions();
  courses.value = await fetchCourses();
});

async function onGenerate(payload: { grade: string; theme: string; duration: number }) {
  result.value = await generatePrepare(payload.grade, payload.theme, payload.duration);
  uni.pageScrollTo({ scrollTop: 0, duration: 200 });
}

// 生成结果变化时解析推荐课程详情
watch(result, (r) => {
  if (!r) {
    recommendCourses.value = [];
    return;
  }
  recommendCourses.value = r.recommendedCourses
    .map((id) => courses.value.find((c) => c.id === id))
    .filter((c): c is CourseSummary => !!c);
});

function goCourseDetail(id: number) {
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m $space-m calc(env(safe-area-inset-bottom) + $space-l);
  background: $bg-page;
}

.head {
  padding: $space-s 0 $space-m;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.head-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.head-desc {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.result {
  margin-top: $space-m;
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.result-head {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.result-title {
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
  color: $brand-primary;
}

.result-meta {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.result-card {
  background: $bg-card;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  padding: $space-m;

  &.notice {
    background: rgba(217, 154, 98, 0.1);
    box-shadow: none;
  }
}

.card-title {
  display: block;
  margin-bottom: $space-m;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.course-row {
  display: flex;
  align-items: center;
  gap: $space-s;
  padding: $space-s 0;

  &:not(:last-child) {
    border-bottom: 1rpx solid $border;
  }
}

.course-cover {
  flex-shrink: 0;
  width: 72rpx;
  height: 88rpx;
  border-radius: $radius-s;
}

.course-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.course-name {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  font-size: 20rpx;
  color: $text-secondary;
}

/* 右箭头：SVG 组件（禁裸写字符） */
.course-arrow {
  flex-shrink: 0;
  width: 32rpx;
  height: 32rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23C9CDD4' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E");
}

.flow-row {
  display: flex;
  align-items: center;
  gap: $space-s;
  margin-bottom: $space-s;

  &:last-child {
    margin-bottom: 0;
  }
}

.flow-index {
  flex-shrink: 0;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: $brand-primary-light;
  color: $brand-primary;
  font-size: 20rpx;
  font-weight: $font-weight-bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.flow-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-regular;
}

.check-row {
  display: flex;
  align-items: center;
  gap: $space-s;
  margin-bottom: $space-s;

  &:last-child {
    margin-bottom: 0;
  }
}

/* 行首图标：1.5px 线性墨绿/暖橙单色（对勾 / 警示三角） */
.check-icon {
  flex-shrink: 0;
  width: 28rpx;
  height: 28rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m5 12.5 4.5 4.5L19 7.5'/%3E%3C/svg%3E");
}

.check-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-regular;
}

.note-row {
  display: flex;
  align-items: flex-start;
  gap: $space-s;
  margin-bottom: $space-s;

  &:last-child {
    margin-bottom: 0;
  }
}

.note-icon {
  flex-shrink: 0;
  width: 28rpx;
  height: 28rpx;
  margin-top: 2rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23D99A62' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M12 4 21 19.5H3Z'/%3E%3Cpath d='M12 10.5v4'/%3E%3Cpath d='M12 17.2v.1'/%3E%3C/svg%3E");
}

.note-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-regular;
  line-height: 1.6;
}
</style>
