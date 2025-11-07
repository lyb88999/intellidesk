package com.intellidesk.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.ResultCode;
import com.intellidesk.common.core.exception.BusinessException;
import com.intellidesk.common.core.util.Assert;
import com.intellidesk.ticket.domain.dto.*;
import com.intellidesk.ticket.domain.entity.Ticket;
import com.intellidesk.ticket.domain.entity.TicketLog;
import com.intellidesk.ticket.domain.vo.TicketVO;
import com.intellidesk.ticket.enums.TicketPriority;
import com.intellidesk.ticket.enums.TicketSource;
import com.intellidesk.ticket.enums.TicketStatus;
import com.intellidesk.ticket.mapper.TicketMapper;
import com.intellidesk.ticket.service.ITicketLogService;
import com.intellidesk.ticket.service.ITicketService;
import com.intellidesk.ticket.util.TicketNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单服务实现
 *
 * @author IntelliDesk
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements ITicketService {

    private final TicketMapper ticketMapper;
    private final ITicketLogService ticketLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTicket(TicketCreateRequest request) {
        log.info("创建工单: title={}", request.getTitle());

        // 1. 生成工单编号
        String ticketNo = TicketNoGenerator.generate();

        // 2. 创建工单
        Ticket ticket = Ticket.builder()
                .ticketNo(ticketNo)
                .title(request.getTitle())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM.getCode())
                .status(TicketStatus.PENDING.getCode())
                .submitterId(request.getSubmitterId())
                .submitterName(request.getSubmitterName())
                .submitterEmail(request.getSubmitterEmail())
                .submitterPhone(request.getSubmitterPhone())
                .assigneeId(request.getAssigneeId())
                .departmentId(request.getDepartmentId())
                .source(request.getSource() != null ? request.getSource() : TicketSource.WEB.getCode())
                .tags(request.getTags())
                .dueTime(request.getDueTime())
                .deleted(0)
                .build();

        ticketMapper.insert(ticket);
        log.info("工单创建成功: id={}, ticketNo={}", ticket.getId(), ticket.getTicketNo());

        // 3. 记录创建日志
        TicketLog createLog = TicketLog.builder()
                .ticketId(ticket.getId())
                .operationType(1) // 1-创建
                .operationDesc("创建工单")
                .newValue(ticket.getTitle())
                .operatorId(request.getSubmitterId())
                .operatorName(request.getSubmitterName())
                .build();
        ticketLogService.saveLog(createLog);

        // 4. 如果直接分配了处理人，记录分配日志
        if (ticket.getAssigneeId() != null) {
            TicketLog assignLog = TicketLog.builder()
                    .ticketId(ticket.getId())
                    .operationType(2) // 2-分配
                    .operationDesc("分配工单")
                    .newValue("分配给处理人ID: " + ticket.getAssigneeId())
                    .operatorId(request.getSubmitterId())
                    .operatorName(request.getSubmitterName())
                    .build();
            ticketLogService.saveLog(assignLog);
        }

        return ticket.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTicket(TicketUpdateRequest request) {
        log.info("更新工单: id={}", request.getId());

        // 1. 查询工单是否存在
        Ticket existingTicket = ticketMapper.selectById(request.getId());
        Assert.notNull(existingTicket, ResultCode.TICKET_NOT_FOUND);

        // 2. 检查工单状态
        if (TicketStatus.CLOSED.getCode().equals(existingTicket.getStatus()) ||
            TicketStatus.CANCELLED.getCode().equals(existingTicket.getStatus())) {
            throw new BusinessException(ResultCode.TICKET_STATUS_ERROR, "工单已关闭或已取消，无法修改");
        }

        // 3. 更新工单
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(request, ticket);
        ticketMapper.updateById(ticket);
        log.info("工单更新成功: id={}", request.getId());

        // 4. 记录更新日志
        TicketLog updateLog = TicketLog.builder()
                .ticketId(ticket.getId())
                .operationType(3) // 3-状态变更（这里简化处理）
                .operationDesc("更新工单")
                .oldValue(existingTicket.getTitle())
                .newValue(ticket.getTitle())
                .build();
        ticketLogService.saveLog(updateLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTicket(Long id) {
        log.info("删除工单: id={}", id);

        // 1. 查询工单是否存在
        Ticket ticket = ticketMapper.selectById(id);
        Assert.notNull(ticket, ResultCode.TICKET_NOT_FOUND);

        // 2. 逻辑删除
        ticketMapper.deleteById(id);
        log.info("工单删除成功: id={}", id);
    }

    @Override
    public TicketVO getTicketById(Long id) {
        log.info("查询工单详情: id={}", id);
        Ticket ticket = ticketMapper.selectById(id);
        Assert.notNull(ticket, ResultCode.TICKET_NOT_FOUND);
        return convertToVO(ticket);
    }

    @Override
    public PageResult<TicketVO> getTicketList(TicketQueryRequest request) {
        log.info("查询工单列表: request={}", request);

        // 构建查询条件
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getTicketNo()),
                Ticket::getTicketNo, request.getTicketNo());
        wrapper.like(StringUtils.hasText(request.getTitle()),
                Ticket::getTitle, request.getTitle());
        wrapper.eq(request.getStatus() != null,
                Ticket::getStatus, request.getStatus());
        wrapper.eq(request.getPriority() != null,
                Ticket::getPriority, request.getPriority());
        wrapper.eq(request.getSubmitterId() != null,
                Ticket::getSubmitterId, request.getSubmitterId());
        wrapper.eq(request.getAssigneeId() != null,
                Ticket::getAssigneeId, request.getAssigneeId());
        wrapper.eq(request.getDepartmentId() != null,
                Ticket::getDepartmentId, request.getDepartmentId());
        wrapper.eq(request.getSource() != null,
                Ticket::getSource, request.getSource());
        wrapper.eq(Ticket::getDeleted, 0);
        wrapper.orderByDesc(Ticket::getCreateTime);

        // 分页查询
        Page<Ticket> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Ticket> result = ticketMapper.selectPage(page, wrapper);

        // 转换为VO
        List<TicketVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(result.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTicket(TicketAssignRequest request, Long operatorId) {
        log.info("分配工单: ticketId={}, assigneeId={}", request.getTicketId(), request.getAssigneeId());

        // 1. 查询工单
        Ticket ticket = ticketMapper.selectById(request.getTicketId());
        Assert.notNull(ticket, ResultCode.TICKET_NOT_FOUND);

        // 2. 检查状态
        if (TicketStatus.CLOSED.getCode().equals(ticket.getStatus()) ||
            TicketStatus.CANCELLED.getCode().equals(ticket.getStatus())) {
            throw new BusinessException(ResultCode.TICKET_STATUS_ERROR);
        }

        // 3. 更新处理人
        Ticket updateEntity = new Ticket();
        updateEntity.setId(ticket.getId());
        updateEntity.setAssigneeId(request.getAssigneeId());

        // 如果是首次分配，更新状态为处理中
        if (TicketStatus.PENDING.getCode().equals(ticket.getStatus())) {
            updateEntity.setStatus(TicketStatus.PROCESSING.getCode());
            updateEntity.setFirstResponseTime(LocalDateTime.now());
        }

        ticketMapper.updateById(updateEntity);
        log.info("工单分配成功: ticketId={}", ticket.getId());

        // 4. 记录分配日志
        TicketLog assignLog = TicketLog.builder()
                .ticketId(ticket.getId())
                .operationType(2) // 2-分配
                .operationDesc("分配工单")
                .oldValue(ticket.getAssigneeId() != null ? ticket.getAssigneeId().toString() : "未分配")
                .newValue(request.getAssigneeId().toString())
                .operatorId(operatorId)
                .build();
        ticketLogService.saveLog(assignLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeTicket(Long id, Long operatorId) {
        log.info("关闭工单: id={}", id);

        // 1. 查询工单
        Ticket ticket = ticketMapper.selectById(id);
        Assert.notNull(ticket, ResultCode.TICKET_NOT_FOUND);

        // 2. 更新状态
        Ticket updateEntity = new Ticket();
        updateEntity.setId(id);
        updateEntity.setStatus(TicketStatus.CLOSED.getCode());
        updateEntity.setClosedTime(LocalDateTime.now());
        ticketMapper.updateById(updateEntity);

        // 3. 记录日志
        TicketLog closeLog = TicketLog.builder()
                .ticketId(id)
                .operationType(5) // 5-关闭
                .operationDesc("关闭工单")
                .oldValue(TicketStatus.getDesc(ticket.getStatus()))
                .newValue(TicketStatus.CLOSED.getDesc())
                .operatorId(operatorId)
                .build();
        ticketLogService.saveLog(closeLog);

        log.info("工单关闭成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveTicket(Long id, Long operatorId) {
        log.info("解决工单: id={}", id);

        // 1. 查询工单
        Ticket ticket = ticketMapper.selectById(id);
        Assert.notNull(ticket, ResultCode.TICKET_NOT_FOUND);

        // 2. 更新状态
        Ticket updateEntity = new Ticket();
        updateEntity.setId(id);
        updateEntity.setStatus(TicketStatus.RESOLVED.getCode());
        updateEntity.setResolvedTime(LocalDateTime.now());
        ticketMapper.updateById(updateEntity);

        // 3. 记录日志
        TicketLog resolveLog = TicketLog.builder()
                .ticketId(id)
                .operationType(3) // 3-状态变更
                .operationDesc("解决工单")
                .oldValue(TicketStatus.getDesc(ticket.getStatus()))
                .newValue(TicketStatus.RESOLVED.getDesc())
                .operatorId(operatorId)
                .build();
        ticketLogService.saveLog(resolveLog);

        log.info("工单解决成功: id={}", id);
    }

    /**
     * 实体转VO
     *
     * @param ticket 工单实体
     * @return 工单VO
     */
    private TicketVO convertToVO(Ticket ticket) {
        TicketVO vo = new TicketVO();
        BeanUtils.copyProperties(ticket, vo);

        // 添加枚举文本
        vo.setStatusText(TicketStatus.getDesc(ticket.getStatus()));
        vo.setPriorityText(TicketPriority.getDesc(ticket.getPriority()));
        vo.setSourceText(TicketSource.getDesc(ticket.getSource()));

        return vo;
    }
}
