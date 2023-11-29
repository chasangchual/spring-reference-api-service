package com.surefor.service.common.utils;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Hasher {
    public static String hashString(final String inStr) {
        return Hashing.sha256().hashString(inStr, StandardCharsets.UTF_8).toString();
    }
}
