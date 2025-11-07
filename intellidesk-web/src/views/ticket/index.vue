<template>
  <div class="ticket-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="工单编号">
          <el-input
            v-model="queryForm.ticketNo"
            placeholder="请输入工单编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="标题">
          <el-input
            v-model="queryForm.title"
            placeholder="请输入标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已解决" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="queryForm.priority" placeholder="请选择优先级" clearable>
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
            <el-option label="紧急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">工单列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">创建工单</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="ticketNo" label="工单编号" width="150" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="priorityText" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ row.priorityText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitterName" label="提交人" width="120" />
        <el-table-column prop="assigneeName" label="处理人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="success"
              size="small"
              @click="handleAssign(row)"
            >
              分配
            </el-button>
            <el-button
              v-if="row.status === 2"
              link
              type="warning"
              size="small"
              @click="handleResolve(row)"
            >
              解决
            </el-button>
            <el-button
              v-if="row.status === 3"
              link
              type="info"
              size="small"
              @click="handleClose(row)"
            >
              关闭
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 创建工单对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建工单"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createFormRules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="createForm.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入工单描述"
          />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="createForm.priority" placeholder="请选择优先级">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
            <el-option label="紧急" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-select v-model="createForm.source" placeholder="请选择来源">
            <el-option label="Web" :value="1" />
            <el-option label="Mobile" :value="2" />
            <el-option label="Email" :value="3" />
            <el-option label="Phone" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="createForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配工单对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配工单"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item label="处理人ID">
          <el-input-number v-model="assignForm.assigneeId" :min="1" placeholder="请输入处理人ID" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 解决工单对话框 -->
    <el-dialog
      v-model="resolveDialogVisible"
      title="解决工单"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form label-width="100px">
        <el-form-item label="解决方案">
          <el-input
            v-model="resolveForm.resolution"
            type="textarea"
            :rows="6"
            placeholder="请输入解决方案"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResolveSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看工单详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="工单详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ currentTicket.ticketNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTicket.status)">
            {{ currentTicket.statusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentTicket.title }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentTicket.description }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentTicket.priority)">
            {{ currentTicket.priorityText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="来源">{{ currentTicket.sourceText }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ currentTicket.submitterName }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentTicket.assigneeName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTicket.createTime }}</el-descriptions-item>
        <el-descriptions-item label="首次响应时间">{{ currentTicket.firstResponseTime }}</el-descriptions-item>
        <el-descriptions-item label="解决时间">{{ currentTicket.resolvedTime }}</el-descriptions-item>
        <el-descriptions-item label="关闭时间">{{ currentTicket.closedTime }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTicket.resolution" label="解决方案" :span="2">
          {{ currentTicket.resolution }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTicket.tags" label="标签" :span="2">
          {{ currentTicket.tags }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getTicketList,
  getTicketById,
  createTicket,
  deleteTicket,
  assignTicket,
  resolveTicket,
  closeTicket
} from '@/api/ticket'
import type {
  Ticket,
  TicketQueryRequest,
  TicketCreateRequest
} from '@/types/ticket'

// 查询表单
const queryForm = reactive<TicketQueryRequest>({
  ticketNo: '',
  title: '',
  status: undefined,
  priority: undefined,
  submitterId: undefined,
  assigneeId: undefined,
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref<Ticket[]>([])
const total = ref(0)
const loading = ref(false)

// 创建表单
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<TicketCreateRequest>({
  title: '',
  description: '',
  categoryId: undefined,
  priority: 2,
  source: 1,
  attachmentUrls: '',
  tags: ''
})

// 创建表单校验规则
const createFormRules: FormRules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入工单描述', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  source: [{ required: true, message: '请选择来源', trigger: 'change' }]
}

// 分配表单
const assignDialogVisible = ref(false)
const assignForm = reactive({
  ticketId: 0,
  assigneeId: 1
})

// 解决表单
const resolveDialogVisible = ref(false)
const resolveForm = reactive({
  ticketId: 0,
  resolution: ''
})

// 查看详情
const viewDialogVisible = ref(false)
const currentTicket = ref<Ticket>({} as Ticket)

// 优先级类型
const getPriorityType = (priority: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: '',
    3: 'warning',
    4: 'danger'
  }
  return types[priority] || ''
}

// 状态类型
const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'info'
  }
  return types[status] || ''
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await getTicketList(queryForm)
    tableData.value = result.data
    total.value = result.total
  } catch (error) {
    console.error('加载工单数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryForm.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.ticketNo = ''
  queryForm.title = ''
  queryForm.status = undefined
  queryForm.priority = undefined
  queryForm.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  createForm.title = ''
  createForm.description = ''
  createForm.categoryId = undefined
  createForm.priority = 2
  createForm.source = 1
  createForm.attachmentUrls = ''
  createForm.tags = ''
  createFormRef.value?.clearValidate()
  createDialogVisible.value = true
}

// 提交创建
const handleCreateSubmit = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await createTicket(createForm)
        ElMessage.success('创建成功')
        createDialogVisible.value = false
        await loadData()
      } catch (error) {
        console.error('创建工单失败:', error)
      }
    }
  })
}

// 查看详情
const handleView = async (row: Ticket) => {
  try {
    currentTicket.value = await getTicketById(row.id)
    viewDialogVisible.value = true
  } catch (error) {
    console.error('查询工单详情失败:', error)
  }
}

// 分配
const handleAssign = (row: Ticket) => {
  assignForm.ticketId = row.id
  assignForm.assigneeId = 1
  assignDialogVisible.value = true
}

// 提交分配
const handleAssignSubmit = async () => {
  try {
    await assignTicket(assignForm)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    await loadData()
  } catch (error) {
    console.error('分配工单失败:', error)
  }
}

// 解决
const handleResolve = (row: Ticket) => {
  resolveForm.ticketId = row.id
  resolveForm.resolution = ''
  resolveDialogVisible.value = true
}

// 提交解决
const handleResolveSubmit = async () => {
  if (!resolveForm.resolution.trim()) {
    ElMessage.warning('请输入解决方案')
    return
  }

  try {
    await resolveTicket(resolveForm)
    ElMessage.success('工单已解决')
    resolveDialogVisible.value = false
    await loadData()
  } catch (error) {
    console.error('解决工单失败:', error)
  }
}

// 关闭
const handleClose = async (row: Ticket) => {
  try {
    await ElMessageBox.confirm('确定要关闭该工单吗？', '提示', {
      type: 'warning'
    })
    await closeTicket(row.id)
    ElMessage.success('工单已关闭')
    await loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('关闭工单失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row: Ticket) => {
  try {
    await ElMessageBox.confirm('确定要删除该工单吗？', '提示', {
      type: 'warning'
    })
    await deleteTicket(row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除工单失败:', error)
    }
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.ticket-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
