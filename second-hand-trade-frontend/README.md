# 豫见好物 - 河南文创产品销售平台（前端）

## 项目简介

"豫见好物"是一个专注于河南文创产品销售的电商平台前端项目，采用 Vue 3 + TypeScript + Vite 技术栈构建。

## 技术栈

- **核心框架**：Vue 3.4.15（组合式 API）
- **构建工具**：Vite 5.0.11
- **类型系统**：TypeScript 5.3.3
- **状态管理**：Pinia 2.1.7
- **路由管理**：Vue Router 4.2.5
- **UI 组件库**：Element Plus 2.5.4
- **HTTP 请求**：Axios 1.6.5
- **样式**：SCSS
- **自动导入**：unplugin-auto-import、unplugin-vue-components

## 项目结构

```
second-hand-trade-frontend/
├── public/                  # 静态资源
├── src/
│   ├── api/                # API 接口
│   │   ├── modules/       # 模块 API
│   │   └── index.ts       # API 封装
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   ├── store/             # Pinia 状态管理
│   ├── styles/            # 全局样式
│   ├── types/             # TypeScript 类型定义
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── index.html            # HTML 模板
├── package.json          # 项目依赖
├── tsconfig.json         # TypeScript 配置
├── vite.config.ts        # Vite 配置
└── README.md             # 项目说明
```

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装步骤

1. **进入项目目录**

```bash
cd second-hand-trade-frontend
```

2. **安装依赖**

```bash
npm install
```

3. **启动开发服务器**

```bash
npm run dev
```

访问 http://localhost:3000 查看应用

4. **构建生产版本**

```bash
npm run build
```

5. **预览生产构建**

```bash
npm run preview
```

## 核心功能模块

### 已实现模块

- ✅ 项目基础框架
- ✅ 路由配置（含权限守卫）
- ✅ 状态管理（Pinia + 持久化）
- ✅ API 请求封装（Axios + 拦截器）
- ✅ 全局样式（Element Plus 覆盖）
- ✅ 基础组件（NavBar、Footer、GoodsCard）
- ✅ 首页
- ✅ 登录页

### 待实现模块

- ⏳ 注册页
- ⏳ 商品列表页
- ⏳ 商品详情页
- ⏳ 购物车
- ⏳ 订单管理
- ⏳ 个人中心
- ⏳ 卖家中心
- ⏳ 文化资讯
- ⏳ 后台管理

## 开发规范

### 目录命名

- 文件夹：kebab-case（如：`user-center`）
- 文件：PascalCase（组件）或 camelCase（工具）

### 组件开发

```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

// Props
interface Props {
  title?: string;
}
const props = withDefaults(defineProps<Props>(), {
  title: "默认标题",
});

// Emits
const emit = defineEmits<{
  change: [value: string];
}>();

// 逻辑代码
</script>

<style lang="scss" scoped>
.component-name {
  // 样式代码
}
</style>
```

### API 调用

```typescript
import { http } from "@/api";
import type { ApiResponse } from "@/types";

export const getUserInfo = (id: number) => {
  return http.get<ApiResponse<UserInfo>>(`/users/${id}`);
};
```

### 状态管理

```typescript
import { defineStore } from "pinia";
import { ref } from "vue";

export const useExampleStore = defineStore(
  "example",
  () => {
    const data = ref<any>(null);

    const loadData = async () => {
      // 加载数据
    };

    return { data, loadData };
  },
  {
    persist: true, // 持久化
  },
);
```

## 环境变量

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_PREFIX=/api
VITE_APP_TITLE=豫见好物
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=https://api.yujianhaowu.com
VITE_API_PREFIX=/api
VITE_DROP_CONSOLE=true
```

## 部署说明

### Docker 部署

```bash
# 构建镜像
docker build -t yujianhaowu-frontend:1.0.0 .

# 运行容器
docker run -d \
  -p 80:80 \
  yujianhaowu-frontend:1.0.0
```

### Nginx 部署

```nginx
server {
    listen 80;
    server_name yujianhaowu.com;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 常见问题

### 1. 跨域问题

开发环境已通过 Vite 代理配置解决跨域问题。

### 2. 环境变量不生效

确保环境变量文件命名正确（`.env.development`、`.env.production`），并且以 `VITE_` 开头。

### 3. TypeScript 类型错误

检查类型定义文件（`src/types/index.ts`）是否完整，确保导入路径正确。

## 联系方式

- 项目地址：[待补充]
- 问题反馈：[待补充]
- 邮箱：support@yujianhaowu.com

## 许可证

Apache License 2.0

---

豫见好物 - 发现河南文化之美
