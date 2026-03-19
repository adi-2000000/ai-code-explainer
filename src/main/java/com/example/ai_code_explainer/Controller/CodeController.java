package com.example.ai_code_explainer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.ai_code_explainer.Service.AIService;

import java.util.Map;

@Controller
public class CodeController {

    @Autowired
    private AIService aiService;

    
    @GetMapping("/home")
    public String home() {
        return "index";
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/api/explain")
    @ResponseBody
    public String explainCode(@RequestBody Map<String, String> request) {

        String code = request.get("code");
        String model = request.get("model");

        if (code == null || code.trim().isEmpty()) {
            return "❌ Code is empty!";
        }

        if ("local".equalsIgnoreCase(model)) {
            return aiService.getLocalExplanation(code);
        } else {
            return aiService.getOnlineExplanation(code);
        }
    }
}