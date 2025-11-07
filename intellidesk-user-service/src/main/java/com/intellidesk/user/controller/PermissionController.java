package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.PermissionCreateRequest;
import com.intellidesk.user.domain.dto.PermissionUpdateRequest;
import com.intellidesk.user.domain.vo.PermissionVO;
import com.intellidesk.user.service.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限信息
     */
    @GetMapping("/{id}")
    public Result<PermissionVO> getPermissionById(@PathVariable("id") Long id) {
        PermissionVO permissionVO = permissionService.getPermissionById(id);
        return Result.success(permissionVO);
    }

    /**
     * 创建权限
     *
     * @param request 创建请求
     * @return 权限ID
     */
    @PostMapping
    public Result<Long> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        log.info("创建权限请求: permissionCode={}", request.getPermissionCode());
        Long permissionId = permissionService.createPermission(request);
        return Result.success("创建成功", permissionId);
    }

    /**
     * 更新权限
     *
     * @param id      权限ID
     * @param request 更新请求
     * @return 响应
     */
    @PutMapping("/{id}")
    public Result<String> updatePermission(
            @PathVariable("id") Long id,
            @Valid @RequestBody PermissionUpdateRequest request) {
        log.info("更新权限请求: permissionId={}", id);
        permissionService.updatePermission(id, request);
        return Result.success("更新成功");
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public Result<String> deletePermission(@PathVariable("id") Long id) {
        log.info("删除权限请求: permissionId={}", id);
        permissionService.deletePermission(id);
        return Result.success("删除成功");
    }

    /**
     * 查询权限树（所有权限）
     *
     * @return 权限树
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> listPermissionTree() {
        List<PermissionVO> tree = permissionService.listPermissionTree();
        return Result.success(tree);
    }

    /**
     * 查询启用的权限树
     *
     * @return 权限树
     */
    @GetMapping("/tree/enabled")
    public Result<List<PermissionVO>> listEnabledPermissionTree() {
        List<PermissionVO> tree = permissionService.listEnabledPermissionTree();
        return Result.success(tree);
    }

    /**
     * 查询所有权限列表（扁平结构）
     *
     * @return 权限列表
     */
    @GetMapping("/list")
    public Result<List<PermissionVO>> listAllPermissions() {
        List<PermissionVO> permissions = permissionService.listAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 根据类型查询权限
     *
     * @param type 权限类型（1-菜单 2-按钮 3-接口）
     * @return 权限列表
     */
    @GetMapping("/list/type/{type}")
    public Result<List<PermissionVO>> listPermissionsByType(@PathVariable("type") Integer type) {
        List<PermissionVO> permissions = permissionService.listPermissionsByType(type);
        return Result.success(permissions);
    }
}
