package org.gigachat.askgigachat.handlers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Для обработки ошибок контроллеров
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("Произошла ошибка при обработке запроса", ex);

        // Сообщение для вывода пользователю
        model.addAttribute("error", "Произошла внутренняя ошибка сервера. Попробуйте позже.\n" + ex.getMessage());
        model.addAttribute("response", null); // Очищаем старый ответ
        return "index";
    }
}
