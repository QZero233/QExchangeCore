package com.qzero.exchange.core.io.crypto.utils;

public class RSAKeySet {
    private String publicKeyImPem;
    private String privateKeyInPem;

    public RSAKeySet() {
    }

    public RSAKeySet(String publicKeyImPem, String privateKeyInPem) {
        this.publicKeyImPem = publicKeyImPem;
        this.privateKeyInPem = privateKeyInPem;
    }

    public String getPublicKeyImPem() {
        return publicKeyImPem;
    }

    public void setPublicKeyImPem(String publicKeyImPem) {
        this.publicKeyImPem = publicKeyImPem;
    }

    public String getPrivateKeyInPem() {
        return privateKeyInPem;
    }

    public void setPrivateKeyInPem(String pri) {
        this.privateKeyInPem = pri;
    }

    @Override
    public String toString() {
        return "RSAKeySet{" +
                "publicKeyImPem='" + publicKeyImPem + '\'' +
                ", privateKeyInPem='" + privateKeyInPem + '\'' +
                '}';
    }
}
