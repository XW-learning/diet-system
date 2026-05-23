package com.xw.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.user.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}