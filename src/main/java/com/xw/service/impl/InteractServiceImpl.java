package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xw.common.Result;
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

    @Autowired private ShareLikeMapper likeMapper;
    @Autowired private ShareCollectionMapper collectionMapper;
    @Autowired private CommentMapper commentMapper;
    @Autowired private ShareMapper shareMapper;
    @Autowired private MessageMapper messageMapper;

    // ================= 1. 点赞/取消点赞 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> toggleLike(InteractDTO dto) {
        // 查询是否已经点赞过
        ShareLike existLike = likeMapper.selectOne(new LambdaQueryWrapper<ShareLike>()
                .eq(ShareLike::getUserId, dto.getUserId())
                .eq(ShareLike::getShareId, dto.getShareId()));

        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId());

        if (existLike != null) {
            // 💡 场景 A：已点赞 -> 执行取消点赞
            likeMapper.deleteById(existLike.getId());

            // 🌟 架构师终极优化：处理 NULL 值，且保证数量绝不出现负数
            updateWrapper.setSql("like_count = GREATEST(IFNULL(like_count, 0) - 1, 0)");
            shareMapper.update(null, updateWrapper);

            return Result.success("已取消点赞");
        } else {
            // 💡 场景 B：未点赞 -> 执行点赞
            ShareLike newLike = new ShareLike();
            newLike.setUserId(dto.getUserId());
            newLike.setShareId(dto.getShareId());
            newLike.setCreateTime(LocalDateTime.now());
            likeMapper.insert(newLike);

            // 🌟 架构师终极优化：处理 NULL 值，安全 +1
            updateWrapper.setSql("like_count = IFNULL(like_count, 0) + 1");
            shareMapper.update(null, updateWrapper);

            // 核心：给动态作者发消息 (不能自己给自己发通知)
            if (!dto.getUserId().equals(dto.getAuthorId())) {
                sendMessage(dto.getAuthorId(), dto.getUserId(), 1, dto.getShareId(), null);
            }
            return Result.success("点赞成功");
        }
    }

    // ================= 2. 收藏/取消收藏 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> toggleCollect(InteractDTO dto) {
        ShareCollection existCollect = collectionMapper.selectOne(new LambdaQueryWrapper<ShareCollection>()
                .eq(ShareCollection::getUserId, dto.getUserId())
                .eq(ShareCollection::getShareId, dto.getShareId()));

        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId());

        if (existCollect != null) {
            collectionMapper.deleteById(existCollect.getId());

            // 防 NULL 且防负数兜底
            updateWrapper.setSql("collection_count = GREATEST(IFNULL(collection_count, 0) - 1, 0)");
            shareMapper.update(null, updateWrapper);

            return Result.success("已取消收藏");
        } else {
            ShareCollection newCollect = new ShareCollection();
            newCollect.setUserId(dto.getUserId());
            newCollect.setShareId(dto.getShareId());
            newCollect.setCreateTime(LocalDateTime.now());
            collectionMapper.insert(newCollect);

            // 防 NULL 兜底
            updateWrapper.setSql("collection_count = IFNULL(collection_count, 0) + 1");
            shareMapper.update(null, updateWrapper);

            if (!dto.getUserId().equals(dto.getAuthorId())) {
                sendMessage(dto.getAuthorId(), dto.getUserId(), 3, dto.getShareId(), null);
            }
            return Result.success("收藏成功");
        }
    }

    // ================= 3. 发布评论 =================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addComment(CommentSaveDTO dto) {
        // 1. 保存评论实体 (MyBatis-Plus 会自动生成雪花算法 ID)
        Comment comment = new Comment();
        BeanUtils.copyProperties(dto, comment);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        // 2. 动态的评论数 + 1 (防 NULL 兜底)
        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getShareId())
                .setSql("comment_count = IFNULL(comment_count, 0) + 1");
        shareMapper.update(null, updateWrapper);

        // 3. 发送消息通知
        // 逻辑：如果是二级评论(回复别人)，通知被回复的人；如果是一级评论，通知动态的作者
        Long receiverId = (dto.getReplyUserId() != null) ? dto.getReplyUserId() : dto.getAuthorId();

        if (!dto.getUserId().equals(receiverId)) {
            sendMessage(receiverId, dto.getUserId(), 2, dto.getShareId(), dto.getContent());
        }

        return Result.success("评论发布成功");
    }

    // ================= 4. 增加分享次数 =================
    @Override
    public Result<String> incrementShareCount(Long shareId) {
        UpdateWrapper<Share> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", shareId)
                .setSql("share_count = IFNULL(share_count, 0) + 1"); // 防 NULL 兜底
        shareMapper.update(null, updateWrapper);
        return Result.success("分享数更新成功");
    }

    // ================= 架构师私有方法：统一发送消息 =================
    private void sendMessage(Long receiverId, Long senderId, Integer type, Long sourceId, String content) {
        Message msg = new Message();
        msg.setReceiverId(receiverId);
        msg.setSenderId(senderId);
        msg.setType(type);
        msg.setSourceId(sourceId);
        msg.setContent(content);
        msg.setIsRead(0); // 默认为未读
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }


}