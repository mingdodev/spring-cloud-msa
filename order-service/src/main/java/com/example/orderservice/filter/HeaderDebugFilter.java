package com.example.orderservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class HeaderDebugFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String traceparent = request.getHeader("X-B3-ParentSpanId");
        String xB3SpanId = request.getHeader("X-B3-SpanId");
        String xB3TraceId = request.getHeader("X-B3-TraceId");

        log.info("incoming headers: traceparent={}, b3={}, X-B3-TraceId={}",
                traceparent, xB3SpanId, xB3TraceId);

        filterChain.doFilter(request, response);
    }
}
