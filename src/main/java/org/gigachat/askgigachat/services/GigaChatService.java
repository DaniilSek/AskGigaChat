package org.gigachat.askgigachat.services;

import chat.giga.client.GigaChatClient;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GigaChatService {

    private final GigaChatClient gigaChatClient;

    @Autowired
    public GigaChatService(GigaChatClient gigaChatClient) {
        this.gigaChatClient = gigaChatClient;
    }

    public String ask(String prompt) {
            // 1. Отправляем запрос, ожидаем CompletionsResponse
            CompletionResponse response = gigaChatClient.completions(buildRequest(prompt));

            // 2. Передаем ответ в отдельный обработчик
            return extractResponseText(response);
    }

    // --- Метод-обработчик ответа ---
    private String extractResponseText(CompletionResponse response) {
        //Проверка, что ответ и список вариантов не пусты
        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            // Извлекаем текст из первого варианта ответа
            return response.choices().getFirst().message().content();
        } else {
            return "Модель не смогла сгенерировать ответ.";
        }
    }

    // --- Вспомогательный метод для построения запроса ---
    private CompletionRequest buildRequest(String prompt) {
        return CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(ChatMessage.builder()
                        .content(prompt)
                        .role(ChatMessageRole.USER)
                        .build())
                .build();
    }
}