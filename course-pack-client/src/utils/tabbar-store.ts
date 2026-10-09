// 伴芽 · tabBar 全局选中态 store（跨页面/跨 tabBar 实例共享）
// custom-tab-bar 每个 tab 页各持一个实例，必须借助全局状态驱动选中态更新。

let selectedIndex = 0;
const listeners: Array<(idx: number) => void> = [];

export const tabBarStore = {
  get selected() {
    return selectedIndex;
  },
  setSelected(idx: number) {
    if (idx === selectedIndex) return;
    selectedIndex = idx;
    listeners.forEach((fn) => fn(idx));
  },
  subscribe(fn: (idx: number) => void) {
    listeners.push(fn);
    return () => {
      const i = listeners.indexOf(fn);
      if (i >= 0) listeners.splice(i, 1);
    };
  },
};
