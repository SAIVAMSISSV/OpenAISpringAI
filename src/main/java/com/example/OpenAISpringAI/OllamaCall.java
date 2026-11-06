package com.example.OpenAISpringAI;

import org.springframework.http.MediaType;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class OllamaCall {
	
	private ChatClient chatClient;
	
	public OllamaCall(OllamaChatModel chatModal) {
		this.chatClient = ChatClient.create(chatModal);
		
	}
	@GetMapping("/Ollamacall")
	public String Call(@RequestParam String Msg) {
		 return chatClient.prompt()
				 .system("You are a java trainer explain in 2 lines , if question is no releated to java dont responsed say sorry vamsi")
				 .user(Msg)
				 .call()
				 .content();
	}
	
	@GetMapping(value="/Streaming",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> stream(@RequestParam String message){
		 
		return chatClient.prompt()
				.user(message)
				.stream()
				.content();
	}
	
	

	
}
