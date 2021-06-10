package com.sparkystudios.traklibrary.security;

import java.security.Key;

public interface KeyService {

    Key readPublicKey(String publicKey);

    Key readPrivateKey(String privateKey);
}
