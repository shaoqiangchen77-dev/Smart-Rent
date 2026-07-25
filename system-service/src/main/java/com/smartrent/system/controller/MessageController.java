package com.smartrent.system.controller;

import com.smartrent.common.result.R;
import com.smartrent.system.entity.Message;
import com.smartrent.system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/list")
    public R<List<Message>> list(@RequestHeader("X-User-Id") Long userId,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "20") Integer size) {
        return R.ok(messageService.getUserMessages(userId, page, size));
    }

    @GetMapping("/unread-count")
    public R<Long> unreadCount(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(messageService.getUnreadCount(userId));
    }

    @PostMapping("/read/{id}")
    public R<Void> markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        messageService.markAsRead(userId, id);
        return R.ok();
    }

    @PostMapping("/read-all")
    public R<Void> markAllRead(@RequestHeader("X-User-Id") Long userId) {
        messageService.markAllAsRead(userId);
        return R.ok();
    }

    @PostMapping("/send")
    public R<Void> send(@RequestBody Map<String, Object> body) {
        Long receiverId = body.get("receiverId") != null ? Long.valueOf(body.get("receiverId").toString()) : null;
        String msgType = (String) body.get("msgType");
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        if (receiverId != null) {
            messageService.sendMessage(null, receiverId, msgType, title, content, null, null);
        } else {
            messageService.sendSystemMessage(null, title, content);
        }
        return R.ok();
    }
}
