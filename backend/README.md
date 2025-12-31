# 虚拟仿真实训教学管理平台 - 后端项目

## 项目结构

```
backend/
├── src/main/java/com/training/
│   ├── VirtualTrainingApplication.java          # 启动类
│   ├── common/                                   # 公共模块
│   │   ├── base/                                # 基础类
│   │   │   ├── BaseEntity.java                 # 实体基类
│   │   │   ├── Result.java                     # 统一响应结果
│   │   │   └── PageResult.java                 # 分页响应结果
│   │   ├── config/                              # 配置类
│   │   │   ├── MybatisPlusConfig.java          # MyBatis Plus配置
│   │   │   ├── CorsConfig.java                 # 跨域配置
│   │   │   ├── SecurityConfig.java             # Spring Security配置(待实现)
│   │   │   └── RedisConfig.java                # Redis配置(待实现)
│   │   ├── constant/                            # 常量定义
│   │   │   └── CommonConstant.java             # 通用常量
│   │   ├── enums/                               # 枚举类
│   │   │   └── RoleEnum.java                   # 角色枚举
│   │   ├── exception/                           # 异常处理
│   │   │   ├── BusinessException.java          # 业务异常
│   │   │   └── GlobalExceptionHandler.java     # 全局异常处理器
│   │   ├── utils/                               # 工具类
│   │   │   ├── SecurityUtils.java              # Security工具类
│   │   │   ├── JwtUtils.java                   # JWT工具类
│   │   │   └── RedisUtils.java                 # Redis工具类(待实现)
│   │   ├── aspect/                              # AOP切面(待实现)
│   │   └── annotation/                          # 自定义注解(待实现)
│   ├── module/                                  # 业务模块
│   │   ├── auth/                               # 认证授权模块(待实现)
│   │   ├── user/                               # 用户管理模块(待实现)
│   │   ├── news/                               # 新闻公告模块(待实现)
│   │   ├── resource/                           # 资源中心模块(待实现)
│   │   ├── training/                           # 实训中心模块(待实现)
│   │   ├── lab/                                # 实验室模块(待实现)
│   │   └── share/                              # 共享开放模块(待实现)
│   └── resources/
│       ├── mapper/                              # MyBatis XML映射文件
│       ├── application.yml                      # 主配置文件
│       ├── application-dev.yml                  # 开发环境配置
│       └── application-prod.yml                 # 生产环境配置
├── pom.xml                                      # Maven依赖配置
└── README.md                                    # 项目说明

```

## 技术栈

- **Spring Boot**: 3.2.1
- **MyBatis Plus**: 3.5.5
- **Spring Security**: JWT认证
- **MySQL**: 8.0+
- **Redis**: 缓存和会话管理
- **Druid**: 数据库连接池
- **Knife4j**: API文档
- **Lombok**: 简化代码
- **Hutool**: 工具类库

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 2. 数据库初始化

执行 `database/schema.sql` 文件创建数据库和表结构。

```bash
mysql -u root -p < database/schema.sql
```

### 3. 修改配置

编辑 `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    druid:
      username: your_username
      password: your_password
  data:
    redis:
      password: your_redis_password
```

### 4. 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者在IDE中直接运行 VirtualTrainingApplication.java
```

### 5. 访问接口文档

启动成功后访问: http://localhost:8080/api/doc.html

## 开发规范

### 1. 包命名规范

- `controller`: 控制器层,处理HTTP请求
- `service`: 服务层,业务逻辑
- `mapper`: 持久层,数据库操作
- `entity`: 实体类,对应数据库表
- `dto`: 数据传输对象,接收前端请求
- `vo`: 视图对象,返回给前端

### 2. 接口命名规范

遵循RESTful API规范:

- `GET /api/v1/users`: 查询用户列表
- `GET /api/v1/users/{id}`: 查询用户详情
- `POST /api/v1/users`: 新增用户
- `PUT /api/v1/users/{id}`: 更新用户
- `DELETE /api/v1/users/{id}`: 删除用户

### 3. 响应格式

所有接口返回统一格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1704038400000
}
```

### 4. 分页参数

- `current`: 当前页码(从1开始)
- `size`: 每页大小(默认10)

### 5. 异常处理

- 业务异常: 抛出 `BusinessException`
- 参数校验: 使用 `@Valid` 注解
- 全局异常: 由 `GlobalExceptionHandler` 统一处理

## 核心功能实现状态

### 已完成

- [x] 项目基础架构
- [x] 数据库设计
- [x] 公共基础类
- [x] MyBatis Plus配置
- [x] 统一响应格式
- [x] 全局异常处理
- [x] 跨域配置
- [x] JWT工具类

### 待实现

- [ ] Spring Security配置
- [ ] Redis工具类
- [ ] 用户认证模块(登录/登出/注册)
- [ ] 用户管理模块
- [ ] 新闻公告模块
- [ ] 资源中心模块
- [ ] 实训中心模块
- [ ] 实验室模块
- [ ] 共享开放模块
- [ ] 日志记录(AOP)
- [ ] 权限控制(注解)
- [ ] 文件上传下载
- [ ] 导入导出功能

## 设计模式应用

### SOLID原则

- **单一职责(SRP)**: 每个类只负责一个功能
- **开闭原则(OCP)**: 对扩展开放,对修改封闭
- **里氏替换(LSP)**: 子类可以替换父类
- **接口隔离(ISP)**: 细粒度接口设计
- **依赖倒置(DIP)**: 面向接口编程

### DRY原则

- BaseEntity统一公共字段
- Result统一响应格式
- GlobalExceptionHandler统一异常处理
- MetaObjectHandler自动填充公共字段

### KISS原则

- 简洁的代码结构
- 清晰的命名规范
- 最小化复杂度

## 注意事项

1. **安全配置**: 生产环境必须修改JWT密钥
2. **数据库密码**: 使用环境变量管理敏感信息
3. **日志级别**: 生产环境关闭DEBUG日志
4. **连接池**: 根据实际并发量调整Druid配置
5. **Redis**: 生产环境建议配置密码

## 常见问题

### 1. 数据库连接失败

检查MySQL是否启动,用户名密码是否正确。

### 2. Redis连接失败

检查Redis是否启动,端口是否正确。

### 3. JWT验证失败

检查Token是否过期,密钥是否一致。

## 联系方式

- 项目负责人: Training Team
- 邮箱: dev@example.com
