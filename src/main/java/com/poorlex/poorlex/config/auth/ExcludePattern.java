package com.poorlex.poorlex.config.auth;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.HttpMethod;

public class ExcludePattern {

    private final Pattern pattern;
    private final List<HttpMethod> methods;

    public ExcludePattern(final Pattern pattern, final List<HttpMethod> methods) {
        this.pattern = pattern;
        this.methods = methods;
    }

    public boolean matches(final String url, final HttpMethod method) {
        return pattern.matcher(url).matches() && methods.contains(method);
    }
}
