package com.smartrent.agent.controller;

import com.smartrent.agent.entity.Dialog;
import com.smartrent.agent.service.AgentService;
import com.smartrent.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 普通对话（非流式）
     */
    @PostMapping("/chat")
    public R<Map<String, Object>> chat(@RequestHeader("X-User-Id") Long userId,
                                       @RequestBody Map<String, String> body) {
        String message = body.get("message");
        String sessionId = body.get("sessionId");
        String reply = agentService.chat(userId, sessionId, message);
        return R.ok(Map.of("reply", reply, "sessionId", sessionId != null ? sessionId : ""));
    }

    /**
     * 流式对话（SSE）
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestHeader("X-User-Id") Long userId,
                                 @RequestBody Map<String, String> body) {
        SseEmitter emitter = new SseEmitter(60000L);
        String message = body.get("message");
        String sessionId = body.get("sessionId");

        executor.execute(() -> {
            try {
                String reply = agentService.chat(userId, sessionId, message);

                // 模拟流式输出（逐字发送）
                for (int i = 0; i < reply.length(); i++) {
                    emitter.send(SseEmitter.event()
                            .data(Map.of("content", String.valueOf(reply.charAt(i)),
                                    "sessionId", sessionId != null ? sessionId : "")));
                    Thread.sleep(20);
                }

                emitter.send(SseEmitter.event().data(Map.of("done", true)));
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    public R<List<Dialog>> history(@RequestParam String sessionId) {
        return R.ok(agentService.getHistory(sessionId));
    }

    /**
     * 获取用户会话列表
     */
    @GetMapping("/sessions")
    public R<List<String>> sessions(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(agentService.getUserSessions(userId));
    }
}
