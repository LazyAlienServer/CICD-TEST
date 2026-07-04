#!/bin/bash
# 刷新IDEA项目配置

echo "=========================================="
echo "刷新Maven项目"
echo "=========================================="
echo ""

cd /Users/martin/Desktop/CICD-TEST

echo "1. 清理所有模块..."
find . -name "target" -type d -exec rm -rf {} + 2>/dev/null
echo "   ✓ 清理完成"
echo ""

echo "2. 删除IDEA缓存..."
rm -rf .idea/libraries
rm -rf .idea/modules
rm -f .idea/*.iml
rm -f */*.iml
echo "   ✓ IDEA缓存已清除"
echo ""

echo "=========================================="
echo "完成！"
echo "=========================================="
echo ""
echo "请在IDEA中执行以下操作："
echo "1. File -> Invalidate Caches... -> Invalidate and Restart"
echo "2. 或者 右键点击根pom.xml -> Maven -> Reload Project"
echo ""
echo "然后尝试运行Gateway应用"
echo ""
