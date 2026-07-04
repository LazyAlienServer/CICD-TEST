# Spring Cloud Microservices Project

基于Spring Boot + Spring Cloud Alibaba + Nacos的微服务项目

## 项目结构

```
CICD-TEST/
├── pom.xml                      # 父项目POM
├── cicd/                        # 业务服务模块 (端口: 8082)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/las/cicd/
│       │   │   ├── CicdApplication.java
│       │   │   └── controller/
│       │   │       └── TestController.java
│       │   └── resources/
│       │       └── application.yaml
│       └── test/
├── gateway/                     # 网关服务模块 (端口: 9090)
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/las/gateway/
│           │   └── GatewayApplication.java
│           └── resources/
│               └── application.yaml
└── nacos/                       # Nacos注册中心 (端口: 8848)
    ├── README.md
    ├── start-nacos.sh
    ├── stop-nacos.sh
    └── nacos-server.tar.gz
```

## 技术栈

- **Spring Boot**: 4.1.0
- **Spring Cloud**: 2024.0.1
- **Spring Cloud Alibaba**: 2023.0.3.3
- **Spring Cloud Gateway**: 路由网关
- **Nacos**: 2.4.3 (服务注册与发现)
- **Java**: 21

## 快速开始

### 1. 提取并启动Nacos

```bash
# 进入nacos目录
cd nacos

# 提取Nacos服务器
tar -xzf nacos-server-2.4.3.tar.gz

# 启动Nacos (standalone模式)
./start-nacos.sh
```

访问Nacos控制台:
- URL: http://localhost:8848/nacos
- 用户名: nacos
- 密码: nacos

### 2. 启动CICD服务

```bash
cd cicd
mvn spring-boot:run
```

服务启动后会自动注册到Nacos，可在Nacos控制台查看。

直接访问测试接口:
```bash
curl http://localhost:8082/api/test
```

### 3. 启动Gateway网关

```bash
cd gateway
mvn spring-boot:run
```

网关启动后会自动注册到Nacos。

通过网关访问测试接口:
```bash
curl http://localhost:9090/api/test
```

## 服务说明

### CICD服务 (端口: 8082)

业务服务，提供REST API接口。

**测试接口:**
- GET `/api/test` - 返回测试字符串

### Gateway网关 (端口: 9090)

API网关，负责路由转发和负载均衡。

**路由配置:**
- `/api/**` → `lb://cicd` (负载均衡到cicd服务)

**特性:**
- 支持服务发现自动路由
- 支持负载均衡 (lb://)
- 可通过Nacos动态感知服务上下线

### Nacos注册中心 (端口: 8848)

服务注册与发现中心。

**功能:**
- 服务注册与发现
- 服务健康检查
- 动态配置管理
- Web控制台管理

## 架构说明

```
[客户端请求]
    ↓
[Gateway网关 :9090] ←→ [Nacos :8848]
    ↓ (lb://cicd)         ↑
[CICD服务 :8082] ────────┘
```

1. 客户端请求发送到Gateway网关 (端口9090)
2. Gateway通过Nacos服务发现获取cicd服务实例列表
3. Gateway使用负载均衡 (lb://) 将请求转发到cicd服务
4. 所有服务启动时自动注册到Nacos

## 配置说明

### CICD服务配置 (cicd/src/main/resources/application.yaml)

```yaml
spring:
  application:
    name: cicd                    # 服务名
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848   # Nacos地址
        namespace: public              # 命名空间

server:
  port: 8082                      # 服务端口
```

### Gateway配置 (gateway/src/main/resources/application.yaml)

```yaml
spring:
  application:
    name: gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: public
    gateway:
      routes:
        - id: cicd-route
          uri: lb://cicd          # 使用负载均衡协议
          predicates:
            - Path=/api/**        # 匹配/api/**的请求
      discovery:
        locator:
          enabled: true           # 启用服务发现
          lower-case-service-id: true

server:
  port: 9090
```

## 测试验证

### 1. 检查服务注册

访问Nacos控制台: http://localhost:8848/nacos

在"服务管理 > 服务列表"中应该能看到:
- cicd (1个实例, 端口8082)
- gateway (1个实例, 端口9090)

### 2. 测试直接访问

```bash
curl http://localhost:8082/api/test
```

预期响应: `"Hello! This is a test endpoint."`

### 3. 测试网关路由

```bash
curl http://localhost:9090/api/test
```

预期响应: `"Hello! This is a test endpoint."`

此请求通过Gateway使用`lb://cicd`路由到cicd服务。

## 停止服务

### 停止Nacos
```bash
cd nacos
./stop-nacos.sh
```

### 停止Spring Boot服务
在对应的终端窗口按 `Ctrl+C`

## 故障排查

### 服务未注册到Nacos
1. 检查Nacos是否正常启动: http://localhost:8848/nacos
2. 检查服务配置中Nacos地址是否正确
3. 查看服务启动日志是否有错误

### 网关无法路由
1. 检查Gateway是否成功启动
2. 检查cicd服务是否在Nacos中注册
3. 检查Gateway日志中是否有路由错误

### 端口冲突
如果端口已被占用，可以修改application.yaml中的端口配置:
- CICD服务: `server.port`
- Gateway: `server.port`
- Nacos: 修改 `nacos/conf/application.properties` 中的 `server.port`

## 扩展

### 添加新的微服务

1. 在父POM中添加新模块
2. 创建新的Spring Boot项目
3. 添加Nacos Discovery依赖
4. 配置application.yaml注册到Nacos
5. 在Gateway中添加对应的路由规则 (可选，启用服务发现自动路由)

### 多实例部署

可以启动同一服务的多个实例，Gateway会自动进行负载均衡:

```bash
# 启动cicd服务的第二个实例
cd cicd
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8083
```

在Nacos控制台中会看到cicd服务有2个实例。

## 参考文档

- [Spring Cloud Alibaba](https://spring-cloud-alibaba-group.github.io/github-pages/2023/zh-cn/index.html)
- [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Nacos](https://nacos.io/zh-cn/)
