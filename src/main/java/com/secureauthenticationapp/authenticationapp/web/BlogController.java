package com.secureauthenticationapp.authenticationapp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secureauthenticationapp.authenticationapp.domain.bean.HttpResponse;
import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.BlogNotFoundException;
import com.secureauthenticationapp.authenticationapp.domain.service.BlogService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/blog")
@AllArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create-blog")
    public ResponseEntity<HttpResponse> createBlog(
            @Valid @RequestPart("blog") String blogJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            BlogEntity blogEntity = objectMapper.readValue(blogJson, BlogEntity.class);
            BlogEntity savedBlog = blogService.createBlog(blogEntity, imageFile);

            HttpResponse response = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.CREATED.value())
                    .status(HttpStatus.CREATED)
                    .reason(HttpStatus.CREATED.getReasonPhrase())
                    .message("Blog created successfully")
                    .developerMessage("Blog creation processed")
                    .data(Collections.singletonMap("blogId", savedBlog.getId()))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            HttpResponse response = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message("Failed to create blog")
                    .developerMessage(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<HttpResponse> updateBlog(
            @PathVariable Long id,
            @RequestPart("blog") String blogJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            BlogEntity blogEntity = objectMapper.readValue(blogJson, BlogEntity.class);
            BlogEntity updatedBlog = blogService.updateBlog(id, blogEntity, imageFile);
            HttpResponse response = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .reason(HttpStatus.OK.getReasonPhrase())
                    .message("Blog updated successfully")
                    .developerMessage("Blog update processed")
                    .data(Collections.singletonMap("blogId", updatedBlog.getId()))
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpResponse response = HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .reason(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message("Failed to update blog")
                    .developerMessage(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getBlogById(@PathVariable Long id) {
        BlogEntity blog = blogService.getBlogById(id)
                .orElseThrow(() -> new BlogNotFoundException("Blog not found with id " + id));
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .message("Blog found successfully")
                .developerMessage("Blog retrieval processed")
                .data(Collections.singletonMap("blog", blog))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-blogs")
    public ResponseEntity<HttpResponse> getAllBlogs(@PageableDefault(size = 5) Pageable pageable) {
        Page<BlogEntity> page = blogService.getAllBlogs(pageable);
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .message("Blogs found successfully")
                .developerMessage("Blog retrieval processed")
                .data(Collections.singletonMap("blogs", page))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        HttpResponse response = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .reason(HttpStatus.OK.getReasonPhrase())
                .message("Blog deleted successfully")
                .developerMessage("Blog deletion processed")
                .data(Collections.emptyMap())
                .build();
        return ResponseEntity.ok(response);
    }

}

