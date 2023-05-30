package org.example.controller;

import org.example.model.Rifle;
import org.example.repository.RifleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RifleController {
    @Autowired
    private RifleRepository repository;

    @GetMapping("/")
    public String getAllRifles(Model model){
        List<Rifle> rifles = repository.findAll();
        model.addAttribute("rifles", rifles);
        return "index";
    }

    @PostMapping(value = "/add")
    public String saveProduct(@ModelAttribute("rifle")Rifle rifle ){
        repository.save(rifle);
        return "redirect:/";
    }
}
