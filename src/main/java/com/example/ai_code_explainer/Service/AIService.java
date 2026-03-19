package com.example.ai_code_explainer.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.url}")
    private String apiUrl;

    //  using groq online
    public String getOnlineExplanation(String code) {

        WebClient webClient = WebClient.create();

       
        String prompt = "Explain this code in ONLY 4-5 short points.\n"
                + "Also give Time & Space Complexity in 1 line.\n"
                + "Keep answer clean, short and readable.\n\n"
                + code;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        body.put("max_tokens", 200);
     
        try {
            Map response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("ONLINE RESPONSE: " + response);

            if (response == null || response.get("choices") == null) {
                return "❌ Invalid response from API";
            }

            List choices = (List) response.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Online Error: " + e.getMessage();
        }
    }

    // local i use ollama
    public String getLocalExplanation(String code) {

        WebClient webClient = WebClient.create("http://localhost:11434");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3");
        body.put("prompt", "Explain this code in ONLY 4-5 short points.\\n\"\r\n"
        		+ "                + \"Also give Time & Space Complexity in 1 line.\\n\"\r\n"
        		+ "                + \"Keep answer clean, short and readable.\\n\\n" + code);
       
        body.put("stream", false);

        try {
            Map response = webClient.post()
                    .uri("/api/generate")
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            System.out.println("LOCAL RESPONSE: " + response);

            if (response == null || response.get("response") == null) {
                return "❌ Local model not responding";
            }

            return response.get("response").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Local Error: " + e.getMessage();
        }
    }
}