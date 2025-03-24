package dev.anton_kulakov.service;

import dev.anton_kulakov.dto.ResourceInfoDto;
import dev.anton_kulakov.exception.MinioException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FileService implements ResourceServiceInterface {
    private final MinioClient minioClient;
    private final MinioHelper minioHelper;
    private final PathHelper pathHelper;
    private static final String BUCKET_NAME = "user-files";


    @Override
    public ResourceInfoDto getInfo(String fileName) {
        StatObjectResponse resourceInfo = minioHelper.getResourceInfo(fileName);
        return minioHelper.convertToFileDto(resourceInfo);
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
        }
    }

    @Override
    public void move(String from, String to) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(to)
                    .source(CopySource.builder()
                            .bucket(BUCKET_NAME)
                            .object(from)
                            .build())
                    .build());
        } catch (Exception e) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(to)
                        .build());
            } catch (Exception ex) {
                throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
            }

            throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
        }
    }

    public List<ResourceInfoDto> search(String query) {
        Iterable<Result<Item>> allResources = getAllResources();
        ArrayList<ResourceInfoDto> foundFiles = new ArrayList<>();

        for (Result<Item> resource : allResources) {
            try {
                String fileName = pathHelper.getFileName(resource.get().objectName());

                if (isFile(resource) && fileName.contains(query)) {
                    Item foundedFile = resource.get();
                    foundFiles.add(minioHelper.convertToFileDto(foundedFile));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return foundFiles;
    }

    private static boolean isFile(Result<Item> resource) {
        try {
            return !resource.get().objectName().endsWith("/");
        } catch (Exception e) {
            throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
        }
    }

    public Iterable<Result<Item>> getAllResources() {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(BUCKET_NAME)
                .recursive(true)
                .build());
    }

    public void streamFile(String fileName, Consumer<InputStream> streamConsumer) {
        try (InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(fileName)
                .build())) {
            streamConsumer.accept(inputStream);
        } catch (Exception e) {
            throw new MinioException("The MinIO service is currently unavailable. Please check the service status and try again later");
        }
    }
}
