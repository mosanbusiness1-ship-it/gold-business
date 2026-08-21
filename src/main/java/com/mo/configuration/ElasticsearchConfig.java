package com.mo.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    @Value("${spring.elasticsearch.apikey:}")
    private String elasticsearchApiKey;

    @Value("${spring.elasticsearch.connection-timeout:10000}")
    private int connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout:60000}")
    private int socketTimeout;

    @Bean
    public ElasticsearchClient elasticsearchClient(ObjectMapper objectMapper) {
        HttpHost httpHost = HttpHost.create(elasticsearchUris);

        Header[] headers = new Header[] {
            new BasicHeader(HttpHeaders.AUTHORIZATION, "ApiKey " + elasticsearchApiKey)
        };

        RestClient restClient = RestClient.builder(httpHost)
            .setDefaultHeaders(headers)
            .setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                    .setConnectTimeout(connectionTimeout)
                    .setSocketTimeout(socketTimeout)
            )
            .build();

        RestClientTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper(objectMapper)
        );

        return new ElasticsearchClient(transport);
    }

}

