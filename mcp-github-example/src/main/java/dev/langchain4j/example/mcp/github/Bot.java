package dev.langchain4j.example.mcp.github;

import dev.langchain4j.service.TokenStream;

public interface Bot {

    TokenStream chat(String prompt);
}
