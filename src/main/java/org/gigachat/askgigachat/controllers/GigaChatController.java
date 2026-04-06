package org.gigachat.askgigachat.controllers;

import org.gigachat.askgigachat.services.GigaChatService;
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
