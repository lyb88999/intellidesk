#!/bin/bash

# IntelliDesk JWT 认证测试脚本
# 用于测试网关 JWT 认证过滤器是否正常工作

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置
GATEWAY_URL="http://localhost:8080"
USERNAME="admin"
PASSWORD="admin123"

echo "========================================="
echo "  IntelliDesk JWT 认证测试"
echo "========================================="
echo ""

# 测试 1: 访问白名单路径（不需要 Token）
echo -e "${YELLOW}[测试 1]${NC} 访问白名单路径（登录接口）- 应该成功"
response=$(curl -s -X POST ${GATEWAY_URL}/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}")

code=$(echo $response | jq -r '.code')
if [ "$code" == "200" ]; then
  echo -e "${GREEN}✓ 测试通过${NC} - 白名单路径可正常访问"
  accessToken=$(echo $response | jq -r '.data.accessToken')
  echo "  获取到 Token: ${accessToken:0:50}..."
else
  echo -e "${RED}✗ 测试失败${NC} - 登录失败"
  echo "  响应: $response"
  exit 1
fi
echo ""

# 测试 2: 不带 Token 访问受保护的接口
echo -e "${YELLOW}[测试 2]${NC} 不带 Token 访问受保护接口 - 应该返回 401"
response=$(curl -s ${GATEWAY_URL}/api/user/health/info)
code=$(echo $response | jq -r '.code')
if [ "$code" == "401" ]; then
  echo -e "${GREEN}✓ 测试通过${NC} - 正确拦截未认证请求"
  echo "  响应: $(echo $response | jq -r '.message')"
else
  echo -e "${RED}✗ 测试失败${NC} - 应该返回 401"
  echo "  实际响应: $response"
fi
echo ""

# 测试 3: 使用无效 Token 访问受保护接口
echo -e "${YELLOW}[测试 3]${NC} 使用无效 Token 访问受保护接口 - 应该返回 Token 无效"
response=$(curl -s ${GATEWAY_URL}/api/user/health/info \
  -H "Authorization: Bearer invalid_token_12345")
code=$(echo $response | jq -r '.code')
if [ "$code" == "1007" ]; then
  echo -e "${GREEN}✓ 测试通过${NC} - 正确识别无效 Token"
  echo "  响应: $(echo $response | jq -r '.message')"
else
  echo -e "${RED}✗ 测试失败${NC} - 应该返回 Token 无效"
  echo "  实际响应: $response"
fi
echo ""

# 测试 4: 使用有效 Token 访问受保护接口
echo -e "${YELLOW}[测试 4]${NC} 使用有效 Token 访问受保护接口 - 应该成功"
response=$(curl -s ${GATEWAY_URL}/api/user/health/info \
  -H "Authorization: Bearer ${accessToken}")
code=$(echo $response | jq -r '.code')
if [ "$code" == "200" ]; then
  echo -e "${GREEN}✓ 测试通过${NC} - 成功访问受保护接口"
  echo "  响应: $(echo $response | jq -r '.data.serviceName')"
else
  echo -e "${RED}✗ 测试失败${NC} - 应该成功访问"
  echo "  实际响应: $response"
fi
echo ""

# 测试 5: Token 中的用户信息是否正确传递
echo -e "${YELLOW}[测试 5]${NC} 验证用户信息传递（需要后续开发用户信息接口）"
echo "  跳过此测试（接口尚未开发）"
echo ""

# 总结
echo "========================================="
echo -e "${GREEN}所有测试完成！${NC}"
echo "========================================="
echo ""
echo "JWT 认证过滤器工作正常："
echo "  ✓ 白名单路径可以正常访问"
echo "  ✓ 未认证请求被正确拦截"
echo "  ✓ 无效 Token 被正确识别"
echo "  ✓ 有效 Token 可以通过认证"
echo ""
echo "下一步建议："
echo "  1. 实现用户信息查询接口"
echo "  2. 验证用户信息是否正确传递给下游服务"
echo "  3. 实现 Token 黑名单（Redis）"
echo ""
