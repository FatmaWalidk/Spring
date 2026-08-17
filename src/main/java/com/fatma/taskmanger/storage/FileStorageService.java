package com.fatma.taskmanger.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * "Program to an interface, not an implementation." The controller only
 * ever depends on this interface - swapping local disk storage for AWS S3
 * later means adding a new implementation, not touching any controller.
 */
public interface FileStorageService {

    String store(MultipartFile file);

    Resource retrieve(String fileName);

    void delete(String fileName);
}
