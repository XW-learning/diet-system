package com.xw.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.user.UserPreference;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食偏好 Mapper 接口
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
}