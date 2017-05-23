package net.ws.go.plugin.task.powershell;

import java.util.Collections;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

public interface Constants {
    // The type of this extension
    String EXTENSION_TYPE = "task";

    // The extension point API version that this plugin understands
    String API_VERSION = "1.0";

    String REQUEST_CONFIGURATION = "configuration";
    String REQUEST_VALIDATION = "validate";
    String REQUEST_TASK_VIEW = "view";
    String REQUEST_EXECUTION = "execute";

    GoPluginIdentifier PLUGIN_IDENTIFIER = new GoPluginIdentifier(EXTENSION_TYPE, Collections.singletonList(API_VERSION));

    String ERROR_ACTION_KEY = "errorAction";
    String SCRIPT_KEY = "script";
    String CPU_ARCHITECTURE_KEY = "cpuArch";

    String SUCCESS = "success";
    String MESSAGE = "message";
}
