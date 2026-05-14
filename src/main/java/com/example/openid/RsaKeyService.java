package com.example.openid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates and holds an RSA-2048 key pair for the lifetime of the application.
 * The same key pair is reused for all token signing and JWKS exposure.
 */
@Component
public class RsaKeyService {

    private final KeyPair keyPair;
    private final String keyId = "sample-key-1";

    public RsaKeyService() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            this.keyPair = gen.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getKeyId() {
        return keyId;
    }

    /**
     * Returns the public key in JWK (JSON Web Key) format.
     */
    public Map<String, Object> toJwk() {
        RSAPublicKey pub = (RSAPublicKey) keyPair.getPublic();
        Map<String, Object> jwk = new LinkedHashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("use", "sig");
        jwk.put("alg", "RS256");
        jwk.put("kid", keyId);
        jwk.put("n", Base64.getUrlEncoder().withoutPadding()
                .encodeToString(pub.getModulus().toByteArray()));
        jwk.put("e", Base64.getUrlEncoder().withoutPadding()
                .encodeToString(pub.getPublicExponent().toByteArray()));
        return jwk;
    }

    public Map<String, Object> toJwkSet() {
        Map<String, Object> jwks = new LinkedHashMap<>();
        jwks.put("keys", List.of(toJwk()));
        return jwks;
    }
}
