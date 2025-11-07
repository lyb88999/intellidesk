package com.intellidesk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.enums.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.util.Assert;
import com.intellidesk.user.domain.dto.DepartmentCreateRequest;
import com.intellidesk.user.domain.dto.DepartmentQueryRequest;
import com.intellidesk.user.domain.dto.DepartmentUpdateRequest;
import com.intellidesk.user.domain.entity.SysDepartment;
import com.intellidesk.user.domain.entity.SysUser;
import com.intellidesk.user.domain.vo.DepartmentVO;
import com.intellidesk.user.mapper.SysDepartmentMapper;
import com.intellidesk.user.mapper.SysUserMapper;
import com.intellidesk.user.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements IDepartmentService {

    private final SysDepartmentMapper departmentMapper;
    private final SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentCreateRequest request) {
        log.info("创建部门: deptName={}, deptCode={}", request.getDeptName(), request.getDeptCode());

        // 1. 校验部门编码唯一性
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDepartment::getDeptCode, request.getDeptCode());
        wrapper.eq(SysDepartment::getDeleted, 0);
        if (departmentMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "部门编码已存在");
        }

        // 2. 如果有父部门，检查父部门是否存在
        if (request.getParentId() != null && request.getParentId() > 0) {
            SysDepartment parentDept = departmentMapper.selectById(request.getParentId());
            Assert.notNull(parentDept, ResultCode.BAD_REQUEST, "父部门不存在");
        }

        // 3. 如果有负责人，检查负责人是否存在
        if (request.getLeaderId() != null) {
            SysUser leader = userMapper.selectById(request.getLeaderId());
            Assert.notNull(leader, ResultCode.BAD_REQUEST, "负责人不存在");
        }

        // 4. 创建部门
        SysDepartment department = SysDepartment.builder()
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .deptName(request.getDeptName())
                .deptCode(request.getDeptCode())
                .leaderId(request.getLeaderId())
                .phone(request.getPhone())
                .email(request.getEmail())
                .sort(request.getSort() != null ? request.getSort() : 0)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .deleted(0)
                .build();

        departmentMapper.insert(department);
        log.info("部门创建成功: id={}", department.getId());
        return department.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(DepartmentUpdateRequest request) {
        log.info("更新部门: id={}", request.getId());

        // 1. 查询部门是否存在
        SysDepartment existingDept = departmentMapper.selectById(request.getId());
        Assert.notNull(existingDept, ResultCode.DEPARTMENT_NOT_FOUND);

        // 2. 检查是否修改为自己的子部门（防止循环引用）
        if (request.getParentId() != null && request.getParentId() > 0) {
            if (request.getParentId().equals(request.getId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "不能将部门设置为自己的子部门");
            }
            // TODO: 可以进一步检查是否设置为自己的子孙部门
        }

        // 3. 校验部门编码唯一性（排除自己）
        if (!existingDept.getDeptCode().equals(request.getDeptCode())) {
            LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysDepartment::getDeptCode, request.getDeptCode());
            wrapper.ne(SysDepartment::getId, request.getId());
            wrapper.eq(SysDepartment::getDeleted, 0);
            if (departmentMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "部门编码已存在");
            }
        }

        // 4. 如果有负责人，检查负责人是否存在
        if (request.getLeaderId() != null) {
            SysUser leader = userMapper.selectById(request.getLeaderId());
            Assert.notNull(leader, ResultCode.BAD_REQUEST, "负责人不存在");
        }

        // 5. 更新部门
        SysDepartment department = new SysDepartment();
        BeanUtils.copyProperties(request, department);
        departmentMapper.updateById(department);
        log.info("部门更新成功: id={}", request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        log.info("删除部门: id={}", id);

        // 1. 查询部门是否存在
        SysDepartment department = departmentMapper.selectById(id);
        Assert.notNull(department, ResultCode.DEPARTMENT_NOT_FOUND);

        // 2. 检查是否有子部门
        LambdaQueryWrapper<SysDepartment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysDepartment::getParentId, id);
        childWrapper.eq(SysDepartment::getDeleted, 0);
        if (departmentMapper.selectCount(childWrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该部门下还有子部门，不能删除");
        }

        // 3. 检查是否有用户
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getDepartmentId, id);
        userWrapper.eq(SysUser::getDeleted, 0);
        if (userMapper.selectCount(userWrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该部门下还有用户，不能删除");
        }

        // 4. 逻辑删除部门
        departmentMapper.deleteById(id);
        log.info("部门删除成功: id={}", id);
    }

    @Override
    public DepartmentVO getDepartmentById(Long id) {
        log.info("查询部门详情: id={}", id);
        SysDepartment department = departmentMapper.selectById(id);
        Assert.notNull(department, ResultCode.DEPARTMENT_NOT_FOUND);
        return convertToVO(department);
    }

    @Override
    public PageResult<DepartmentVO> getDepartmentList(DepartmentQueryRequest request) {
        log.info("查询部门列表: request={}", request);

        // 构建查询条件
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getDeptName()),
                SysDepartment::getDeptName, request.getDeptName());
        wrapper.like(StringUtils.hasText(request.getDeptCode()),
                SysDepartment::getDeptCode, request.getDeptCode());
        wrapper.eq(request.getStatus() != null,
                SysDepartment::getStatus, request.getStatus());
        wrapper.eq(SysDepartment::getDeleted, 0);
        wrapper.orderByAsc(SysDepartment::getSort);
        wrapper.orderByDesc(SysDepartment::getCreateTime);

        // 分页查询
        Page<SysDepartment> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysDepartment> result = departmentMapper.selectPage(page, wrapper);

        // 转换为VO
        List<DepartmentVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 批量查询负责人姓名
        List<Long> leaderIds = voList.stream()
                .map(DepartmentVO::getLeaderId)
                .filter(leaderId -> leaderId != null && leaderId > 0)
                .distinct()
                .collect(Collectors.toList());

        if (!leaderIds.isEmpty()) {
            List<SysUser> leaders = userMapper.selectBatchIds(leaderIds);
            Map<Long, String> leaderNameMap = leaders.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));

            voList.forEach(vo -> {
                if (vo.getLeaderId() != null && leaderNameMap.containsKey(vo.getLeaderId())) {
                    vo.setLeaderName(leaderNameMap.get(vo.getLeaderId()));
                }
            });
        }

        return PageResult.of(result.getTotal(), voList);
    }

    @Override
    public List<DepartmentVO> getDepartmentTree() {
        log.info("查询部门树");

        // 1. 查询所有部门
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDepartment::getDeleted, 0);
        wrapper.orderByAsc(SysDepartment::getSort);
        List<SysDepartment> allDepartments = departmentMapper.selectList(wrapper);

        // 2. 转换为VO
        List<DepartmentVO> allVOs = allDepartments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 3. 批量查询负责人姓名
        List<Long> leaderIds = allVOs.stream()
                .map(DepartmentVO::getLeaderId)
                .filter(leaderId -> leaderId != null && leaderId > 0)
                .distinct()
                .collect(Collectors.toList());

        if (!leaderIds.isEmpty()) {
            List<SysUser> leaders = userMapper.selectBatchIds(leaderIds);
            Map<Long, String> leaderNameMap = leaders.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));

            allVOs.forEach(vo -> {
                if (vo.getLeaderId() != null && leaderNameMap.containsKey(vo.getLeaderId())) {
                    vo.setLeaderName(leaderNameMap.get(vo.getLeaderId()));
                }
            });
        }

        // 4. 构建树形结构
        return buildTree(allVOs, 0L);
    }

    /**
     * 构建部门树
     *
     * @param allDepartments 所有部门
     * @param parentId       父部门ID
     * @return 部门树
     */
    private List<DepartmentVO> buildTree(List<DepartmentVO> allDepartments, Long parentId) {
        List<DepartmentVO> tree = new ArrayList<>();

        for (DepartmentVO dept : allDepartments) {
            if (dept.getParentId().equals(parentId)) {
                List<DepartmentVO> children = buildTree(allDepartments, dept.getId());
                dept.setChildren(children.isEmpty() ? null : children);
                tree.add(dept);
            }
        }

        return tree;
    }

    /**
     * 实体转VO
     *
     * @param department 部门实体
     * @return 部门VO
     */
    private DepartmentVO convertToVO(SysDepartment department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        return vo;
    }
}
