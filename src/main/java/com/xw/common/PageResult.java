package com.xw.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装
 * @author XW
 */
@Data
public class PageResult<T> {
    private List<T> records;      // 数据列表
    private Long total;           // 总记录数
    private Integer pageNum;      // 当前页码
    private Integer pageSize;     // 每页数量
    private Integer pages;        // 总页数

    public PageResult(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
}
