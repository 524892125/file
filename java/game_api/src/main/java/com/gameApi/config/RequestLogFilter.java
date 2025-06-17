package com.gameApi.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
public class RequestLogFilter implements WebFilter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmmssSSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        StringBuilder  sb = new StringBuilder();

        long nanoTime = System.nanoTime();
        LocalDateTime localDateTime = LocalDateTime.now();
        String formattedDateTime = localDateTime.format(FORMATTER);
        int subMillisInt = (int) ((nanoTime / 100) % 1000);
        String uuid = formattedDateTime + String.format("%03d", subMillisInt);
        MDC.put("requestId", uuid);

        // 获取请求信息
        String method = exchange.getRequest().getMethodValue();
        String path = exchange.getRequest().getURI().getPath();
        Map<String, String> query = exchange.getRequest().getQueryParams().toSingleValueMap();
        String ip = getClientIp(exchange);

        sb.append(ip).append(",").append(method).append(",").append(path).append(",").append(query);
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(exchange.getRequest().getHeaders().getContentType())) {
            return exchange.getFormData().doOnNext(data -> {
                sb.append(",").append(data.toSingleValueMap());
                log.info(",{}", sb);
            }).then(chain.filter(exchange).doFinally(signalType -> {
                MDC.remove("requestId");
            }));
        }
        if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(exchange.getRequest().getHeaders().getContentType())) {
            return exchange.getMultipartData().doOnNext(data -> {
                data.forEach((key, parts) -> {
                    parts.forEach(part -> {
                        if (part instanceof FormFieldPart) {
                            FormFieldPart formPart = (FormFieldPart) part;
                            sb.append(key).append("=").append(formPart.value()).append(",");
                        } else if (part instanceof FilePart) {
                            FilePart filePart = (FilePart) part;
                            sb.append(key).append("=").append(filePart.filename()).append(",");
                        } else {
                            sb.append(key).append("=").append(part.name()).append(",");
                        }
                    });
                });

                log.info(",{}", sb);
            }).then(chain.filter(exchange).doFinally(signalType -> {
                MDC.remove("requestId");
            }));
        }

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    MDC.remove("requestId");
                });
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }
}
