package com.westeros.diagnostics.runners;

import com.westeros.diagnostics.services.contract.Diagnostics;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DiskSpaceDiagnostics implements IDiagnose {

    private static final double REQUIRED_FREE_SPACE_PERCENTAGE = 0.05;
    private static final long BYTES_IN_MB = 1024 * 1024;

    @Override
    public String getName() {
        return "Disk Space Diagnostics";
    }

    @Override
    public String getDescription() {
        return "Checks the available disk space.";
    }

    @Override
    public Diagnostics run() {
        Diagnostics diagnostics = new Diagnostics();
        diagnostics.setName(getName());
        diagnostics.setDescription(getDescription());

        try {
            File root = new File(".");
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();

            // Zabezpieczenie przed totalSpace == 0
            double freePercentage = (totalSpace > 0) ? ((double) freeSpace / totalSpace) * 100 : 0;
            long freeSpaceMb = freeSpace / BYTES_IN_MB;

            boolean isSpaceSufficient = freePercentage > (REQUIRED_FREE_SPACE_PERCENTAGE * 100);

            diagnostics.setSuccess(isSpaceSufficient);

            String statusPrefix = isSpaceSufficient ? "OK. Required > 5%." : "Low disk space. Required > 5%.";
            diagnostics.setErrorMessage(String.format("%s Free space: %d MB (%.2f%%).",
                    statusPrefix,
                    freeSpaceMb,
                    freePercentage));

        } catch (Exception e) {
            diagnostics.setSuccess(false);
            diagnostics.setErrorMessage("Failed to check disk space: " + e.getMessage());
        }

        return diagnostics;
    }
}