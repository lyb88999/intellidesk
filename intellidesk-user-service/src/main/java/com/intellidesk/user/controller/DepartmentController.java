package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.DepartmentCreateRequest;
import com.intellidesk.user.domain.dto.DepartmentQueryRequest;
import com.intellidesk.user.domain.dto.DepartmentUpdateRequest;
import com.intellidesk.user.domain.vo.DepartmentVO;
import com.intellidesk.user.service.IDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final IDepartmentService departmentService;

    /**
     * 创建部门
     *
     * @param request 创建请求
     * @return 部门ID
     */
    @PostMapping
    public Result<Long> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        log.info("创建部门请求: deptName={}", request.getDeptName());
        Long deptId = departmentService.createDepartment(request);
        return Result.success("创建成功", deptId);
    }

    /**
     * 更新部门
     *
     * @param request 更新请求
     * @return 响应
     */
    @PutMapping
    public Result<String> updateDepartment(@Valid @RequestBody DepartmentUpdateRequest request) {
        log.info("更新部门请求: id={}", request.getId());
        departmentService.updateDepartment(request);
        return Result.success("更新成功");
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDepartment(@PathVariable Long id) {
        log.info("删除部门请求: id={}", id);
        departmentService.deleteDepartment(id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public Result<DepartmentVO> getDepartmentById(@PathVariable Long id) {
        log.info("查询部门详情: id={}", id);
        DepartmentVO department = departmentService.getDepartmentById(id);
        return Result.success(department);
    }

    /**
     * 查询部门列表（分页）
     *
     * @param request 查询请求
     * @return 部门列表
     */
    @GetMapping("/list")
    public Result<PageResult<DepartmentVO>> getDepartmentList(DepartmentQueryRequest request) {
        log.info("查询部门列表: request={}", request);
        PageResult<DepartmentVO> result = departmentService.getDepartmentList(request);
        return Result.success(result);
    }

    /**
     * 查询部门树
     *
     * @return 部门树
     */
    @GetMapping("/tree")
    public Result<List<DepartmentVO>> getDepartmentTree() {
        log.info("查询部门树");
        List<DepartmentVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }
}
