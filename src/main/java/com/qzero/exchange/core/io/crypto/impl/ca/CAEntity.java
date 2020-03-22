package com.qzero.exchange.core.io.crypto.impl.ca;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

public class CAEntity {

    @JSONField(name = "identity")
    private String identity;
    @JSONField(name = "remotePublicKeyHash")
    private byte[] remotePublicKeyHash;
    @JSONField(name = "endTime")
    private long endTime;

    @JSONField(name = "signaturePublicKey")
    private String signaturePublicKey;
    @JSONField(name = "signature")
    private byte[] signature;

    public CAEntity() {
    }

    public CAEntity(String identity, byte[] remotePublicKeyHash, long endTime, String signaturePublicKey, byte[] signature) {
        this.identity = identity;
        this.remotePublicKeyHash = remotePublicKeyHash;
        this.endTime = endTime;
        this.signaturePublicKey = signaturePublicKey;
        this.signature = signature;
    }

    public byte[] getRemotePublicKeyHash() {
        return remotePublicKeyHash;
    }

    public void setRemotePublicKeyHash(byte[] remotePublicKeyHash) {
        this.remotePublicKeyHash = remotePublicKeyHash;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getSignaturePublicKey() {
        return signaturePublicKey;
    }

    public void setSignaturePublicKey(String signaturePublicKey) {
        this.signaturePublicKey = signaturePublicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "CAEntity{" +
                "identity='" + identity + '\'' +
                ", remotePublicKeyHash=" + Arrays.toString(remotePublicKeyHash) +
                ", endTime=" + endTime +
                ", signaturePublicKey='" + signaturePublicKey + '\'' +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
