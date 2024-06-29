package com.naeem.blogs.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs"; // We should place this in a secure location or an encrypted file
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer_";
    public static final String HEADER_STRING = "Authorization";
    public static final String HEADER_STRING_AUTHENTICATION = "Authentication";
    public static final String REGISTER = "/users/register";
    public static final String CONFIRM = "/users/confirm";

}
