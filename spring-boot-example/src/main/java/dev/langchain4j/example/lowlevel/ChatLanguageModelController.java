package dev.langchain4j.example.lowlevel;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatLanguageModelController {

    /**
     * This is an example of using a {@link ChatLanguageModel}, a low-level LangChain4j API
     */

    @Autowired
    ChatLanguageModel chatLanguageModel;

    @GetMapping("/model")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatLanguageModel.generate(message);
    }
}
