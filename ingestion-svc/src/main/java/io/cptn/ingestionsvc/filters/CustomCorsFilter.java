package io.cptn.ingestionsvc.filters;

import io.cptn.common.pojos.Header;
import io.cptn.ingestionsvc.services.SourceService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* @author: kc, created on 5/1/23 */
@Component
@RequiredArgsConstructor
public class CustomCorsFilter implements Filter {

    private final SourceService sourceService;

    private final Pattern eventUrlPattern = Pattern.compile("/event/source/([a-fA-F0-9\\-]+)");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        if (!httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String uri = httpRequest.getRequestURI();
        Matcher m = eventUrlPattern.matcher(uri);
        if (m.find()) {
            String sourceId = m.group(1);
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            sourceService.getById(UUID.fromString(sourceId)).ifPresent(source -> {
                List<Header> headers = source.getHeaders();
                if (headers != null) {
                    headers.forEach(header -> {
                        if (header.getKey().startsWith("Access-Control-")) {
                            response.setHeader(header.getKey(), header.getValue());
                        }
                    });
                }
            });
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
