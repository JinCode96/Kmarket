package com.kmarket.security.provider;

import java.util.Map;

public class FaceBookUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes; // getAttributes()

    public FaceBookUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "facebook";
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
