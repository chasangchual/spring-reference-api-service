package com.surefor.service.common.authentication;

import com.surefor.service.common.exception.TMSPlatformException;
import com.surefor.service.domain.user.UserRoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest(classes = {JwtTokenProvider.class})
@ComponentScan(basePackages = {"com.swidch.tms"})
class JwtTokenProviderTest {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void succeedToCreateAccessToken() {
        UUID userID = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();

        String jwt = jwtTokenProvider.createAccessToken(userID, List.of(UserRoleType.ADMIN),timestamp);

        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);
        assertTrue(JwtTokenProvider.isJWT(jwt));
        assertEquals(userID.toString(), JwtTokenProvider.getSub(jwt));
        assertEquals(userID, JwtTokenProvider.getUserId(jwt));
        assertEquals((int)((timestamp) / 1000), JwtTokenProvider.getIssuedAt(jwt));
        assertEquals((int)((timestamp + JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_TIME_MILLIS) / 1000), JwtTokenProvider.getExpiresAt(jwt));
        assertEquals(1, JwtTokenProvider.getRoles(jwt).size());
        assertEquals(UserRoleType.ADMIN, JwtTokenProvider.getRoles(jwt).get(0));
    }

    @Test
    void succeedToCreateRefreshToken() {
        UUID userID = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();

        String jwt = jwtTokenProvider.createRefreshToken(userID, List.of(UserRoleType.ADMIN),timestamp);

        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);
        assertTrue(JwtTokenProvider.isJWT(jwt));
        assertEquals(userID.toString(), JwtTokenProvider.getSub(jwt));
        assertEquals(userID, JwtTokenProvider.getUserId(jwt));
        assertEquals((int)((timestamp) / 1000), JwtTokenProvider.getIssuedAt(jwt));
        assertEquals((int)((timestamp + JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS) / 1000), JwtTokenProvider.getExpiresAt(jwt));
        assertEquals(1, JwtTokenProvider.getRoles(jwt).size());
        assertEquals(UserRoleType.ADMIN, JwtTokenProvider.getRoles(jwt).get(0));
    }

    @Test
    void succeedToRotateAccessToken() throws InterruptedException {
        UUID userID = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();

        String jwt = jwtTokenProvider.createAccessToken(userID, List.of(UserRoleType.ADMIN),timestamp);
        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);

        Thread.sleep(5 * 1000);

        long timestampRotate = System.currentTimeMillis();
        jwt = jwtTokenProvider.rotateAccessToken(jwt, timestampRotate);

        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);
        assertTrue(JwtTokenProvider.isJWT(jwt));
        assertEquals(userID.toString(), JwtTokenProvider.getSub(jwt));
        assertEquals(userID, JwtTokenProvider.getUserId(jwt));
        assertEquals((int)((timestampRotate) / 1000), JwtTokenProvider.getIssuedAt(jwt));
        assertEquals((int)((timestampRotate + JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_TIME_MILLIS) / 1000), JwtTokenProvider.getExpiresAt(jwt));
        assertEquals(1, JwtTokenProvider.getRoles(jwt).size());
        assertEquals(UserRoleType.ADMIN, JwtTokenProvider.getRoles(jwt).get(0));
    }

    @Test
    void succeedToRotateRefreshToken() throws InterruptedException {
        UUID userID = UUID.randomUUID();
        long timestamp = System.currentTimeMillis();

        String jwt = jwtTokenProvider.createRefreshToken(userID, List.of(UserRoleType.ADMIN),timestamp);
        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);

        Thread.sleep(5 * 1000);

        long timestampRotate = System.currentTimeMillis();
        jwt = jwtTokenProvider.rotateRefreshToken(jwt, timestampRotate);

        assertNotNull(jwt);
        JwtTokenProvider.validateJWT(jwt);
        assertTrue(JwtTokenProvider.isJWT(jwt));
        assertEquals(userID.toString(), JwtTokenProvider.getSub(jwt));
        assertEquals(userID, JwtTokenProvider.getUserId(jwt));
        assertEquals((int)((timestampRotate) / 1000), JwtTokenProvider.getIssuedAt(jwt));
        assertEquals((int)((timestampRotate + JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS) / 1000), JwtTokenProvider.getExpiresAt(jwt));
        assertEquals(1, JwtTokenProvider.getRoles(jwt).size());
        assertEquals(UserRoleType.ADMIN, JwtTokenProvider.getRoles(jwt).get(0));
    }

    @Test
    void failToRotateExpiredAccessToken() throws InterruptedException {
        Exception exception = assertThrows(TMSPlatformException.class, () -> {
            UUID userID = UUID.randomUUID();
            long timestamp = System.currentTimeMillis() - JwtTokenProvider.ACCESS_TOKEN_EXPIRATION_TIME_MILLIS - 5000;

            String jwt = jwtTokenProvider.createAccessToken(userID, List.of(UserRoleType.ADMIN),timestamp);
            assertNotNull(jwt);
            JwtTokenProvider.validateJWT(jwt);

            long timestampRotate = System.currentTimeMillis();
            jwt = jwtTokenProvider.rotateAccessToken(jwt, timestampRotate);
        });
        assertTrue(exception.getLocalizedMessage().toLowerCase().contains("expired token"));
    }

    @Test
    void failToRotateExpiredRefreshToken() throws InterruptedException {
        Exception exception = assertThrows(TMSPlatformException.class, () -> {
            UUID userID = UUID.randomUUID();
            long timestamp = System.currentTimeMillis() - JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME_MILLIS  - 5000;

            String jwt = jwtTokenProvider.createRefreshToken(userID, List.of(UserRoleType.ADMIN),timestamp);
            assertNotNull(jwt);
            JwtTokenProvider.validateJWT(jwt);

            long timestampRotate = System.currentTimeMillis();
            jwt = jwtTokenProvider.rotateRefreshToken(jwt, timestampRotate);
        });
        assertTrue(exception.getLocalizedMessage().toLowerCase().contains("expired token"));
    }
}