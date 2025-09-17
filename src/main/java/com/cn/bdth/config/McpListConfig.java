package com.cn.bdth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.List;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class McpListConfig {

    private final ToolCallbackProvider mcpClient;

    //百度地图mcp
//    @Bean
//    @Primary
    public List<ToolCallback> baiduMap() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://mcp.map.baidu.com")
                    .sseEndpoint("/sse?ak=KcNwVlCW4RRG6ZqE6dQouQEUWODvrEja")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(20)).build();
            client.initialize().block();
            log.info("百度地图mcp初始化成功");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("百度地图MCP客户端初始化失败", e);
            return null;
        }
    }

    //高德地图mcp
//    @Bean
    public List<ToolCallback> amapMap() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://mcp.amap.com")
                    .sseEndpoint("/sse?key=b52b45fca01dea4e1ca15d7210ba5f45")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(15)).build();
            client.initialize().block();
            log.info("高德地图mcp初始化成功");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("高德地图mcp客户端初始化失败", e);
            return null;
        }
    }

    //fetch网页解析
//    @Bean
    public List<ToolCallback> webSearch() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://mcp.api-inference.modelscope.net")
                    .sseEndpoint("/eeb2fbf8afcc40/sse")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(20)).build();
            client.initialize().block();
            log.info("fetch网页解析MCP初始化成功");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("Web搜索MCP客户端初始化失败", e);
            return null;
        }
    }


    //12306 mcp
//    @Bean
    public List<ToolCallback> trainMcp() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://mcp.api-inference.modelscope.net")
                    .sseEndpoint("/600e5cc7290a44/sse")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(10)).build();
            client.initialize().block();
            log.info("12306-MCP车票查询工具已加载");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("12306-MCP客户端初始化失败", e);
            return null;
        }
    }

    //Bing搜索
//    @Bean
//    public List<ToolCallback> bingSearch() {
//        try {
//            McpClientTransport transport = HttpClientSseClientTransport
//                    .builder("https://mcp.api-inference.modelscope.net")
//                    .sseEndpoint("/488b61d56c6540/sse")
//                    .objectMapper(new ObjectMapper())
//                    .build();
//            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(20)).build();
//            client.initialize().block();
//            log.info("Bing搜索工具已加载");
//            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
//        } catch (Exception e) {
//            log.error("Bing搜索工具初始化失败", e);
//            return null;
//        }
//    }

    //Zhipu搜索
//    @Bean
    public List<ToolCallback> zhipuSearch() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://open.bigmodel.cn")
                    .sseEndpoint("/api/mcp/web_search/sse?Authorization=191c67ea8f8bc8cbe664d05bacb1d8cd.Qt9duQgDwKQXJU7I")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(20)).build();
            client.initialize().block();
            log.info("Zhipu搜索工具已加载");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("Zhipu搜索工具初始化失败", e);
            return null;
        }
    }

    //HowToCook-MCP Server 🥘 -- 炫一周好饭，拒绝拼好饭
    public List<ToolCallback> howToCookMCP() {
        try {
            McpClientTransport transport = HttpClientSseClientTransport
                    .builder("https://mcp.api-inference.modelscope.net")
                    .sseEndpoint("d89bbc68e89b4b/sse")
                    .objectMapper(new ObjectMapper())
                    .build();
            McpAsyncClient client = McpClient.async(transport).initializationTimeout(Duration.ofSeconds(20)).build();
            client.initialize().block();
            log.info("HowToCook-MCP工具已加载");
            return McpToolUtils.getToolCallbacksFromAsyncClients(client);
        } catch (Exception e) {
            log.error("HowToCook-MCP工具初始化失败", e);
            return null;
        }
    }



    /**
     * 根据方法名获取对应的ToolCallback列表
     * @param methodName 方法名
     * @return ToolCallback列表
     */
    public List<ToolCallback> getToolCallbackByName(String methodName) {
        switch (methodName) {
            case "baiduMap" -> {
                return this.baiduMap();
            }
            case "amapMap" -> {
                return this.amapMap();
            }
            case "webSearch" -> {
                return this.webSearch();
            }
            case "trainMcp" -> {
                return this.trainMcp();
            }
            case "zhipuSearch" -> {
                return this.zhipuSearch();
            }
            case "howToCookMCP" ->{
                return this.howToCookMCP();
            }
            default -> {
                log.warn("未知的MCP方法名: {}", methodName);
                return null;
            }
        }
    }

}
