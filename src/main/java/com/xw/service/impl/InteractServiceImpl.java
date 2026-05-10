package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xw.dto.CommentSaveDTO;
import com.xw.dto.InteractDTO;
import com.xw.entity.*;
import com.xw.mapper.*;
import com.xw.service.InteractService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Service
public class InteractServiceImpl implements InteractService {

    @Autowired
    private ShareLikeMapper likeMapper;
    @Autowired
    private ShareCollectionMapper collectionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String toggleLike(Long userId, InteractDTO dto) {
        ShareLike existLike = likeMapper.selectOne(new LambdaQueryWrapper<ShareLike>()
                .eq(ShareLike::getUserId, userId)
                .eq(ShareLike::getShareId, dto.getShareId()));

        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId());

        if (existLike != null) {
            likeMapper.deleteById(existLike.getId());

            updateWrapper.setSql("like_count = GREATEST(IFNULL(like_count, 0) - 1, 0)");
            shareMapper.update(null, updateWrapper);

            return "已取消点赞";
        } else {
            ShareLike newLike = new ShareLike();
            newLike.setUserId(userId);
            newLike.setShareId(dto.getShareId());
            newLike.setCreateTime(LocalDateTime.now());
            likeMapper.insert(newLike);

            updateWrapper.setSql("like_count = IFNULL(like_count, 0) + 1");
            shareMapper.update(null, updateWrapper);

            if (!userId.equals(dto.getAuthorId())) {
                sendMessage(dto.getAuthorId(), userId, 1, dto.getShareId(), null);
            }
            return "点赞成功";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String toggleCollect(Long userId, InteractDTO dto) {
        ShareCollection existCollect = collectionMapper.selectOne(new LambdaQueryWrapper<ShareCollection>()
                .eq(ShareCollection::getUserId, userId)
                .eq(ShareCollection::getShareId, dto.getShareId()));

        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId());

        if (existCollect != null) {
            collectionMapper.deleteById(existCollect.getId());

            updateWrapper.setSql("collection_count = GREATEST(IFNULL(collection_count, 0) - 1, 0)");
            shareMapper.update(null, updateWrapper);

            return "已取消收藏";
        } else {
            ShareCollection newCollect = new ShareCollection();
            newCollect.setUserId(userId);
            newCollect.setShareId(dto.getShareId());
            newCollect.setCreateTime(LocalDateTime.now());
            collectionMapper.insert(newCollect);

            updateWrapper.setSql("collection_count = IFNULL(collection_count, 0) + 1");
            shareMapper.update(null, updateWrapper);

            if (!userId.equals(dto.getAuthorId())) {
                sendMessage(dto.getAuthorId(), userId, 3, dto.getShareId(), null);
            }
            return "收藏成功";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addComment(Long userId, CommentSaveDTO dto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(dto, comment);

        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId())
                .setSql("comment_count = IFNULL(comment_count, 0) + 1");
        shareMapper.update(null, updateWrapper);

        Long receiverId = (dto.getReplyUserId() != null) ? dto.getReplyUserId() : dto.getAuthorId();

        if (!userId.equals(receiverId)) {
            sendMessage(receiverId, userId, 2, dto.getShareId(), dto.getContent());
        }

        return "评论发布成功";
    }

    @Override
    public String incrementShareCount(Long shareId) {
        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shareId)
                .setSql("share_count = IFNULL(share_count, 0) + 1");
        shareMapper.update(null, updateWrapper);
        return "分享数更新成功";
    }

    private void sendMessage(Long receiverId, Long senderId, Integer type, Long sourceId, String content) {
        Message msg = new Message();
        msg.setReceiverId(receiverId);
        msg.setSenderId(senderId);
        msg.setType(type);
        msg.setSourceId(sourceId);
        msg.setContent(content);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }
}
