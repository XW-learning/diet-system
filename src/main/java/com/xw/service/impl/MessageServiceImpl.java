package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.entity.Message;
import com.xw.entity.User;
import com.xw.mapper.MessageMapper;
import com.xw.mapper.UserMapper;
import com.xw.service.MessageService;
import com.xw.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<Long> getUnreadCount(Long userId) {
        // 利用我们在 SQL 里建的 idx_receiver_read 复合索引，这里的 COUNT 查询极快
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, 0); // 0 为未读
        Long count = messageMapper.selectCount(wrapper);
        return Result.success(count);
    }

    @Override
    public Result<List<MessageVO>> getMessageList(Long userId) {
        // 查询当前用户的所有消息，按时间倒序排列
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .orderByDesc(Message::getCreateTime);
        List<Message> messages = messageMapper.selectList(wrapper);

        // 组装 VO (聚合发送者信息)
        List<MessageVO> voList = new ArrayList<>();
        for (Message msg : messages) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(msg, vo);

            // 如果是系统通知(senderId为0或null)，给予默认头像和名字
            if (msg.getSenderId() == null || msg.getSenderId() == 0L) {
                vo.setSenderName("系统管家");
                vo.setSenderAvatar("https://example.com/system-avatar.png"); // 替换为你的默认系统头像URL
            } else {
                // 去数据库查发送者的真实信息
                User sender = userMapper.selectById(msg.getSenderId());
                if (sender != null) {
                    vo.setSenderName(sender.getUsername());
                    vo.setSenderAvatar(sender.getAvatar());
                } else {
                    vo.setSenderName("已注销用户");
                }
            }
            voList.add(vo);
        }
        return Result.success(voList);
    }

    @Override
    public Result<String> readMessage(Long id, Long userId) {
        Message msg = messageMapper.selectById(id);
        if (msg == null) return Result.error("消息不存在");

        // 🛡️ 水平越权校验：只能标记自己的消息为已读
        if (!msg.getReceiverId().equals(userId)) {
            return Result.error("非法操作！无权修改他人消息状态");
        }

        // 更新状态为已读
        msg.setIsRead(1);
        messageMapper.updateById(msg);

        return Result.success("已读状态更新成功");
    }
}