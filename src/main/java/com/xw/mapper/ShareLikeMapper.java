package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.ShareLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态点赞 Mapper 接口
 * @author XW
 */
@Mapper
public interface ShareLikeMapper extends BaseMapper<ShareLike> {
}