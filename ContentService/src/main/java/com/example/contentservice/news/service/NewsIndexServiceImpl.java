package com.example.contentservice.news.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsIndexServiceImpl implements NewsIndexService {

  private final ElasticsearchClient elasticsearchClient;

  public String createNewsIndex() {
    String indexName = "news-index";

    String jsonBody = """
        {
          "settings": {
            "analysis": {
              "tokenizer": {
                "my_nori_tokenizer": {
                  "type": "nori_tokenizer",
                  "decompound_mode": "mixed",
                  "discard_punctuation": "false"
                },
                "my_ngram_tokenizer": {
                    "type": "ngram",
                    "min_gram": 2,
                    "max_gram": 3,
                    "token_chars": ["letter", "digit"]
                  }
              },
              "filter": {
                "stopwords": {
                  "type": "stop",
                  "stopwords": [" ", "."]
                }
              },
              "char_filter": {
                "html_strip": {
                  "type": "html_strip"
                }
              },
              "analyzer": {
                "my_nori_analyzer": {
                  "type": "custom",
                  "tokenizer": "my_nori_tokenizer",
                  "filter": ["lowercase", "stop", "trim", "stopwords", "nori_part_of_speech"],
                  "char_filter": ["html_strip"]
                },
                "my_ngram_analyzer": {
                    "type": "custom",
                    "tokenizer": "my_ngram_tokenizer",
                    "filter": ["lowercase"]
                  }
              }
            }
          },
          "mappings": {
            "properties": {
              "title": {
                "type": "text",
                "fields": {
                  "korean": {
                    "type": "text",
                    "analyzer": "my_nori_analyzer",
                    "search_analyzer": "my_nori_analyzer"
                  },
                  "ngram": {
                        "type": "text",
                        "analyzer": "my_ngram_analyzer",
                        "search_analyzer": "my_ngram_analyzer"
                      },
                  "english": {
                    "type": "text",
                    "analyzer": "standard",
                    "search_analyzer": "standard"
                  }
                }
              },
              "combinedTokens": { // 필드 추가
                    "type": "keyword"\s
                  },
              "content": {
                "type": "text",
                "fields": {
                  "korean": {
                    "type": "text",
                    "analyzer": "my_nori_analyzer",
                    "search_analyzer": "my_nori_analyzer"
                  },
                  "ngram": {
                        "type": "text",
                        "analyzer": "my_ngram_analyzer",
                        "search_analyzer": "my_ngram_analyzer"
                      },
                  "english": {
                    "type": "text",
                    "analyzer": "standard",
                    "search_analyzer": "standard"
                  }
                }
              },
              "createdAt": {
                "type": "date",
                "format": "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis"
              }
            }
          }
        }
        """;

    try (RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build()) {
      Request request = new Request("PUT", "/" + indexName);
      request.setJsonEntity(jsonBody);

      Response response = restClient.performRequest(request);
      int statusCode = response.getStatusLine().getStatusCode();
      return statusCode == 200 ? "인덱스 생성 성공" : "인덱스 생성 실패 (상태 코드: " + statusCode + ")";
    } catch (IOException e) {
      e.printStackTrace();
      return "IOException 발생: " + e.getMessage();
    }
  }
}
