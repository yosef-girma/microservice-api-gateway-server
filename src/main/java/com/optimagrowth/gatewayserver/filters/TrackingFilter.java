package com.optimagrowth.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;

@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

    private Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    private FilterUtils filterUtils; // commonly used function accross filter encapsulated here

    // executed everytime rquest pass through filter
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) { // ex

        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        if (isCorrelationIdPresent(httpHeaders)) {
            logger.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId(httpHeaders));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationId);
            logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationId);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        // check if correclatio nid present in request header
        if (filterUtils.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }


    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }


}
