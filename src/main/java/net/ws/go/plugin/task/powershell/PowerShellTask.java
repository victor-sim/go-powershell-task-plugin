package net.ws.go.plugin.task.powershell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ws.go.plugin.task.powershell.config.PluginConfig;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.http.HttpStatus;

import static net.ws.go.plugin.task.powershell.Constants.*;
import static net.ws.go.plugin.task.powershell.PowerShellTaskExtension.*;
import static net.ws.go.plugin.task.powershell.Utils.isWindows;

@Extension
public class PowerShellTask implements GoPlugin {
    static final Logger LOG = Logger.getLoggerFor(PowerShellTask.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        // do nothing
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
        GoPluginApiResponse response = createGoResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Should not reachable.");
        LOG.info(String.format("Get request [%s] %n body[%s]", requestMessage.requestName(), requestMessage.requestBody()));

        try {
            if (requestMessage.requestName().equals(REQUEST_CONFIGURATION)) {
                response = PluginConfig.getConfiguration();
            } else if (requestMessage.requestName().equals(REQUEST_VALIDATION)) {
                Map<String, Object> res = new HashMap<>();
                response = createGoResponse(HttpStatus.SC_OK, res);
            } else if (requestMessage.requestName().equals(REQUEST_TASK_VIEW)) {
                response = handleView();
            } else if (requestMessage.requestName().equals(REQUEST_EXECUTION)) {
                response = handleExecute(requestMessage.requestBody());
            } else {
                LOG.error(String.format("UnhandledRequestTypeException - requestName::%s", requestMessage.requestName()));
                throw new UnhandledRequestTypeException(requestMessage.requestName());
            }
        } catch (Exception e) {
            LOG.error("Exception on handling", e);
        }

        LOG.info("Response" + response.responseCode());
        return response;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PLUGIN_IDENTIFIER;
    }


    private GoPluginApiResponse handleExecute(String requestBody) {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS, false);
        if (isWindows()) {
            String scriptFilePath = null;

            try {

                Map<String, Map> requestMap = new GsonBuilder().create().fromJson(requestBody, Map.class);

                scriptFilePath = creteTempScriptFile(requestMap);
                int exitCode = executeScript(requestMap, scriptFilePath);

                if (exitCode == 0) {
                    response.put(SUCCESS, true);
                    addMessage(response, "Script completed successfully.");
                } else {
                    addMessage(response, "Script completed with exit code: [" + exitCode + "].");
                }

            } catch (Exception e) {
                addMessage(response, "Script execution interrupted. Reason::" + e);
            }
            Utils.deleteFile(scriptFilePath);

        } else {
            addMessage(response, "Assigned agent is not Windows machine.");
        }

        return createGoResponse(HttpStatus.SC_OK, response);
    }

    private GoPluginApiResponse handleView() throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("displayValue", "PowerShell");
        response.put("template", Utils.readResource("/task.template.html"));
        return createGoResponse(HttpStatus.SC_OK, response);
    }
}
