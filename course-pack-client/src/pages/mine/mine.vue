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
      <PageIntro kicker="个人中心" title="我的成长手记" description="学习、备课与一线实践都在这里沉淀" />
      <view class="logged-body">
        <ProfileHeader
          :user="user"
          :percent="percent"
          :completed="user.trainingProgress?.completed || 0"
          :total="user.trainingProgress?.total || 0"
          @continue="goTraining"
        />

      <!-- 我的内容 -->
      <MenuGrid title="我的内容" :items="menus.content" @navigate="onMenuNavigate" />

      <!-- 我的服务 -->
      <MenuGrid title="我的服务" :items="menus.service" @navigate="onMenuNavigate" />

        <view class="account-card">
          <text class="account-title">账号</text>
          <view class="logout-row pressable" hover-class="hv-press" :hover-stay-time="80" @tap="onLogout">
            <view class="logout-icon"></view>
            <text class="logout-text">退出登录</text>
            <view class="logout-arrow"></view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import ProfileHeader from '@/components/mine/ProfileHeader.vue';
import MenuGrid from '@/components/mine/MenuGrid.vue';
import PageIntro from '@/components/common/PageIntro.vue';
import { fetchCurrentUser, fetchMineMenus, wxLogin } from '@/api/user';
import { useMock } from '@/api/use-mock';
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
  // 已登录才拉取：未登录时请求 /me 只会得到 401（mock 模式无影响）
  if (logged.value) {
    user.value = await fetchCurrentUser();
  }
  const m = await fetchMineMenus();
  menus.content = m.content;
  menus.service = m.service;
});

const percent = computed(() =>
  Math.round((user.value.trainingProgress?.completed ?? 0) / (user.value.trainingProgress?.total || 1) * 100),
);

async function onLogin() {
  // mock 模式：演示登录（登录态与 token 持久化）
  if (useMock()) {
    uni.setStorageSync(STORAGE_LOGIN_FLAG, '1');
    uni.setStorageSync(STORAGE_TOKEN, 'mock-token');
    logged.value = true;
    uni.showToast({ title: '登录成功', icon: 'none' });
    return;
  }
  // 真实模式：wx.login → code → POST /api/auth/wx-login（批次I）
  try {
    const { code } = await new Promise<UniApp.LoginRes>((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: resolve,
        fail: (err) => reject(new Error(err?.errMsg || '拉起登录失败')),
      });
    });
    const res = await wxLogin(code);
    if (res.user?.role && res.user.role !== 'TEACHER') {
      uni.showToast({ title: '请在浏览器登录伴芽小站工作台使用该账号', icon: 'none' });
      return;
    }
    uni.setStorageSync(STORAGE_LOGIN_FLAG, '1');
    uni.setStorageSync(STORAGE_TOKEN, res.token);
    logged.value = true;
    user.value = await fetchCurrentUser();
    uni.showToast({ title: '登录成功', icon: 'none' });
  } catch (err) {
    const body = err as { message?: string; code?: number };
    uni.showToast({ title: body?.message || '登录失败，请检查网络或稍后再试', icon: 'none' });
  }
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
  padding-bottom: calc($tab-bar-height + env(safe-area-inset-bottom) + 56rpx);
}

.logged-body {
  padding: 0 $space-m;
  display: flex;
  flex-direction: column;
  gap: 18rpx;
}

.account-card { padding: 28rpx $space-m 4rpx; border: $border-card; border-radius: $radius-card; background: $bg-card; }
.account-title { display: block; margin-bottom: 12rpx; font-size: $font-size-s; font-weight: 600; color: $text-primary; }
.logout-row { min-height: 100rpx; display: flex; align-items: center; gap: $space-s; border-top: 1rpx solid $border; }
.logout-icon { width: 56rpx; height: 56rpx; border-radius: $radius-s; background: $bg-oat center / 32rpx 32rpx no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237B8986' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M10 5H5v14h5M14 8l4 4-4 4M8 12h10'/%3E%3C/svg%3E"); }
.logout-arrow { width: 28rpx; height: 28rpx; background: center / contain no-repeat url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237B8986' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='m9 6 6 6-6 6'/%3E%3C/svg%3E"); }

.logout-text {
  flex: 1;
  font-size: $font-size-s;
  color: $text-secondary;
}
</style>
