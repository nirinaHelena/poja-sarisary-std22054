package hei.school.sarisary.endpoint.rest.controller;

import lombok.AllArgsConstructor;

public class BlackAndWhiteResponse {
    private final String originalUrl;
    private final String transformedUrl;

    public BlackAndWhiteResponse(String originalUrl, String transformedUrl) {
        this.originalUrl = originalUrl;
        this.transformedUrl = transformedUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getTransformedUrl() {
        return transformedUrl;
    }
}
