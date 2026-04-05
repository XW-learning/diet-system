package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Share;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分享(饮食动态) Mapper 接口
 * * @author XW
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    // 目前使用 MyBatis-Plus 自带的 selectList、insert、updateById、deleteById 即可满足需求。
    // 后期如果我们需要做非常复杂的联表聚合查询（比如按点赞数排行热榜并联表查出所有信息），
    // 我们就可以在这里手写 @Select 注解或绑定 XML 来实现。
}