package com.intellidesk.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充创建时间、更新时间等字段
 *
 * @author IntelliDesk
 */
@Slf4j
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 如果有 createBy 字段，可以从上下文中获取当前用户ID
        // 这里暂时不填充，如果需要可以从 SecurityContextHolder 中获取
        // this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 如果有 updateBy 字段，可以从上下文中获取当前用户ID
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 获取当前登录用户ID
     * 可以从 ThreadLocal、SecurityContext 等获取
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 从认证上下文中获取当前用户ID
        // 示例：从 SecurityContextHolder 或自定义的 UserContext 中获取
        // return UserContext.getCurrentUserId();
        return null;
    }
}
