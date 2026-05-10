package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.ShareSaveDTO;
import com.xw.entity.Share;
import com.xw.entity.ShareImage;
import com.xw.entity.User;
import com.xw.exception.BusinessException;
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
    @Transactional(rollbackFor = Exception.class)
    public String saveShare(ShareSaveDTO dto) {
        if (dto.getUserId() == null) throw new BusinessException("用户ID不能为空");

        LocalDateTime now = LocalDateTime.now();
        Share share = new Share();
        BeanUtils.copyProperties(dto, share);
        share.setCreateTime(now);

        if (share.getId() == null) {
            share.setLikeCount(0);
            share.setCommentCount(0);
            share.setAuditStatus(1);
            shareMapper.insert(share);
        } else {
            shareMapper.updateById(share);
            shareImageMapper.delete(new LambdaQueryWrapper<ShareImage>().eq(ShareImage::getShareId, share.getId()));
        }

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (String url : dto.getImages()) {
                ShareImage img = new ShareImage();
                img.setShareId(share.getId());
                img.setImageUrl(url);
                img.setCreateTime(now);
                shareImageMapper.insert(img);
            }
        }

        return "发布成功！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteShare(Long id, Long userId) {
        Share share = shareMapper.selectById(id);
        if (share == null) throw new BusinessException("动态不存在");

        if (!share.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人的动态");
        }

        shareImageMapper.delete(new LambdaQueryWrapper<ShareImage>().eq(ShareImage::getShareId, id));
        shareMapper.deleteById(id);

        return "删除成功";
    }

    @Override
    public List<ShareVO> getShareList() {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getPrivacy, 0)
                .eq(Share::getAuditStatus, 1)
                .orderByDesc(Share::getCreateTime)
                .last("LIMIT 20");

        List<Share> shares = shareMapper.selectList(wrapper);
        return buildShareVOList(shares);
    }

    @Override
    public List<ShareVO> getMyShares(Long userId) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId)
                .orderByDesc(Share::getCreateTime);

        List<Share> shares = shareMapper.selectList(wrapper);
        return buildShareVOList(shares);
    }

    private List<ShareVO> buildShareVOList(List<Share> shares) {
        List<ShareVO> voList = new ArrayList<>();
        if (shares == null || shares.isEmpty()) return voList;

        for (Share share : shares) {
            ShareVO vo = new ShareVO();
            BeanUtils.copyProperties(share, vo);

            User user = userMapper.selectById(share.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }

            LambdaQueryWrapper<ShareImage> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.eq(ShareImage::getShareId, share.getId()).orderByAsc(ShareImage::getCreateTime);
            List<ShareImage> images = shareImageMapper.selectList(imgWrapper);

            List<String> urls = images.stream().map(ShareImage::getImageUrl).collect(Collectors.toList());
            vo.setImages(urls);

            voList.add(vo);
        }
        return voList;
    }
}
