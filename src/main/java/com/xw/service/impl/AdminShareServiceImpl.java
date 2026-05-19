package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xw.common.PageResult;
import com.xw.dto.AdminShareQueryDTO;
import com.xw.entity.*;
import com.xw.exception.BusinessException;
import com.xw.mapper.*;
import com.xw.service.AdminShareService;
import com.xw.vo.AdminCommentVO;
import com.xw.vo.AdminShareVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminShareServiceImpl implements AdminShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private ShareImageMapper shareImageMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<AdminShareVO> getShareList(AdminShareQueryDTO queryDTO) {
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Share::getContent, queryDTO.getKeyword())
                    .or()
                    .inSql(Share::getUserId,
                            "SELECT id FROM t_user WHERE username LIKE '%" + queryDTO.getKeyword().replace("'", "''") + "%'"));
        }
        if (queryDTO.getAuditStatus() != null) {
            wrapper.eq(Share::getAuditStatus, queryDTO.getAuditStatus());
        }
        if (queryDTO.getUserId() != null) {
            wrapper.eq(Share::getUserId, queryDTO.getUserId());
        }
        wrapper.orderByDesc(Share::getCreateTime);

        Page<Share> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Share> sharePage = shareMapper.selectPage(page, wrapper);

        List<AdminShareVO> voList = sharePage.getRecords().stream().map(share -> {
            AdminShareVO vo = new AdminShareVO();
            BeanUtils.copyProperties(share, vo);

            User user = userMapper.selectById(share.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
                vo.setPhone(user.getPhone());
            }

            LambdaQueryWrapper<ShareImage> imgWrapper = new LambdaQueryWrapper<>();
            imgWrapper.eq(ShareImage::getShareId, share.getId()).orderByAsc(ShareImage::getCreateTime);
            List<ShareImage> images = shareImageMapper.selectList(imgWrapper);
            vo.setImages(images.stream().map(ShareImage::getImageUrl).collect(Collectors.toList()));

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, sharePage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AdminShareVO getShareDetail(Long shareId) {
        if (shareId == null) throw new BusinessException("动态ID不能为空");
        Share share = shareMapper.selectById(shareId);
        if (share == null) throw new BusinessException("动态不存在");

        AdminShareVO vo = new AdminShareVO();
        BeanUtils.copyProperties(share, vo);

        User user = userMapper.selectById(share.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            vo.setPhone(user.getPhone());
        }

        LambdaQueryWrapper<ShareImage> imgWrapper = new LambdaQueryWrapper<>();
        imgWrapper.eq(ShareImage::getShareId, shareId).orderByAsc(ShareImage::getCreateTime);
        List<ShareImage> images = shareImageMapper.selectList(imgWrapper);
        vo.setImages(images.stream().map(ShareImage::getImageUrl).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditShare(Long shareId, Integer auditStatus, String reason) {
        if (shareId == null) throw new BusinessException("动态ID不能为空");
        if (auditStatus == null || (auditStatus != 1 && auditStatus != 2))
            throw new BusinessException("审核状态参数错误");

        Share share = shareMapper.selectById(shareId);
        if (share == null) throw new BusinessException("动态不存在");

        Share updateShare = new Share();
        updateShare.setId(shareId);
        updateShare.setAuditStatus(auditStatus);
        shareMapper.updateById(updateShare);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteShare(Long shareId) {
        if (shareId == null) throw new BusinessException("动态ID不能为空");

        Share share = shareMapper.selectById(shareId);
        if (share == null) throw new BusinessException("动态不存在");

        shareImageMapper.delete(new LambdaQueryWrapper<ShareImage>().eq(ShareImage::getShareId, shareId));
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getShareId, shareId));
        shareMapper.deleteById(shareId);
    }

    @Override
    public List<AdminCommentVO> getShareComments(Long shareId) {
        if (shareId == null) throw new BusinessException("动态ID不能为空");

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getShareId, shareId).orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(wrapper);

        List<AdminCommentVO> voList = new ArrayList<>();
        for (Comment comment : comments) {
            AdminCommentVO vo = new AdminCommentVO();
            BeanUtils.copyProperties(comment, vo);

            User commentUser = userMapper.selectById(comment.getUserId());
            if (commentUser != null) {
                vo.setUsername(commentUser.getUsername());
                vo.setAvatar(commentUser.getAvatar());
            }
            if (comment.getReplyUserId() != null) {
                User replyUser = userMapper.selectById(comment.getReplyUserId());
                if (replyUser != null) vo.setReplyUsername(replyUser.getUsername());
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        if (commentId == null) throw new BusinessException("评论ID不能为空");
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BusinessException("评论不存在");
        commentMapper.deleteById(commentId);
    }
}
