package com.xw.mapper.ai;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.ai.ChatSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface ChatSummaryMapper extends BaseMapper<ChatSummary> {

    @Select("SELECT * FROM t_chat_summary WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<ChatSummary> getRecentSummaries(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT COALESCE(MAX(end_msg_id), 0) FROM t_chat_summary WHERE user_id = #{userId}")
    Long getLastCompressedMsgId(@Param("userId") Long userId);
}
