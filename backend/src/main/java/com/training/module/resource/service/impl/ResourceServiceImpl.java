package com.training.module.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.training.common.base.PageResult;
import com.training.common.exception.BusinessException;
import com.training.common.utils.SecurityUtils;
import com.training.module.resource.dto.ResourceCreateDTO;
import com.training.module.resource.dto.ResourceQueryDTO;
import com.training.module.resource.dto.ResourceUpdateDTO;
import com.training.module.resource.entity.*;
import com.training.module.resource.mapper.*;
import com.training.module.resource.service.ResourceService;
import com.training.module.resource.vo.ResourceDetailVO;
import com.training.module.resource.vo.ResourceStatsVO;
import com.training.module.resource.vo.ResourceVO;
import com.training.module.user.entity.User;
import com.training.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源服务实现类
 *
 * @author Training Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceSimulationMapper simulationMapper;
    private final ResourceVideoMapper videoMapper;
    private final ResourceAudioMapper audioMapper;
    private final ResourceDocumentMapper documentMapper;
    private final UserMapper userMapper;
    private final com.training.module.system.service.SystemConfigService systemConfigService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.resource-path:/resources}")
    private String resourceFilePath;

    @Value("${file.upload.cover-path:/covers}")
    private String coverPath;

    // 允许的文件类型
    private static final Map<String, Set<String>> ALLOWED_EXTENSIONS = new HashMap<>();
    static {
        ALLOWED_EXTENSIONS.put("VIDEO", Set.of(".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".webm"));
        ALLOWED_EXTENSIONS.put("AUDIO", Set.of(".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma"));
        ALLOWED_EXTENSIONS.put("DOCUMENT", Set.of(".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".txt"));
        ALLOWED_EXTENSIONS.put("SIMULATION", Set.of(".zip", ".rar", ".7z", ".html", ".unity3d"));
    }

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    // 资源类型映射
    private static final Map<String, String> RESOURCE_TYPE_MAP = new HashMap<>();
    static {
        RESOURCE_TYPE_MAP.put("SIMULATION", "虚拟仿真");
        RESOURCE_TYPE_MAP.put("VIDEO", "视频");
        RESOURCE_TYPE_MAP.put("AUDIO", "音频");
        RESOURCE_TYPE_MAP.put("DOCUMENT", "文档");
    }

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put(0, "待审核");
        STATUS_MAP.put(1, "已发布");
        STATUS_MAP.put(2, "已下架");
    }

    @Override
    public PageResult<ResourceVO> getResourcePage(ResourceQueryDTO queryDTO) {
        Page<Resource> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 判断是否为访客（未登录或GUEST角色），访客只能查看共享资源
        Boolean onlyShared = isGuest();

        IPage<Resource> resourcePage = resourceMapper.selectResourcePage(
                page,
                queryDTO.getResourceType(),
                queryDTO.getCategory(),
                queryDTO.getKeyword(),
                queryDTO.getStatus(),
                onlyShared
        );

        List<ResourceVO> voList = resourcePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.from(resourcePage).convert(voList);
    }

    @Override
    public ResourceDetailVO getResourceById(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException("资源不存在");
        }

        // 访客（未登录或GUEST角色）访问非共享资源时拒绝访问
        if (isGuest() && resource.getIsShared() != 1) {
            throw new BusinessException("该资源不对访客开放，请使用其他账户登录后访问");
        }

        ResourceDetailVO detailVO = new ResourceDetailVO();
        BeanUtil.copyProperties(resource, detailVO);

        // 设置类型名称和状态名称
        detailVO.setResourceTypeName(RESOURCE_TYPE_MAP.getOrDefault(resource.getResourceType(), "未知"));
        detailVO.setStatusName(STATUS_MAP.getOrDefault(resource.getStatus(), "未知"));
        detailVO.setFileSizeFormat(formatFileSize(resource.getFileSize()));

        // 设置上传者名称
        if (resource.getUploaderId() != null) {
            User uploader = userMapper.selectById(resource.getUploaderId());
            if (uploader != null) {
                detailVO.setUploaderName(uploader.getRealName());
            }
        }

        // 根据资源类型加载详情
        switch (resource.getResourceType()) {
            case "SIMULATION":
                loadSimulationDetail(detailVO, id);
                break;
            case "VIDEO":
                loadVideoDetail(detailVO, id);
                break;
            case "AUDIO":
                loadAudioDetail(detailVO, id);
                break;
            case "DOCUMENT":
                loadDocumentDetail(detailVO, id);
                break;
        }

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createResource(ResourceCreateDTO createDTO, Long userId) {
        // 创建主资源
        Resource resource = new Resource();
        resource.setResourceName(createDTO.getResourceName());
        resource.setResourceType(createDTO.getResourceType());
        resource.setDescription(createDTO.getDescription());
        resource.setCoverImage(createDTO.getCoverImage());
        resource.setCategory(createDTO.getCategory());
        resource.setTags(createDTO.getTags());
        resource.setFileSize(createDTO.getFileSize());
        resource.setUploaderId(userId);
        resource.setDownloadCount(0);
        resource.setViewCount(0);
        resource.setIsShared(createDTO.getIsShared() != null ? createDTO.getIsShared() : 0);
        resource.setStatus(0); // 默认待审核
        resource.setCreateBy(userId);
        resource.setUpdateBy(userId);

        resourceMapper.insert(resource);
        Long resourceId = resource.getId();

        // 根据类型创建子资源
        switch (createDTO.getResourceType()) {
            case "SIMULATION":
                createSimulationResource(resourceId, createDTO);
                break;
            case "VIDEO":
                createVideoResource(resourceId, createDTO);
                break;
            case "AUDIO":
                createAudioResource(resourceId, createDTO);
                break;
            case "DOCUMENT":
                createDocumentResource(resourceId, createDTO);
                break;
            default:
                throw new BusinessException("不支持的资源类型");
        }

        return resourceId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateResource(ResourceUpdateDTO updateDTO, Long userId) {
        Resource resource = resourceMapper.selectById(updateDTO.getId());
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException("资源不存在");
        }

        // 更新主资源
        if (StrUtil.isNotBlank(updateDTO.getResourceName())) {
            resource.setResourceName(updateDTO.getResourceName());
        }
        if (StrUtil.isNotBlank(updateDTO.getDescription())) {
            resource.setDescription(updateDTO.getDescription());
        }
        if (StrUtil.isNotBlank(updateDTO.getCoverImage())) {
            resource.setCoverImage(updateDTO.getCoverImage());
        }
        if (StrUtil.isNotBlank(updateDTO.getCategory())) {
            resource.setCategory(updateDTO.getCategory());
        }
        if (StrUtil.isNotBlank(updateDTO.getTags())) {
            resource.setTags(updateDTO.getTags());
        }
        if (updateDTO.getIsShared() != null) {
            resource.setIsShared(updateDTO.getIsShared());
        }
        if (updateDTO.getStatus() != null) {
            resource.setStatus(updateDTO.getStatus());
        }
        resource.setUpdateBy(userId);

        resourceMapper.updateById(resource);

        // 根据类型更新子资源
        switch (resource.getResourceType()) {
            case "SIMULATION":
                updateSimulationResource(updateDTO.getId(), updateDTO);
                break;
            case "VIDEO":
                updateVideoResource(updateDTO.getId(), updateDTO);
                break;
            case "AUDIO":
                updateAudioResource(updateDTO.getId(), updateDTO);
                break;
            case "DOCUMENT":
                updateDocumentResource(updateDTO.getId(), updateDTO);
                break;
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteResource(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return false;
        }
        return resourceMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteResource(Long[] ids) {
        return resourceMapper.deleteBatchIds(Arrays.asList(ids)) > 0;
    }

    @Override
    public boolean publishResource(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException("资源不存在");
        }
        resource.setStatus(1);
        return resourceMapper.updateById(resource) > 0;
    }

    @Override
    public boolean offlineResource(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException("资源不存在");
        }
        resource.setStatus(2);
        return resourceMapper.updateById(resource) > 0;
    }

    @Override
    public ResourceStatsVO getResourceStats() {
        // 判断是否为访客，访客只统计共享资源
        Boolean onlyShared = isGuest();

        ResourceStatsVO stats = new ResourceStatsVO();
        stats.setSimulationCount(resourceMapper.countByType("SIMULATION", onlyShared));
        stats.setVideoCount(resourceMapper.countByType("VIDEO", onlyShared));
        stats.setAudioCount(resourceMapper.countByType("AUDIO", onlyShared));
        stats.setDocumentCount(resourceMapper.countByType("DOCUMENT", onlyShared));
        stats.setTotalCount(stats.getSimulationCount() + stats.getVideoCount() +
                stats.getAudioCount() + stats.getDocumentCount());
        return stats;
    }

    @Override
    public List<ResourceVO> getHotResources(Integer limit) {
        // 判断是否为访客，访客只能查看共享资源
        Boolean onlyShared = isGuest();

        List<Resource> resources = resourceMapper.selectHotResources(limit != null ? limit : 10, onlyShared);
        return resources.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceVO> getLatestResources(Integer limit) {
        // 判断是否为访客，访客只能查看共享资源
        Boolean onlyShared = isGuest();

        List<Resource> resources = resourceMapper.selectLatestResources(limit != null ? limit : 10, onlyShared);
        return resources.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(Long id) {
        resourceMapper.incrementViewCount(id);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        resourceMapper.incrementDownloadCount(id);
    }

    @Override
    public boolean updateResourceShare(Long id, Integer isShared) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null || resource.getIsDeleted() == 1) {
            throw new BusinessException("资源不存在");
        }
        resource.setIsShared(isShared);
        return resourceMapper.updateById(resource) > 0;
    }

    @Override
    public String uploadResourceFile(MultipartFile file, String resourceType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        Set<String> allowedExtensions = ALLOWED_EXTENSIONS.get(resourceType);
        if (allowedExtensions == null || !allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件格式，该资源类型允许的格式为：" +
                    (allowedExtensions != null ? String.join(", ", allowedExtensions) : "无"));
        }

        // 从平台设置获取最大文件大小（默认500MB）
        long maxFileSizeMB = getMaxFileSize();
        long maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;

        if (file.getSize() > maxFileSizeBytes) {
            throw new BusinessException("文件大小不能超过" + maxFileSizeMB + "MB");
        }

        try {
            // 生成文件名
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 将相对路径转换为绝对路径，避免 Tomcat 临时目录问题
            Path basePath = Paths.get(uploadPath);
            if (!basePath.isAbsolute()) {
                basePath = Paths.get(System.getProperty("user.dir")).resolve(uploadPath).normalize();
            }

            // 按类型分目录存储
            String typePath = resourceFilePath.startsWith("/") ? resourceFilePath.substring(1) : resourceFilePath;
            Path dirPath = basePath.resolve(typePath).resolve(resourceType.toLowerCase());
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件 - 使用绝对路径
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toAbsolutePath().toFile());

            log.info("资源文件上传成功: {}", filePath);
            // 返回带前导斜杠的路径，确保前端代理能正确匹配
            return "/" + typePath + "/" + resourceType.toLowerCase() + "/" + filename;
        } catch (Exception e) {
            log.error("资源文件上传失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String uploadCoverImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的图片格式，允许的格式为：" + String.join(", ", IMAGE_EXTENSIONS));
        }

        // 验证文件大小 (最大5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        try {
            // 生成文件名
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 将相对路径转换为绝对路径，避免 Tomcat 临时目录问题
            Path basePath = Paths.get(uploadPath);
            if (!basePath.isAbsolute()) {
                basePath = Paths.get(System.getProperty("user.dir")).resolve(uploadPath).normalize();
            }

            // 创建目录
            String coverDir = coverPath.startsWith("/") ? coverPath.substring(1) : coverPath;
            Path dirPath = basePath.resolve(coverDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件 - 使用绝对路径
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toAbsolutePath().toFile());

            log.info("封面图片上传成功: {}", filePath);
            return coverPath + "/" + filename;
        } catch (Exception e) {
            log.error("封面图片上传失败", e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 从平台设置获取最大文件大小（MB）
     */
    private long getMaxFileSize() {
        try {
            Object maxSize = systemConfigService.getConfigValue("upload", "maxFileSize");
            if (maxSize instanceof Number) {
                return ((Number) maxSize).longValue();
            }
        } catch (Exception e) {
            log.warn("获取平台上传配置失败，使用默认值", e);
        }
        return 500; // 默认500MB
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    // ===== 私有辅助方法 =====

    private ResourceVO convertToVO(Resource resource) {
        ResourceVO vo = new ResourceVO();
        BeanUtil.copyProperties(resource, vo);
        vo.setResourceTypeName(RESOURCE_TYPE_MAP.getOrDefault(resource.getResourceType(), "未知"));
        vo.setStatusName(STATUS_MAP.getOrDefault(resource.getStatus(), "未知"));
        vo.setFileSizeFormat(formatFileSize(resource.getFileSize()));

        // 设置上传者名称
        if (resource.getUploaderId() != null) {
            User uploader = userMapper.selectById(resource.getUploaderId());
            if (uploader != null) {
                vo.setUploaderName(uploader.getRealName());
            }
        }
        return vo;
    }

    private String formatFileSize(Long size) {
        if (size == null || size <= 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return "00:00:00";
        }
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    private void loadSimulationDetail(ResourceDetailVO detailVO, Long resourceId) {
        ResourceSimulation simulation = simulationMapper.selectByResourceId(resourceId);
        if (simulation != null) {
            detailVO.setSimulationUrl(simulation.getSimulationUrl());
            detailVO.setTechnology(simulation.getTechnology());
            detailVO.setSupportPlatform(simulation.getSupportPlatform());
            detailVO.setMinRequirement(simulation.getMinRequirement());
        }
    }

    private void loadVideoDetail(ResourceDetailVO detailVO, Long resourceId) {
        ResourceVideo video = videoMapper.selectByResourceId(resourceId);
        if (video != null) {
            detailVO.setVideoUrl(video.getVideoUrl());
            detailVO.setDuration(video.getDuration());
            detailVO.setDurationFormat(formatDuration(video.getDuration()));
            detailVO.setResolution(video.getResolution());
            detailVO.setVideoFormat(video.getFormat());
            detailVO.setSubtitleUrl(video.getSubtitleUrl());
        }
    }

    private void loadAudioDetail(ResourceDetailVO detailVO, Long resourceId) {
        ResourceAudio audio = audioMapper.selectByResourceId(resourceId);
        if (audio != null) {
            detailVO.setAudioUrl(audio.getAudioUrl());
            detailVO.setAudioDuration(audio.getDuration());
            detailVO.setAudioDurationFormat(formatDuration(audio.getDuration()));
            detailVO.setAudioFormat(audio.getFormat());
            detailVO.setBitrate(audio.getBitrate());
        }
    }

    private void loadDocumentDetail(ResourceDetailVO detailVO, Long resourceId) {
        ResourceDocument document = documentMapper.selectByResourceId(resourceId);
        if (document != null) {
            detailVO.setDocumentUrl(document.getDocumentUrl());
            detailVO.setDocumentFormat(document.getFormat());
            detailVO.setPageCount(document.getPageCount());
            detailVO.setPreviewUrl(document.getPreviewUrl());
        }
    }

    private void createSimulationResource(Long resourceId, ResourceCreateDTO dto) {
        ResourceSimulation simulation = new ResourceSimulation();
        simulation.setResourceId(resourceId);
        simulation.setSimulationUrl(dto.getSimulationUrl());
        simulation.setTechnology(dto.getTechnology());
        simulation.setSupportPlatform(dto.getSupportPlatform());
        simulation.setMinRequirement(dto.getMinRequirement());
        simulationMapper.insert(simulation);
    }

    private void createVideoResource(Long resourceId, ResourceCreateDTO dto) {
        ResourceVideo video = new ResourceVideo();
        video.setResourceId(resourceId);
        video.setVideoUrl(dto.getVideoUrl());
        video.setDuration(dto.getDuration());
        video.setResolution(dto.getResolution());
        video.setFormat(dto.getVideoFormat());
        video.setSubtitleUrl(dto.getSubtitleUrl());
        videoMapper.insert(video);
    }

    private void createAudioResource(Long resourceId, ResourceCreateDTO dto) {
        ResourceAudio audio = new ResourceAudio();
        audio.setResourceId(resourceId);
        audio.setAudioUrl(dto.getAudioUrl());
        audio.setDuration(dto.getAudioDuration());
        audio.setFormat(dto.getAudioFormat());
        audio.setBitrate(dto.getBitrate());
        audioMapper.insert(audio);
    }

    private void createDocumentResource(Long resourceId, ResourceCreateDTO dto) {
        ResourceDocument document = new ResourceDocument();
        document.setResourceId(resourceId);
        document.setDocumentUrl(dto.getDocumentUrl());
        document.setFormat(dto.getDocumentFormat());
        document.setPageCount(dto.getPageCount());
        document.setPreviewUrl(dto.getPreviewUrl());
        documentMapper.insert(document);
    }

    private void updateSimulationResource(Long resourceId, ResourceUpdateDTO dto) {
        ResourceSimulation simulation = simulationMapper.selectByResourceId(resourceId);
        if (simulation != null) {
            if (StrUtil.isNotBlank(dto.getSimulationUrl())) {
                simulation.setSimulationUrl(dto.getSimulationUrl());
            }
            if (StrUtil.isNotBlank(dto.getTechnology())) {
                simulation.setTechnology(dto.getTechnology());
            }
            if (StrUtil.isNotBlank(dto.getSupportPlatform())) {
                simulation.setSupportPlatform(dto.getSupportPlatform());
            }
            if (StrUtil.isNotBlank(dto.getMinRequirement())) {
                simulation.setMinRequirement(dto.getMinRequirement());
            }
            simulationMapper.updateById(simulation);
        }
    }

    private void updateVideoResource(Long resourceId, ResourceUpdateDTO dto) {
        ResourceVideo video = videoMapper.selectByResourceId(resourceId);
        if (video != null) {
            if (StrUtil.isNotBlank(dto.getVideoUrl())) {
                video.setVideoUrl(dto.getVideoUrl());
            }
            if (dto.getDuration() != null) {
                video.setDuration(dto.getDuration());
            }
            if (StrUtil.isNotBlank(dto.getResolution())) {
                video.setResolution(dto.getResolution());
            }
            if (StrUtil.isNotBlank(dto.getVideoFormat())) {
                video.setFormat(dto.getVideoFormat());
            }
            if (StrUtil.isNotBlank(dto.getSubtitleUrl())) {
                video.setSubtitleUrl(dto.getSubtitleUrl());
            }
            videoMapper.updateById(video);
        }
    }

    private void updateAudioResource(Long resourceId, ResourceUpdateDTO dto) {
        ResourceAudio audio = audioMapper.selectByResourceId(resourceId);
        if (audio != null) {
            if (StrUtil.isNotBlank(dto.getAudioUrl())) {
                audio.setAudioUrl(dto.getAudioUrl());
            }
            if (dto.getAudioDuration() != null) {
                audio.setDuration(dto.getAudioDuration());
            }
            if (StrUtil.isNotBlank(dto.getAudioFormat())) {
                audio.setFormat(dto.getAudioFormat());
            }
            if (StrUtil.isNotBlank(dto.getBitrate())) {
                audio.setBitrate(dto.getBitrate());
            }
            audioMapper.updateById(audio);
        }
    }

    private void updateDocumentResource(Long resourceId, ResourceUpdateDTO dto) {
        ResourceDocument document = documentMapper.selectByResourceId(resourceId);
        if (document != null) {
            if (StrUtil.isNotBlank(dto.getDocumentUrl())) {
                document.setDocumentUrl(dto.getDocumentUrl());
            }
            if (StrUtil.isNotBlank(dto.getDocumentFormat())) {
                document.setFormat(dto.getDocumentFormat());
            }
            if (dto.getPageCount() != null) {
                document.setPageCount(dto.getPageCount());
            }
            if (StrUtil.isNotBlank(dto.getPreviewUrl())) {
                document.setPreviewUrl(dto.getPreviewUrl());
            }
            documentMapper.updateById(document);
        }
    }

    /**
     * 判断当前用户是否为访客（未登录或GUEST角色）
     * 访客只能访问共享资源
     *
     * @return 如果是访客返回 true，否则返回 false
     */
    private Boolean isGuest() {
        return SecurityUtils.isGuest();
    }
}
