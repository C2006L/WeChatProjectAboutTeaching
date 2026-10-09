<template>
  <view class="page">
    <FilterBar :filter="filter" @change="onFilterChange" />

    <view v-if="list.length" class="list">
      <CourseCard
        v-for="course in list"
        :key="course.id"
        :course="course"
        @click="goDetail(course.id)"
        @action="goPrepare"
      />
    </view>

    <EmptyState v-else class="empty" text="没有找到匹配的课程，换个筛选试试" />
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import FilterBar from '@/components/course/FilterBar.vue';
import CourseCard from '@/components/course/CourseCard.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import { fetchCourses, defaultCourseFilter } from '@/api/course';
import type { CourseFilter } from '@/api/course';
import type { CourseSummary } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';

// 同步 tabBar 选中态：课程 = 1
useTabBarSync(1);

const filter = reactive<CourseFilter>({ ...defaultCourseFilter });
const all = ref<CourseSummary[]>([]);

const list = computed(() =>
  all.value.filter((c) => {
    if (filter.keyword && !c.title.includes(filter.keyword)) return false;
    if (filter.version !== '全部' && c.version !== filter.version) return false;
    if (filter.grade !== '全部' && !c.grade.includes(filter.grade)) return false;
    if (filter.theme !== '全部' && c.themeLabel !== filter.theme) return false;
    return true;
  }),
);

// 页面取数必须用 uni-app 的 onLoad（Vue 的 onMounted 在 mp 页面上不触发）
onLoad(async () => {
  all.value = await fetchCourses();
});

function onFilterChange(next: CourseFilter) {
  Object.assign(filter, next);
}

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
  padding-bottom: calc($tab-bar-height + 32rpx);
  background: $bg-page;
}

.list {
  margin-top: 8rpx;
}

.empty {
  margin-top: $space-l;
}
</style>
