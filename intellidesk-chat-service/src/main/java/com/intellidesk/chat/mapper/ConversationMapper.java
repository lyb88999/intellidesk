package com.intellidesk.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intellidesk.chat.domain.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话 Mapper 接口
 *
 * @author IntelliDesk
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
