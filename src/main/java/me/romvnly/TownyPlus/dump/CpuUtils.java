package me.romvnly.TownyPlus.dump;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public final class CpuUtils {

    public static String tryGetProcessorName() {
        try {
            if (new File("/proc/cpuinfo").canRead()) {
                return getLinuxProcessorName();
            } else {
                return getWindowsProcessorName();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Much of the code here was copied from the OSHI project. This is simply stripped down to only get the CPU model.
     * https://github.com/oshi/oshi/
     */
    private static String getLinuxProcessorName() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("/proc/cpuinfo"), StandardCharsets.UTF_8);
        Pattern whitespaceColonWhitespace = Pattern.compile("\\s+:\\s"); // From ParseUtil
        for (String line : lines) {
            String[] splitLine = whitespaceColonWhitespace.split(line);
            if ("model name".equals(splitLine[0]) || "Processor".equals(splitLine[0])) {
                return splitLine[1];
            }
        }
        return "unknown";
    }

    /**
     * https://stackoverflow.com/a/6327663
     */
    private static String getWindowsProcessorName() throws Exception {
        final String cpuNameCmd = "reg query \"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\" /v ProcessorNameString";
        final String regstrToken = "REG_SZ";

        Process process = Runtime.getRuntime().exec(cpuNameCmd);
        process.waitFor();
        InputStream is = process.getInputStream();

        StringBuilder sb = new StringBuilder();
        while (is.available() != 0) {
            sb.append((char) is.read());
        }

        String result = sb.toString();
        int p = result.indexOf(regstrToken);

        if (p == -1) {
            return null;
        }

        return result.substring(p + regstrToken.length()).trim();
    }

    private CpuUtils() {
    }
}