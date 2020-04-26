package com.qzero.exchange.core;

import com.qzero.exchange.core.utils.UUIDUtils;

import java.util.Map;

public class QExchangeRequest extends QExchangeAction {

    /**
     * 自动生成ID+用对象自动填充参数的版本
     * @param actionName
     * @param parameter
     */
    public QExchangeRequest(String actionName, Object parameter) {
        super(UUIDUtils.getRandomUUID(), actionName, null);
        addParameterInObject(parameter);
    }

    /**
     * 系统自动生成ID的版本
     * @param actionName
     * @param parameterMap
     */
    public QExchangeRequest(String actionName, Map<String, QExchangeParameter> parameterMap) {
        super(UUIDUtils.getRandomUUID(), actionName, parameterMap);
    }

    public QExchangeRequest(String actionId, String actionName, Map<String, QExchangeParameter> parameterMap) {
        super(actionId, actionName, parameterMap);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ACTION_TYPE_REQUEST;
    }
}
