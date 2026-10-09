<template>
  <view
    class="fav-btn pressable"
    :style="{ width: box + 'rpx', height: box + 'rpx' }"
    hover-class="hv-press"
    :hover-stay-time="80"
    @tap.stop="onToggle"
  >
    <view class="fav-ico" :class="{ active }" :style="{ width: icon + 'rpx', height: icon + 'rpx' }"></view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { isFav, toggleFav } from '@/utils/favorite';

interface Props {
  courseId: number;
  /** 点击热区尺寸（rpx） */
  box?: number;
  /** 图标尺寸（rpx） */
  icon?: number;
}

const props = withDefaults(defineProps<Props>(), { box: 56, icon: 34 });
const emit = defineEmits<{ (e: 'change', fav: boolean): void }>();

const active = computed(() => isFav(props.courseId));

function onToggle() {
  const next = toggleFav(props.courseId);
  emit('change', next);
  uni.showToast({ title: next ? '已收藏' : '已取消收藏', icon: 'none', duration: 900 });
}
</script>

<style scoped lang="scss">
.fav-btn {
  --press-shadow: none; /* 图标按钮无投影：按压仅缩放 */
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 星形：未收藏=墨绿描边空心；已收藏=暖橙填充（辅色 token 用途即收藏/提醒） */
.fav-ico {
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2336524D' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m12 4 2.4 4.9 5.4.8-3.9 3.8.9 5.4-4.8-2.5-4.8 2.5.9-5.4L4.2 9.7l5.4-.8Z'/%3E%3C/svg%3E") center / contain no-repeat;
}

.fav-ico.active {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23D99A62' stroke='%23D99A62' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m12 4 2.4 4.9 5.4.8-3.9 3.8.9 5.4-4.8-2.5-4.8 2.5.9-5.4L4.2 9.7l5.4-.8Z'/%3E%3C/svg%3E");
}
</style>
