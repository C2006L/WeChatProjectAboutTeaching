<template>
  <view class="page">
    <!-- 未登录：品牌时刻（上半屏实墨绿有机色块 + 白芽logo + 衬线字 + slogan） -->
    <view v-if="!logged" class="login">
      <view class="brand-hero">
        <view class="brand-logo"></view>
        <text class="brand-name">伴芽</text>
        <text class="brand-slogan">陪孩子，慢慢发芽</text>
      </view>
      <view class="login-form">
        <text class="login-desc">登录后可收藏课程、获取资源</text>
        <view class="login-btn" @tap="onLogin">微信快捷登录</view>
      </view>
    </view>

    <!-- 已登录 -->
    <view v-else class="logged">
      <ProfileHeader :user="user" />

      <!-- 学习进度 -->
      <view class="progress-card pressable" hover-class="hv-press" :hover-stay-time="80" @tap="goTraining">
        <view class="progress-info">
          <text class="progress-title">培训学习进度</text>
          <text class="progress-desc">已完成 {{ user.trainingProgress.completed }}/{{ user.trainingProgress.total }} 节</text>
          <text class="progress-encourage">继续加油</text>
        </view>
        <view class="progress-bar">
          <GrowProgress :percent="percent" :height="10" />
        </view>
        <text class="progress-percent">{{ percent }}%</text>
      </view>

      <!-- 我的内容 -->
      <MenuGrid title="我的内容" :items="menus.content" @navigate="onMenuNavigate" />

      <!-- 我的服务 -->
      <MenuGrid title="我的服务" :items="menus.service" @navigate="onMenuNavigate" />

      <!-- 退出登录 -->
      <view class="logout-btn pressable" hover-class="hv-press" :hover-stay-time="80" @tap="onLogout">
        <text class="logout-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import ProfileHeader from '@/components/mine/ProfileHeader.vue';
import MenuGrid from '@/components/mine/MenuGrid.vue';
import GrowProgress from '@/components/common/GrowProgress.vue';
import { fetchCurrentUser, fetchMineMenus } from '@/api/user';
import type { UserProfile, MineMenuItem } from '@/types/mock';
import { useTabBarSync } from '@/utils/tabbar';
import { STORAGE_TOKEN, STORAGE_LOGIN_FLAG } from '@/config';

// 同步 tabBar 选中态：我的 = 4
useTabBarSync(4);

// 登录态持久化：冷启动从 storage 恢复，重启/切后台再进入不掉线
const logged = ref(uni.getStorageSync(STORAGE_LOGIN_FLAG) === '1');
const user = ref<UserProfile>({} as UserProfile);
const menus = reactive<{ content: MineMenuItem[]; service: MineMenuItem[] }>({
  content: [],
  service: [],
});

onLoad(async () => {
  user.value = await fetchCurrentUser();
  const m = await fetchMineMenus();
  menus.content = m.content;
  menus.service = m.service;
});

const percent = computed(() =>
  Math.round((user.value.trainingProgress?.completed ?? 0) / (user.value.trainingProgress?.total || 1) * 100),
);

function onLogin() {
  // 微信快捷登录（演示）：登录态与 token 持久化，后端就绪后替换为 wx.login + /auth/login
  uni.setStorageSync(STORAGE_LOGIN_FLAG, '1');
  uni.setStorageSync(STORAGE_TOKEN, 'mock-token');
  logged.value = true;
  uni.showToast({ title: '登录成功', icon: 'none' });
}

function onLogout() {
  uni.removeStorageSync(STORAGE_LOGIN_FLAG);
  uni.removeStorageSync(STORAGE_TOKEN);
  logged.value = false;
  uni.showToast({ title: '已退出', icon: 'none' });
}

function goTraining() {
  uni.switchTab({ url: '/pages/training/index' });
}

/** tabBar 页面必须用 switchTab（且不能带 query），其余 navigateTo */
const TAB_PATHS = [
  '/pages/home/home',
  '/pages/course/list',
  '/pages/training/index',
  '/pages/tool/index',
  '/pages/mine/mine',
];

function onMenuNavigate(item: MineMenuItem) {
  if (!item.path) {
    uni.showToast({ title: `${item.label}功能建设中`, icon: 'none' });
    return;
  }
  const purePath = item.path.split('?')[0];
  if (TAB_PATHS.includes(purePath)) {
    uni.switchTab({ url: purePath });
  } else {
    uni.navigateTo({ url: item.path });
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: $bg-page;
}

/* ── 品牌时刻：上半屏实墨绿有机色块（一个圆润大形状，禁卡通元素与彩色点缀） ── */
.brand-hero {
  height: 44vh;
  min-height: 560rpx;
  background: $bg-hero;
  border-bottom-left-radius: 200rpx 90rpx;   /* 有机不对称圆角 */
  border-bottom-right-radius: 340rpx 140rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
}

/* 白色小芽 logo（线性单色白，同 HERO 水印语言） */
.brand-logo {
  width: 132rpx;
  height: 132rpx;
  background: center / contain no-repeat;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23FFFFFF' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M7 20h10'/%3E%3Cpath d='M10 20c5.5-2.5.8-6.4 3-10'/%3E%3Cpath d='M9.5 9.4c1.1.8 1.8 2.2 2.3 3.7-2 .4-3.5.4-4.8-.3-1.2-.6-2.3-1.9-3-4.2 2.8-.5 4.4 0 5.5.8z'/%3E%3Cpath d='M14.1 6a7 7 0 0 0-1.1 4c1.9-.1 3.3-.6 4.3-1.4 1-1 1.6-2.3 1.7-4.6-2.7.1-4 1-4.9 2z'/%3E%3C/svg%3E");
}

/* 品牌名：衬线字 */
.brand-name {
  margin-top: 8rpx;
  font-family: $font-family-serif;
  font-size: 64rpx;
  font-weight: 600;
  letter-spacing: 12rpx;
  text-indent: 12rpx; /* 平衡字距造成的偏移 */
  color: #FFFFFF;
}

.brand-slogan {
  font-size: 26rpx;
  letter-spacing: 6rpx;
  text-indent: 6rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* ── 下半屏：登录表单（沿用现有样式） ── */
.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 40rpx 0;
}

.login-desc {
  color: $text-secondary;
  font-size: $font-size-s;
}

.login-btn {
  margin-top: 48rpx;
  width: 480rpx;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  background: $brand-primary;
  color: #ffffff;
  border-radius: 44rpx;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
}

.logged {
  padding: $space-m $space-m calc($tab-bar-height + 32rpx);
  display: flex;
  flex-direction: column;
  gap: $space-m;
}

.progress-card {
  display: flex;
  align-items: center;
  gap: $space-m;
  padding: $space-m;
  background: $bg-card;
  border: $border-card;
  border-radius: $radius-card;
  border-top-left-radius: $radius-card-tl; // 叶片裁角（左上 40rpx）
  box-shadow: $shadow-card;
}

.progress-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.progress-title {
  font-size: $font-size-s;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.progress-desc {
  font-size: $font-size-xs;
  color: $text-secondary;
}

.progress-encourage {
  font-size: $font-size-xs;
  color: $brand-primary;
}

.progress-bar {
  flex: 1;
  /* 轨道由 GrowProgress 自带（浅绿底 + scaleX 生长 + 末端小芽淡入） */
}

.progress-percent {
  flex-shrink: 0;
  font-size: $font-size-m;
  font-weight: $font-weight-bold;
  color: $brand-primary;
}

.logout-btn {
  --press-shadow: none; /* 描边按钮无投影：按压仅缩放 */
  margin-top: $space-s;
  height: 88rpx;
  border-radius: $radius-card;
  background: transparent;
  border: 2rpx solid $brand-primary; /* 墨绿 1px 描边 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-text {
  font-size: $font-size-m;
  color: $brand-primary;
  font-weight: $font-weight-bold;
}
</style>
