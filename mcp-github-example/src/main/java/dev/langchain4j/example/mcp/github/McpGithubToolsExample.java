package dev.langchain4j.example.mcp.github;

import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.http.client.jdk.JdkHttpClientBuilder;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import utils.LangChain4jOllamaContainer;

import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static utils.AbstractOllamaInfrastructure.*;

public class McpGithubToolsExample {

    /**
     * This example uses the GitHub MCP server to showcase how
     * to use an LLM to summarize the last commits of a public GitHub repo.
     * Being a public repository (the LangChain4j repository is used as an example), you don't need any
     * authentication to access the data.
     * <p>
     * Running this example requires Docker to be installed on your machine,
     * because it spawns the GitHub MCP Server as a subprocess via Docker:
     * `docker run -i mcp/github`.
     * <p>
     * You first need to build the Docker image of the GitHub MCP Server that is available at `mcp/github`.
     * See https://github.com/modelcontextprotocol/servers/tree/main/src/github to build the image.
     * <p>
     * The communication with the GitHub MCP server is done directly via stdin/stdout.
     */
    public static void main(String[] args) {

//        ChatLanguageModel model = OpenAiChatModel.builder()
//                .apiKey("sk-pRhefgsSUnY0OmPxuelIV4o30wQfZl1Fl1CdLShZqwCJVKtE")
//                .modelName("gpt-3.5-turbo")
//                .logRequests(true)
//                .logResponses(true)
//                .build();

//        // 连接本地 Ollama 服务
//        ChatLanguageModel model = OllamaChatModel.builder()
//                .baseUrl("http://localhost:11434") // Ollama 默认本地服务地址
//                .modelName("llama3-groq-tool-use:8b") // 你本地 Ollama 拉取的模型名称
//                .logRequests(true)
//                .logResponses(true)
//                .build();
        // 连接本地 LMStudio 服务 - 流式输出版本
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1);
        JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
                .httpClientBuilder(httpClientBuilder);

        // 使用流式模型
        StreamingChatModel streamingModel = OpenAiStreamingChatModel.builder()
                .baseUrl("http://127.0.0.1:1234/v1/")
                .httpClientBuilder(jdkHttpClientBuilder)
                .modelName("mistralai/ministral-3-14b-reasoning")
                .logRequests(true)
                .logResponses(true)
                .timeout(java.time.Duration.ofSeconds(1200))
                .build();

//        McpTransport transport = new StdioMcpTransport.Builder()
//                .command(List.of("/usr/local/bin/docker", "run", "-i", "mcp/github"))
//                .logEvents(true)
//                .build();
//        McpClient mcpClient = new DefaultMcpClient.Builder()
//                .transport(transport)
//                .build();
//
//        ToolProvider toolProvider = McpToolProvider.builder()
//                .mcpClients(List.of(mcpClient))
//                .build();

        Bot bot = AiServices.builder(Bot.class)
                .streamingChatModel(streamingModel)
//                .toolProvider(toolProvider)
                .build();

        System.out.println("开始流式输出响应：\n");
        System.out.println("RESPONSE: ");

        // 使用CompletableFuture等待流式响应完成
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        StringBuilder fullResponse = new StringBuilder();

        bot.chat("Summarize the last 3 commits of the LangChain4j GitHub repository")
                .onPartialResponse(partialResponse -> {
                    // 每次收到部分响应时立即打印
                    System.out.print(partialResponse);
                    fullResponse.append(partialResponse);
                })
                .onCompleteResponse(completeResponse -> {
                    // 响应完成
                    System.out.println("\n\n--- 流式输出完成 ---");
                    futureResponse.complete(fullResponse.toString());
                })
                .onError(error -> {
                    // 错误处理
                    System.err.println("\n错误: " + error.getMessage());
                    error.printStackTrace();
                    futureResponse.completeExceptionally(error);
                })
                .start();

        // 等待流式响应完成
        futureResponse.join();
    }
}