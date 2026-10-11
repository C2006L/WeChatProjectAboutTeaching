<template>
  <view class="page" v-if="loaded">
    <PageIntro kicker="教师成长" title="把准备变成课堂底气" description="完成核心培训，再按需要学习专题课程" />
    <view class="progress-wrap">
      <TrainingProgressCard
        :percent="percent"
        :completed="user.trainingProgress.completed"
        :total="user.trainingProgress.total"
        @continue="goCurrent"
      />
    </view>

    <view class="section">
      <text class="section-title">必修课程（{{ required.length }}节）</text>
      <view class="list">
        <ChecklistItem
          v-for="(t, i) in required"
          :key="t.id"
          :index="i + 1"
          :title="t.title"
          :duration="t.duration"
          :status="t.status"
          :progress="t.progress"
          @click="goPlay(t)"
        />
      </view>
    </view>

    <view class="section">
      <text class="section-title">选修课程（{{ elective.length }}节）</text>
      <view class="list">
        <ChecklistItem
          v-for="(t, i) in elective"
          :key="t.id"
          :index="i + 1"
          :title="t.title"
          :duration="t.duration"
          :status="t.status"
          :progress="t.progress"
          @click="goPlay(t)"
        />
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import TrainingProgressCard from '@/components/training/TrainingProgressCard.vue';
import ChecklistItem from '@/components/training/ChecklistItem.vue';
import PageIntro from '@/components/common/PageIntro.vue';
import { fetchTrainingList } from '@/api/training';
import { currentUser } from '@/mock/user';
import type { Training } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';

// 同步 tabBar 选中态：培训 = 2
useTabBarSync(2);

const user = currentUser;
const all = ref<Training[]>([]);
const loaded = ref(false);

onLoad(async () => {
  // try/finally 兜底：数据加载失败也要置 loaded，避免整页空白
  try {
    all.value = await fetchTrainingList();
  } catch (e) {
    console.error('培训数据加载失败', e);
  } finally {
    loaded.value = true;
  }
});

const percent = computed(() =>
  Math.round((user.trainingProgress.completed / user.trainingProgress.total) * 100),
);

const required = computed(() => all.value.filter((t) => t.type === 'required'));
const elective = computed(() => all.value.filter((t) => t.type === 'elective'));

function goPlay(t: Training) {
  uni.navigateTo({ url: `/pages/training/play?id=${t.id}` });
}

function goCurrent() {
  const current = all.value.find((t) => t.status === 'current') ?? all.value[0];
  if (current) {
    uni.navigateTo({ url: `/pages/training/play?id=${current.id}` });
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding-bottom: calc($tab-bar-height + env(safe-area-inset-bottom) + 48rpx);
  background: $bg-page;
}

.progress-wrap, .section { margin-left: $space-m; margin-right: $space-m; }

.section {
  margin-top: 28rpx;
}

.section-title {
  display: block;
  margin-bottom: 14rpx;
  font-size: 30rpx;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.list {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.list::before {
  content: '';
  position: absolute;
  z-index: 0;
  top: 34rpx;
  bottom: 34rpx;
  left: 44rpx;
  width: 3rpx;
  border-radius: 2rpx;
  background: rgba(23, 111, 102, .2);
}
</style>
