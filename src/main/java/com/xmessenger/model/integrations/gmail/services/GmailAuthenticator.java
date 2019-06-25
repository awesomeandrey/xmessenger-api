package com.xmessenger.model.integrations.gmail.services;

import com.xmessenger.model.database.entities.core.AppUser;
import com.xmessenger.model.integrations.gmail.decorators.GmailAccount;
import com.xmessenger.model.services.core.user.dao.UserBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@Service
public class GmailAuthenticator {
    private final GsonJsonParser parser;
    private final GmailUrlBuilder urlBuilder;

    @Autowired
    public GmailAuthenticator(GmailUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
        this.parser = new GsonJsonParser();
    }

    public GmailUrlBuilder urlBuilder() {
        return this.urlBuilder;
    }

    public GmailAccount getGmailAccount(String accessToken) throws Exception {
        final URL requestUsernameUrl = this.urlBuilder.requestUsername(accessToken);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUsernameUrl.toString(), HttpMethod.GET, null, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.valueOf(200)) {
            Map<String, Object> payload = this.parser.parseMap(responseEntity.getBody());
            return new GmailAccount((String) payload.get("emailAddress"));
        } else {
            throw new GmailAuthenticationException(responseEntity.toString());
        }
    }

    public AppUser composeUser(GmailAccount gmailAccount, String accessToken) throws Exception {
        final URL requestUserInfoUrl = this.urlBuilder.requestUserInfo(accessToken);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUserInfoUrl.toString(), HttpMethod.GET, null, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.valueOf(200)) {
            Map<String, Object> payloadMap = this.parser.parseMap(responseEntity.getBody());
            return new UserBuilder(String.valueOf(payloadMap.get("name")))
                    .withUsername(gmailAccount.getUsername())
                    .withPassword(gmailAccount.getPassword(String.valueOf(payloadMap.get("id"))))
                    .withEmail(gmailAccount.getEmailAddress())
                    .withPicture(this.requestUserPicture(new URL(String.valueOf(payloadMap.get("picture")))))
                    .withExternal(true)
                    .withActive(true)
                    .build();
        } else {
            throw new GmailAuthenticationException(responseEntity.toString());
        }
    }

    //******************************************************************************************************************

    private byte[] requestUserPicture(URL pictureUrl) {
        try {
            URLConnection conn = pictureUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private String composePassword(String param) {
        return param.substring(0, Math.min(param.length(), 10)); // first 10 characters;
    }

    public class GmailAuthenticationException extends Exception {
        GmailAuthenticationException(String message) {
            super(message);
        }
    }
}
