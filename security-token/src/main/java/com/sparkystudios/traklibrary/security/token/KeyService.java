package com.sparkystudios.traklibrary.security.token;

import java.security.Key;

public interface KeyService {

    Key readPublicKey(String publicKey);

    Key readPrivateKey(String privateKey);
}
