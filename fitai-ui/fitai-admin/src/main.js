import { createApp } from "vue";
import App from "./App.vue";
// Tailwind CSS + custom styles
import "@/styles/index.css";
// element css
import "element-plus/dist/index.css";
// element dark css
import "element-plus/theme-chalk/dark/css-vars.css";

// Vxe UI + Vxe Table
import VxeUI from "vxe-pc-ui";
import "vxe-pc-ui/lib/style.css";
import VxeUITable from "vxe-table";
import "vxe-table/lib/style.css";

// 导入中文语言包
import zhCN from "vxe-pc-ui/lib/language/zh-CN";

// Vxe UI Plugins
import VxeUIPluginRenderElement from "@vxe-ui/plugin-render-element";
import "@vxe-ui/plugin-render-element/dist/style.css";
import VxeUIPluginExportXLSX from "@vxe-ui/plugin-export-xlsx";
import VxeUIPluginExportPDF from "@vxe-ui/plugin-export-pdf";
// Excel library
import ExcelJS from "exceljs";
// jsPDF
import { jsPDF } from "jspdf";

// 将 jsPDF 挂载到 window（字体文件需要）
window.jsPDF = jsPDF;

// 设置语言
VxeUI.setI18n("zh-CN", zhCN);
VxeUI.setLanguage("zh-CN");

// Register Vxe UI plugins
VxeUI.use(VxeUIPluginRenderElement);
VxeUI.use(VxeUIPluginExportXLSX, { ExcelJS });

// PDF 导出插件 - 配置中文字体（使用本地字体文件）
VxeUI.use(VxeUIPluginExportPDF, {
  jsPDF,
  fontName: "SourceHanSans-Normal",
  fonts: [
    {
      fontName: "SourceHanSans-Normal",
      fontStyle: "normal",
      fontUrl: "/fonts/source-han-sans-normal.js"
    }
  ]
});

// svg icons
import "virtual:svg-icons-register";
// element plus
import ElementPlus from "element-plus";
// element icons
import * as Icons from "@element-plus/icons-vue";
// custom directives
import directives from "@/directives/index";
// vue Router
import router from "@/routers";
// vue i18n
import I18n from "@/languages/index";
// pinia store
import pinia from "@/stores";
// errorHandler
import errorHandler from "@/utils/errorHandler";

const app = createApp(App);

app.config.errorHandler = errorHandler;

// register the element Icons component
Object.keys(Icons).forEach(key => {
  app.component(key, Icons[key]);
});

app.use(VxeUI).use(VxeUITable).use(ElementPlus).use(directives).use(router).use(I18n).use(pinia).mount("#app");
