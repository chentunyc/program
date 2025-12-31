package com.training.module.resource.vo;

import lombok.Data;

/**
 * 资源统计VO
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Data
public class ResourceStatsVO {

    /**
     * 虚拟仿真资源数量
     */
    private Long simulationCount;

    /**
     * 视频资源数量
     */
    private Long videoCount;

    /**
     * 音频资源数量
     */
    private Long audioCount;

    /**
     * 文档资源数量
     */
    private Long documentCount;

    /**
     * 资源总数
     */
    private Long totalCount;
}
