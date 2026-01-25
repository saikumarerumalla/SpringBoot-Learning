package com.explore.Spring_AI.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {
    private ChatClient chatClient;
    private OpenAiImageModel openAiImageModel;

    public ImageController(OpenAiImageModel openAiImageModel, OpenAiChatModel openAiChatModel){
        this.openAiImageModel = openAiImageModel;
        this.chatClient = ChatClient.create(openAiChatModel);
    }

    @GetMapping("/api/genImage")
    public String genImage(@RequestParam  String query){
//        ImagePrompt prompt = new ImagePrompt(query);
        ImagePrompt prompt = new ImagePrompt(query, OpenAiImageOptions.builder()
                .quality("hd")
                .height(1024)
                .width(1024)
                .style("natural")
                .build() );
        ImageResponse response = openAiImageModel.call(prompt);
        return response.getResult().getOutput().getUrl();
    }

    @PostMapping("/api/describeImage")
    public String describeImage(@RequestParam String query , @RequestParam MultipartFile file){
        return chatClient.prompt()
                .user(us -> us.text(query)
                        .media(MimeTypeUtils.IMAGE_JPEG, file.getResource()))
                .call()
                .content();
    }
}
