package io.github.jokoframework.myproject.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

public class RestCustomizer implements RestTemplateCustomizer {

	public RestCustomizer() {
		super();
	}
	
	private HttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}

	@Override
	public void customize(RestTemplate restTemplate) {
		HttpClient httpClient = getHttpClient();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	}
}
