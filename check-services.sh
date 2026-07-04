#!/bin/bash
# 验证所有服务是否正常运行

echo "=========================================="
echo "服务状态检查"
echo "=========================================="
echo ""

# 检查Nacos
echo "1. 检查Nacos服务..."
if curl -s http://localhost:8848/nacos/v1/console/health/readiness | grep -q "OK"; then
    echo "   ✓ Nacos运行正常 (http://localhost:8848/nacos)"
else
    echo "   ✗ Nacos未运行"
    echo "   启动命令: cd nacos && ./start-nacos.sh"
fi
echo ""

# 检查CICD服务
echo "2. 检查CICD服务..."
if curl -s http://localhost:8082/api/test > /dev/null 2>&1; then
    echo "   ✓ CICD服务运行正常 (端口: 8082)"
    echo "   响应: $(curl -s http://localhost:8082/api/test)"
else
    echo "   ✗ CICD服务未运行"
    echo "   启动命令: cd cicd && mvn spring-boot:run"
fi
echo ""

# 检查Gateway
echo "3. 检查Gateway服务..."
if curl -s http://localhost:9090/actuator/health > /dev/null 2>&1 || curl -s http://localhost:9090/api/test > /dev/null 2>&1; then
    echo "   ✓ Gateway运行正常 (端口: 9090)"
    if curl -s http://localhost:9090/api/test > /dev/null 2>&1; then
        echo "   响应: $(curl -s http://localhost:9090/api/test)"
    fi
else
    echo "   ✗ Gateway未运行"
    echo "   启动命令: cd gateway && mvn spring-boot:run"
fi
echo ""

# 检查Nacos注册的服务
echo "4. 检查Nacos服务注册..."
if curl -s http://localhost:8848/nacos/v1/console/health/readiness | grep -q "OK"; then
    SERVICES=$(curl -s "http://localhost:8848/nacos/v1/ns/catalog/services?hasIpCount=true&withInstances=false&pageNo=1&pageSize=10&serviceNameParam=&groupNameParam=&namespaceId=" 2>/dev/null | grep -o '"name":"[^"]*"' | cut -d'"' -f4)

    if [ ! -z "$SERVICES" ]; then
        echo "   已注册的服务:"
        echo "$SERVICES" | while read service; do
            echo "   - $service"
        done
    else
        echo "   ℹ 暂无服务注册"
    fi
fi
echo ""

echo "=========================================="
echo "快速访问链接"
echo "=========================================="
echo "Nacos控制台: http://localhost:8848/nacos (nacos/nacos)"
echo "CICD服务: http://localhost:8082/api/test"
echo "Gateway网关: http://localhost:9090/api/test"
echo ""
