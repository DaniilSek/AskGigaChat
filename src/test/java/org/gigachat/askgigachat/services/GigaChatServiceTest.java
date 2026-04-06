package org.gigachat.askgigachat.services;

import chat.giga.model.completion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import chat.giga.client.GigaChatClient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GigaChatServiceTest {

    // 1. Создаем мок для клиента
    @Mock
    private GigaChatClient gigaChatClientMock;

    // 2. Создаем экземпляр сервиса для внедрения мока
    private GigaChatService gigaChatService;

    private CompletionResponse mockResponse;

    // Запуск перед каждым тестом, подготовка "поддельных" данных
    @BeforeEach
    void setUp() {
        // Подготавливаем мок CompletionResponse
        mockResponse = CompletionResponse.builder()
                .model("GIGA_CHAT")
                .choices(List.of(Choice.builder()
                                .message(ChoiceMessage.builder()
                                        .content("Тестовый ответ от GigaChat")
                                        .build())
                                .index(0)
                        .build()))
                .build();

        gigaChatService = new GigaChatService(gigaChatClientMock);

    }

    // Тест успешного ответа
    @Test
    void testAsk_WhenSuccessfulResponse_ShouldReturnContent() {
        // Arrange
        String userPrompt = "Привет, как дела?";

        // Когда клиент вызывается с любым CompletionRequest, он должен вернуть наш mockResponse.
        when(gigaChatClientMock.completions(any(CompletionRequest.class))).thenReturn(mockResponse);

        // Act (Действие)
        String actualResponse = gigaChatService.ask(userPrompt);

        // Assert (Проверка)
        assertEquals("Тестовый ответ от GigaChat", actualResponse);
    }

    // Тест для случая, когда API возвращает пустой ответ
    @Test
    void testAsk_WhenResponseIsEmpty_ShouldReturnFallbackMessage() {

        // Arrange
        CompletionResponse emptyResponse = CompletionResponse.builder().build();
        when(gigaChatClientMock.completions(any(CompletionRequest.class))).thenReturn(emptyResponse);

        // Act & Assert
        String actualResponse = gigaChatService.ask("Жду пустой овтет");

        // Проверяем, что сработала ваша логика обработки пустого ответа
        assertEquals("Модель не смогла сгенерировать ответ.", actualResponse);
    }
}
