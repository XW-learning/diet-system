package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.entity.Message;
import com.xw.entity.User;
import com.xw.exception.BusinessException;
import com.xw.mapper.MessageMapper;
import com.xw.mapper.UserMapper;
import com.xw.service.MessageService;
import com.xw.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XW
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }

    @Override
    public List<MessageVO> getMessageList(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .orderByDesc(Message::getCreateTime);
        List<Message> messages = messageMapper.selectList(wrapper);

        List<MessageVO> voList = new ArrayList<>();
        for (Message msg : messages) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(msg, vo);

            if (msg.getSenderId() == null || msg.getSenderId() == 0L) {
                vo.setSenderName("系统管家");
                vo.setSenderAvatar("https://example.com/system-avatar.png");
            } else {
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
        return voList;
    }

    @Override
    public String readMessage(Long id, Long userId) {
        Message msg = messageMapper.selectById(id);
        if (msg == null) throw new BusinessException("消息不存在");

        if (!msg.getReceiverId().equals(userId)) {
            throw new BusinessException("非法操作！无权修改他人消息状态");
        }

        msg.setIsRead(1);
        messageMapper.updateById(msg);

        return "已读状态更新成功";
    }
}
