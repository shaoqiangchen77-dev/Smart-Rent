package com.smartrent.system.service;

import com.smartrent.system.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * 发送消息
     */
    void sendMessage(Long senderId, Long receiverId, String msgType, String title, String content,
                     String bizType, Long bizId);

    /**
     * 发送系统消息
     */
    void sendSystemMessage(Long receiverId, String title, String content);

    /**
     * 获取用户消息列表
     */
    List<Message> getUserMessages(Long userId, Integer page, Integer size);

    /**
     * 未读消息数
     */
    long getUnreadCount(Long userId);

    /**
     * 标记已读
     */
    void markAsRead(Long userId, Long messageId);

    /**
     * 全部已读
     */
    void markAllAsRead(Long userId);
}
