package com.example.SSO_Intergration.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

public class JwtService {


    public static DecodedJWT getClaims(String token) {
         return JWT.decode(token);

    }
}
