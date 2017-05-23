package net.ws.go.plugin.task.powershell;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import net.ws.go.plugin.task.powershell.config.model.CpuArchitecture;
import net.ws.go.plugin.task.powershell.config.model.ErrorActions;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import static net.ws.go.plugin.task.powershell.Constants.*;

final class PowerShellTaskExtension {
    private PowerShellTaskExtension() {

    }

    static String creteTempScriptFile(Map<String, Map> requestMap) throws IOException {
        String fileName = createTempFileName(getEnvironments(requestMap));
        String workingDirectory = getWorkingDir(requestMap);
        String scriptValue = getConfig(requestMap, SCRIPT_KEY);
        String errorAction = getConfig(requestMap, ERROR_ACTION_KEY);
        if (StringUtils.isBlank(errorAction)) {
            errorAction = ErrorActions.SILENTLY_CONTINUE.getActionString();
        }

        File file = new File(Utils.getScriptPath(workingDirectory, fileName));
        StringBuilder script = new StringBuilder("$script:ErrorActionPreference = [System.Management.Automation.ActionPreference]::");
        script.append(ErrorActions.fromString(errorAction).getActionString()).append(System.getProperty("line.separator"));
        script.append(Utils.cleanupScript(scriptValue));
        FileUtils.writeStringToFile(file, script.toString());

        return file.getAbsolutePath();
    }

    static int executeScript(Map<String, Map> requestMap, String scriptFileName) throws IOException, InterruptedException {
        return executeCommand(getWorkingDir(requestMap),
                getEnvironments(requestMap),
                Utils.getPowerShellPath(CpuArchitecture.fromString(getConfig(requestMap, CPU_ARCHITECTURE_KEY))),
                "-ExecutionPolicy", "RemoteSigned", "-NonInteractive", "-Command",
                scriptFileName);
    }

    static void addMessage(Map<String, Object> response, String message) {
        JobConsoleLogger.getConsoleLogger().printLine(String.format("[PowerShell-executor] %s", message));
        response.put(MESSAGE, message);
    }

    static GoPluginApiResponse createGoResponse(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }

    private static Map<String, Map<String, String>> getConfigMap(Map<String, Map> requestMap) {
        return (Map<String, Map<String, String>>) requestMap.get("config");
    }

    private static Map<String, String> getEnvironments(Map<String, Map> requestMap) {
        return (Map<String, String>) getContext(requestMap).get("environmentVariables");
    }

    private static String getWorkingDir(Map<String, Map> requestMap) {
        return (String) getContext(requestMap).get("workingDirectory");
    }

    private static Map<String, Object> getContext(Map<String, Map> requestMap) {
        return (Map<String, Object>) requestMap.get("context");
    }

    private static String getConfig(Map<String, Map> requestMap, String key) {
        return getConfigMap(requestMap).get(key).get("value");
    }

    private static int executeCommand(String workingDirectory, Map<String, String> environmentVariables, String... command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(workingDirectory));
        if (environmentVariables != null && !environmentVariables.isEmpty()) {
            processBuilder.environment().putAll(environmentVariables);
        }
        Process process = processBuilder.start();

        JobConsoleLogger.getConsoleLogger().readOutputOf(process.getInputStream());
        JobConsoleLogger.getConsoleLogger().readErrorOf(process.getErrorStream());

        return process.waitFor();
    }

    private static String createTempFileName(Map<String, String> environmentVariables) {
        return String.format("%s%s_%s%s_%s.ps1",
                environmentVariables.get("GO_PIPELINE_NAME"), environmentVariables.get("GO_PIPELINE_LABEL"),
                environmentVariables.get("GO_STAGE_NAME"), environmentVariables.get("GO_STAGE_COUNTER"),
                environmentVariables.get("GO_JOB_NAME"));
    }
}
