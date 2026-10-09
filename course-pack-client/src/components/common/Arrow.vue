<template>
  <!-- 伴芽 · 线性右箭头（SVG data-uri，1.5px 墨绿单色，禁止裸写 > 字符）
       颜色可通过 color 属性传入 CSS 变量值；绝不可在 SVG 内引用 CSS 变量，
       只能由外层把具体色值套进 data-uri。 -->
  <view class="arrow" :style="arrowStyle"></view>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  /** 描边色值（十六进制，如 #6B7A75），默认墨绿 #4F8F83 */
  color?: string;
  /** 尺寸 rpx，默认 28 */
  size?: number;
}

const props = withDefaults(defineProps<Props>(), {
  color: '#6B7A75',
  size: 28,
});

const arrowStyle = computed(() => ({
  width: `${props.size}rpx`,
  height: `${props.size}rpx`,
  backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='${encodeURIComponent(props.color)}' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M9 6l6 6-6 6'/%3E%3C/svg%3E")`,
}));
</script>

<style scoped lang="scss">
.arrow {
  flex-shrink: 0;
  background: center / contain no-repeat;
}
</style>