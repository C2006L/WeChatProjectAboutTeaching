<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { syncFavorites } from "@/utils/favorite";

onLaunch(() => {
  // tabBarStore 是模块级单例：custom-tab-bar 与页面 require 同一模块即可共享，
  // 无需经 getApp().globalData 中转（onLaunch 中 getApp() 返回 undefined，
  // 会在启动阶段抛错且 store 挂载失败）。
  // 批次H：真实模式下同步服务端收藏状态（mock/未登录静默跳过）
  void syncFavorites();
});
onShow(() => {});
onHide(() => {});
</script>
<style lang="scss">
/* ── 全局动效基建（所有页面共享） ──
   按压反馈统一：scale(0.98)，120ms，只动 transform / box-shadow（配合元素自带投影减淡）。
   用法：可交互元素加 class="pressable" + hover-class="hv-press" :hover-stay-time="80"。 */
.pressable {
  /* 按压投影减淡：默认减淡值；无投影元素（燕麦条/实墨绿 HERO）置 --press-shadow: none */
  --press-shadow: 0 2rpx 8rpx rgba(54, 82, 77, 0.02);
  transition:
    transform $motion-fast $motion-ease,
    box-shadow $motion-fast $motion-ease;
}

.hv-press {
  transform: scale(0.98);
}

/* 双类提升特异性：保证按压减淡投影能覆盖页面内卡片自身的 $shadow-card */
.pressable.hv-press {
  transform: scale(0.98);
  box-shadow: var(--press-shadow);
}

/* 降级策略：系统开启"减少动态效果"时关闭过渡与动画（支持的端生效，不支持的端忽略） */
@media (prefers-reduced-motion: reduce) {
  .pressable,
  .pressable.hv-press {
    transition: none;
  }

  .pressable.hv-press {
    transform: none;
  }

  /* 页面级进场/生长动画一并降级；元素选择器在不支持的端被忽略 */
  view,
  text,
  image {
    animation-duration: 0.01ms;
    animation-delay: 0ms;
  }
}
</style>
