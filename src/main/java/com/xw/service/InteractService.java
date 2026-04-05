package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.CommentSaveDTO;
import com.xw.dto.InteractDTO;

/**
 * 用户互动服务接口
 * 提供点赞、收藏、评论等社交互动功能
 *
 * @author XW
 */
public interface InteractService {

    /**
     * 切换点赞状态（点赞/取消点赞）
     *
     * 业务逻辑：
     * 1. 如果用户已点赞，则取消点赞，分享点赞数-1
     * 2. 如果用户未点赞，则添加点赞记录，分享点赞数+1，并发送通知给作者
     * 3. 不会给自己发送点赞通知
     *
     * @param dto 互动请求参数，包含 userId, shareId, authorId
     * @return 操作结果提示
     */
    Result<String> toggleLike(InteractDTO dto);

    /**
     * 切换收藏状态（收藏/取消收藏）
     *
     * 业务逻辑：
     * 1. 如果用户已收藏，则取消收藏，分享收藏数-1
     * 2. 如果用户未收藏，则添加收藏记录，分享收藏数+1，并发送通知给作者
     * 3. 不会给自己发送收藏通知
     *
     * @param dto 互动请求参数，包含 userId, shareId, authorId
     * @return 操作结果提示
     */
    Result<String> toggleCollect(InteractDTO dto);

    /**
     * 发布评论
     *
     * 业务逻辑：
     * 1. 保存评论内容到数据库
     * 2. 分享评论数+1
     * 3. 发送通知：
     *    - 如果是一级评论，通知动态作者
     *    - 如果是二级评论（回复），通知被回复的用户
     * 4. 不会给自己发送评论通知
     *
     * @param dto 评论保存参数，包含 shareId, userId, content, replyUserId, authorId 等
     * @return 操作结果提示
     */
    Result<String> addComment(CommentSaveDTO dto);

    /**
     * 增加动态的分享/转发次数
     * @param shareId 动态ID
     */
    Result<String> incrementShareCount(Long shareId);
}
