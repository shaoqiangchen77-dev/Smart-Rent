# SmartRent 管理端 UI 设计需求（admin-panel）

> 用途：本文档供 AI/设计师据此产出管理后台（Web）的 HTML 界面。
> 技术栈：Vue3 + Vite + Element Plus + ECharts（图表）。整体为深色侧边栏 + 浅色内容区的中后台风格。
> 阅读对象：UI 生成 Agent。请严格按"统一设计语言"落地配色与组件风格，再逐页实现。

---

## 一、统一设计语言（必须遵循）

### 配色（墨黑 · 香槟金 · 高级风，2026-07-17 更新）
> 视觉以 `docs/ui-prototype/*.html` 为准。原青绿 `#0f766e` 已弃用，改为墨黑底 + 香槟金点缀。
| 用途 | 色值 | 说明 |
|---|---|---|
| 主色 Primary | `#c9a86a`（香槟金）/ `#e2c690`（亮金） | 主按钮、激活态、链接、金线 |
| 深色背景 Deep | `#0e0f13` / `#16181f` | 侧边栏、登录页、头部 hero、卡片深底 |
| 金线 / 描边 | `rgba(201,168,106,.30)` | 分隔线、卡片高光、聚焦环 |
| 强调 Accent | `#b8893f`（深金） | 价格、金额、重要数值 |
| 页面背景 | `#f5f3ee` | 内容区底色（暖米灰） |
| 卡片底 | `#ffffff` | 数据卡片 |
| 卡片描边 | `#e9e4da` | 1px 细边框 |
| 标题文字 | `#1b1c20` | 主标题 |
| 正文 / 辅助 | `#56524b` / `#928c80` | 次级 / 占位说明 |
| 状态语义色 | `#5aa06b` / `#cf9b4e` / `#cf6a6a` / `#8a93a3` | 成功 / 警告 / 危险 / 信息 |

### 字体与圆角
- 字体：系统无衬线，`Microsoft YaHei` / `PingFang SC` 优先。
- 圆角：卡片 `8px`，按钮 `6px`，标签/徽标 `999px`（胶囊）。
- 阴影：轻量 `0 10px 24px rgba(31,42,46,0.04)`。
- CSS 变量建议：`--sr-primary:#0f766e; --sr-bg:#f6f4ef; --sr-text:#18332f; --sr-muted:#7b8582; --sr-border:#e7e1d6;`

### 通用组件
- 顶部 `el-card` 包裹表格，标题在 `#header` 插槽（左标题 + 右筛选/操作）。
- 表格统一 `stripe` 斑马纹、`v-loading` 加载态。
- 分页统一 `layout="total, prev, pager, next"`，右对齐。
- 状态一律用 `el-tag` 上色（见各页 statusMap / statusType）。

---

## 二、登录页（/login）

**布局**：左右分栏（`grid-template-columns: minmax(420px,0.92fr) minmax(420px,1fr)`），满屏，深色底 `#18332f`。

**左栏 · 品牌区**（深底，含 3D 城市线框动画背景 + 遮罩渐变）
- 品牌标记块：方块 `.logo-mark` 写"住"字（白底深字）。
- 主标题：`SmartRent 管理端`。
- 副文案：`房源审核、预约跟进、合同与消息运营集中处理。`
- 两个 brief 行（上边线分隔）：`房源状态 / 实时同步`、`运营看板 / 按日追踪`。

**右栏 · 登录卡片**（半透明白卡 `rgba(255,255,255,0.92)` + 毛玻璃 `backdrop-filter: blur(16px)`，宽 420px）
- 卡片头：标题`管理员登录` + 副标题`使用后台账号进入系统`。
- 表单项：
  - 用户名（`el-input` + `User` 前缀图标，size large）。
  - 密码（`el-input` type=password + `Lock` 图标 + `show-password`，回车提交）。
- 主按钮：`登录`（primary，整宽，height 44px）。
- 校验：用户名/密码必填；登录后校验 `isAdmin`，非管理员提示`仅管理员可登录后台`并退出。

---

## 三、整体布局（AdminLayout）

**左 · 侧边栏**（`#18332f`，宽 248px，可折叠至 72px）
- 顶部 Logo：方块"住" + 文字 `SmartRent`（折叠时仅剩"住"）。
- 菜单（`el-menu`，router 模式，透明底，文字 `#d7e2dc`，激活 `#fff`）：
  - 数据看板 `/dashboard`（图标 DataBoard）
  - 用户管理 `/user`（图标 User）
  - 房源管理 `/house`（子菜单，图标 House）
    - 房源列表 `/house`
    - 房源审核 `/house/audit`
  - 预约管理 `/order`（图标 Document）
  - 合同管理 `/contract`（图标 Files）
  - 消息管理 `/message`（图标 Bell）
- 激活项背景 `rgba(255,255,255,0.13)`，hover `rgba(255,255,255,0.08)`。

**右 · 顶栏**（`height 72px`，白底毛玻璃，底边线）
- 左：折叠按钮（Fold/Expand 图标）+ 面包屑（`工作台 / 当前页标题`）。
- 右：工作日期（`zh-CN` 月日+星期）+ 用户下拉（头像首字母 + 用户名 → 退出登录）。

**右 · 主内容区**（背景 `--sr-bg`，padding 24px，可滚动，承载 `router-view`）。

---

## 四、数据看板（/dashboard）

**页面头**
- eyebrow：`运营概览`；主标题：`数据看板`；右侧范围胶囊：`近 7 日`。

**统计卡（4 列 el-col:span=6）**
| 卡片 | 图标字 | 数值字段 | 标签 |
|---|---|---|---|
| 用户总数 | 用 | `stats.userCount` | 用户总数 |
| 房源总数 | 房 | `stats.houseCount` | 房源总数 |
| 今日新增用户 | 新 | `stats.todayNewUsers` | 今日新增用户 |
| 今日新增房源 | 审 | `stats.todayNewHouses` | 今日新增房源 |

> 图标底色：用=`#e5f3ef`/绿，房=`#f5eddd`/橙，新=`#e7f0f7`/蓝，审=`#fff2e6`/珊瑚。

**趋势图（两列 el-col:span=12，el-card + ECharts）**
- 卡片1 标题：`用户增长趋势`（折线，色 `#0f766e`）。
- 卡片2 标题：`房源增长趋势`（折线，色 `#d97706`）。
- 图表：近 7 日，X 轴日期、Y 轴数量，平滑面积折线，tooltip 深色底。
- 容器高度 `300px`，随窗口 resize 自适应。

---

## 五、用户管理（/user）

**筛选**（卡片头右侧，el-select 可清空）
- 角色：`租客(0)` / `房东(1)` / `管理员(2)`。
- 状态：`正常(1)` / `禁用(0)`。

**表格列**
| 列 | 字段 | 备注 |
|---|---|---|
| ID | `id` | 80px |
| 用户名 | `username` | |
| 手机号 | `phone` | |
| 角色 | `role` | `el-tag`：租客(默认) / 房东(success) / 管理员(danger) |
| 状态 | `status` | `el-switch`，开=正常，关=禁用；切换调 `updateUserStatus` |
| 注册时间 | `createTime` | 180px |

**分页**：默认 `page=1, size=10`。

---

## 六、房源管理 - 房源列表（/house）

**筛选**：状态下拉 `待审核(0)` / `已上架(1)` / `已下架(2)` / `已租出(3)`。

**表格列**
| 列 | 字段 | 备注 |
|---|---|---|
| ID | `id` | 80px |
| 标题 | `title` | 超长省略 |
| 区域 | `area` | 100px |
| 月租(元) | `price` | 100px |
| 户型 | `houseType` | 80px |
| 状态 | `status` | `el-tag`：待审核(warning)/已上架(success)/已下架(info)/已租出(danger) |
| 浏览 | `viewCount` | 80px |
| 评分 | `avgRating` | 80px |
| 发布时间 | `createTime` | 180px |

**分页**：`page=1, size=10`。

---

## 七、房源审核（/house/audit）

- 默认仅展示 `status=0`（待审核）。
- 表格列与"房源列表"相似（ID / 标题 / 区域 / 月租 / 户型 / 提交时间 `createTime`）。
- 操作列（fixed 右，200px）：
  - `通过` 按钮（success）→ `auditHouse(id, 1)`。
  - `拒绝` 按钮（danger）→ 弹出 `ElMessageBox.prompt` 输入"拒绝原因"，再 `auditHouse(id, 2, remark)`。
- 操作后刷新列表。

---

## 八、预约管理（/order）

- 标题：`预约管理`。
- 表格列：
  | 列 | 字段 | 备注 |
  |---|---|---|
  | ID | `id` | 80px |
  | 租客ID | `userId` | 100px |
  | 房源ID | `houseId` | 100px |
  | 预约时间 | `viewingTime` | 180px |
  | 联系电话 | `contactPhone` | 140px |
  | 状态 | `status` | `el-tag`：待确认(warning)/已确认(success)/已取消(info)/已完成(默认) |
  | 创建时间 | `createTime` | 180px |
- 空态：`el-empty` "暂无预约数据"。

---

## 九、合同管理（/contract）

- 标题：`合同管理`。
- 表格列：
  | 列 | 字段 | 备注 |
  |---|---|---|
  | ID | `id` | 80px |
  | 合同编号 | `contractNo` | 200px |
  | 租客ID | `userId` | 100px |
  | 房源ID | `houseId` | 100px |
  | 月租(元) | `monthlyRent` | 100px |
  | 开始日期 | `startDate` | 120px |
  | 结束日期 | `endDate` | 120px |
  | 状态 | `status` | `el-tag`：待生效(warning)/生效中(success)/已到期(info)/已终止(danger) |
  | 创建时间 | `createTime` | 180px |

---

## 十、消息管理（/message）

- 卡片头：`消息管理` 标题 + 右侧 `发送消息` 主按钮（打开对话框）。
- 表格列：
  | 列 | 字段 | 备注 |
  |---|---|---|
  | ID | `id` | 80px |
  | 标题 | `title` | min-width 150 |
  | 类型 | `msgType` | `el-tag`：system(默认)/appointment(warning)/contract(success)/bill(danger)/chat(info) |
  | 接收用户ID | `receiverId` | 120px |
  | 已读 | `isRead` | `el-tag`：已读(success) / 未读(info) |
  | 发送时间 | `createTime` | 180px |
- 分页：`page=1, size=20`。

**发送消息对话框**（width 500px）
- 接收用户：`el-input`（用户ID，留空=全部用户）。
- 消息类型：`el-select` → 系统通知(system) / 预约通知(appointment) / 合同通知(contract) / 账单通知(bill)。
- 标题：`el-input`。
- 内容：`el-input` type=textarea :rows=4。
- 底部：`取消` / `发送`（primary）。校验标题+内容必填。

---

## 十一、交付清单（给生成 Agent）

- [ ] 登录页：左品牌 3D 城市视觉 + 右毛玻璃登录卡，主色 `#0f766e`。
- [ ] 布局：深色侧边栏（6 个菜单项，房源管理含子菜单）+ 顶栏（折叠/面包屑/日期/用户下拉）。
- [ ] 看板：4 统计卡 + 2 趋势折线图（ECharts）。
- [ ] 用户/房源/预约/合同/消息：统一 el-card + stripe 表格 + 状态 el-tag + 分页。
- [ ] 房源审核：行内"通过/拒绝"，拒绝弹原因输入。
- [ ] 消息：发送对话框（类型/接收人/标题/内容）。
- [ ] 全部页面遵循上方"统一设计语言"配色与圆角。
