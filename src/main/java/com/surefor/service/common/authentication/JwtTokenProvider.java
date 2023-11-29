package com.surefor.service.common.authentication;

import com.surefor.service.domain.user.UserRoleType;
import com.surefor.service.common.exception.TMSException;
import com.surefor.service.common.exception.TMSPlatformException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.json.simple.parser.JSONParser;

/**
 * JWT token helper class.
 *
 * After the authentication is completed with the given user credential,
 * JwtTokenProvider creates an access and refresh JWT, validates the token for TMS platform service. <br>
 * When it creates a token the user access control info, such as user role, will be embedded to the JWT payload for the privilege verification.
 * It improves the responsiveness with skipping user privilege look-up but the user needs to re-login when the privilege data is changed.
 */
@Slf4j
@Component
public class JwtTokenProvider {
    private static final int HEADER = 0;
    private static final int PAYLOAD = 1;
    private static final int JWT_PARTS = 3;

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String TOKEN_TYPE = "JWT";

    // token life cycle configuration
    // TODO: dynamically adjust the token life period parameters (e.g. Spring Cloud Configuration, DB, AWS Parameter Store)
    public static final long ACCESS_TOKEN_EXPIRATION_TIME_MILLIS = 1000L * 60 * 60; // 1 hour
    public static final long REFRESH_TOKEN_EXPIRATION_TIME_MILLIS = 1_209_600_000L; // 14 days
    public static final long TOKEN_ROTATE_PERIOD = 259_200_000L; // 3 days

    private final String secretKey;

    public JwtTokenProvider(@Value("${jwt.header.secret-key}") final String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * parse the JWT token and returns the header in Json
     * @param jwt JWT token
     * @return headers in Json
     */
    public static JSONObject getJWTHeaders(@NonNull final String jwt) {
        try {
            validateJWT(jwt);
            Base64.Decoder dec = Base64.getDecoder();
            final byte[] sectionDecoded = dec.decode(jwt.split("\\.")[HEADER]);
            final String jwtSection = new String(sectionDecoded, StandardCharsets.UTF_8);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jwtSection);
            return jsonObject;
        } catch (final Exception e) {
            throw new IllegalArgumentException("error in parsing JSON");
        }
    }

    /**
     * parse the JWT token and returns the payload in Json
     * @param jwt JWT token
     * @return payload in Json
     */
    public static JSONObject getPayload(@NonNull final String jwt) {
        try {
            validateJWT(jwt);
            Base64.Decoder dec = Base64.getDecoder();
            final String payload = jwt.split("\\.")[PAYLOAD];
            final byte[] sectionDecoded = dec.decode(payload);
            final String jwtSection = new String(sectionDecoded, StandardCharsets.UTF_8);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jwtSection);
            return jsonObject;
        } catch (final Exception e) {
            throw new IllegalArgumentException("error in parsing JSON");
        }
    }


    /**
     * get the token issued date/time
     * @param jwt JWT token
     * @return token issued timestamp in seconds
     */
    public static Long getIssuedAt(final String jwt) {
        JSONObject jsonObject = getPayload(jwt);
        return (Long) jsonObject.get("iat");
    }

    /**
     * get the token issued date/time
     * @param jwt JWT token
     * @return token issued timestamp in milliseconds
     */
    public static Long getIssuedAtMillis(@NonNull final String jwt) {
        return getIssuedAt(jwt) * 1000;
    }

    /**
     * get the token expiry
     * @param jwt JWT token
     * @return token expiry timestamp in seconds
     */
    public static Long getExpiresAt(final String jwt) {
        JSONObject jsonObject = getPayload(jwt);
        return (Long) jsonObject.get("exp");
    }

    /**
     * get the token expiry
     * @param jwt JWT token
     * @return token expiry timestamp in milliseconds
     */
    public static Long getExpiresAtMillis(final String jwt) {
        return getExpiresAt(jwt) * 1000;
    }

    /**
     * validate if the given string is in JWT format
     */
    public static void validateJWT(@NonNull final String jwt) {
        if (Boolean.FALSE.equals(isJWT(jwt))) {
            throw new IllegalArgumentException("not a JSON Web Token");
        }
    }

    /**
     * return true if the given string is in JWT format
     */
    public static boolean isJWT(@NonNull final String jwt) {
        // Check if the JWT has the three parts
        final String[] jwtParts = jwt.split("\\.");
        return jwtParts.length == JWT_PARTS;
    }
    /**
     * return true if JWT token is expired
     */
    public Boolean expired(@NonNull final String jwt) {
        Boolean expired = Boolean.FALSE;

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(jwt);
            expired = claims.getBody().getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            expired = Boolean.TRUE;
        } catch (Exception e) {
            throw new TMSPlatformException(TMSException.INVALID_TOKEN);
        }
        return expired;
    }


    /**
     * create an access token in JWT for the TMS platform user
     *
     * @param userPublicId user identifier
     * @param roles roles of the user.
     * @param now timestamp when the access token creation was requested
     * @return created access token
     */
    public String createAccessToken(@NonNull final UUID userPublicId, @NonNull final List<UserRoleType> roles, @NonNull final Long now) {
        return createToken(userPublicId, roles.stream().map(e -> e.name()).collect(Collectors.toList()), now, ACCESS_TOKEN_EXPIRATION_TIME_MILLIS);
    }

    /**
     * create a refresh token in JWT for the TMS platform user
     *
     * @param userPublicId user identifier
     * @param roles roles of the user.
     * @param now timestamp when the access token creation was requested
     * @return created refresh token
     */
    public String createRefreshToken(@NonNull final UUID userPublicId, @NonNull final List<UserRoleType> roles, @NonNull final Long now) {
        return createToken(userPublicId, roles.stream().map(e -> e.name()).collect(Collectors.toList()), now, REFRESH_TOKEN_EXPIRATION_TIME_MILLIS);
    }

    /**
     *
     * @param userPublicId user identifier
     * @param roles roles of the user.
     * @param now timestamp when the access token creation was requested
     * @param expiry period of the expiry in milliseconds
     * @return created JWT token
     */
    private String createToken(@NonNull final UUID userPublicId, @NonNull final List<String> roles, @NonNull final Long now, @NonNull final Long expiry) {
        validatePastTimestamp(now);

        Map<String, Object> header = createJWTHeader();

        Claims claims = Jwts.claims().setSubject(userPublicId.toString());
        setRolesInPayload(claims, roles);

        return Jwts.builder()
                .setHeader(header)              // header
                .setClaims(claims)              // payload
                .setIssuedAt(new Date(now))     // timestamp
                .setExpiration(new Date(now + expiry))                  // expiry
                .signWith(getSigningKey(secretKey), SIGNATURE_ALGORITHM) // signing algorithm and secret key
                .compact();
    }

    /**
     * refresh the access token
     * @param jwt old JWT token
     * @param now timestamp when the rotation is request
     * @return rotated access token
     */
    public String rotateAccessToken(@NonNull final String jwt, @NonNull final Long now) {
        validateToken(jwt);

        String userPublicId = getSub(jwt);
        List<UserRoleType> roleList = getRoles(jwt);
        return createAccessToken(UUID.fromString(userPublicId), roleList, now);
    }

    /**
     * rotate the refresh token
     * @param jwt old JWT token
     * @param now timestamp when the rotation is request
     * @return rotated refresh token
     */
    public String rotateRefreshToken(@NonNull final String jwt, @NonNull final Long now) {
        validateToken(jwt);

        String userPublicId = getSub(jwt);
        List<UserRoleType> roleList = getRoles(jwt);
        return createRefreshToken(UUID.fromString(userPublicId), roleList, now);
    }


    // Jwt Token 인증 정보 조회

    /**
     * returns user's public id which is 'sub' value of the JWT token
     * @param jwt JWT token
     * @param secretKey signing key
     * @return user's public id in String
     */
    public static String getUserPublicId(@NonNull final String jwt,@NonNull final String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    /**
     * decapsulate user public Id from the given JWT
     * @param jwt token
     * @return public Id
     */
    public static UUID getUserId(@NonNull final String jwt) {
        String uuid = getSub(jwt);
        return UUID.fromString(uuid);
    }

    /**
     * decapsulate iis value from the given JWT
     * @param jwt token
     * @return iis value
     */
    public static String getIss(@NonNull final String jwt) {
        JSONObject payload = getPayload(jwt);
        return (String) payload.get("iss");
    }

    /**
     * decapsulate aud value from the given JWT
     * @param jwt token
     * @return aud value
     */
    public static String getAud(@NonNull final String jwt) {
        JSONObject payload = getPayload(jwt);
        return (String) payload.get("aud");
    }

    /**
     * decapsulate sub value from the given JWT
     * @param jwt token
     * @return sub value
     */
    public static String getSub(@NonNull final String jwt) {
        JSONObject payload = getPayload(jwt);
        return (String) payload.get("sub");
    }

    public static List<UserRoleType> getRoles(@NonNull final String jwt) {
        JSONObject payload = getPayload(jwt);
        List<String> jsonRole = (List<String>) payload.get("roles");
        return jsonRole.stream().map(UserRoleType::valueOf).collect(Collectors.toList());
    }


    /**
     * validate the JWT token
     * @param jwt JWT token
     * @return true if the token is valid
     */
    public boolean validateToken(@NonNull final String jwt) {
        try {
            log.debug("[JwtTokenProvider >> validateToken]");
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(jwt);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new TMSPlatformException(TMSException.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new TMSPlatformException(TMSException.INVALID_TOKEN);
        }
    }

    /**
     * returns HMAC signing key based on the length of the secretKey
     * @param secretKey secretKey
     * @return HMAC signing key
     */
    private static Key getSigningKey(@NonNull final String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * set roles in the JWT payload
     * @param claims payload
     * @param roles list of roles for the user
     */
    private void setRolesInPayload(@NonNull final Claims claims, @NonNull final List<String> roles) {
        claims.put("roles", roles);
    }

    /**
     * create JTW header
     * @return JTW headers
     */
    private Map<String, Object> createJWTHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", SIGNATURE_ALGORITHM.getValue());
        header.put("typ", TOKEN_TYPE);
        return header;
    }

    /**
     * validate the timestamp is the past
     * @param now timestamp
     */
    private void validatePastTimestamp(@NonNull final Long now) {
        if(Boolean.FALSE.equals(isPast(now))) {
            throw new IllegalArgumentException("the timestamp should be the past");
        }
    }

    /**
     * returns true if the given timestamp is the past
     */
    private boolean isPast(@NonNull final Long now) {
        return (System.currentTimeMillis() >= now);
    }
}
