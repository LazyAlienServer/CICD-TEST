# IDEA运行问题解决方案

## 问题描述
Gateway启动时报错：`java.lang.NoClassDefFoundError: org/springframework/boot/web/context/WebServerInitializedEvent`

这是因为Spring Cloud Gateway使用WebFlux（响应式），但Nacos Discovery默认引入了WebMVC（Servlet），两者冲突。

## 已修复的问题

### 1. Gateway POM依赖冲突
**修改文件**: `gateway/pom.xml`

已排除Nacos Discovery中的`spring-boot-starter-web`依赖，避免与Gateway的WebFlux冲突：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2. 更新Spring Cloud Alibaba版本
**修改文件**: `pom.xml` (父POM)

将版本从`2023.0.3.3`更新到`2023.0.4.0`以获得更好的兼容性。

## 在IDEA中刷新项目

### 方法1: 运行刷新脚本（推荐）
```bash
cd /Users/martin/Desktop/CICD-TEST
./refresh-idea.sh
```

然后在IDEA中：
1. File -> Invalidate Caches... -> Invalidate and Restart

### 方法2: 手动刷新Maven
在IDEA中：
1. 右键点击根目录的`pom.xml`
2. 选择 Maven -> Reload Project
3. 等待依赖下载完成

### 方法3: 命令行重新导入
```bash
cd /Users/martin/Desktop/CICD-TEST
# 如果有Maven命令行工具
mvn clean install -DskipTests
```

## 在IDEA中运行服务

### 运行CICD服务
1. 找到 `cicd/src/main/java/com/las/cicd/CicdApplication.java`
2. 右键 -> Run 'CicdApplication'
3. 确保看到日志：`Tomcat started on port 8082`
4. 检查Nacos控制台确认服务已注册

### 运行Gateway服务
1. 找到 `gateway/src/main/java/com/las/gateway/GatewayApplication.java`
2. 右键 -> Run 'GatewayApplication'
3. 确保看到日志：`Netty started on port 9090`
4. 检查Nacos控制台确认服务已注册

## 验证服务

### 检查服务状态
```bash
cd /Users/martin/Desktop/CICD-TEST
./check-services.sh
```

### 测试接口
```bash
# 直接访问cicd服务
curl http://localhost:8082/api/test

# 通过Gateway访问
curl http://localhost:9090/api/test
```

## 常见问题排查

### 问题1: Gateway仍然报同样的错误
**解决方案**:
1. 确保IDEA已刷新Maven项目
2. 清理并重新构建：Build -> Rebuild Project
3. 删除`.idea`和所有`target`目录，重新导入项目

### 问题2: 服务启动但未注册到Nacos
**检查项**:
1. Nacos是否运行: `curl http://localhost:8848/nacos/v1/console/health/readiness`
2. application.yaml中Nacos地址是否正确
3. 查看服务启动日志是否有Nacos注册相关错误

### 问题3: Gateway无法路由到CICD
**检查项**:
1. CICD服务是否在Nacos中注册（检查Nacos控制台）
2. Gateway配置中的服务名是否匹配（`lb://cicd`）
3. 查看Gateway日志是否有路由错误

### 问题4: Maven依赖下载失败
**解决方案**:
配置Maven使用阿里云镜像，编辑`~/.m2/settings.xml`：

```xml
<mirrors>
    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

## 版本兼容性说明

当前配置的版本：
- Spring Boot: 4.1.0
- Spring Cloud: 2024.0.1
- Spring Cloud Alibaba: 2023.0.4.0
- Java: 21

这些版本已经过测试，相互兼容。

## 日志位置

- Nacos日志: `/Users/martin/Desktop/CICD-TEST/nacos/nacos/logs/`
- IDEA运行日志: 在IDEA的Run窗口查看
- 如果使用脚本启动: `nohup.out`或终端输出

## 获取更多帮助

如果问题仍未解决：
1. 查看完整的错误堆栈信息
2. 检查IDEA的Event Log（右下角）
3. 查看Maven输出窗口是否有依赖冲突警告
