package org.gigachat.askgigachat.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

// Для лолгирования контроллеров
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    // Этот метод вызывается ДО того, как запрос дойдет до контроллера
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Генерируем уникальный ID для каждого запроса (полезно для логирования)
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);

        // Логируем детали запроса
        logger.info("------------------------------");
        logger.info("НОВЫЙ ЗАПРОС [{}]", requestId);
        logger.info("Метод: {}", request.getMethod());
        logger.info("URL: {}", request.getRequestURL());
        logger.info("IP Клиента: {}", request.getRemoteAddr());
        logger.info("Время: {}", System.currentTimeMillis());

        // Возвращаем true, чтобы разрешить обработку запроса дальше.
        // Если вернуть false, цепочка прервется и контроллер не будет вызван.
        return true;
    }

    // Этот метод вызывается ПОСЛЕ того, как контроллер отработал, но ДО отрисовки View
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        String requestId = (String) request.getAttribute("requestId");
        logger.info("ЗАПРОС ЗАВЕРШЕН [{}]", requestId);
        logger.info("Статус ответа: {}", response.getStatus());
        logger.info("------------------------------");
    }
}
