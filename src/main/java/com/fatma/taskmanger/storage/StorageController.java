package com.fatma.taskmanger.storage;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class StorageController {

    private final FileStorageService storageService;

    public StorageController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource resource = storageService.retrieve(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public void delete(@PathVariable String filename) {
        storageService.delete(filename);
    }
}
