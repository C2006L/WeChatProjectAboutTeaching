<template>
  <view class="page">
    <view class="header">
      <text class="header-title">我的收藏</text>
      <text class="header-count">{{ list.length }} 门课程</text>
    </view>

    <view v-if="list.length" class="list">
      <CourseCard
        v-for="course in list"
        :key="course.id"
        :course="course"
        @click="goDetail(course.id)"
        @action="goPrepare"
      />
    </view>
    <EmptyState v-else class="empty" text="还没有收藏课程，去课程页点亮小星星吧" />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import CourseCard from '@/components/course/CourseCard.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchCourses } from '@/api/course';
import { useFavIds } from '@/utils/favorite';
import type { CourseSummary } from '@/types/mock';
import { ref } from 'vue';

// favIds 为模块级响应式状态：详情页取消收藏返回后列表自动更新
const favIds = useFavIds();
const all = ref<CourseSummary[]>([]);

const list = computed(() => all.value.filter((c) => favIds.value.includes(c.id)));

onShow(async () => {
  if (!all.value.length) all.value = await fetchCourses();
});

function goDetail(id: number) {
  uni.navigateTo({ url: `/pages/course/detail?id=${id}` });
}

function goPrepare() {
  uni.navigateTo({ url: '/subpkg/tools/prepare' });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: $space-m 0 32rpx;
  background: $bg-page;
}

.header {
  display: flex;
  align-items: baseline;
  gap: $space-s;
  padding: 0 $space-m $space-m;
}

.header-title {
  font-size: $font-size-l;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.header-count {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.empty {
  margin-top: $space-l;
}
</style>
