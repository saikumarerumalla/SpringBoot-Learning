package com.explore.Spring_AI.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllammaController {
//    private ChatClient chatClient;
//
//    private OllammaController(OllamaChatModel chatModel){
//        this.chatClient = ChatClient.builder(chatModel).build();
//    }
//
////    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
//
////    public OllammaController(ChatClient.Builder builder ){
////        this.chatClient = builder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()).build();
////    }
//
////    @GetMapping("/api/ollama/{message}")
//    public String getAnswer(@PathVariable String message){
//        ChatResponse chatResponse = chatClient.prompt(message)
//                .call()
//                .chatResponse();
//
//        String res = chatResponse
//                .getResult()
//                .getOutput()
//                .getText();
//
//        return res;
//
//    }
}
