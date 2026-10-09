# 设计 Token（伴芽小站 · V4 工作站版）

> 品牌定位：田野成长感 × 专业教育工具。小程序强调温暖、直接的任务入口，Web 强调清楚、高效的业务处理。
> 叶片异形圆角是品牌强调形态，仅用于首页主任务、欢迎区和重要空状态；普通卡片、表格与表单使用规整圆角。

## 1. 色彩

### 品牌色
| Token | 值 | 用途 |
|---|---|---|
| `--brand-primary` | `#238C82` | 主品牌青绿，主按钮/tabBar 选中/关键操作 |
| `--brand-primary-dark` | `#176B64` | 专业深青绿，主角卡/按压/悬停态 |
| `--brand-primary-light` | `#E2F1EE` | 选中底/进度背景/轻提示 |
| `--brand-secondary` | `#EE8B52` | 暖橙，待办/提醒/少量情感强调 |
| `--brand-secondary-light` | `#FDEBDD` | 暖橙浅底，提醒状态背景 |

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
| `--text-primary` | `#1F3431` | 主文本（深青墨色） |
| `--text-regular` | `#52625F` | 常规文本 |
| `--text-secondary` | `#7B8986` | 次要文本/tabBar 未选中 |
| `--text-disabled` | `#C9CDD4` | 禁用文本 |
| `--border` | `#E5E6EB` | 边框/分隔线 |
| `--bg-page` | `#F5F7F3` | 页面背景（轻暖灰绿） |
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
| `--radius-card` | `10–12px` | 普通业务卡片、表格容器 |
| `--radius-l` | `16px` | 大圆角/弹层 |
| `--radius-leaf` | `24px 10px 10px 10px` | 品牌重点区域，禁止全页面滥用 |
| `--shadow-card` | `none` | 常规卡片依靠边框和留白分层 |
| `--shadow-float` | `0 4px 20px rgba(35, 60, 54, 0.1)` | 悬浮阴影 |

## 4. 小程序专用 Token
| Token | 值 | 用途 |
|---|---|---|
| `--tab-bar-height` | `98rpx` | 底部 tabBar 高度 |
| `--tab-active` | `#238C82` | tabBar 选中色 |
| `--tab-inactive` | `#7B8986` | tabBar 未选中色 |
| `--nav-bar-height` | `88rpx` | 导航栏高度 |

## 5. Web 端专用 Token
| Token | 值 | 用途 |
|---|---|---|
| `--side-width` | `216px` | 左侧菜单宽度 |
| `--side-bg` | `#F0F3EE` | 侧边栏背景 |
| `--side-active-bg` | `#DDEDE9` | 侧边栏选中项背景 |
| `--side-active-text` | `#176B64` | 侧边栏选中项文字 |

## 6. 命名约定
- 命名采用 `--模块-语义-状态`，表意清晰。
- 小程序端将其落到 `uni.scss` 或 CSS 变量；管理端挂到 `:root` 变量并同步 Element Plus 主题色。
- **强制规则**：凡打样图中出现的颜色/字号/圆角/阴影，必须引用上表 Token；禁止在 Vue/SCSS/CSS 中写死 `#F7F6F1`、`#4F8F83`、`14px` 等值。
