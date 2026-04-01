package com.xw.vo;

import lombok.Data;

/**
 * 过敏食材视图对象
 * @author XW
 */
@Data
public class AllergyVO {
    // 食材ID（前端将来调用删除接口时需要传这个ID）
    private Long materialId;

    // 食材名称（如：花生、牛奶，用于前端展示）
    private String name;
}