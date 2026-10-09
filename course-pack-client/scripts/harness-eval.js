/**
 * 模块求值测试：模拟小程序运行环境，逐个加载 dist 中的 JS 模块，
 * 定位导致页面 JS 挂掉的模块级错误（表现为页面空白 + tabBar 不同步）。
 */
const base = 'd:/WeChatProject/course-pack-client/dist/dev/mp-weixin';

function makeWx() {
  const fn = (ok) => (opt) => {
    if (opt && typeof opt === 'object') {
      if (typeof opt.success === 'function') opt.success(ok || {});
      if (typeof opt.complete === 'function') opt.complete({});
    }
    return ok || {};
  };
  // 可枚举自身属性：vendor.js 的 initWx() 会浅拷贝 global.wx，缺失的 key 会在拷贝后丢失
  const target = {
    env: { USER_DATA_PATH: 'wxfile://usr' },
    getSystemInfoSync: fn({ statusBarHeight: 20, windowWidth: 375, windowHeight: 667, pixelRatio: 2, platform: 'devtools', SDKVersion: '3.0.0', language: 'zh_CN' }),
    getAppBaseInfo: fn({ SDKVersion: '3.0.0' }),
    getWindowInfo: fn({ windowWidth: 375, windowHeight: 667, statusBarHeight: 20 }),
    getDeviceInfo: fn({ platform: 'devtools' }),
    getAccountInfoSync: fn({ miniProgram: { appId: 'touristappid' } }),
    getMenuButtonBoundingClientRect: fn({ top: 24, height: 32 }),
    canIUse: () => true,
    shareVideoMessage: fn({}),
    createSelectorQuery: fn({}),
    onAppRoute: fn({}),
    getStorageInfoSync: fn({ keys: [] }),
    getStorageSync: () => '',
    setStorageSync: () => {},
    removeStorageSync: () => {},
    request: fn({ statusCode: 200, data: {} }),
    showToast: fn({}),
    hideToast: fn({}),
    showModal: fn({ confirm: true }),
    navigateTo: fn({}),
    switchTab: fn({}),
    nextTick: (cb) => cb && cb(),
  };
  return new Proxy(target, {
      get(target, prop) {
        if (prop in target) return target[prop];
        if (prop === 'getSystemInfoSync') return () => ({ statusBarHeight: 20, windowWidth: 375, windowHeight: 667, pixelRatio: 2, platform: 'devtools', SDKVersion: '3.0.0', language: 'zh_CN' });
        if (prop === 'getAppBaseInfo' || prop === 'getWindowInfo' || prop === 'getDeviceInfo') return () => ({});
        if (prop === 'canIUse') return () => true;
        if (prop === 'getAccountInfoSync') return () => ({ miniProgram: { appId: 'touristappid' } });
        if (prop === 'getMenuButtonBoundingClientRect') return () => ({ top: 24, height: 32 });
        return (...args) => {
          const opt = args && args[0];
          if (opt && typeof opt === 'object') {
            if (typeof opt.success === 'function') opt.success(opt && opt.__ok || {});
            if (typeof opt.complete === 'function') opt.complete({});
          }
          return {};
        };
      }
    }
  );
}
global.wx = makeWx();
global.getApp = () => undefined;
global.getCurrentPages = () => [];
global.__wxConfig = { pages: [], window: {} };
global.__wxRoute = '';
global.App = (opts) => { global.__appOptions = opts; };
global.Component = (opts) => { global.__lastComponent = opts; };
global.Page = (opts) => { global.__lastPage = opts; };
global.Behavior = (opts) => opts;

const files = process.argv.slice(2);
let fail = 0;
for (const f of files) {
  try {
    require(base + '/' + f);
    console.log('[OK]   ' + f);
  } catch (e) {
    fail++;
    console.log('[FAIL] ' + f + '  ->  ' + e.message);
    console.log('       ' + e.stack.split('\n').slice(1, 4).join('\n       '));
  }
}
process.exit(fail ? 1 : 0);
