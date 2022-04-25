package com.mikorpar.brbljavac_api.controllers;

import com.mikorpar.brbljavac_api.data.dtos.files.FileCreateResDTO;
import com.mikorpar.brbljavac_api.data.models.File;
import com.mikorpar.brbljavac_api.exceptions.files.FileNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.files.UserNotFileOwnerException;
import com.mikorpar.brbljavac_api.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Size;
import java.io.IOException;

@Validated
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final String UNRECOGNIZED_TYPE = "application/octet-stream";
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileCreateResDTO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(new FileCreateResDTO(fileService.storeFile(file)));
    }

    @GetMapping
    public ResponseEntity<ByteArrayResource> getFile(
        @Size(min = 24, max = 24, message = "Must contain 24 characters")
        @RequestParam String id
    ) throws IOException {
        try {
            File file = fileService.getFile(id);
            String contentType = file.getFiletype() != null ? file.getFiletype() : UNRECOGNIZED_TYPE;
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(new ByteArrayResource(file.getData()));
        } catch (FileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(
            @Size(min = 24, max = 24, message = "Must contain 24 characters")
            @PathVariable("id") String id
    ) {
        try {
            fileService.deleteFile(id);
        } catch (FileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (UserNotFileOwnerException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
