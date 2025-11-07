/**
 * 部门信息
 */
export interface Department {
  id: number
  departmentName: string
  parentId: number
  parentName?: string
  leaderId?: number
  leaderName?: string
  phone?: string
  email?: string
  sort: number
  status: number
  createTime?: string
  updateTime?: string
  children?: Department[]
}

/**
 * 部门查询请求
 */
export interface DepartmentQueryRequest {
  departmentName?: string
  status?: number
}

/**
 * 部门创建请求
 */
export interface DepartmentCreateRequest {
  departmentName: string
  parentId: number
  leaderId?: number
  phone?: string
  email?: string
  sort: number
  status: number
}

/**
 * 部门更新请求
 */
export interface DepartmentUpdateRequest {
  id: number
  departmentName: string
  parentId: number
  leaderId?: number
  phone?: string
  email?: string
  sort: number
  status: number
}
