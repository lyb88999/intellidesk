<template>
  <div class="department-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input
            v-model="queryForm.departmentName"
            placeholder="请输入部门名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <span class="card-title">部门列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增部门</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="departmentName" label="部门名称" width="250" />
        <el-table-column prop="leaderName" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="treeData"
            :props="{ label: 'departmentName', value: 'id' }"
            check-strictly
            placeholder="请选择上级部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="departmentName">
          <el-input v-model="formData.departmentName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人ID" prop="leaderId">
          <el-input-number v-model="formData.leaderId" :min="1" placeholder="请输入负责人ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getDepartmentTree,
  createDepartment,
  updateDepartment,
  deleteDepartment
} from '@/api/department'
import type {
  Department,
  DepartmentQueryRequest,
  DepartmentCreateRequest,
  DepartmentUpdateRequest
} from '@/types/department'

// 查询表单
const queryForm = reactive<DepartmentQueryRequest>({
  departmentName: '',
  status: undefined
})

// 表格数据
const tableData = ref<Department[]>([])
const loading = ref(false)

// 树形数据（用于选择上级部门）
const treeData = ref<Department[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const formRef = ref<FormInstance>()
const formData = reactive({
  id: 0,
  departmentName: '',
  parentId: 0,
  leaderId: undefined,
  phone: '',
  email: '',
  sort: 0,
  status: 1
})

// 表单校验规则
const formRules: FormRules = {
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择上级部门', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const data = await getDepartmentTree(queryForm)
    tableData.value = data
    // 添加根节点用于选择
    treeData.value = [
      { id: 0, departmentName: '根部门', parentId: -1, sort: 0, status: 1 },
      ...data
    ]
  } catch (error) {
    console.error('加载部门数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.departmentName = ''
  queryForm.status = undefined
  loadData()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: Department) => {
  dialogTitle.value = '编辑部门'
  formData.id = row.id
  formData.departmentName = row.departmentName
  formData.parentId = row.parentId
  formData.leaderId = row.leaderId
  formData.phone = row.phone || ''
  formData.email = row.email || ''
  formData.sort = row.sort
  formData.status = row.status
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: Department) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
      type: 'warning'
    })
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除部门失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          // 编辑
          await updateDepartment(formData as DepartmentUpdateRequest)
          ElMessage.success('更新成功')
        } else {
          // 新增
          await createDepartment(formData as DepartmentCreateRequest)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadData()
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = 0
  formData.departmentName = ''
  formData.parentId = 0
  formData.leaderId = undefined
  formData.phone = ''
  formData.email = ''
  formData.sort = 0
  formData.status = 1
  formRef.value?.clearValidate()
}

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.department-container {
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
}
</style>
