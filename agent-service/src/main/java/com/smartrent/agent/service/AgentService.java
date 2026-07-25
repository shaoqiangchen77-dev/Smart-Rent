package com.smartrent.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.agent.entity.Dialog;
import com.smartrent.agent.mapper.DialogMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final ChatLanguageModel chatLanguageModel;
    private final DialogMapper dialogMapper;

    private static final String SYSTEM_PROMPT = """
            你是智能租房助手，专门帮助用户找房、了解房源信息。
            你的能力包括：
            1. 根据用户需求推荐合适的房源
            2. 解答租房相关问题（合同、押金、看房流程等）
            3. 分析房源优缺点
            4. 提供租房建议和注意事项

            请用友好、专业的语气回答用户问题。如果用户询问的不是租房相关问题，请礼貌地引导回租房话题。
            """;

    /**
     * 普通对话（非流式）
     */
    public String chat(Long userId, String sessionId, String message) {
        if (!StringUtils.hasText(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }

        // 保存用户消息
        saveDialog(userId, sessionId, "user", message);

        // 构建对话历史
        List<ChatMessage> messages = buildMessages(userId, sessionId);

        // 调用LLM
        long start = System.currentTimeMillis();
        ChatResponse response = chatLanguageModel.chat(ChatRequest.builder()
                .messages(messages)
                .build());
        long duration = System.currentTimeMillis() - start;

        String reply = response.aiMessage().text();

        // 保存AI回复
        Dialog aiDialog = saveDialog(userId, sessionId, "assistant", reply);
        aiDialog.setDuration((int) duration);
        aiDialog.setTokenCount(response.tokenUsage() != null ?
                response.tokenUsage().totalTokenCount() : null);
        dialogMapper.updateById(aiDialog);

        return reply;
    }

    /**
     * 获取对话历史
     */
    public List<Dialog> getHistory(String sessionId) {
        return dialogMapper.selectList(
                new LambdaQueryWrapper<Dialog>()
                        .eq(Dialog::getSessionId, sessionId)
                        .orderByAsc(Dialog::getCreateTime));
    }

    /**
     * 获取用户会话列表
     */
    public List<String> getUserSessions(Long userId) {
        List<Dialog> dialogs = dialogMapper.selectList(
                new LambdaQueryWrapper<Dialog>()
                        .eq(Dialog::getUserId, userId)
                        .select(Dialog::getSessionId)
                        .groupBy(Dialog::getSessionId)
                        .orderByDesc(Dialog::getCreateTime));
        return dialogs.stream().map(Dialog::getSessionId).toList();
    }

    private List<ChatMessage> buildMessages(Long userId, String sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));

        // 加载最近20条对话
        List<Dialog> history = dialogMapper.selectList(
                new LambdaQueryWrapper<Dialog>()
                        .eq(Dialog::getSessionId, sessionId)
                        .orderByDesc(Dialog::getCreateTime)
                        .last("LIMIT 20"));

        for (int i = history.size() - 1; i >= 0; i--) {
            Dialog d = history.get(i);
            if ("user".equals(d.getRole())) {
                messages.add(new UserMessage(d.getContent()));
            } else if ("assistant".equals(d.getRole())) {
                messages.add(new AiMessage(d.getContent()));
            }
        }

        return messages;
    }

    private Dialog saveDialog(Long userId, String sessionId, String role, String content) {
        Dialog dialog = new Dialog();
        dialog.setUserId(userId);
        dialog.setSessionId(sessionId);
        dialog.setRole(role);
        dialog.setContent(content);
        dialog.setCreateTime(LocalDateTime.now());
        dialogMapper.insert(dialog);
        return dialog;
    }
}
