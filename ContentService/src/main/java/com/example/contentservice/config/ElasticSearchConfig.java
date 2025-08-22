package com.example.contentservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
    basePackages = {
        "com.example.contentservice.notice.repository",
        "com.example.contentservice.support.repository",
        "com.example.contentservice.news.repository"
    },
    elasticsearchTemplateRef = "elasticsearchTemplate"
)
public class ElasticSearchConfig {

  @Bean
  public RestClient restClient() {
    return RestClient.builder(
        new HttpHost("localhost", 9200, "http")
    ).build();
  }

  @Bean
  public ElasticsearchClient elasticsearchClient(RestClient restClient) {
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    ElasticsearchTransport transport = new RestClientTransport(restClient,
        new JacksonJsonpMapper(objectMapper));
    return new ElasticsearchClient(transport);
  }

}
