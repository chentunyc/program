package com.training.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 *
 * @author Training Team
 * @since 2024-01-01
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 从MyBatis Plus的IPage转换
     */
    public static <T> PageResult<T> from(IPage<T> page) {
        return new PageResult<>(
            page.getRecords(),
            page.getTotal(),
            page.getCurrent(),
            page.getSize(),
            page.getPages()
        );
    }

    /**
     * 转换数据类型
     */
    public <R> PageResult<R> convert(List<R> records) {
        return new PageResult<>(records, this.total, this.current, this.size, this.pages);
    }
}
