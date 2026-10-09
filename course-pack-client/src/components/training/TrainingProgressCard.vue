<template>
  <view class="progress-card">
    <view class="head">
      <text class="title">我的培训进度</text>
      <text class="count">已完成 {{ completed }}/{{ total }} 节</text>
    </view>

    <view class="body">
      <view class="percent-wrap">
        <text class="percent-num">{{ percentDisplay }}</text>
        <text class="percent-unit">%</text>
      </view>
      <view class="bar-area">
        <!-- 进度条：与首页统一复用 GrowProgress（scaleX 生长 600ms + 末端小芽淡入） -->
        <GrowProgress :percent="percent" :height="16" />
        <view
          class="btn-continue pressable"
          hover-class="hv-press"
          :hover-stay-time="80"
          @tap="emit('continue')"
        >
          <text class="btn-text">继续学习</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import GrowProgress from '@/components/common/GrowProgress.vue';
import { useCountUp } from '@/utils/useCountUp';

interface Props {
  percent: number;
  completed: number;
  total: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{ (e: 'continue'): void }>();

/** 完成率数字滚动（600ms count-up） */
const percentDisplay = useCountUp(computed(() => props.percent));
</script>

<style scoped lang="scss">
/* 卡片材质 token */
.progress-card {
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;
  padding: $space-m;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.count {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.body {
  margin-top: $space-m;
  display: flex;
  align-items: center;
  gap: $space-m;
}

/* 完成率数字：衬线 + 增大字号 + 暖橙点缀 */
.percent-wrap {
  display: flex;
  align-items: baseline;
}

.percent-num {
  font-family: $font-family-serif;
  font-size: 88rpx;
  font-weight: $font-weight-bold;
  color: $accent-warm;
  line-height: 1;
}

.percent-unit {
  margin-left: 6rpx;
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $accent-warm;
}

.bar-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $space-s;
}

.btn-continue {
  align-self: flex-end;
  padding: 10rpx 28rpx;
  border-radius: 26rpx;
  background: $brand-primary;
  /* 实底按钮无投影：按压仅缩放 */
  --press-shadow: none;
}

.btn-text {
  font-size: $font-size-xs;
  color: #FFFFFF;
  font-weight: $font-weight-bold;
}
</style>
