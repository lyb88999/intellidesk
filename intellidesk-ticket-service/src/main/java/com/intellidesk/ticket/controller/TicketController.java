package com.intellidesk.ticket.controller;

import com.intellidesk.common.core.constant.SecurityConstants;
import com.intellidesk.common.core.domain.PageResult;
import com.intellidesk.common.core.domain.Result;
import com.intellidesk.ticket.domain.dto.*;
import com.intellidesk.ticket.domain.vo.TicketVO;
import com.intellidesk.ticket.service.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 工单管理 Controller
 *
 * @author IntelliDesk
 */
@Slf4j
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final ITicketService ticketService;

    /**
     * 创建工单
     *
     * @param request 创建请求
     * @return 工单ID
     */
    @PostMapping
    public Result<Long> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        log.info("创建工单请求: title={}", request.getTitle());
        Long ticketId = ticketService.createTicket(request);
        return Result.success("创建成功", ticketId);
    }

    /**
     * 更新工单
     *
     * @param request 更新请求
     * @return 响应
     */
    @PutMapping
    public Result<Void> updateTicket(@Valid @RequestBody TicketUpdateRequest request) {
        log.info("更新工单请求: id={}", request.getId());
        ticketService.updateTicket(request);
        return Result.success("更新成功");
    }

    /**
     * 删除工单
     *
     * @param id 工单ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTicket(@PathVariable Long id) {
        log.info("删除工单请求: id={}", id);
        ticketService.deleteTicket(id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询工单
     *
     * @param id 工单ID
     * @return 工单信息
     */
    @GetMapping("/{id}")
    public Result<TicketVO> getTicketById(@PathVariable Long id) {
        log.info("查询工单详情: id={}", id);
        TicketVO ticket = ticketService.getTicketById(id);
        return Result.success(ticket);
    }

    /**
     * 查询工单列表（分页）
     *
     * @param request 查询请求
     * @return 工单列表
     */
    @GetMapping("/list")
    public Result<PageResult<TicketVO>> getTicketList(TicketQueryRequest request) {
        log.info("查询工单列表: request={}", request);
        PageResult<TicketVO> result = ticketService.getTicketList(request);
        return Result.success(result);
    }

    /**
     * 分配工单
     *
     * @param userId 用户ID（从JWT Token中获取）
     * @param request 分配请求
     * @return 响应
     */
    @PostMapping("/assign")
    public Result<Void> assignTicket(
            @RequestHeader(value = SecurityConstants.USER_ID, required = false) Long userId,
            @Valid @RequestBody TicketAssignRequest request) {
        log.info("分配工单请求: ticketId={}, assigneeId={}", request.getTicketId(), request.getAssigneeId());
        ticketService.assignTicket(request, userId);
        return Result.success("分配成功");
    }

    /**
     * 关闭工单
     *
     * @param userId 用户ID（从JWT Token中获取）
     * @param id 工单ID
     * @return 响应
     */
    @PostMapping("/{id}/close")
    public Result<Void> closeTicket(
            @RequestHeader(value = SecurityConstants.USER_ID, required = false) Long userId,
            @PathVariable Long id) {
        log.info("关闭工单请求: id={}", id);
        ticketService.closeTicket(id, userId);
        return Result.success("工单已关闭");
    }

    /**
     * 解决工单
     *
     * @param userId 用户ID（从JWT Token中获取）
     * @param id 工单ID
     * @return 响应
     */
    @PostMapping("/{id}/resolve")
    public Result<Void> resolveTicket(
            @RequestHeader(value = SecurityConstants.USER_ID, required = false) Long userId,
            @PathVariable Long id) {
        log.info("解决工单请求: id={}", id);
        ticketService.resolveTicket(id, userId);
        return Result.success("工单已解决");
    }
}
