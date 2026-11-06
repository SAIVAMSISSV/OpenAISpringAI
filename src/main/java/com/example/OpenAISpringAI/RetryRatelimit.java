package com.example.OpenAISpringAI;

import java.time.Duration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openai.errors.OpenAIException;
import org.springframework.ai.chat.metadata.RateLimit;

@RestController
public class RetryRatelimit {
	
	private ChatClient chatClient;
	
	public RetryRatelimit(OpenAiChatModel chatModel) {
		this.chatClient=ChatClient.create(chatModel);
	}
	
	
	@GetMapping("/Wrong-model")
	public String WrongModel() {
		
		return CallAndExplain(OpenAiChatOptions.builder()
				.model("gpt-5.6-luna")
				 .maxRetries(0));
		
		
	}
	@GetMapping("/Time-out")
	public String Timeout(){
		
		return CallAndExplain(OpenAiChatOptions.builder()
				.model("")
				.timeout(Duration.ofSeconds(2)));
		
	}
	@GetMapping("/Rate-limit")
	public RateLimitInfo rateLimit() {
		ChatResponse chat = chatClient.prompt()
				.user("what is JVM")
				.call()
				.chatResponse();
		System.out.println(chat.getResult().getOutput().getText());
		RateLimit limit = chat.getMetadata().getRateLimit();
		 return new RateLimitInfo(limit.getRequestsLimit(),limit.getRequestsRemaining(),limit.getTokensReset()
				 ,limit.getTokensLimit(),limit.getTokensRemaining(),limit.getTokensReset());
		
	}
 private String CallAndExplain(OpenAiChatOptions.Builder options) {
	 try {
		 return chatClient.prompt()
				 .user("Write a 300 words on History of Java")
				 .options(options)
				 .call()
				 .content();
	 }catch(OpenAIException e){
		 String action;
		 if(ErrorRules.shouldRetry(e)) {
			 action="Wait little time again retry";
		 }
		 else {
			 action="Do not retry";
			 System.out.println("Exception "+e);
		 }
		 return action;
	 }
	 
 }
 public record RateLimitInfo(Long requestsLimit, Long requestsRemaining, Duration requestsReset,
         Long tokensLimit, Long tokensRemaining, Duration tokensReset) {
	 
 }
}
