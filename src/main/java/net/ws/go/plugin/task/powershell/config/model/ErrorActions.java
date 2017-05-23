package net.ws.go.plugin.task.powershell.config.model;

import org.apache.commons.lang3.StringUtils;

public enum ErrorActions {
    STOP(Constants.STOP),
    CONTINUE(Constants.CONTINUE),
    IGNORE(Constants.IGNORE),
    SILENTLY_CONTINUE(Constants.SILENTLY_CONTINUE),
    SUSPEND(Constants.SUSPEND),
    INQUIRE(Constants.INQUIRE);

    private final String actionString;

    ErrorActions(String actionString) {
        this.actionString = actionString;
    }

    public static ErrorActions fromString(String actionType) {
        if(!StringUtils.isEmpty(actionType)) {
            for(ErrorActions action : ErrorActions.values()) {
                if(StringUtils.equalsIgnoreCase(action.actionString, actionType)) {
                    return action;
                }
            }
        }

        return null;
    }

    public String getActionString() {
        return actionString;
    }

    private static class Constants {
        static final String STOP = "Stop";
        static final String CONTINUE = "Continue";
        static final String IGNORE = "Ignore";
        static final String SILENTLY_CONTINUE = "SilentlyContinue";
        static final String SUSPEND = "Suspend";
        static final String INQUIRE = "Inquire";

        private Constants() {

        }
    }
}
