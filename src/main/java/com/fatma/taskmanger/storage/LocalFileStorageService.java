package com.fatma.taskmanger.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Stores files on local disk. @Primary means this implementation is used
 * whenever FileStorageService is injected, unless a specific bean is
 * requested with @Qualifier (see S3FileStorageService for the alternative
 * implementation).
 *
 * Production hardening applied here (all covered in the course):
 *  - fails fast at startup if the upload directory can't be created
 *  - rejects empty files
 *  - cleans the filename and rejects ".." (directory traversal defense)
 *  - only keeps the extension from the original filename, discards the rest
 *  - validates the extension against an allow-list
 *  - generates a random UUID filename - never trusts the client's filename
 *  - streams the file via InputStream instead of loading it into memory
 *    (file.getBytes() would load the entire file into RAM; a large upload
 *    could exhaust server memory)
 *
 * Still to store separately (left as a TODO for the File/FileRepository
 * module the course was about to start): the mapping of
 * storedFilename (UUID) -> originalFilename, so downloads can present the
 * user with their original filename via Content-Disposition while the
 * disk itself only ever holds UUID-named files.
 */
@Service
@Primary
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "pdf");

    private final Path uploadPath;

    public LocalFileStorageService(FileStorageProperties properties) {
        this.uploadPath = Paths.get(properties.uploadDir());
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not initialize upload directory", exception);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String originalFilename = StringUtils.cleanPath(
                java.util.Objects.requireNonNull(file.getOriginalFilename())
        );

        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Invalid filename.");
        }

        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type.");
        }

        // Extensions and Content-Type headers can both be faked by a
        // client. For high-security applications, inspect the actual file
        // bytes with a library such as Apache Tika instead of trusting
        // either of them.

        String fileName = UUID.randomUUID() + "." + extension;
        Path destination = uploadPath.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), destination);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store file.", exception);
        }

        return fileName;
    }

    @Override
    public Resource retrieve(String fileName) {
        try {
            Path filePath = uploadPath.resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("File not found.");
            }
            return resource;
        } catch (MalformedURLException exception) {
            throw new IllegalStateException("Failed to load file.", exception);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            Files.deleteIfExists(uploadPath.resolve(fileName));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to delete file.", exception);
        }
    }
}
