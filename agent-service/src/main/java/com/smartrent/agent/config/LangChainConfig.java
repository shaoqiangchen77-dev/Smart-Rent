package com.smartrent.agent.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${langchain4j.open-ai.chat-model.api-key}") String apiKey,
            @Value("${langchain4j.open-ai.chat-model.model-name:qwen-plus}") String modelName,
            @Value("${langchain4j.open-ai.chat-model.base-url:https://dashscope.aliyuncs.com/api/v1}") String baseUrl) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .temperature(0.7f)
                .build();
    }
}
