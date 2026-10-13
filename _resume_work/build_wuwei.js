/**
 * Generate 吴炜 resume (docx) — structure mirrors 丁浩 Word resume,
 * content depth references 平安求职简历, personal/education from user.
 */
const { Document, Packer, Paragraph, TextRun, AlignmentType, BorderStyle,
        HeadingLevel, TabStopType, TabStopPosition, WidthType } = require('docx');
const fs = require('fs');
const path = require('path');

const BLUE = "1F4E79";
const DARK = "222222";
const GRAY = "555555";

function sectionTitle(text) {
  return new Paragraph({
    spacing: { before: 200, after: 80 },
    border: {
      bottom: { style: BorderStyle.SINGLE, size: 12, color: BLUE, space: 4 },
    },
    children: [
      new TextRun({ text, bold: true, size: 24, font: "微软雅黑", color: BLUE }),
    ],
  });
}

function body(text, opts = {}) {
  return new Paragraph({
    spacing: { after: opts.after ?? 60, before: opts.before ?? 0, line: 276 },
    indent: opts.indent ? { left: opts.indent } : undefined,
    children: [
      new TextRun({
        text,
        size: opts.size ?? 20,
        font: "微软雅黑",
        color: opts.color ?? DARK,
        bold: !!opts.bold,
      }),
    ],
  });
}

function mixedLine(parts, opts = {}) {
  return new Paragraph({
    spacing: { after: opts.after ?? 60, before: opts.before ?? 0, line: 276 },
    indent: opts.indent ? { left: opts.indent } : undefined,
    children: parts.map((p) =>
      new TextRun({
        text: p.text,
        size: p.size ?? 20,
        font: "微软雅黑",
        color: p.color ?? DARK,
        bold: !!p.bold,
      })
    ),
  });
}

const doc = new Document({
  styles: {
    default: {
      document: {
        styles: [{ id: "Normal", run: { font: "微软雅黑", size: 20 } }],
      },
    },
  },
  sections: [
    {
      properties: {
        page: {
          margin: { top: 567, bottom: 567, left: 680, right: 680 }, // ~1cm/1.2cm
        },
      },
      children: [
        // ===== Header =====
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 80 },
          children: [
            new TextRun({ text: "吴炜", bold: true, size: 36, font: "微软雅黑", color: BLUE }),
          ],
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 40 },
          children: [
            new TextRun({
              text: "电话：17513677910　|　邮箱：2794661931@qq.com",
              size: 18,
              font: "微软雅黑",
              color: GRAY,
            }),
          ],
        }),
        new Paragraph({
          alignment: AlignmentType.CENTER,
          spacing: { after: 120 },
          children: [
            new TextRun({
              text: "23岁　|　男　|　应届生（专升本）　|　求职意向：Java后端/全栈工程师、AI应用工程师",
              size: 18,
              font: "微软雅黑",
              color: GRAY,
            }),
          ],
        }),

        // ===== 教育背景 =====
        sectionTitle("教育背景"),
        mixedLine([
          { text: "新乡工程学院", bold: true },
          { text: "　　本科（专升本）　　电气工程及其自动化　　2024.09 - 2026.06", color: GRAY },
        ]),
        body("· 专升本应届，系统学习电路、自动控制、嵌入式等相关课程，同时通过实训系统掌握Java全栈与AI应用开发能力。", { indent: 100 }),
        mixedLine([
          { text: "安阳工学院", bold: true },
          { text: "　　专科　　机电一体化　　2021.09 - 2024.06", color: GRAY },
        ], { before: 60 }),
        body("· 具备机电一体化专业基础，熟悉机械结构、电气控制与自动化相关知识，培养了较强的动手实践与问题排查能力。", { indent: 100 }),

        // ===== 专业技能 =====
        sectionTitle("专业技能"),
        body("后端核心技术", { bold: true, after: 40 }),
        body("Java基础：熟练掌握Java面向对象、集合、异常处理、注解等核心知识点；熟悉JVM内存模型、类加载机制与常见垃圾回收思路；具备JavaWeb基础（Servlet/Filter等）。"),
        body("并发编程：掌握多线程与线程池核心参数，了解常见锁机制与并发工具类的基本用法。"),
        body("主流框架：熟练使用Spring Boot、Spring MVC、MyBatis/MyBatis-Plus；理解IOC、AOP与自动装配；了解Spring Cloud Alibaba微服务生态（Nacos、Gateway、OpenFeign、Sentinel等）；具备基于若依（RuoYi-Vue）二次开发能力。"),
        body("安全认证：掌握JWT、Sa-Token等认证方案，熟悉RBAC权限模型，可实现接口鉴权与动态菜单。"),

        body("数据库与中间件", { bold: true, before: 80, after: 40 }),
        body("数据库：熟练使用MySQL，掌握索引优化、事务与InnoDB锁机制基础；熟悉Redis常用数据结构与缓存穿透/击穿/雪崩应对思路。"),
        body("消息队列：了解RabbitMQ基本模型，熟悉消息可靠投递、幂等消费与异步解耦应用场景。"),
        body("运维工程化：熟悉Linux常用命令、Docker基础部署、Nginx反向代理；熟练使用Git与Maven进行协作与构建。"),

        body("前端与工具链", { bold: true, before: 80, after: 40 }),
        body("前端能力：掌握HTML5、CSS3、JavaScript；熟练使用Vue2/Vue3 + Element Plus构建管理后台；了解Axios请求封装与ECharts数据可视化；了解微信小程序开发流程。"),
        body("工程工具：熟练使用IntelliJ IDEA、VS Code、Apifox/Postman、Knife4j；掌握Cursor、Codex、Trae等AI编程工具，能显著提升开发与联调效率。"),

        body("AI相关技术", { bold: true, before: 80, after: 40 }),
        body("大模型应用：了解Spring AI / Spring AI Alibaba基本用法，掌握Prompt工程；了解RAG检索增强生成与向量库接入思路；了解Tool/MCP工具调用与多轮对话记忆；可参与Agent智能问答、知识库问答等业务落地。"),

        // ===== 项目经历 =====
        sectionTitle("项目经历"),
        mixedLine([
          { text: "FitAI 智能健身平台", bold: true },
          { text: "　　Java后端开发（实训/在校项目）　　2025.03 - 2025.08", color: GRAY },
        ]),
        body("主要技术：JDK 21、Spring Boot、MyBatis-Plus、Sa-Token、MySQL、Redis、Vue3、Element-Plus、uni-app、Spring AI、阿里云OSS/VOD、Docker、Nginx、Knife4j、Git"),
        body("项目描述：面向健身用户与运营方的一站式智能健身平台，整合AI健身助手、训练计划、饮食管理、视频课程、教练约课、社区互动与健身商城/会员体系。采用前后端分离与模块化单体架构，支持管理后台、PC用户端与移动端（uni-app）多端接入。"),
        body("责任描述：", { bold: true, before: 40 }),
        body("1、基础架构与安全认证"),
        body("· 参与Spring Boot模块化后端搭建，落地统一响应、全局异常处理、分页封装与Knife4j接口文档，支撑管理端/PC/移动端联调。", { indent: 100 }),
        body("· 使用Sa-Token + JWT + Redis实现Admin/App双端鉴权；管理端落地RBAC动态菜单与接口权限控制。", { indent: 100 }),
        body("2、核心业务模块"),
        body("· 参与训练、饮食、课程、教练约课、社区、商城订单、会员等业务模块的接口设计与开发；商品侧落地分类/品牌/SPU-SKU/库存模型。", { indent: 100 }),
        body("· 集成阿里云OSS/VOD完成媒资上传与点播能力；配合Redis缓存热点数据，优化高频查询。", { indent: 100 }),
        body("3、AI健身助手"),
        body("· 参与基于大模型的多轮健身咨询能力建设，支持训练指导、饮食建议等场景；配合提示词与知识库降低幻觉风险。", { indent: 100 }),
        body("· 支持SSE流式输出提升交互体验；结合会员体系做用量展示与配额控制。", { indent: 100 }),
        body("4、部署与协作"),
        body("· 使用Docker Compose编排MySQL、Redis、Nginx与业务服务，完成可复现演示环境；使用Git进行版本协作。", { indent: 100 }),

        mixedLine([
          { text: "泽润智能仓储后台管理系统", bold: true },
          { text: "　　全栈开发（实训项目）　　2025.09 - 2025.12", color: GRAY },
        ], { before: 120 }),
        body("主要技术：Spring Boot + MyBatis-Plus + MySQL + Redis + JWT + Vue3 + Element Plus + ECharts"),
        body("项目描述：前后端分离的一体化仓储管理系统，涵盖商品、采购、出入库、库存盘点、调拨、权限管理与数据看板等模块，支撑采购下单、入库质检、出库发货、库存盘点的业务闭环。"),
        body("责任描述：", { bold: true, before: 40 }),
        body("· 基于JWT + 拦截器 + RBAC（用户-角色-权限）实现接口鉴权与前端动态菜单渲染。", { indent: 100 }),
        body("· 使用AOP + 自定义注解采集操作日志；全局异常处理与统一返回体规范接口。", { indent: 100 }),
        body("· 使用Redis缓存验证码与登录态；定时任务巡检库存并做预警提醒。", { indent: 100 }),
        body("· 前端完成商品/采购/出入库/盘点等页面，ECharts展示库存统计与出入库趋势。", { indent: 100 }),

        // ===== 自我评价 =====
        sectionTitle("自我评价"),
        body("本人性格踏实稳重，做事认真负责，具备较强的学习能力与执行力，能够快速适应新环境并投入工作。专升本背景兼具机电/电气专业实践基础与软件开发实训能力，遇到问题善于对比多种方案并找到可行路径。"),
        body("具备Java后端与基础全栈开发能力，了解Spring AI、RAG与Agent等方向，希望将工程化能力与大模型应用相结合，在实际业务中持续提升技术深度与综合素养，成长为一名技术扎实、责任心强、能独立解决问题的开发工程师。"),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buffer) => {
  const out1 = path.join(__dirname, "吴炜_简历.docx");
  const desk = "C:\\Users\\Administrator\\Desktop\\吴炜.docx";
  fs.writeFileSync(out1, buffer);
  fs.writeFileSync(desk, buffer);
  console.log("Wrote", out1, buffer.length);
  console.log("Wrote", desk, buffer.length);
});
