package com.explore.Spring_AI.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioController {

    private final OpenAiAudioSpeechModel audioSpeechModel;
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public AudioController(OpenAiAudioTranscriptionModel transcriptionModel, OpenAiAudioSpeechModel audioSpeechModel){
        this.audioSpeechModel=  audioSpeechModel;
        this.transcriptionModel= transcriptionModel;
    }

    @PostMapping("/api/speechToText")
    public String speechToText(@RequestParam MultipartFile file){
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("en")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.VTT)
                .build();
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource(), options);

        return transcriptionModel.call(prompt)
                .getResult().getOutput();
    }

    @PostMapping("/api/textToSpeech")
    public byte[] textToSpeech(@RequestParam String text){
//        return audioSpeechModel.call(text);
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .speed(1.5)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .build();
        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);

        return audioSpeechModel.call(prompt).getResult().getOutput();
    }
}
