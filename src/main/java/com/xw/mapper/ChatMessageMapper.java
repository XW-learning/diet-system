package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT * FROM t_chat_message WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<ChatMessage> getHistory(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM t_chat_message WHERE user_id = #{userId}")
    int countByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM t_chat_message WHERE user_id = #{userId} AND id > #{afterId}")
    int countAfterId(@Param("userId") Long userId, @Param("afterId") Long afterId);

    @Select("SELECT * FROM t_chat_message WHERE user_id = #{userId} AND id > #{afterId} ORDER BY id ASC LIMIT #{limit}")
    List<ChatMessage> getMessagesAfterId(@Param("userId") Long userId, @Param("afterId") Long afterId, @Param("limit") int limit);
}
