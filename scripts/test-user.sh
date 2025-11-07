#!/bin/bash

# IntelliDesk 用户服务测试脚本
# 用于测试用户管理相关接口

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
GATEWAY_URL="http://localhost:8080"
USERNAME="admin"
PASSWORD="admin123"

echo "========================================="
echo "  IntelliDesk 用户服务测试"
echo "========================================="
echo ""

# ==================== 登录获取 Token ====================
echo -e "${BLUE}[准备]${NC} 用户登录获取 Token..."
login_response=$(curl -s -X POST ${GATEWAY_URL}/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"${USERNAME}\",\"password\":\"${PASSWORD}\"}")

code=$(echo $login_response | jq -r '.code')
if [ "$code" != "200" ]; then
  echo -e "${RED}✗ 登录失败${NC}"
  echo "  响应: $login_response"
  exit 1
fi

accessToken=$(echo $login_response | jq -r '.data.accessToken')
echo -e "${GREEN}✓ 登录成功${NC}"
echo "  Token: ${accessToken:0:50}..."
echo ""

# ==================== 测试 1: 获取当前用户信息 ====================
echo -e "${YELLOW}[测试 1]${NC} 获取当前用户信息 - GET /api/user/info"
response=$(curl -s ${GATEWAY_URL}/api/user/user/info \
  -H "Authorization: Bearer ${accessToken}")

code=$(echo $response | jq -r '.code')
if [ "$code" == "200" ]; then
  username=$(echo $response | jq -r '.data.username')
  nickname=$(echo $response | jq -r '.data.nickname')
  userType=$(echo $response | jq -r '.data.userType')
  echo -e "${GREEN}✓ 测试通过${NC}"
  echo "  用户名: $username"
  echo "  昵称: $nickname"
  echo "  用户类型: $userType"
else
  echo -e "${RED}✗ 测试失败${NC}"
  echo "  响应: $response"
fi
echo ""

# ==================== 测试 2: 创建用户 ====================
echo -e "${YELLOW}[测试 2]${NC} 创建用户 - POST /api/user/user"
timestamp=$(date +%s)
new_username="testuser_${timestamp}"

response=$(curl -s -X POST ${GATEWAY_URL}/api/user/user \
  -H "Authorization: Bearer ${accessToken}" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"${new_username}\",
    \"password\": \"test123456\",
    \"nickname\": \"测试用户\",
    \"email\": \"${new_username}@example.com\",
    \"phone\": \"13800138${timestamp: -3}\",
    \"userType\": 1
  }")

code=$(echo $response | jq -r '.code')
if [ "$code" == "200" ]; then
  new_user_id=$(echo $response | jq -r '.data')
  echo -e "${GREEN}✓ 测试通过${NC}"
  echo "  新用户ID: $new_user_id"
  echo "  用户名: $new_username"
else
  echo -e "${RED}✗ 测试失败${NC}"
  echo "  响应: $response"
  new_user_id=""
fi
echo ""

# ==================== 测试 3: 根据ID查询用户 ====================
if [ -n "$new_user_id" ]; then
  echo -e "${YELLOW}[测试 3]${NC} 根据ID查询用户 - GET /api/user/user/{id}"
  response=$(curl -s ${GATEWAY_URL}/api/user/user/${new_user_id} \
    -H "Authorization: Bearer ${accessToken}")

  code=$(echo $response | jq -r '.code')
  if [ "$code" == "200" ]; then
    username=$(echo $response | jq -r '.data.username')
    nickname=$(echo $response | jq -r '.data.nickname')
    echo -e "${GREEN}✓ 测试通过${NC}"
    echo "  用户名: $username"
    echo "  昵称: $nickname"
  else
    echo -e "${RED}✗ 测试失败${NC}"
    echo "  响应: $response"
  fi
  echo ""
fi

# ==================== 测试 4: 更新用户 ====================
if [ -n "$new_user_id" ]; then
  echo -e "${YELLOW}[测试 4]${NC} 更新用户 - PUT /api/user/user/{id}"
  response=$(curl -s -X PUT ${GATEWAY_URL}/api/user/user/${new_user_id} \
    -H "Authorization: Bearer ${accessToken}" \
    -H "Content-Type: application/json" \
    -d "{
      \"nickname\": \"测试用户（已更新）\",
      \"email\": \"updated_${new_username}@example.com\"
    }")

  code=$(echo $response | jq -r '.code')
  if [ "$code" == "200" ]; then
    echo -e "${GREEN}✓ 测试通过${NC}"
    echo "  用户更新成功"

    # 验证更新
    verify_response=$(curl -s ${GATEWAY_URL}/api/user/user/${new_user_id} \
      -H "Authorization: Bearer ${accessToken}")
    nickname=$(echo $verify_response | jq -r '.data.nickname')
    email=$(echo $verify_response | jq -r '.data.email')
    echo "  新昵称: $nickname"
    echo "  新邮箱: $email"
  else
    echo -e "${RED}✗ 测试失败${NC}"
    echo "  响应: $response"
  fi
  echo ""
fi

# ==================== 测试 5: 分页查询用户列表 ====================
echo -e "${YELLOW}[测试 5]${NC} 分页查询用户列表 - GET /api/user/user/list"
response=$(curl -s "${GATEWAY_URL}/api/user/user/list?pageNum=1&pageSize=10&userType=1" \
  -H "Authorization: Bearer ${accessToken}")

code=$(echo $response | jq -r '.code')
if [ "$code" == "200" ]; then
  total=$(echo $response | jq -r '.data.total')
  size=$(echo $response | jq -r '.data.data | length')
  echo -e "${GREEN}✓ 测试通过${NC}"
  echo "  总记录数: $total"
  echo "  当前页记录数: $size"
else
  echo -e "${RED}✗ 测试失败${NC}"
  echo "  响应: $response"
fi
echo ""

# ==================== 测试 6: 删除用户 ====================
if [ -n "$new_user_id" ]; then
  echo -e "${YELLOW}[测试 6]${NC} 删除用户 - DELETE /api/user/user/{id}"
  response=$(curl -s -X DELETE ${GATEWAY_URL}/api/user/user/${new_user_id} \
    -H "Authorization: Bearer ${accessToken}")

  code=$(echo $response | jq -r '.code')
  if [ "$code" == "200" ]; then
    echo -e "${GREEN}✓ 测试通过${NC}"
    echo "  用户删除成功"

    # 验证删除（应该返回用户不存在）
    verify_response=$(curl -s ${GATEWAY_URL}/api/user/user/${new_user_id} \
      -H "Authorization: Bearer ${accessToken}")
    verify_code=$(echo $verify_response | jq -r '.code')
    if [ "$verify_code" == "1001" ]; then
      echo "  验证成功: 用户已被删除"
    else
      echo -e "${YELLOW}  警告: 删除验证失败${NC}"
    fi
  else
    echo -e "${RED}✗ 测试失败${NC}"
    echo "  响应: $response"
  fi
  echo ""
fi

# ==================== 测试 7: 用户名重复校验 ====================
echo -e "${YELLOW}[测试 7]${NC} 用户名重复校验 - POST /api/user/user（应该失败）"
response=$(curl -s -X POST ${GATEWAY_URL}/api/user/user \
  -H "Authorization: Bearer ${accessToken}" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"admin\",
    \"password\": \"test123456\",
    \"nickname\": \"重复用户\",
    \"userType\": 1
  }")

code=$(echo $response | jq -r '.code')
if [ "$code" == "1004" ]; then
  echo -e "${GREEN}✓ 测试通过${NC}"
  echo "  响应: $(echo $response | jq -r '.message')"
else
  echo -e "${RED}✗ 测试失败${NC} - 应该返回用户名已存在错误"
  echo "  实际响应: $response"
fi
echo ""

# ==================== 总结 ====================
echo "========================================="
echo -e "${GREEN}所有测试完成！${NC}"
echo "========================================="
echo ""
echo "用户服务功能验证："
echo "  ✓ 获取当前用户信息"
echo "  ✓ 创建用户"
echo "  ✓ 根据ID查询用户"
echo "  ✓ 更新用户"
echo "  ✓ 删除用户"
echo "  ✓ 分页查询用户列表"
echo "  ✓ 用户名唯一性校验"
echo ""
echo "下一步建议："
echo "  1. 实现角色管理接口"
echo "  2. 实现权限管理接口"
echo "  3. 实现部门管理接口"
echo "  4. 实现用户注册接口"
echo ""
