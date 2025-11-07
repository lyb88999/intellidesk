<template>
  <div class="permission-container">
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">权限列表</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新增权限</el-button>
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
        <el-table-column prop="permissionName" label="权限名称" width="200" />
        <el-table-column prop="permissionCode" label="权限编码" width="250" />
        <el-table-column prop="permissionType" label="权限类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.permissionType === 0" type="danger">菜单</el-tag>
            <el-tag v-else-if="row.permissionType === 1" type="warning">按钮</el-tag>
            <el-tag v-else type="success">接口</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径/接口" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
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
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="form.permissionType" placeholder="请选择权限类型">
            <el-option label="菜单" :value="0" />
            <el-option label="按钮" :value="1" />
            <el-option label="接口" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="permissionTreeOptions"
            :props="{ label: 'permissionName', value: 'id' }"
            check-strictly
            :render-after-expand="false"
            placeholder="请选择父级权限"
          />
        </el-form-item>
        <el-form-item v-if="form.permissionType === 0" label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item v-if="form.permissionType === 2" label="接口URL" prop="path">
          <el-input v-model="form.path" placeholder="请输入接口URL" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { gsap } from 'gsap'
// import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/permission'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增权限')
const formRef = ref<FormInstance>()

const form = reactive({
  id: undefined as number | undefined,
  permissionName: '',
  permissionCode: '',
  permissionType: 0,
  parentId: 0,
  path: '',
  sort: 0,
  icon: '',
  description: ''
})

const formRules: FormRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 模拟数据
const tableData = ref([
  {
    id: 1,
    permissionName: '系统管理',
    permissionCode: 'system',
    permissionType: 0,
    path: '/system',
    sort: 1,
    icon: 'Setting',
    children: [
      {
        id: 11,
        permissionName: '用户管理',
        permissionCode: 'system:user',
        permissionType: 0,
        path: '/system/user',
        sort: 1,
        icon: 'User',
        children: [
          {
            id: 111,
            permissionName: '新增用户',
            permissionCode: 'system:user:add',
            permissionType: 1,
            path: '',
            sort: 1
          },
          {
            id: 112,
            permissionName: '编辑用户',
            permissionCode: 'system:user:edit',
            permissionType: 1,
            path: '',
            sort: 2
          }
        ]
      },
      {
        id: 12,
        permissionName: '角色管理',
        permissionCode: 'system:role',
        permissionType: 0,
        path: '/system/role',
        sort: 2,
        icon: 'UserFilled'
      },
      {
        id: 13,
        permissionName: '权限管理',
        permissionCode: 'system:permission',
        permissionType: 0,
        path: '/system/permission',
        sort: 3,
        icon: 'Lock'
      }
    ]
  },
  {
    id: 2,
    permissionName: '仪表盘',
    permissionCode: 'dashboard',
    permissionType: 0,
    path: '/dashboard',
    sort: 0,
    icon: 'DataAnalysis'
  }
])

// 权限树选择选项
const permissionTreeOptions = computed(() => {
  return [
    { id: 0, permissionName: '根节点', children: tableData.value }
  ]
})

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增权限'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑权限'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除权限 "${row.permissionName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      // TODO: 调用 API 删除
      ElMessage.success('删除成功')
      loadData()
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
        loadData()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  form.id = undefined
  form.permissionName = ''
  form.permissionCode = ''
  form.permissionType = 0
  form.parentId = 0
  form.path = ''
  form.sort = 0
  form.icon = ''
  form.description = ''
}

const loadData = () => {
  loading.value = true
  // TODO: 调用 API 获取数据
  setTimeout(() => {
    loading.value = false
  }, 500)
}

onMounted(() => {
  loadData()

  // 动画效果
  gsap.from('.table-card', {
    duration: 0.5,
    y: 20,
    opacity: 0,
    ease: 'power2.out'
  })
})
</script>

<style scoped>
.permission-container {
  width: 100%;
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
</style>
