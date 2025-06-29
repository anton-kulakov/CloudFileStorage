package dev.anton_kulakov.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PathProcessor {
    public String getLastFolderName(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }

        List<String> clearedOfEmptyPartsName = Arrays.stream(path.split("/"))
                .filter(s -> !s.isBlank())
                .toList();

        if (clearedOfEmptyPartsName.isEmpty()) {
            return "";
        }

        return clearedOfEmptyPartsName.get(clearedOfEmptyPartsName.size() - 1) + "/";
    }

    public String getRelativePath(String pathWithoutResourceName, String fullResourcePath) {
        return fullResourcePath.substring(pathWithoutResourceName.length());
    }

    public String getFileName(String objectName) {
        String[] splitObjectName;

        try {
            splitObjectName = objectName.split("/");
        } catch (Exception e) {
            log.error("Failed to split object name '{}' due to an unexpected exception", objectName, e);
            throw new RuntimeException(e);
        }

        return splitObjectName[splitObjectName.length - 1];
    }

    public String getFolderPath(String fullPath, String fileName) {

        if (fullPath.endsWith(fileName)) {
            int lastSlashIndex = fullPath.lastIndexOf("/");

            if (lastSlashIndex != -1) {
                return fullPath.substring(0, lastSlashIndex + 1);
            }
        }

        return fullPath;
    }

    public String getUserRootFolder(int userId) {
        return "user-" + userId + "-files/";
    }

    public String getPathWithUserRootFolder(String path, String userRootFolder) {
        if (!path.contains(userRootFolder)) {
            path = userRootFolder + path;
        }

        return path;
    }

    public String getPathWithoutLastFolder(String fullPath, String folderName) {
        int fullPathLength = fullPath.length();
        int folderNameLength = folderName.length();

        return fullPath.substring(0, fullPathLength - folderNameLength);
    }
}
