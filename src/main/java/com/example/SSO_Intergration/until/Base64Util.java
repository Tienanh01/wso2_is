package com.example.SSO_Intergration.until;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    // Encode a string to Base64
    public static String encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    // Overloaded method to encode byte array
    public static String encode(byte[] inputBytes) {
        return Base64.getEncoder().encodeToString(inputBytes);
    }

    // Decode a Base64 string back to its original form
    public static String decode(String encoded) {
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
