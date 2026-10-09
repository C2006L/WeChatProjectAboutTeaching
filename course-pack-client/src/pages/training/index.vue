<template>
  <view class="page" v-if="loaded">
    <TrainingProgressCard
      :percent="percent"
      :completed="user.trainingProgress.completed"
      :total="user.trainingProgress.total"
      @continue="goCurrent"
    />

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
  padding: $space-m $space-m calc($tab-bar-height + 32rpx);
  background: $bg-page;
}

.section {
  margin-top: $space-m;
}

.section-title {
  display: block;
  margin-bottom: $space-s;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.list {
  display: flex;
  flex-direction: column;
  gap: $space-s;
}
</style>
