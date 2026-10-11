<template>
  <view class="page" v-if="loaded">
    <PageIntro kicker="课堂支持" title="教师工具箱" description="把现场难题拆成随手可用的工具" />

    <view v-if="supportEntries.length" class="section-block">
      <text class="section-title">课堂支持</text>
      <view class="tool-list">
      <ToolCard
        v-for="entry in supportEntries"
        :key="entry.title"
        :entry="entry"
        @navigate="onNavigate"
      />
      </view>
    </view>

    <view v-if="caseEntry" class="section-block case-block">
      <text class="section-title">实践沉淀</text>
      <ToolCard :entry="caseEntry" featured @navigate="onNavigate" />
    </view>

    <view v-if="!entries.length" class="empty-wrap">
      <EmptyState text="工具箱暂无可用工具" />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import ToolCard from '@/components/tool/ToolCard.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import PageIntro from '@/components/common/PageIntro.vue';
import { fetchToolEntries } from '@/api/tool';
import type { ToolEntry } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';

// 同步 tabBar 选中态：工具 = 3
useTabBarSync(3);

const entries = ref<ToolEntry[]>([]);
const loaded = ref(false);
const supportEntries = computed(() => entries.value.filter((entry) => entry.icon !== 'case'));
const caseEntry = computed(() => entries.value.find((entry) => entry.icon === 'case'));

onLoad(async () => {
  // try/finally 兜底：数据加载失败也要置 loaded，避免整页空白
  try {
    entries.value = await fetchToolEntries();
  } catch (e) {
    console.error('工具箱数据加载失败', e);
  } finally {
    loaded.value = true;
  }
});

function onNavigate(entry: ToolEntry) {
  if (!entry.path) {
    uni.showToast({ title: `${entry.title}功能建设中`, icon: 'none' });
    return;
  }
  uni.navigateTo({ url: entry.path });
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding-bottom: calc($tab-bar-height + env(safe-area-inset-bottom) + 48rpx);
  background: $bg-page;
}

.section-block { margin: 0 $space-m 30rpx; }
.section-title { display: block; margin-bottom: 14rpx; font-size: 30rpx; font-weight: 700; color: $text-primary; }
.tool-list { display: flex; flex-direction: column; gap: 16rpx; }
.case-block { margin-top: 4rpx; }

.empty-wrap {
  margin: $space-l $space-m 0;
}
</style>
