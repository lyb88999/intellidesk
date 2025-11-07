<template>
  <div class="chat-container">
    <el-container>
      <!-- 左侧会话列表 -->
      <el-aside width="300px" class="conversation-list">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">会话列表</span>
              <el-button type="primary" icon="Plus" size="small" @click="handleCreateConversation">
                新建会话
              </el-button>
            </div>
          </template>

          <div class="filter-bar">
            <el-select v-model="statusFilter" placeholder="筛选状态" size="small" @change="loadConversations">
              <el-option label="全部" :value="undefined" />
              <el-option label="等待中" :value="1" />
              <el-option label="进行中" :value="2" />
              <el-option label="已结束" :value="3" />
            </el-select>
          </div>

          <el-scrollbar height="calc(100vh - 280px)">
            <div
              v-for="conv in conversations"
              :key="conv.id"
              class="conversation-item"
              :class="{ active: currentConversation?.id === conv.id }"
              @click="selectConversation(conv)"
            >
              <div class="conv-header">
                <span class="conv-name">{{ conv.customerName }}</span>
                <el-tag :type="getStatusType(conv.status)" size="small">
                  {{ conv.statusText }}
                </el-tag>
              </div>
              <div class="conv-info">
                <span class="conv-no">{{ conv.conversationNo }}</span>
                <span class="conv-time">{{ formatTime(conv.createTime) }}</span>
              </div>
              <div class="conv-meta">
                <span>消息数: {{ conv.messageCount }}</span>
                <span v-if="conv.agentName">客服: {{ conv.agentName }}</span>
              </div>
            </div>
          </el-scrollbar>
        </el-card>
      </el-aside>

      <!-- 右侧聊天区域 -->
      <el-main class="chat-main">
        <el-card v-if="currentConversation" class="chat-card">
          <template #header>
            <div class="chat-header">
              <div>
                <span class="chat-title">{{ currentConversation.customerName }}</span>
                <el-tag :type="getStatusType(currentConversation.status)" size="small" style="margin-left: 10px">
                  {{ currentConversation.statusText }}
                </el-tag>
                <!-- AI助手状态 -->
                <el-tag type="success" size="small" style="margin-left: 10px">
                  <el-icon><ChatDotRound /></el-icon> AI助手在线
                </el-tag>
              </div>
              <div>
                <el-tag v-if="wsConnected" type="success" size="small">
                  <el-icon><Connection /></el-icon> 已连接
                </el-tag>
                <el-tag v-else type="danger" size="small">
                  <el-icon><Connection /></el-icon> 未连接
                </el-tag>
                <el-button
                  v-if="currentConversation.status === 1"
                  type="primary"
                  size="small"
                  style="margin-left: 10px"
                  @click="handleAssignAgent"
                >
                  分配客服
                </el-button>
                <el-button
                  v-if="currentConversation.status === 2"
                  type="warning"
                  size="small"
                  style="margin-left: 10px"
                  @click="handleEndConversation"
                >
                  结束会话
                </el-button>
              </div>
            </div>
          </template>

          <!-- 消息列表 -->
          <el-scrollbar ref="scrollbarRef" height="calc(100vh - 400px)" class="message-list">
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="message-item"
              :class="{
                'message-right': msg.senderType === 2,
                'message-ai': msg.senderType === 4,
                'message-system': msg.senderType === 3
              }"
            >
              <!-- 消息头部（发送者信息） -->
              <div class="message-header">
                <!-- AI消息头像 -->
                <div v-if="msg.senderType === 4" class="message-avatar ai-avatar">
                  <el-icon :size="24"><Robot /></el-icon>
                </div>
                <!-- 人工客服头像 -->
                <div v-else-if="msg.senderType === 2" class="message-avatar agent-avatar">
                  <el-icon :size="24"><Service /></el-icon>
                </div>
                <!-- 客户头像 -->
                <div v-else-if="msg.senderType === 1" class="message-avatar customer-avatar">
                  <el-icon :size="24"><User /></el-icon>
                </div>

                <div class="message-info">
                  <div class="message-sender">
                    <span class="sender-name">{{ msg.senderName }}</span>
                    <!-- AI标识 -->
                    <el-tag v-if="msg.senderType === 4" type="success" size="small" effect="dark">
                      <el-icon><Cpu /></el-icon> AI助手
                    </el-tag>
                    <!-- 人工标识 -->
                    <el-tag v-else-if="msg.senderType === 2" type="primary" size="small">
                      <el-icon><User /></el-icon> 人工客服
                    </el-tag>
                    <span class="message-time">{{ formatTime(msg.createTime) }}</span>
                  </div>
                </div>
              </div>

              <!-- 消息内容 -->
              <div class="message-content">
                <div v-if="msg.messageType === 1" class="message-text">
                  {{ msg.content }}
                </div>
                <div v-else-if="msg.messageType === 2" class="message-image">
                  <el-image :src="msg.attachmentUrl" :preview-src-list="[msg.attachmentUrl || '']" />
                </div>
                <div v-else class="message-file">
                  <el-link :href="msg.attachmentUrl" target="_blank">{{ msg.content }}</el-link>
                </div>
              </div>

              <!-- AI消息快捷操作 -->
              <div v-if="msg.senderType === 4" class="message-actions">
                <el-button type="text" size="small" @click="handleRequestHuman">
                  <el-icon><Connection /></el-icon> 转人工客服
                </el-button>
              </div>
            </div>
          </el-scrollbar>

          <!-- 发送消息区域 -->
          <div class="message-input">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="输入消息...（客户消息将由AI助手自动回复）"
              @keydown.enter.exact.prevent="handleSendMessage"
            />
            <div class="input-actions">
              <div class="input-left">
                <span class="input-tip">按 Enter 发送，Shift + Enter 换行</span>
                <el-tag type="info" size="small" style="margin-left: 10px">
                  <el-icon><InfoFilled /></el-icon> 客户消息由AI自动回复
                </el-tag>
              </div>
              <el-button type="primary" @click="handleSendMessage">发送</el-button>
            </div>
          </div>
        </el-card>

        <el-empty v-else description="请选择一个会话" />
      </el-main>
    </el-container>

    <!-- 创建会话对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建会话"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item label="客户ID">
          <el-input-number v-model="createForm.customerId" :min="1" placeholder="请输入客户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户姓名">
          <el-input v-model="createForm.customerName" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="createForm.source" placeholder="请选择来源">
            <el-option label="Web" :value="1" />
            <el-option label="Mobile" :value="2" />
            <el-option label="WeChat" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配客服对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配客服"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item label="客服ID">
          <el-input-number v-model="assignForm.agentId" :min="1" placeholder="请输入客服ID" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, ElScrollbar } from 'element-plus'
import {
  Connection,
  ChatDotRound,
  Robot,
  Service,
  User,
  Cpu,
  InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import {
  createConversation,
  getAgentConversations,
  getConversationMessages,
  assignAgent,
  endConversation
} from '@/api/chat'
import type {
  Conversation,
  Message,
  WebSocketMessage,
  CreateConversationRequest
} from '@/types/chat'
import { WebSocketClient } from '@/utils/websocket'

const userStore = useUserStore()

// 会话列表
const conversations = ref<Conversation[]>([])
const currentConversation = ref<Conversation | null>(null)
const statusFilter = ref<number | undefined>(undefined)

// 消息列表
const messages = ref<Message[]>([])
const scrollbarRef = ref<InstanceType<typeof ElScrollbar>>()

// WebSocket
const wsClient = ref<WebSocketClient | null>(null)
const wsConnected = ref(false)

// 输入消息
const inputMessage = ref('')

// 创建会话对话框
const createDialogVisible = ref(false)
const createForm = reactive<CreateConversationRequest>({
  customerId: 1,
  customerName: '',
  source: 1
})

// 分配客服对话框
const assignDialogVisible = ref(false)
const assignForm = reactive({
  conversationId: 0,
  agentId: 1
})

// 状态类型
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success'
  }
  return types[status] || ''
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

  return time.substring(5, 16)
}

// 初始化WebSocket
const initWebSocket = () => {
  const userId = userStore.userInfo?.id
  if (!userId) {
    console.error('用户ID不存在，无法连接WebSocket')
    return
  }

  // WebSocket URL (根据环境配置)
  const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8082/ws/chat'

  wsClient.value = new WebSocketClient(wsUrl, userId)

  wsClient.value.onConnected(() => {
    wsConnected.value = true
    ElMessage.success('WebSocket连接成功，AI助手已就绪')
  })

  wsClient.value.onDisconnected(() => {
    wsConnected.value = false
  })

  wsClient.value.onMessage((message: WebSocketMessage) => {
    handleWebSocketMessage(message)
  })

  wsClient.value.connect()
}

// 处理WebSocket消息
const handleWebSocketMessage = (wsMessage: WebSocketMessage) => {
  if (wsMessage.type === 10) {
    // 连接成功
    console.log('连接成功:', wsMessage.content)
  } else if (wsMessage.type === 12) {
    // 系统消息
    ElMessage.info(wsMessage.content || '系统通知')
  } else if (wsMessage.type >= 1 && wsMessage.type <= 5) {
    // 聊天消息
    if (currentConversation.value && wsMessage.conversationId === currentConversation.value.id) {
      loadMessages(currentConversation.value.id)
    }
    // 刷新会话列表
    loadConversations()
  }
}

// 加载会话列表
const loadConversations = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) return

    const result = await getAgentConversations(userId, statusFilter.value, 1, 100)
    conversations.value = result.data
  } catch (error) {
    console.error('加载会话列表失败:', error)
  }
}

// 选择会话
const selectConversation = async (conv: Conversation) => {
  currentConversation.value = conv
  await loadMessages(conv.id)
}

// 加载消息
const loadMessages = async (conversationId: number) => {
  try {
    const data = await getConversationMessages(conversationId, 1, 100)
    messages.value = data
    // 滚动到底部
    await nextTick()
    scrollbarRef.value?.setScrollTop(99999)
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

// 创建会话
const handleCreateConversation = () => {
  createForm.customerId = 1
  createForm.customerName = ''
  createForm.source = 1
  createDialogVisible.value = true
}

// 提交创建会话
const handleCreateSubmit = async () => {
  if (!createForm.customerName.trim()) {
    ElMessage.warning('请输入客户姓名')
    return
  }

  try {
    await createConversation(createForm)
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    await loadConversations()
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

// 分配客服
const handleAssignAgent = () => {
  if (!currentConversation.value) return
  assignForm.conversationId = currentConversation.value.id
  assignForm.agentId = 1
  assignDialogVisible.value = true
}

// 提交分配客服
const handleAssignSubmit = async () => {
  try {
    await assignAgent(assignForm)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    await loadConversations()
    if (currentConversation.value) {
      await selectConversation(currentConversation.value)
    }
  } catch (error) {
    console.error('分配客服失败:', error)
  }
}

// 结束会话
const handleEndConversation = async () => {
  if (!currentConversation.value) return

  try {
    await ElMessageBox.confirm('确定要结束该会话吗？', '提示', {
      type: 'warning'
    })
    await endConversation(currentConversation.value.id)
    ElMessage.success('会话已结束')
    await loadConversations()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('结束会话失败:', error)
    }
  }
}

// 请求转人工
const handleRequestHuman = () => {
  ElMessageBox.confirm('是否需要转接人工客服？', '转接提示', {
    confirmButtonText: '是的，转人工',
    cancelButtonText: '继续AI助手',
    type: 'info'
  }).then(() => {
    handleAssignAgent()
  }).catch(() => {
    // 取消
  })
}

// 发送消息
const handleSendMessage = () => {
  if (!inputMessage.value.trim()) {
    return
  }

  if (!currentConversation.value) {
    ElMessage.warning('请先选择一个会话')
    return
  }

  if (!wsClient.value || !wsClient.value.isConnected()) {
    ElMessage.error('WebSocket未连接，无法发送消息')
    return
  }

  const userInfo = userStore.userInfo
  if (!userInfo) {
    ElMessage.error('用户信息不存在')
    return
  }

  const message: WebSocketMessage = {
    type: 1, // 文本消息
    conversationId: currentConversation.value.id,
    senderId: userInfo.id,
    senderName: userInfo.nickname,
    senderType: 2, // 客服
    receiverId: currentConversation.value.customerId,
    receiverName: currentConversation.value.customerName,
    content: inputMessage.value,
    timestamp: Date.now()
  }

  wsClient.value.send(message)
  inputMessage.value = ''

  // 延迟刷新消息列表
  setTimeout(() => {
    if (currentConversation.value) {
      loadMessages(currentConversation.value.id)
    }
  }, 500)
}

// 组件挂载
onMounted(() => {
  loadConversations()
  initWebSocket()
})

// 组件卸载
onUnmounted(() => {
  if (wsClient.value) {
    wsClient.value.disconnect()
  }
})
</script>

<style scoped lang="scss">
.chat-container {
  height: calc(100vh - 60px);
  padding: 20px;

  .el-container {
    height: 100%;
  }

  .conversation-list {
    margin-right: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        font-size: 16px;
        font-weight: 600;
      }
    }

    .filter-bar {
      margin-bottom: 15px;
    }

    .conversation-item {
      padding: 12px;
      border-bottom: 1px solid #eee;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      &.active {
        background-color: #ecf5ff;
        border-left: 3px solid #409eff;
      }

      .conv-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .conv-name {
          font-weight: 600;
          font-size: 14px;
        }
      }

      .conv-info {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #909399;
        margin-bottom: 5px;
      }

      .conv-meta {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .chat-main {
    padding: 0;

    .chat-card {
      height: 100%;

      .chat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .chat-title {
          font-size: 16px;
          font-weight: 600;
        }
      }

      .message-list {
        padding: 20px;
        background-color: #f5f7fa;

        .message-item {
          margin-bottom: 25px;
          display: flex;
          flex-direction: column;

          // 客服消息右对齐
          &.message-right {
            align-items: flex-end;

            .message-header {
              flex-direction: row-reverse;
            }

            .message-content .message-text {
              background-color: #409eff;
              color: white;
            }
          }

          // AI消息样式
          &.message-ai {
            .message-header {
              .ai-avatar {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
              }
            }

            .message-content .message-text {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
              border: 2px solid #667eea;
              box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
            }

            .message-actions {
              display: flex;
              justify-content: flex-start;
              margin-top: 8px;
              margin-left: 40px;
            }
          }

          // 系统消息
          &.message-system {
            align-items: center;

            .message-content .message-text {
              background-color: #f4f4f5;
              color: #909399;
              font-size: 13px;
              text-align: center;
            }
          }

          .message-header {
            display: flex;
            align-items: center;
            margin-bottom: 8px;

            .message-avatar {
              width: 36px;
              height: 36px;
              border-radius: 50%;
              display: flex;
              align-items: center;
              justify-content: center;
              margin-right: 10px;
              flex-shrink: 0;

              &.ai-avatar {
                background-color: #667eea;
                color: white;
              }

              &.agent-avatar {
                background-color: #409eff;
                color: white;
              }

              &.customer-avatar {
                background-color: #67c23a;
                color: white;
              }
            }

            .message-info {
              flex: 1;

              .message-sender {
                display: flex;
                align-items: center;
                gap: 8px;

                .sender-name {
                  font-weight: 600;
                  font-size: 14px;
                  color: #303133;
                }

                .message-time {
                  font-size: 12px;
                  color: #909399;
                }
              }
            }
          }

          .message-content {
            display: flex;

            .message-text {
              max-width: 60%;
              padding: 12px 16px;
              background-color: white;
              border-radius: 12px;
              word-break: break-word;
              box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
              line-height: 1.6;
              font-size: 14px;
            }

            .message-image {
              max-width: 300px;
            }

            .message-file {
              padding: 10px 15px;
              background-color: white;
              border-radius: 8px;
            }
          }

          .message-actions {
            margin-top: 5px;
          }
        }
      }

      .message-input {
        padding: 15px;
        border-top: 1px solid #eee;
        background-color: white;

        .input-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 10px;

          .input-left {
            display: flex;
            align-items: center;

            .input-tip {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
    }
  }
}
</style>
