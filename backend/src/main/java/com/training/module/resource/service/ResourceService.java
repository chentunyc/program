package com.training.module.resource.service;

import com.training.common.base.PageResult;
import com.training.module.resource.dto.ResourceCreateDTO;
import com.training.module.resource.dto.ResourceQueryDTO;
import com.training.module.resource.dto.ResourceUpdateDTO;
import com.training.module.resource.vo.ResourceDetailVO;
import com.training.module.resource.vo.ResourceStatsVO;
import com.training.module.resource.vo.ResourceVO;

import java.util.List;

/**
 * 资源服务接口
 *
 * @author Training Team
 * @since 2024-01-01
 */
public interface ResourceService {

    /**
     * 分页查询资源列表
     *
     * @param queryDTO 查询条件
     * @return 资源分页列表
     */
    PageResult<ResourceVO> getResourcePage(ResourceQueryDTO queryDTO);

    /**
     * 根据ID获取资源详情
     *
     * @param id 资源ID
     * @return 资源详情
     */
    ResourceDetailVO getResourceById(Long id);

    /**
     * 创建资源
     *
     * @param createDTO 创建请求
     * @param userId 用户ID
     * @return 资源ID
     */
    Long createResource(ResourceCreateDTO createDTO, Long userId);

    /**
     * 更新资源
     *
     * @param updateDTO 更新请求
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateResource(ResourceUpdateDTO updateDTO, Long userId);

    /**
     * 删除资源
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean deleteResource(Long id);

    /**
     * 批量删除资源
     *
     * @param ids 资源ID数组
     * @return 是否成功
     */
    boolean batchDeleteResource(Long[] ids);

    /**
     * 发布资源
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean publishResource(Long id);

    /**
     * 下架资源
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean offlineResource(Long id);

    /**
     * 获取资源统计信息
     *
     * @return 统计信息
     */
    ResourceStatsVO getResourceStats();

    /**
     * 获取热门资源
     *
     * @param limit 数量限制
     * @return 资源列表
     */
    List<ResourceVO> getHotResources(Integer limit);

    /**
     * 获取最新资源
     *
     * @param limit 数量限制
     * @return 资源列表
     */
    List<ResourceVO> getLatestResources(Integer limit);

    /**
     * 增加浏览次数
     *
     * @param id 资源ID
     */
    void incrementViewCount(Long id);

    /**
     * 增加下载次数
     *
     * @param id 资源ID
     */
    void incrementDownloadCount(Long id);

    /**
     * 更新资源共享状态
     *
     * @param id 资源ID
     * @param isShared 是否共享（0-否，1-是）
     * @return 是否成功
     */
    boolean updateResourceShare(Long id, Integer isShared);

    /**
     * 上传资源文件
     *
     * @param file 文件
     * @param resourceType 资源类型
     * @return 文件访问路径
     */
    String uploadResourceFile(org.springframework.web.multipart.MultipartFile file, String resourceType);

    /**
     * 上传封面图片
     *
     * @param file 图片文件
     * @return 图片访问路径
     */
    String uploadCoverImage(org.springframework.web.multipart.MultipartFile file);

    /**
     * 分页查询当前用户上传的资源
     *
     * @param queryDTO 查询条件
     * @param userId 当前用户ID
     * @return 资源分页列表
     */
    PageResult<ResourceVO> getMyResourcePage(ResourceQueryDTO queryDTO, Long userId);
}
