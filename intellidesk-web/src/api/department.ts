import request from './request'
import type {
  Department,
  DepartmentQueryRequest,
  DepartmentCreateRequest,
  DepartmentUpdateRequest
} from '@/types/department'

/**
 * 查询部门树
 */
export const getDepartmentTree = (params?: DepartmentQueryRequest) => {
  return request({
    url: '/user/api/v1/departments/tree',
    method: 'get',
    params
  }).then((res: any) => res.data as Department[])
}

/**
 * 查询部门列表
 */
export const getDepartmentList = (params?: DepartmentQueryRequest) => {
  return request({
    url: '/user/api/v1/departments',
    method: 'get',
    params
  }).then((res: any) => res.data as Department[])
}

/**
 * 查询部门详情
 */
export const getDepartmentById = (id: number) => {
  return request({
    url: `/user/api/v1/departments/${id}`,
    method: 'get'
  }).then((res: any) => res.data as Department)
}

/**
 * 创建部门
 */
export const createDepartment = (data: DepartmentCreateRequest) => {
  return request({
    url: '/user/api/v1/departments',
    method: 'post',
    data
  }).then((res: any) => res.data as number)
}

/**
 * 更新部门
 */
export const updateDepartment = (data: DepartmentUpdateRequest) => {
  return request({
    url: `/user/api/v1/departments/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除部门
 */
export const deleteDepartment = (id: number) => {
  return request({
    url: `/user/api/v1/departments/${id}`,
    method: 'delete'
  })
}
