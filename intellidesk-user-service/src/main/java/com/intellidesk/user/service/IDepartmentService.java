package com.intellidesk.user.service;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.user.domain.dto.DepartmentCreateRequest;
import com.intellidesk.user.domain.dto.DepartmentQueryRequest;
import com.intellidesk.user.domain.dto.DepartmentUpdateRequest;
import com.intellidesk.user.domain.vo.DepartmentVO;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author IntelliDesk
 */
public interface IDepartmentService {

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 部门ID
     */
    Long createDepartment(DepartmentCreateRequest request);

    /**
     * 更新部门
     *
     * @param request 更新请求
     */
    void updateDepartment(DepartmentUpdateRequest request);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门信息
     */
    DepartmentVO getDepartmentById(Long id);

    /**
     * 查询部门列表（分页）
     *
     * @param request 查询请求
     * @return 部门列表
     */
    PageResult<DepartmentVO> getDepartmentList(DepartmentQueryRequest request);

    /**
     * 查询部门树
     *
     * @return 部门树
     */
    List<DepartmentVO> getDepartmentTree();
}
