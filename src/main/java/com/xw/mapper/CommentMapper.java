package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态评论 Mapper 接口
 * @author XW
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}