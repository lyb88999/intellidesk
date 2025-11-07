package com.intellidesk.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.chat.domain.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息 Mapper 接口
 *
 * @author IntelliDesk
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
