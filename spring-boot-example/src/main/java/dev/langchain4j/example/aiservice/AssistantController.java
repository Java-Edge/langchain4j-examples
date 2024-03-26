package dev.langchain4j.example.aiservice;

import dev.langchain4j.service.spring.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AssistantController {

    /**
     * This is an example of using an {@link AiService}, a high-level LangChain4j API
     */

    @Autowired
    Assistant assistant;

    @GetMapping("/assistant")
    public String assistant(@RequestParam(value = "message", defaultValue = "What is the time now?") String message) {
        return assistant.chat(message);
    }
}
