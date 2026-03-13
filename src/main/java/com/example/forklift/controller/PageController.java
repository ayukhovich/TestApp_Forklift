package com.example.forklift.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для отображения страниц
 */
@Controller
public class PageController {

    /**
     * Главная страница приложения
     * @return имя шаблона
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
