# AskGigaChat
Test API GigaChat
```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── DemoApplication.java
│   │       ├── GigaChatController.java
│   │       └── GigaChatService.java
│   └── resources/
│       ├── templates/
│       │   └── index.html
│       └── application.properties
pom.xml
```
**pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>gigachat-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>gigachat-demo</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
                  <groupId>chat.giga</groupId>
                  <artifactId>gigachat-java</artifactId>
                  <version>0.1.9</version>
         </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
**DemoApplication.java**
```Java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
**GigaChatController.java**
```Java
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GigaChatController {

    @Autowired
    private GigaChatService gigaChatService;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/ask")
    public String askGigaChat(@RequestParam("query") String query, Model model) {
        if (query == null || query.isBlank()) {
            model.addAttribute("error", "Запрос не может быть пустым.");
            return "index";
        }

        try {
            String response = gigaChatService.ask(query);
            model.addAttribute("query", query);
            model.addAttribute("response", response);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обращении к GigaChat: " + e.getMessage());
        }
        return "index";
    }
}
```
**GigaChatService.java**
```Java
package com.application.services;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GigaChatService {

    private final GigaChatClient gigaChatClient;

    public GigaChatService(
            @Value("${gigachat.client-id}") String clientId,
            @Value("${gigachat.client-secret}") String clientSecret,
            @Value("${gigachat.verify-ssl:true}") boolean verifySsl) {

        this.gigaChatClient = GigaChatClient.builder()
                .verifySslCerts(verifySsl)
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .clientId(clientId)
                                .clientSecret(clientSecret)
                                .build())
                        .build())
                .build();
    }

    public String ask(String prompt) {
        try {
            return gigaChatClient.completions(CompletionRequest.builder()
                    .model(ModelName.GIGA_CHAT)
                    .message(ChatMessage.builder()
                            .content(prompt)
                            .build()
                    )
                    .build()
            ).toString();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обращении к GigaChat: " + e.getMessage(), e);
        }
    }
}
```
**index.html**
```html
<!DOCTYPE html>
<html lang="ru" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8"/>
    <title>GigaChat Интерфейс</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background-color: #f5f5f5;
        }
        h1 {
            color: #2c3e50;
        }
        form {
            margin: 20px 0;
        }
        textarea {
            width: 100%;
            height: 100px;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            background-color: #3498db;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #2980b9;
        }
        .result {
            margin-top: 20px;
            padding: 15px;
            background-color: #e8f4f8;
            border: 1px solid #cce0ff;
            border-radius: 4px;
        }
        .error {
            color: red;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h1>💬 GigaChat Интерфейс</h1>

    <form method="post" action="/ask">
        <label for="query">Введите ваш запрос:</label><br/>
        <textarea id="query" name="query" placeholder="Например: Расскажи о птицах"></textarea><br/>
        <button type="submit">Отправить</button>
    </form>

    <div th:if="${error}" class="error" th:text="${error}"></div>

    <div th:if="${response}" class="result">
        <strong>Вы:</strong><br/>
        <p th:text="${query}"></p>
        <strong>GigaChat:</strong><br/>
        <p th:text="${response}"></p>
    </div>
</body>
</html>
```
**application.properties**
```properties
# GigaChat API credentials
gigachat.client-id=YOUR_CLIENT_ID_HERE
gigachat.client-secret=YOUR_CLIENT_SECRET_HERE
gigachat.verify-ssl=false

# GigaChat API URLs
gigachat.token-url=https://ngw.devices.sberbank.ru:9443/api/v2/oauth
gigachat.api-url=https://gigachat.devices.sberbank.ru/api/v1/chat/completions

# Логгирование
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```
