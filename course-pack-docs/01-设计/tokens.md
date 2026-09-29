# 设计 Token（伴芽品牌 · V1 打样对齐版）

> 伴芽 = 面向乡村支教教师的生命教育与安全成长课程赋能平台。主色取自然、沉稳、可信赖的森林绿，辅色取温暖橙，背景取米白纸张质感。
> 两端前端工程均以此为准，禁止硬编码色值；后续若接入 P5 设计语言，本文件需同步映射。

## 1. 色彩

### 品牌色
| Token | 值 | 用途 |
|---|---|---|
| `--brand-primary` | `#4F8F83` | 主品牌色（伴芽绿），主按钮/tabBar 选中/关键强调 |
| `--brand-primary-dark` | `#3A6E64` | 主色加深，按压/悬停态 |
| `--brand-primary-light` | `#E6F0EE` | 主色浅底，标签/选中底/进度条背景 |
| `--brand-secondary` | `#D99A62` | 辅色（暖橙），收藏/提醒/次要强调 |
| `--brand-secondary-light` | `#F8E9DA` | 暖橙浅底，提示卡片/勋章背景 |

### 功能色
| Token | 值 | 用途 |
|---|---|---|
| `--succ` | `#5D9B78` | 成功/已完成 |
| `--warn` | `#D99A62` | 警告/待处理（与暖橙辅色一致） |
| `--danger` | `#C96D68` | 错误/删除 |
| `--info` | `#8FB7C5` | 中性提示 |

> 功能色为伴芽自有色板，**禁止使用 Ant Design 默认色板**（#52C41A / #FAAD14 / #F5222D / #909399）。

### 中性色（文本/背景）
| Token | 值 | 用途 |
|---|---|---|
| `--text-primary` | `#233C36` | 主文本（深森绿） |
| `--text-regular` | `#4E5969` | 常规文本 |
| `--text-secondary` | `#86909C` | 次要文本/tabBar 未选中 |
| `--text-disabled` | `#C9CDD4` | 禁用文本 |
| `--border` | `#E5E6EB` | 边框/分隔线 |
| `--bg-page` | `#F7F6F1` | 页面背景（米白纸张色） |
| `--bg-card` | `#FFFFFF` | 卡片/内容背景 |
| `--bg-nav` | `#F7F6F1` | 导航栏背景（与页面背景一致） |

### 主题分类色（课程/培训标签）
| Token | 值 | 用途 |
|---|---|---|
| `--theme-body` | `#4F8F83` | 身体与安全 |
| `--theme-family` | `#D99A62` | 家庭与合作 |
| `--theme-respect` | `#6B9BD0` | 人际与尊重 |
| `--theme-growth` | `#7BB57D` | 成长与变化 |
| `--theme-general` | `#A78BCB` | 综合主题 |

## 2. 字体
| Token | 值 | 用途 |
|---|---|---|
| `--font-family` | `system-ui, -apple-system, 'PingFang SC', 'Microsoft YaHei', sans-serif` | 全局字体 |
| `--font-size-xs` | `12px` | 辅助信息/标签 |
| `--font-size-s` | `14px` | 正文次要 |
| `--font-size-m` | `16px` | 正文/按钮 |
| `--font-size-l` | `20px` | 标题 |
| `--font-size-xl` | `24px` | 大标题/数字 |
| `--font-size-xxl` | `32px` | 数据看板大数字 |
| `--font-weight-bold` | `600` | 加粗 |

## 3. 间距 / 圆角 / 阴影
| Token | 值 | 用途 |
|---|---|---|
| `--space-xs` | `4px` | 微间距 |
| `--space-s` | `8px` | 小间距 |
| `--space-m` | `16px` | 常规内边距 |
| `--space-l` | `24px` | 区块间距 |
| `--space-xl` | `32px` | 大模块间距 |
| `--radius-s` | `8px` | 小圆角（按钮/标签） |
| `--radius-m` | `12px` | 中圆角 |
| `--radius-card` | `14px` | 卡片圆角（打样图统一值） |
| `--radius-l` | `16px` | 大圆角/弹层 |
| `--shadow-card` | `0 2px 12px rgba(35, 60, 54, 0.06)` | 卡片阴影 |
| `--shadow-float` | `0 4px 20px rgba(35, 60, 54, 0.1)` | 悬浮阴影 |

## 4. 小程序专用 Token
| Token | 值 | 用途 |
|---|---|---|
| `--tab-bar-height` | `98rpx` | 底部 tabBar 高度 |
| `--tab-active` | `#4F8F83` | tabBar 选中色 |
| `--tab-inactive` | `#86909C` | tabBar 未选中色 |
| `--nav-bar-height` | `88rpx` | 导航栏高度 |

## 5. Web 端专用 Token
| Token | 值 | 用途 |
|---|---|---|
| `--side-width` | `220px` | 左侧菜单宽度 |
| `--side-bg` | `#FFFFFF` | 侧边栏背景 |
| `--side-active-bg` | `#4F8F83` | 侧边栏选中项背景 |
| `--side-active-text` | `#FFFFFF` | 侧边栏选中项文字 |

## 6. 命名约定
- 命名采用 `--模块-语义-状态`，表意清晰。
- 小程序端将其落到 `uni.scss` 或 CSS 变量；管理端挂到 `:root` 变量并同步 Element Plus 主题色。
- **强制规则**：凡打样图中出现的颜色/字号/圆角/阴影，必须引用上表 Token；禁止在 Vue/SCSS/CSS 中写死 `#F7F6F1`、`#4F8F83`、`14px` 等值。