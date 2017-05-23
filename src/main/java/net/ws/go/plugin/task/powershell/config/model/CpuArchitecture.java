package net.ws.go.plugin.task.powershell.config.model;

import org.apache.commons.lang3.StringUtils;

public enum CpuArchitecture {
    X86,
    X64;

    CpuArchitecture() {

    }

    public static CpuArchitecture fromString(String architectureName) {
        if (!StringUtils.isEmpty(architectureName)) {
            for (CpuArchitecture value : CpuArchitecture.values()) {
                if (StringUtils.equalsIgnoreCase(value.name(), architectureName)) {
                    return value;
                }
            }
        }

        return null;
    }
}
