package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.UserAllergy;
import com.xw.vo.AllergyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface UserAllergyMapper extends BaseMapper<UserAllergy> {

    /**
     * 查询指定用户的过敏食材列表（关联查询食材名称）
     */
    @Select("""
        SELECT
            a.material_id AS materialId,
            r.name AS name
        FROM t_user_allergy a
        LEFT JOIN t_raw_material r ON a.material_id = r.id
        WHERE a.user_id = #{userId}
    """)
    List<AllergyVO> getUserAllergies(@Param("userId") Long userId);
}