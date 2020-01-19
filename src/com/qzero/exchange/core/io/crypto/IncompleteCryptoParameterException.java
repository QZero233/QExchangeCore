package com.qzero.exchange.core.io.crypto;

public class IncompleteCryptoParameterException extends IllegalArgumentException {

    public enum CryptoAction{
        ACTION_ENCRYPT("Encrypt"),
        ACTION_DECRYPT("Decrypt");

        private String name;

        CryptoAction(String name) {
            this.name = name;
        }

    }

    private String cryptoModuleName;
    private CryptoAction action;

    public IncompleteCryptoParameterException(String cryptoModuleName, CryptoAction action) {
        this.cryptoModuleName = cryptoModuleName;
        this.action = action;
    }

    @Override
    public String getMessage() {
        String message="Exception caused by incomplete parameter when executing crypto action \nAction: \""+action.name+"\"";
        message+="\nModule:\""+cryptoModuleName+"\"";
        return message;
    }
}
