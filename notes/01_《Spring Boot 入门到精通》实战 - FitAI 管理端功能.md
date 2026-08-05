---
type: lecture
course: SpringBoot入门到精通
chapter: project
title: FitAI 管理端功能
tags:
  - 后端
  - SpringBoot
  - 管理端
  - FitAI
jdk: "21"
spring-boot: "4.1.1"
cover: "[[01_后端/prj/fitai-parent/docs/notes/img/实战_脚手架与认证.svg]]"
status: 草稿
project: "[[01_后端/prj/fitai-parent]]"
note_home: "[[01_后端/prj/fitai-parent/docs/notes]]"
---
# 《Spring Boot 入门到精通》实战 - FitAI 管理端功能

<table class="hero-banner" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:separate;border:none;margin:0 0 4px 0;">
<tr>
<td style="padding:0;border:none;">
<img src="img/实战_脚手架与认证.svg" alt="FitAI 管理端功能 · Spring Boot 入门到精通" style="display:block;width:100%;height:auto;" />
</td>
</tr>
</table>
<table class="hero-card" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:separate;border:none;margin:0 0 18px 0;">
<tr>
<td style="padding:0;border:none;">
<img src="img/promo.svg" alt="扫码答疑 · 资料领取" style="display:block;width:100%;height:auto;" />
</td>
</tr>
</table>

> 📖 本章导读
>
> 本专篇是 FitAI 实战的第 01 篇（管理端总索引）。管理员通过 /admin/login 登录独立后台；接口统一 /admin/**，与 C 端分鉴权（StpKit.ADMIN）。各业务已拆成专篇，按“功能说明 → 相关表 → 接口 → 参考代码 → 自检”跟做。
>
> 技术栈：JDK 21 + Spring Boot 4.1.1 + MyBatis-Plus + Sa-Token + Knife4j + Redis。
>
> 前置阅读：[01_项目概览](../01_项目概览/01_《Spring Boot 入门到精通》实战 - FitAI 项目总览.md)（01～04：总览、架构、数据与流程、环境搭建）。用户端见 03_用户端模块。
>
> 系列专篇：01 管理端功能（本篇）→ 02 脚手架与认证 → 03 系统管理 → 04 仪表盘 → 05 商品管理 → 06 订单管理 → 07 秒杀管理 → 08 用户管理 → 09 社区管理 → 10 训练管理 → 11 营销管理 → 12 会员管理。

> 💡 阅读约定
>
> 1、按章节序号 1.1 → 1.2 → … 顺序读，建立全局地图后再进各专篇编码
>
> 2、各专篇代码示例不写 package、import；类放到注释里写的路径即可
>
> 3、命名沿用工程约定：findByPage / VO 出参 / Mapper 不带 I / Service 带 ServiceI
>
> 4、管理端 Controller 放 controller.admin，路径挂 /admin/...，方法上加 @SaCheckPermission
>
> 5、交叉引用用短名，如 05 商品管理，文件名形如 05_《Spring Boot 入门到精通》实战 - FitAI 商品管理.md

## 1.1 本篇目录与学习目标

| 序号 | 章节 | 你要做出什么 |
| --- | --- | --- |
| 1.1 | 目录与约定 | 知道本篇干什么、怎么跟系列 |
| 1.2 | 系列全景 | 搞清菜单 ↔ 表前缀 ↔ 接口前缀 ↔ 专篇 |
| 1.3 | 建议学习顺序 | 按依赖排好跟做顺序 |
| 1.4 | 子文档索引 | 快速跳到对应专篇 |
| 1.5 | 能力速查 | 复习/面试时一句话定位 |
| 1.6 | 系列自检 | 做完全部已成篇后对照验收 |
| 1.7 | 章节小结 | 复盘 |

> 💡 学完本篇你将能够
>
> 1、理解管理端与用户端接口分离，掌握 RBAC 动态菜单与 @SaCheckPermission 在系列中的位置
>
> 2、按专篇从 0 到 1 落地各管理端菜单的后端能力
>
> 3、分清表前缀（sys_* / prd_* / ord_* / usr_* / community_* / train_* / mkt_* / member_*）与接口路径、权限码的历史命名差异
>
> 4、独立联调 fitai-admin，完成已覆盖模块的分页、详情、状态流转与统计看板

---

## 1.2 系列全景

### 1.2.1 一句话说清楚

管理端是独立后台：登录拿 Admin Token，菜单由 RBAC 动态下发，业务接口全部挂在 /admin/** 下。本篇不写业务代码，只给地图。

### 1.2.2 菜单 / 表 / 接口 / 专篇对照

| 菜单/能力 | 表前缀 | 接口前缀 | 专篇 |
| --- | --- | --- | --- |
| 脚手架与双端认证 | sys_*（登录/日志等） | POST /admin/login、GET /admin/info、POST /admin/logout | 02 |
| 系统管理（RBAC） | sys_* | /admin/system/** | 03 |
| 仪表盘 | 多表汇总 | /admin/dashboard/** | 04 |
| 商品管理 | prd_* | /admin/product/** | 05 |
| 订单管理 | ord_* | /admin/order/** | 06 |
| 秒杀管理 | seckill_* | /admin/seckill/、/admin/seckill/plus/ | 07 |
| 用户与教练 | usr_* | /admin/user/、/admin/coach/ | 08 |
| 社区管理 | community_* | /admin/community/** | 09 |
| 训练管理 | train_* | /admin/exercise/、/admin/training/ | 10 |
| 营销管理 | mkt_* | /admin/marketing/、/admin/challenge/ | 11 |
| 会员管理 | member_* | /admin/member/** | 12 |

> 💡 尚未单独成篇、前端菜单可能仍可见的能力（可后续补专篇）：课程管理、支付管理、消息管理、AI 管理。仪表盘会读 crs_course 做课程总数统计，完整课程 CRUD 另篇。

### 1.2.3 本步验收

> ✅ 你能说出：管理端路径前缀是 /admin；用户管理管的是 usr_* 不是 sys_user；挑战赛接口在 /admin/challenge 而不是 /admin/marketing。

---

## 1.3 建议学习顺序

专篇文件名编号即系列序号。仪表盘（04）放在系统管理之后、业务加深之前，是为了登录后首页立刻有数；秒杀依赖商品 SKU 选项；训练建议在用户模块之后（计划/记录联查昵称）。

| 顺序 | 专篇 | 说明 |
| :---: | :--- | :--- |
| 1 | 02 脚手架与认证 | 工程结构、统一响应、Sa-Token 双端登录、权限注解、sys_* 骨架 |
| 2 | 03 系统管理 | 账号/角色/菜单/部门/字典/配置/操作日志/公告/定时任务 |
| 3 | 04 仪表盘 | stats / trend / activities / distribution 四个统计接口 |
| 4 | 05 商品管理 | 分类树、品牌、SPU/SKU；库存在 prd_stock；状态 0/1 前后端相反需转换 |
| 5 | 06 订单管理 | 分页、发货、取消、物流；前端状态字串与库内数字互转 |
| 6 | 07 秒杀管理 | 活动 + 秒杀 SKU + Redis 预热；Plus 订单分页与统计 |
| 7 | 08 用户管理 | C 端用户/分群/体测/设置/地址 + 教练/排期/约课/绑定（非 sys_user） |
| 8 | 09 社区管理 | 动态、评论、内容审核（community_*；权限码仍为 cms:*） |
| 9 | 10 训练管理 | 动作库 /admin/exercise，计划/记录/设备 /admin/training/** |
| 10 | 11 营销管理 | 轮播图、优惠券、领取记录；挑战赛走 /admin/challenge |
| 11 | 12 会员管理 | 套餐、会员订单、权益；订阅由已支付订单派生 |

### 1.3.1 本步验收

> ✅ 你知道：先做 02～03 再碰业务；做秒杀前先完成商品；做训练前最好完成用户。

---

## 1.4 子文档索引

### 1.4.1 已成篇

| 编号 | 专篇短名 | 核心能力 | 关键路径 |
| :---: | :--- | :--- | :--- |
| 01 | 02 脚手架与认证 | 脚手架、统一异常、双端登录、登录日志、sys_* 骨架 | /admin/login、/admin/info、/admin/system/** |
| 02 | 03 系统管理 | RBAC 加深：账号角色菜单部门、字典配置、@OperLog、公告、内置 Cron | /admin/system/** |
| 03 | 04 仪表盘 | 核心卡片、趋势、最近动态、订单状态饼图 | /admin/dashboard/** |
| 04 | 05 商品管理 | 分类、品牌、SPU/SKU、库存汇总 | /admin/product/** |
| 05 | 06 订单管理 | 订单分页/详情、发货、取消、物流轨迹 | /admin/order/** |
| 06 | 07 秒杀管理 | 活动 CRUD、秒杀 SKU、Redis 库存、订单统计 | /admin/seckill/** |
| 07 | 08 用户管理 | App 用户九块能力 + 教练体系 | /admin/user/、/admin/coach/ |
| 08 | 09 社区管理 | 动态/评论软删、审核通过驳回回写 | /admin/community/** |
| 09 | 10 训练管理 | 动作库、计划、记录 KPI、穿戴设备 | /admin/exercise、/admin/training/** |
| 10 | 11 营销管理 | 轮播、优惠券、领取作废、挑战赛监管 | /admin/marketing/、/admin/challenge/ |
| 11 | 12 会员管理 | 套餐、会员订单、权益、订阅派生视图 | /admin/member/** |

文件名统一为：

```text
NN_《Spring Boot 入门到精通》实战 - FitAI {模块名}.md
```

### 1.4.2 规划中（可后续补专篇）

| 模块 | 预期能力 | 备注 |
| --- | --- | --- |
| 课程管理 | 分类、课程 CRUD、视频与播放地址 | 仪表盘已用 crs_course 做课程总数 |
| 支付管理 | 支付流水、退款、统计 | 12 会员管理“下一步”提及 |
| 消息管理 | 站内通知、群发、模板 | 与系统公告区分 |
| AI 管理 | 知识库、对话记录、模型/提示词、统计 | 依赖 Spring AI 配置 |

### 1.4.3 本步验收

> ✅ 打开 `docs/notes/02_管理端模块/`，能按编号找到 01～12；概览系列在 `docs/notes/01_项目概览/`（编号 01～04）。

---

## 1.5 能力速查

便于复习与面试时快速定位“这一块做什么”。

| 专篇 | 一句话 | 易踩坑 |
| --- | --- | --- |
| 02 | 搭工程 + 双端登录闭环 | user-ignore-urls 必须包含 /admin/**，否则管理端被 C 端鉴权拦住 |
| 03 | 系统管理从骨架做到后台真能用 | 菜单树 / 部门 ancestors；删父部门有子则拒 |
| 04 | 首页四个统计接口拆开写 | 销售额只计订单状态 1/2/3 |
| 05 | 商品三件套：分类、品牌、SPU/SKU | 库存在 prd_stock；启用状态前后端 0/1 相反 |
| 06 | 发货写物流、取消校验状态 | 前端 paid/unpaid ↔ 库内 "1"/"0" |
| 07 | 秒杀价库存 + Redis 预热 | 雪花主键 ASSIGN_ID；依赖商品 SKU 选项接口 |
| 08 | 管的是 App 用户和教练，不是管理员 | usr_* ≠ sys_user；多处状态要 VO 互转 |
| 09 | 动态评论 + 审核流水 | 用 community_*，权限码仍是 cms:*；删除是软删 |
| 10 | 动作库表 train_action，接口叫 exercise | 字段名靠 VO“翻译”对齐前端 |
| 11 | 轮播/券走 marketing，挑战赛走 challenge | 秒杀不在本篇（见 07） |
| 12 | 订阅不是表，是已支付订单派生 | 用 member_*，勿混历史 mem_* / ord_member_order |

### 1.5.1 本步验收

> ✅ 随便抽三个模块，能不看专篇说出表前缀、接口前缀和至少一个易踩坑点。

---

## 1.6 系列自检

按顺序勾选（做完全部已成篇后）：

> ✅ 总验收
>
> 1、POST /admin/login 拿 token，GET /admin/info 有菜单与权限
>
> 2、系统管理：账号赋角色、角色赋菜单后，动态路由与按钮权限生效
>
> 3、仪表盘四个接口在有演示数据时数字非空（或合理为 0）
>
> 4、商品 → 订单 → 秒杀链路可联调（选品、发货、Redis 库存）
>
> 5、用户侧与教练侧页面接口 200；社区审核通过/驳回会回写业务状态
>
> 6、训练四块菜单、营销（含挑战赛）、会员（套餐/订单/权益/订阅）均可打开验收

---

## 1.7 章节小结

| 序号 | 你学会了 |
| --- | --- |
| 1 | 管理端系列从 01 总索引到 12 会员的完整地图 |
| 2 | 菜单、表前缀、接口前缀、专篇编号的对应关系 |
| 3 | 按依赖排序的建议学习路径 |
| 4 | 各专篇共性约定：1.x 编号、验收块、Controller 落点 |

> 下一步：从 02 脚手架与认证 开始，或按 1.3 跳到你当前卡点的专篇。

---

## ❓ 常见问题

1、文件名里的中文书名号和空格能改吗？  
不要改。系列统一为 NN_《Spring Boot 入门到精通》实战 - FitAI {模块}.md，与 Obsidian / 讲义侧引用一致。

2、课程 / 支付 / 消息 / AI 为什么没有专篇？  
尚未单独成篇；前端菜单若仍可见，可先联调已有接口或按“规划中”自行扩展。仪表盘已最小依赖 crs_course。

3、**权限码为什么和表前缀不一致（如 cms: vs community_*）？**  
菜单表历史命名保留；表与接口已统一。Controller 上的 @SaCheckPermission 跟菜单权限串走即可。
