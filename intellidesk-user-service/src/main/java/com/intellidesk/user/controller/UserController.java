package com.intellidesk.user.controller;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.user.domain.dto.UserCreateRequest;
import com.intellidesk.user.domain.dto.UserQueryRequest;
import com.intellidesk.user.domain.dto.UserUpdateRequest;
import com.intellidesk.user.domain.vo.UserVO;
import com.intellidesk.user.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID（从JWT Token中获取）
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable("id") Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户ID
     */
    @PostMapping
    public Result<Long> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户请求: username={}", request.getUsername());
        Long userId = userService.createUser(request);
        return Result.success("创建成功", userId);
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 更新请求
     * @return 响应
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户请求: userId={}", id);
        userService.updateUser(id, request);
        return Result.success("更新成功");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("删除用户请求: userId={}", id);
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    /**
     * 分页查询用户列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> listUsers(UserQueryRequest request) {
        log.info("查询用户列表: {}", request);
        PageResult<UserVO> pageResult = userService.listUsers(request);
        return Result.success(pageResult);
    }
}
