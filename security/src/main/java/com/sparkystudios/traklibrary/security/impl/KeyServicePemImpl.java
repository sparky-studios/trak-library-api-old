package com.sparkystudios.traklibrary.security.impl;

import com.sparkystudios.traklibrary.security.KeyService;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.security.Key;

@Component
public class KeyServicePemImpl implements KeyService {

    @Override
    public Key readPublicKey(String publicKey) {
        try (var stringReader = new StringReader(publicKey)) {
            var pemParser = new PEMParser(stringReader);
            var publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());

            return new JcaPEMKeyConverter().getPublicKey(publicKeyInfo);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read public key for JWT decryption.", e);
        }
    }

    @Override
    public Key readPrivateKey(String privateKey) {
        try (var stringReader = new StringReader(privateKey)) {
            var pemParser = new PEMParser(stringReader);
            var pemKeyPair = (PEMKeyPair) pemParser.readObject();

            var privateKeyInfo = pemKeyPair.getPrivateKeyInfo();

            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read private key for JWT encryption.", e);
        }
    }
}
