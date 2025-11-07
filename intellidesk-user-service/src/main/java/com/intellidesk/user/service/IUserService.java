package com.intellidesk.user.service;

import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.user.domain.dto.UserCreateRequest;
import com.intellidesk.user.domain.dto.UserQueryRequest;
import com.intellidesk.user.domain.dto.UserUpdateRequest;
import com.intellidesk.user.domain.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author IntelliDesk
 */
public interface IUserService {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户ID
     */
    Long createUser(UserCreateRequest request);

    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param request 更新请求
     */
    void updateUser(Long userId, UserUpdateRequest request);

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 分页查询用户列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResult<UserVO> listUsers(UserQueryRequest request);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
}
