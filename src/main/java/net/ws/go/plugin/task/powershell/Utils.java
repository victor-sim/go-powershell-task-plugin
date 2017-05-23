package net.ws.go.plugin.task.powershell;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import net.ws.go.plugin.task.powershell.config.model.CpuArchitecture;

import com.google.common.io.CharStreams;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

final class Utils {
    private static final HashMap<CpuArchitecture, String> PS_PATH = new HashMap<>();
    private static final String PS_PATH_TEMPLATE = "%s\\%s\\WindowsPowerShell\\v1.0\\powershell.exe";

    static {
        String arch = System.getProperty("os.arch");
        String sysRoot = System.getenv("SystemRoot");
        sysRoot = StringUtils.isEmpty(sysRoot) ? "C:\\WINDOWS" : sysRoot;

        if (arch.contains("64")) {
            PS_PATH.put(CpuArchitecture.X86, String.format(PS_PATH_TEMPLATE, sysRoot, "SysWOW64"));
            PS_PATH.put(CpuArchitecture.X64, String.format(PS_PATH_TEMPLATE, sysRoot, "system32"));
        } else {
            PS_PATH.put(CpuArchitecture.X86, String.format(PS_PATH_TEMPLATE, sysRoot, "system32"));
            PS_PATH.put(CpuArchitecture.X64, String.format(PS_PATH_TEMPLATE, sysRoot, "sysnative"));
        }
    }

    private Utils() {

    }

    static String getPowerShellPath(CpuArchitecture cpuArchitecture) {
        return PS_PATH.get(cpuArchitecture);
    }

    static boolean isWindows() {
        String osName = System.getProperty("os.name");
        boolean isWindows = StringUtils.containsIgnoreCase(osName, "windows");
        JobConsoleLogger.getConsoleLogger().printLine("[script-executor] OS detected: '" + osName + "'. Is Windows? " + isWindows);
        return isWindows;
    }

    static String readResource(String resourceFile) {
        try (InputStreamReader reader = new InputStreamReader(Utils.class.getResourceAsStream(resourceFile), StandardCharsets.UTF_8)) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            PowerShellTask.LOG.error("Could not find resource " + resourceFile, e);
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    static String getScriptPath(String workingDirectory, String scriptFileName) {
        return workingDirectory + "/" + scriptFileName;
    }

    static String cleanupScript(String scriptValue) {
        return scriptValue.replaceAll("(\\r\\n|\\n|\\r)", System.getProperty("line.separator"));
    }

    static void deleteFile(String scriptFile) {
        if (!StringUtils.isBlank(scriptFile)) {
            FileUtils.deleteQuietly(new File(scriptFile));
        }
    }
}
