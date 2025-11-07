package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.RoleCreateRequest;
import com.intellidesk.user.domain.dto.RoleQueryRequest;
import com.intellidesk.user.domain.dto.RoleUpdateRequest;
import com.intellidesk.user.domain.vo.RoleVO;
import com.intellidesk.user.service.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public Result<RoleVO> getRoleById(@PathVariable("id") Long id) {
        RoleVO roleVO = roleService.getRoleById(id);
        return Result.success(roleVO);
    }

    /**
     * 创建角色
     *
     * @param request 创建请求
     * @return 角色ID
     */
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("创建角色请求: roleCode={}", request.getRoleCode());
        Long roleId = roleService.createRole(request);
        return Result.success("创建成功", roleId);
    }

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param request 更新请求
     * @return 响应
     */
    @PutMapping("/{id}")
    public Result<Void> updateRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        log.info("更新角色请求: roleId={}", id);
        roleService.updateRole(id, request);
        return Result.success("更新成功");
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        log.info("删除角色请求: roleId={}", id);
        roleService.deleteRole(id);
        return Result.success("删除成功");
    }

    /**
     * 分页查询角色列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<RoleVO>> listRoles(RoleQueryRequest request) {
        log.info("查询角色列表: {}", request);
        PageResult<RoleVO> pageResult = roleService.listRoles(request);
        return Result.success(pageResult);
    }

    /**
     * 查询所有启用的角色
     *
     * @return 角色列表
     */
    @GetMapping("/enabled")
    public Result<List<RoleVO>> listEnabledRoles() {
        List<RoleVO> roles = roleService.listEnabledRoles();
        return Result.success(roles);
    }
}
