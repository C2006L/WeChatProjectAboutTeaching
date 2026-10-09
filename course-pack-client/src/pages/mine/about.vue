<template>
  <view class="page">
    <!-- 品牌时刻（与登录页同语言：实墨绿有机色块 + 白芽 + 衬线字） -->
    <view class="brand-hero">
      <view class="brand-logo"></view>
      <text class="brand-name">伴芽小站</text>
      <text class="brand-slogan">陪孩子，慢慢发芽</text>
    </view>

    <view class="info-card">
      <view class="info-row">
        <text class="info-label">产品定位</text>
        <text class="info-value">面向短期支教教师的课程与培训支持工具</text>
      </view>
      <view class="info-row">
        <text class="info-label">版本</text>
        <text class="info-value">V1.0.0</text>
      </view>
    </view>

    <text class="foot-tip">如有使用问题，请在 Web 管理端「问答与反馈」提交工单</text>
  </view>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: $bg-page;
}

.brand-hero {
  padding: 96rpx 0 72rpx;
  background: $bg-hero;
  border-bottom-left-radius: 160rpx 72rpx;
  border-bottom-right-radius: 280rpx 110rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.brand-logo {
  width: 112rpx;
  height: 112rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23FFFFFF' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M7 20h10'/%3E%3Cpath d='M10 20c5.5-2.5.8-6.4 3-10'/%3E%3Cpath d='M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z'/%3E%3Cpath d='M14.1 6a7 7 0 0 0-1.1 4c1.9-.1 3.3-.6 4.3-1.4 1-1 1.6-2.3 1.7-4.6-2.7.1-4 1-4.9 2z'/%3E%3C/svg%3E");
}

.brand-name {
  font-family: $font-family-serif;
  font-size: 56rpx;
  font-weight: 600;
  letter-spacing: 12rpx;
  text-indent: 12rpx;
  color: #FFFFFF;
}

.brand-slogan {
  font-size: 26rpx;
  letter-spacing: 4rpx;
  color: rgba(255, 255, 255, 0.7);
}

.info-card {
  margin: $space-l $space-m 0;
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl;
  box-shadow: $shadow-card;
  padding: 0 $card-padding;
}

.info-row {
  display: flex;
  align-items: flex-start;
  padding: $space-m 0;
  border-bottom: 1rpx solid rgba(54, 82, 77, 0.08);

  &:last-child {
    border-bottom: none;
  }
}

.info-label {
  flex-shrink: 0;
  width: 128rpx;
  font-size: $font-size-s;
  color: $text-secondary;
}

.info-value {
  flex: 1;
  font-size: $font-size-s;
  color: $text-primary;
}

.foot-tip {
  display: block;
  margin: $space-l $space-m;
  font-size: $font-size-xs;
  color: $text-secondary;
  text-align: center;
}
</style>
