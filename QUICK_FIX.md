# 快速修复指南

## ✅ 已修复的问题
Gateway启动错误：WebFlux和WebMVC依赖冲突

## 🔧 修复内容
1. ✅ Gateway POM - 排除了WebMVC依赖
2. ✅ 更新Spring Cloud Alibaba到2023.0.4.0版本
3. ✅ 添加Bootstrap支持
4. ✅ 清理了IDEA缓存和target目录

## 📋 现在请执行

### 步骤1: 在IDEA中刷新Maven
**右键点击根目录的 `pom.xml`**
→ Maven → Reload Project

或者

**菜单栏**: File → Invalidate Caches... → Invalidate and Restart

### 步骤2: 等待依赖下载完成
查看IDEA右下角的进度条，等待Maven依赖全部下载完成

### 步骤3: 运行服务

#### 先运行CICD服务
文件: `cicd/src/main/java/com/las/cicd/CicdApplication.java`
右键 → Run 'CicdApplication'

#### 再运行Gateway服务
文件: `gateway/src/main/java/com/las/gateway/GatewayApplication.java`
右键 → Run 'GatewayApplication'

### 步骤4: 验证
```bash
# 检查所有服务状态
./check-services.sh

# 测试直接访问
curl http://localhost:8082/api/test

# 测试网关路由
curl http://localhost:9090/api/test
```

## 🔍 如果还有问题

查看详细的故障排除文档：
```bash
cat IDEA_TROUBLESHOOTING.md
```

## 📞 服务信息
- Nacos: http://localhost:8848/nacos (nacos/nacos)
- CICD: http://localhost:8082/api/test
- Gateway: http://localhost:9090/api/test
