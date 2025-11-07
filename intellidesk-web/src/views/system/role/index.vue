<template>
  <div class="role-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryForm.roleName"
            placeholder="请输入角色名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input
            v-model="queryForm.roleCode"
            placeholder="请输入角色编码"
            clearable
          />
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
          <span class="card-title">角色列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增角色</el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="200" />
        <el-table-column prop="roleCode" label="角色编码" width="250" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handlePermission(row)">
              分配权限
            </el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              编辑
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-tree
        ref="treeRef"
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { gsap } from 'gsap'
// import { getRoleList, createRole, updateRole, deleteRole, assignPermissions } from '@/api/role'

const loading = ref(false)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref<FormInstance>()
const treeRef = ref()
const total = ref(0)
const checkedPermissions = ref<number[]>([])

const queryForm = reactive({
  roleName: '',
  roleCode: '',
  pageNum: 1,
  pageSize: 10
})

const form = reactive({
  id: undefined as number | undefined,
  roleName: '',
  roleCode: '',
  description: ''
})

const formRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
}

// 模拟数据
const tableData = ref([
  {
    id: 1,
    roleName: '超级管理员',
    roleCode: 'ROLE_SUPER_ADMIN',
    description: '系统超级管理员',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    roleName: '客服人员',
    roleCode: 'ROLE_CUSTOMER_SERVICE',
    description: '客服人员角色',
    createTime: '2024-01-02 11:00:00'
  }
])

const permissionTree = ref([
  {
    id: 1,
    permissionName: '系统管理',
    children: [
      { id: 11, permissionName: '用户管理' },
      { id: 12, permissionName: '角色管理' },
      { id: 13, permissionName: '权限管理' }
    ]
  },
  {
    id: 2,
    permissionName: '工单管理',
    children: [
      { id: 21, permissionName: '工单列表' },
      { id: 22, permissionName: '工单分配' }
    ]
  }
])

const handleQuery = () => {
  loading.value = true
  // TODO: 调用 API 获取数据
  setTimeout(() => {
    loading.value = false
    total.value = tableData.value.length
  }, 500)
}

const handleReset = () => {
  queryForm.roleName = ''
  queryForm.roleCode = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handlePermission = (row: any) => {
  form.id = row.id
  checkedPermissions.value = [11, 12] // 模拟已分配的权限
  permissionDialogVisible.value = true
}

const handleSavePermission = () => {
  const checkedKeys = treeRef.value.getCheckedKeys()
  // TODO: 调用 API 保存权限
  ElMessage.success('权限分配成功')
  permissionDialogVisible.value = false
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      // TODO: 调用 API 删除
      ElMessage.success('删除成功')
      handleQuery()
    })
    .catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用 API 保存
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        handleQuery()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  form.id = undefined
  form.roleName = ''
  form.roleCode = ''
  form.description = ''
}

onMounted(() => {
  handleQuery()

  // 动画效果
  gsap.from('.search-card', {
    duration: 0.5,
    y: -20,
    opacity: 0,
    ease: 'power2.out'
  })

  gsap.from('.table-card', {
    duration: 0.5,
    y: 20,
    opacity: 0,
    delay: 0.2,
    ease: 'power2.out'
  })
})
</script>

<style scoped>
.role-container {
  width: 100%;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
