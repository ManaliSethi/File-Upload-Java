// FileController.java
package com.example.fileupload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {

    @Value("${upload.directory:/home/manali-sethi/Downloads/fileupload (1)/fileupload/src/main/resources}")
    private String uploadDirectory;

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // Handle empty file
            return "redirect:/error";
        }

        try {
            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the upload directory
            String filePath = uploadDirectory + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Redirect to home page after successful upload
            return "redirect:/";
        } catch (IOException e) {
            // Handle the exception
            return "redirect:/error";
        }
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> handleFileDownload(@RequestParam("fileName") String fileName) {
        try {
            // Load file as Resource
            Path filePath = Paths.get(uploadDirectory).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists
            if (resource.exists()) {
                // Set Content-Disposition header to trigger download
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());

                // Return ResponseEntity with file data and headers
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // Handle file not found
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // Handle MalformedURLException
            return ResponseEntity.status(500).build();
        }
    }
}
