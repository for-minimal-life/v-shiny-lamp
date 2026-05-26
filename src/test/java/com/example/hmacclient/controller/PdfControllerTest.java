package com.example.hmacclient.controller;

import com.example.hmacclient.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class, properties = {
        "api.server.url=https://mock.api.example.com",
        "api.secret.key=testSecretKey"
})
@AutoConfigureMockMvc
public class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String UPLOAD_DIR = "uploads/";

    @AfterEach
    public void cleanup() {
        File file = new File(UPLOAD_DIR + "test-document.pdf");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testUploadValidPdf() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/pdf/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully uploaded PDF file: test-document.pdf"));
    }

    @Test
    public void testUploadInvalidFileType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.txt",
                "text/plain",
                "Dummy Text content".getBytes()
        );

        mockMvc.perform(multipart("/api/pdf/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Only PDF files are allowed."));
    }

    @Test
    public void testUploadEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/pdf/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please select a file to upload."));
    }

    @Test
    public void testUploadPathTraversal() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "../../../etc/passwd.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );

        mockMvc.perform(multipart("/api/pdf/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully uploaded PDF file: passwd.pdf"));

        // Cleanup
        File maliciousFile = new File(UPLOAD_DIR + "passwd.pdf");
        if (maliciousFile.exists()) {
            maliciousFile.delete();
        }
    }
}
