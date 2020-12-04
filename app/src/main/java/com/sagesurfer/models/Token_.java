package com.sagesurfer.models;

/**
 * @author Girish Mane(girish@sagesurfer.com)
 *         Created on 06-07-2017
 *         Last Modified on 06-07-2017
 */

public class Token_ {
    private final Long expires_in;
    private final Long expires_at;
    private final String scope;
    private final String refresh_token;
    private final String access_token;
    private final int status;

    public Token_(Long expires_in, String scope, String refresh_token, String access_token, int status) {
        this.expires_in = expires_in;
        this.scope = scope;
        this.status=status;
        this.refresh_token = refresh_token;
        this.access_token = access_token;
        this.expires_at = (expires_in * 1000) + System.currentTimeMillis();
    }

    public Long getExpiresIn() {
        return expires_in;
    }

    private Long getExpiresAt() {
        return expires_at;
    }

    public String getScope() {
        return scope;
    }

    public int getStatus() {
        return status;
    }

    public String getRefreshToken() {
        return refresh_token;
    }


    public String getAccessToken() {
        return access_token;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() >= this.getExpiresAt());
    }

}
