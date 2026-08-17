package com.fatma.taskmanger.storage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Alternative implementation of FileStorageService, discussed in the
 * course as the reason FileStorageService is an interface in the first
 * place: swap storage backends without touching StorageController or
 * anything else that depends on FileStorageService.
 *
 * Not wired up yet (no @Primary) - to activate it instead of local disk
 * storage, either add @Primary here and remove it from
 * LocalFileStorageService, or inject it explicitly with:
 *   public StorageController(@Qualifier("s3FileStorageService") FileStorageService storageService)
 *
 * TODO: implement using the AWS SDK (software.amazon.awssdk:s3) once the
 * project is ready to move off local disk - store() would upload via
 * S3Client.putObject(...), retrieve() would return a Resource backed by
 * S3Client.getObject(...), delete() would call S3Client.deleteObject(...).
 */
@Service
public class S3FileStorageService implements FileStorageService {

    @Override
    public String store(MultipartFile file) {
        throw new UnsupportedOperationException("S3 storage not implemented yet.");
    }

    @Override
    public Resource retrieve(String fileName) {
        throw new UnsupportedOperationException("S3 storage not implemented yet.");
    }

    @Override
    public void delete(String fileName) {
        throw new UnsupportedOperationException("S3 storage not implemented yet.");
    }
}
