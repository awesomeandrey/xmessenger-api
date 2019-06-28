package com.xmessenger.model.integrations.gmail;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class GmailUrlBuilder {
    private final String ACCESS_TOKEN_PROPERTY_NAME = "access_token";
    @Value(value = "${gmail.client_id}")
    private String clientId;
    @Value(value = "${gmail.response_type}")
    private String responseType;
    @Value(value = "${gmail.redirect_uri}")
    private String redirectUri;
    @Value(value = "${gmail.scope}")
    private String scope;

    public URL requestAccessToken(String host) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost("accounts.google.com");
        builder.setPath("/o/oauth2/v2/auth");
        builder.addParameter("client_id", this.clientId);
        builder.addParameter("response_type", this.responseType);
        builder.addParameter("scope", this.scope);
        builder.addParameter("redirect_uri", host.concat(this.redirectUri));
        return builder.build().toURL();
    }

    URL requestUsername(String accessToken) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost("www.googleapis.com");
        builder.setPath("/gmail/v1/user/me/profile");
        builder.addParameter(this.ACCESS_TOKEN_PROPERTY_NAME, accessToken);
        return builder.build().toURL();
    }

    URL requestUserInfo(String accessToken) throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https");
        builder.setHost("www.googleapis.com");
        builder.setPath("/oauth2/v1/userinfo");
        builder.addParameter(this.ACCESS_TOKEN_PROPERTY_NAME, accessToken);
        builder.addParameter("alt", "json");
        return builder.build().toURL();
    }
}
