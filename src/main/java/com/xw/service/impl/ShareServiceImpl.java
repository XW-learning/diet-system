package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.ShareSaveDTO;
import com.xw.entity.Share;
import com.xw.entity.ShareImage;
import com.xw.entity.User;
import com.xw.mapper.ShareImageMapper;
import com.xw.mapper.ShareMapper;
import com.xw.mapper.UserMapper;
import com.xw.service.ShareService;
import com.xw.vo.ShareVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private ShareImageMapper shareImageMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 🌟 开启事务
    public Result<String> saveShare(ShareSaveDTO dto) {
        if (dto.getUserId() == null) return Result.error("用户ID不能为空");

        LocalDateTime now = LocalDateTime.now();
        Share share = new Share();
        BeanUtils.copyProperties(dto, share);
        share.setCreateTime(now);

        // 🌟 初始化点赞评论数为0，默认状态设为1(审核通过)，方便现阶段你前端联调
        // 后期接了AI审核，这里要改成 0(待审核)
        if (share.getId() == null) {
            share.setLikeCount(0);
            share.setCommentCount(0);
            share.setAuditStatus(1);
            shareMapper.insert(share);
        } else {
            // 如果是修改，先删掉旧图片
            shareMapper.updateById(share);
            shareImageMapper.delete(new LambdaQueryWrapper<ShareImage>().eq(ShareImage::getShareId, share.getId()));
        }

        // 🌟 批量保存图片 (如果用户传了图片)
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (String url : dto.getImages()) {
                ShareImage img = new ShareImage();
                img.setShareId(share.getId()); // MyBatis-Plus 插入后会自动回填 ID
                img.setImageUrl(url);
                img.setCreateTime(now);
                shareImageMapper.insert(img);
            }
        }

        return Result.success("发布成功！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteShare(Long id, Long userId) {
        Share share = shareMapper.selectById(id);
        if (share == null) return Result.error("动态不存在");

        // 安全校验：只能删自己的
        if (!share.getUserId().equals(userId)) {
            return Result.error("无权删除他人的动态");
        }

        // 1. 删除关联图片
        shareImageMapper.delete(new LambdaQueryWrapper<ShareImage>().eq(ShareImage::getShareId, id));
        // 2. 删除主记录 (后期这里还可以加上删除该动态下的点赞和评论)
        shareMapper.deleteById(id);

        return Result.success("删除成功");
    }

    @Override
    public Result<List<ShareVO>> getShareList() {
        // 🌟 大厅查询条件：必须是 公开的(0) 且 审核通过的(1)
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getPrivacy, 0)
                .eq(Share::getAuditStatus, 1)
                .orderByDesc(Share::getCreateTime)
                .last("LIMIT 20"); // 暂时代替分页，防止数据太大撑爆内存

        List<Share> shares = shareMapper.selectList(wrapper);
        return Result.success(buildShareVOList(shares));
    }

    @Override
    public Result<List<ShareVO>> getMyShares(Long userId) {
        // 🌟 个人主页：无论公私、无论审核状态，只要是自己的都能看
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId)
                .orderByDesc(Share::getCreateTime);

        List<Share> shares = shareMapper.selectList(wrapper);
        return Result.success(buildShareVOList(shares));
    }

    /**
     * 架构师的私有方法：统一处理数据聚合装配，避免代码重复
     */
    private List<ShareVO> buildShareVOList(List<Share> shares) {
        List<ShareVO> voList = new ArrayList<>();
        if (shares == null || shares.isEmpty()) return voList;

        for (Share share : shares) {
            ShareVO vo = new ShareVO();
            BeanUtils.copyProperties(share, vo);

            // 1. 查用户信息(头像、昵称)
            User user = userMapper.selectById(share.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }

            // 2. 查配图列表
            LambdaQueryWrapper<ShareImage> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.eq(ShareImage::getShareId, share.getId()).orderByAsc(ShareImage::getCreateTime);
            List<ShareImage> images = shareImageMapper.selectList(imgWrapper);

            // 使用 Java8 Stream API 提取出纯 URL 列表
            List<String> urls = images.stream().map(ShareImage::getImageUrl).collect(Collectors.toList());
            vo.setImages(urls);

            voList.add(vo);
        }
        return voList;
    }
}