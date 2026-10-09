import { onShow } from '@dcloudio/uni-app';
import { tabBarStore } from './tabbar-store';

/**
 * 同步自定义 tabBar 选中态。
 * 由于微信 custom-tab-bar 每个 tab 页各持有一个组件实例，切页时新实例的
 * selected 停留在初始值 0。这里通过全局 store 驱动：页面 onShow 时更新
 * store，tabBar 组件订阅 store 并 setData 刷新当前实例。
 *
 * @param selected 当前页在 tabBar.list 中的索引
 */
export function useTabBarSync(selected: number) {
  onShow(() => {
    // #ifdef MP-WEIXIN
    tabBarStore.setSelected(selected);
    // #endif
  });
}
