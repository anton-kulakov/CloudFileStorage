package dev.anton_kulakov.service;

import dev.anton_kulakov.dto.ResourceInfoDto;
import dev.anton_kulakov.dto.ResourceTypeEnum;
import dev.anton_kulakov.exception.MinioException;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class MinioHelper {
    private final MinioClient minioClient;
    private final PathHelper pathHelper;
    private static final String BUCKET_NAME = "user-files";

    public StatObjectResponse getResourceInfo(String resourcePath) {
        StatObjectResponse statObjectResponse;

        try {
            statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(resourcePath)
                    .build());
        } catch (Exception e) {
            throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
        }

        return statObjectResponse;
    }

    public ResourceInfoDto convertToFileDto(StatObjectResponse statObjectResponse) {
        ResourceInfoDto resourceInfoDto = new ResourceInfoDto();
        resourceInfoDto.setPath(statObjectResponse.object());
        resourceInfoDto.setName(Paths.get(statObjectResponse.object()).getFileName().toString());
        resourceInfoDto.setSize(statObjectResponse.size());
        resourceInfoDto.setType(ResourceTypeEnum.FILE);

        return resourceInfoDto;
    }

    public ResourceInfoDto convertToFolderDto(String folderPath) {
        ResourceInfoDto resourceInfoDto = new ResourceInfoDto();
        resourceInfoDto.setPath(folderPath);
        resourceInfoDto.setName(pathHelper.getLastFolderName(folderPath));
        resourceInfoDto.setType(ResourceTypeEnum.DIRECTORY);

        return resourceInfoDto;
    }
}
