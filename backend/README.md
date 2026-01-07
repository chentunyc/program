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
