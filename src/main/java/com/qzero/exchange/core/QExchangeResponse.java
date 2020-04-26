package com.qzero.exchange.core;

import com.qzero.exchange.core.coder.ParameterCoder;
import com.qzero.exchange.core.utils.UUIDUtils;

import java.util.Map;

public class QExchangeResponse extends QExchangeAction {

    public static final String STATUS_CODE_SUCCEEDED="succeeded";
    public static final String STATUS_CODE_FAILED="failed";

    private String statusCode;

    /**
     *
     * @param actionName
     * @param isSucceeded 是否成功
     * @param parameter
     */
    public QExchangeResponse(String actionName,boolean isSucceeded,Object parameter) {
        this(actionName,isSucceeded?STATUS_CODE_SUCCEEDED:STATUS_CODE_FAILED,parameter);
    }


    public QExchangeResponse(String actionName,String statusCode,Object parameter) {
        this(actionName,new ParameterCoder().encodeParameter(parameter),statusCode);
    }

    public QExchangeResponse(String actionName, Map<String, QExchangeParameter> parameterMap, String statusCode) {
        this(UUIDUtils.getRandomUUID(),actionName,parameterMap,statusCode);
    }

    public QExchangeResponse(String actionId, String actionName, Map<String, QExchangeParameter> parameterMap, String statusCode) {
        super(actionId, actionName, parameterMap);
        this.statusCode = statusCode;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ACTION_TYPE_RESPONSE;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSucceeded(){
        if(statusCode==null)
            return false;
        switch (statusCode){
            case STATUS_CODE_FAILED:
                return false;
            case STATUS_CODE_SUCCEEDED:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "QExchangeResponse{" +
                "statusCode='" + statusCode + '\'' +
                ", actionId='" + actionId + '\'' +
                ", actionName='" + actionName + '\'' +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
