package net.ws.go.plugin.task.powershell.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.ws.go.plugin.task.powershell.config.model.CpuArchitecture;
import net.ws.go.plugin.task.powershell.config.model.ErrorActions;
import net.ws.go.plugin.task.powershell.config.model.Field;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import net.ws.go.plugin.task.powershell.Constants;

public class PluginConfig {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    private static final Field ERROR_ACTION = new Field(Constants.ERROR_ACTION_KEY, "Error Action Preference",
            ErrorActions.STOP.getActionString(), true, false, "0");
    private static final Field SCRIPT = new Field(Constants.SCRIPT_KEY, "Script",
            null, true, false, "1");
    private static final Field CPU_ARCHITECTURE = new Field(Constants.CPU_ARCHITECTURE_KEY, "CPU Architecture",
            CpuArchitecture.X64.name().toLowerCase(), true, false, "2");

    static {
        FIELDS.put(ERROR_ACTION.getKey(), ERROR_ACTION);
        FIELDS.put(SCRIPT.getKey(), SCRIPT);
        FIELDS.put(CPU_ARCHITECTURE.getKey(), CPU_ARCHITECTURE);
    }

    public static GoPluginApiResponse getConfiguration() throws Exception {
        Map<String, Object> map = getKeyValueMap();
        return DefaultGoPluginApiResponse.success(map.size() > 0 ? GSON.toJson(map) : null);
    }

    private static Map<String, Object> getKeyValueMap() {
        Map<String, Object> keyValueMap = new HashMap<>();
        keyValueMap.putAll(FIELDS);
        return keyValueMap;
    }
}
