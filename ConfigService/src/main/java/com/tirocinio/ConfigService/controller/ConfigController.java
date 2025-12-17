package com.tirocinio.ConfigService.controller;


import com.tirocinio.ConfigService.service.ConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/Home")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public String mostraDashboard(Model model) {
        String stato = configService.getStatoSistema();
        model.addAttribute("statoHtml", stato);
        return "Home";
    }


    @PostMapping("/genera") // Chiamato dal form HTML
    @ResponseBody
    public String avviaGenerazione(Model model) {

        String risposta = configService.inviaDatiAlGateway();

        return risposta;
    }
}