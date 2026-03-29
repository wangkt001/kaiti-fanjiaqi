# 豫见好物 - 河南文创产品销售平台（后端）

## 项目简介

"豫见好物"是一个专注于河南文创产品销售的电商平台，旨在解决河南文创产品销售渠道有限、文化传播不足、供需信息不对称的问题。平台集文化展示、商品销售、文化互动于一体，推动河南传统文化的创新性发展和商业价值转化。

## 技术栈

- **核心框架**：Spring Boot 3.2.3
- **ORM 框架**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0+
- **缓存**：Redis 7.0+
- **安全认证**：Spring Security + JWT
- **API 文档**：Knife4j (Swagger 3.0)
- **工具库**：Hutool、MapStruct、Lombok

## 项目结构

```
second-hand-trade-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/campus/yujianhaowu/
│   │   │       ├── common/          # 通用类（响应、状态码等）
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── exception/       # 异常处理
│   │   │       ├── interceptor/     # 拦截器
│   │   │       ├── mapper/          # Mapper 接口
│   │   │       ├── model/           # 数据模型
│   │   │       │   ├── dto/        # 数据传输对象
│   │   │       │   ├── entity/     # 实体类
│   │   │       │   └── vo/         # 视图对象
│   │   │       ├── service/         # 服务层
│   │   │       │   └── impl/       # 服务实现
│   │   │       └── util/           # 工具类
│   │   └── resources/
│   │       ├── mapper/             # MyBatis XML 映射文件
│   │       ├── application.yml     # 配置文件
│   │       ├── application-dev.yml # 开发环境配置
│   │       ├── application-prod.yml# 生产环境配置
│   │       └── schema.sql          # 数据库初始化脚本
│   └── test/
├── pom.xml
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

### 安装步骤

1. **克隆项目**

```bash
cd second-hand-trade-backend
```

2. **创建数据库**

```sql
CREATE DATABASE employment_fanjiaqi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

启动项目之前，请先在 MySQL 中手动执行上面的 SQL，创建 `employment_fanjiaqi` 数据库。

3. **初始化数据库**
   项目启动时会自动执行 `src/main/resources/schema.sql` 来创建表和初始化基础数据

4. **修改配置**
   编辑 `src/main/resources/application-dev.yml`，配置数据库和 Redis 连接信息

5. **编译项目**

```bash
mvn clean install
```

6. **运行项目**

```bash
mvn spring-boot:run
```

7. **访问 API 文档**

```
http://localhost:8080/api/doc.html
```

## 核心功能模块

### 已实现模块

- ✅ 项目基础框架
- ✅ 统一响应结果
- ✅ 全局异常处理
- ✅ JWT 认证
- ✅ Redis 缓存
- ✅ MyBatis-Plus 配置
- ✅ Swagger API 文档
- ✅ 基础实体类
- ✅ 用户、商品、分类、标签实体

### 待实现模块

- ⏳ 用户管理模块（登录、注册、个人信息）
- ⏳ 商品管理模块（发布、编辑、审核）
- ⏳ 文化标签模块
- ⏳ 订单管理模块
- ⏳ 评价互动模块
- ⏳ 文化资讯模块
- ⏳ 后台管理模块

## API 接口

### 认证相关

- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/logout` - 用户登出
- POST `/api/auth/refresh` - 刷新 Token

### 用户相关

- GET `/api/users/profile` - 获取个人信息
- PUT `/api/users/profile` - 更新个人信息
- POST `/api/users/avatar` - 上传头像

### 商品相关

- GET `/api/products` - 获取商品列表
- GET `/api/products/{id}` - 获取商品详情
- POST `/api/products` - 发布商品（卖家）
- PUT `/api/products/{id}` - 更新商品（卖家）
- DELETE `/api/products/{id}` - 删除商品（卖家）

### 分类相关

- GET `/api/categories` - 获取分类列表
- GET `/api/categories/tree` - 获取分类树

### 标签相关

- GET `/api/tags` - 获取标签列表
- GET `/api/tags/hot` - 获取热门标签

## 数据库表

### 核心表

| 表名                  | 说明               |
| --------------------- | ------------------ |
| users                 | 用户表             |
| categories            | 商品分类表         |
| products              | 商品表             |
| product_images        | 商品图片表         |
| cultural_tags         | 文化标签表         |
| product_tags          | 商品 - 标签关联表  |
| product_cultural_info | 商品文化信息扩展表 |
| admin_users           | 管理员表           |

### 待实现表

- orders - 订单表
- order_items - 订单商品表
- reviews - 评价表
- review_replies - 评价回复表
- likes - 点赞表
- favorites - 收藏表
- user_follows - 关注表
- cultural_contents - 文化资讯表
- content_comments - 资讯评论表
- disputes - 纠纷表
- reports - 举报表
- system_settings - 系统设置表
- system_logs - 系统日志表

## 开发规范

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 使用 MapStruct 进行对象转换
- 统一使用 Result 作为响应结果

### 命名规范

- Controller：`XxxController`
- Service：`XxxService` / `XxxServiceImpl`
- Mapper：`XxxMapper`
- Entity：`Xxx`
- DTO：`XxxDTO`
- VO：`XxxVO`
- Request：`XxxRequest`
- Response：`XxxResponse`

### Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具链相关
```

## 部署说明

### 开发环境

```bash
mvn spring-boot:run
```

### 生产环境

```bash
# 编译打包
mvn clean package -P prod

# 运行
java -jar target/yujianhaowu-backend-1.0.0.jar --spring.profiles.active=prod
```

### Docker 部署

```bash
# 构建镜像
docker build -t yujianhaowu-backend:1.0.0 .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=mysql_host \
  -e DB_PASSWORD=db_password \
  -e REDIS_HOST=redis_host \
  yujianhaowu-backend:1.0.0
```

## 常见问题

### 1. 启动失败

检查数据库和 Redis 是否正常运行，配置文件中的连接信息是否正确。

### 2. 跨域问题

项目已配置 CORS，如需调整请修改 `WebConfig.java`。

### 3. Token 失效

检查 JWT 密钥配置是否一致，Redis 是否正常运行。

## 联系方式

- 项目地址：[待补充]
- 问题反馈：[待补充]
- 邮箱：support@yujianhaowu.com

## 许可证

Apache License 2.0

---

豫见好物 - 发现河南文化之美
