package com.indium.api_gateway_ipl;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("insert-match-data", r -> r.path("/api/json-upload/upload")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .route("get-matches-by-player", r -> r.path("/matches/player")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .route("get-cumulative-score", r -> r.path("/matches/cumulative-score")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .route("get-scores-by-date", r -> r.path("/matches/scores/**")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .route("get-top-batsmen", r -> r.path("/matches/top-batsmen")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .route("get-wicket-count-by-bowler", r -> r.path("/api/json-upload/player/wickets")
                        .uri("http://localhost:8081"))  // Forward to cricket microservice
                .build();
    }
}
