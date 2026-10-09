// 伴芽 · 自定义 tabBar（微信原生组件，uni-app 原样拷贝至 dist）
// 配色与取值对齐 course-pack-docs/01-设计/tokens.md
//
// 选中态由全局 tabBarStore 驱动：custom-tab-bar 与页面 require 的是
// 同一个 store 模块单例（同一个包内共享一套 CommonJS 模块注册表）。
// 注意：不要经 getApp().globalData 共享——App.onLaunch 里 getApp()
// 返回 undefined（微信官方约束），挂载会静默失败，5 个 tab 实例
// 将永远停在初始 selected=0，切页后不高亮。
const { tabBarStore } = require('../utils/tabbar-store.js');

const TAB_LIST = [
  { path: '/pages/home/home', text: '首页', icon: 'icon-home' },
  { path: '/pages/course/list', text: '课程', icon: 'icon-course' },
  { path: '/pages/training/index', text: '培训', icon: 'icon-training' },
  { path: '/pages/tool/index', text: '工具', icon: 'icon-tool' },
  { path: '/pages/mine/mine', text: '我的', icon: 'icon-mine' },
];

Component({
  data: {
    selected: 0,
    list: TAB_LIST,
  },
  lifetimes: {
    attached() {
      this.setData({ selected: tabBarStore.selected });
      this.__unsubscribe = tabBarStore.subscribe((idx) => {
        if (idx !== this.data.selected) {
          this.setData({ selected: idx });
        }
      });
    },
    detached() {
      if (this.__unsubscribe) {
        this.__unsubscribe();
        this.__unsubscribe = null;
      }
    },
  },
  methods: {
    switchTab(e) {
      const path = e.currentTarget.dataset.path;
      wx.switchTab({ url: path });
    },
  },
});
