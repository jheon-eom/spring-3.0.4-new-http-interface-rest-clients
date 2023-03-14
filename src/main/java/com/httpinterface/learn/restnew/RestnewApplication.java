package com.httpinterface.learn.restnew;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication
public class RestnewApplication {
	@Bean
	ApplicationRunner init(ErApi api) {
		return args -> {
			// https://open.er-api.com/v6/latest
			/**
			 * RestTemplate
			 */
			RestTemplate rt = new RestTemplate();
			Map<String, Map<String, Double>> response = rt.getForObject("https://open.er-api.com/v6/latest", Map.class);
			System.out.println(response.get("rates").get("KRW"));

			/**
			 * WebClient
			 */
			WebClient webClient = WebClient.create("https://open.er-api.com");
			Map<String, Map<String, Double>> response2 = webClient.get().uri("/v6/latest").retrieve().bodyToMono(Map.class).block();
			System.out.println(response2.get("rates").get("KRW"));

			/**
			 * HttpInterface
			 */
			HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
					.builder(WebClientAdapter.forClient(webClient))
					.build();
			ErApi erApi = httpServiceProxyFactory.createClient(ErApi.class);
			Map<String, Map<String, Double>> response3 = erApi.getLatest();
			System.out.println(response3.get("rates").get("KRW"));

			/**
			 * HttpInterface
			 * Proxy Bean
			 */
			Map<String, Map<String, Double>> response4 = api.getLatest();
			System.out.println(response4.get("rates").get("KRW"));
		};
	}

	@Bean
	ErApi erApi() {
		WebClient webClient = WebClient.create("https://open.er-api.com");
		HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(webClient))
				.build();
		return httpServiceProxyFactory.createClient(ErApi.class);
	}

	interface ErApi {
		@GetExchange("/v6/latest")
		Map getLatest();
	}

	public static void main(String[] args) {
		SpringApplication.run(RestnewApplication.class, args);
	}

}
