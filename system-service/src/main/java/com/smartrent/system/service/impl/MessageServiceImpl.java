package com.smartrent.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartrent.system.entity.Message;
import com.smartrent.system.mapper.MessageMapper;
import com.smartrent.system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public void sendMessage(Long senderId, Long receiverId, String msgType, String title,
                            String content, String bizType, Long bizId) {
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setMsgType(msgType);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setBizType(bizType);
        msg.setBizId(bizId);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
    }

    @Override
    public void sendSystemMessage(Long receiverId, String title, String content) {
        sendMessage(null, receiverId, "system", title, content, null, null);
    }

    @Override
    public List<Message> getUserMessages(Long userId, Integer page, Integer size) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .orderByDesc(Message::getCreateTime)
                        .last("LIMIT " + size + " OFFSET " + (page - 1) * size));
    }

    @Override
    public long getUnreadCount(Long userId) {
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0));
    }

    @Override
    public void markAsRead(Long userId, Long messageId) {
        messageMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getId, messageId)
                        .eq(Message::getReceiverId, userId)
                        .set(Message::getIsRead, 1));
    }

    @Override
    public void markAllAsRead(Long userId) {
        messageMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0)
                        .set(Message::getIsRead, 1));
    }
}
