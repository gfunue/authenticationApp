package com.secureauthenticationapp.authenticationapp.unitTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.BlogOperationException;
import com.secureauthenticationapp.authenticationapp.domain.service.BlogService;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import com.secureauthenticationapp.authenticationapp.web.BlogController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlogController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogService blogService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createBlog_success() throws Exception {
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setTitle("Test Blog");
        blogEntity.setContent("This is a test blog.");

        BlogEntity savedBlog = new BlogEntity();
        savedBlog.setId(1L);
        savedBlog.setTitle(blogEntity.getTitle());
        savedBlog.setContent(blogEntity.getContent());
        savedBlog.setCreationDate(LocalDateTime.now());

        MockMultipartFile blogPart = new MockMultipartFile("blog", "", "application/json",
                objectMapper.writeValueAsBytes(blogEntity));
        MockMultipartFile imagePart = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", new byte[]{});

        when(blogService.createBlog(any(BlogEntity.class), any(MultipartFile.class))).thenReturn(savedBlog);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blog/create-blog")
                        .file(blogPart)
                        .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.blogId").value(savedBlog.getId()))
                .andExpect(jsonPath("$.message").value("Blog created successfully"));
    }

    @Test
    void createBlog_failure_serviceException() throws Exception {
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setTitle("Test Blog");
        blogEntity.setContent("This is a test blog.");

        MockMultipartFile blogPart = new MockMultipartFile("blog", "", "application/json",
                objectMapper.writeValueAsBytes(blogEntity));
        MockMultipartFile imagePart = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", new byte[]{});

        when(blogService.createBlog(any(BlogEntity.class), any(MultipartFile.class))).thenThrow(new BlogOperationException("Failed to create blog"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blog/create-blog")
                        .file(blogPart)
                        .file(imagePart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to create blog"));
    }


    @Test
    void createBlog_failure_missingBlogPart() throws Exception {
        MockMultipartFile imagePart = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", new byte[]{});

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blog/create-blog")
                        .file(imagePart))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createBlog_failure_largeImageFile() throws Exception {
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setTitle("Test Blog");
        blogEntity.setContent("This is a test blog.");

        MockMultipartFile blogPart = new MockMultipartFile("blog", "", "application/json",
                objectMapper.writeValueAsBytes(blogEntity));
        byte[] largeImageBytes = new byte[5 * 1024 * 1024];
        MockMultipartFile imagePart = new MockMultipartFile("imageFile", "large_image.jpg", "image/jpeg", largeImageBytes);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blog/create-blog")
                        .file(blogPart)
                        .file(imagePart))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBlog_success() throws Exception {
        Long blogId = 1L;
        BlogEntity updatedBlogEntity = new BlogEntity();
        updatedBlogEntity.setTitle("Updated Test Blog");
        updatedBlogEntity.setContent("This is an updated test blog.");
        updatedBlogEntity.setIntro("Updated Introduction");
        updatedBlogEntity.setConclusion("Updated Conclusion");

        MockMultipartFile blogPart = new MockMultipartFile("blog", "", "application/json",
                objectMapper.writeValueAsBytes(updatedBlogEntity));
        MockMultipartFile imagePart = new MockMultipartFile("imageFile", "updated_image.jpg",
                "image/jpeg", new byte[]{});

        when(blogService.updateBlog(eq(blogId), any(BlogEntity.class), any(MultipartFile.class))).thenReturn(updatedBlogEntity);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blog/update/{id}", blogId)
                        .file(blogPart)
                        .file(imagePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Blog updated successfully"));
    }

    @Test
    void getBlogById_success() throws Exception {
        Long blogId = 1L;
        BlogEntity blogEntity = new BlogEntity();
        blogEntity.setId(blogId);
        blogEntity.setTitle("Test Blog");
        blogEntity.setContent("This is a test blog.");

        when(blogService.getBlogById(blogId)).thenReturn(Optional.of(blogEntity));

        mockMvc.perform(get("/api/v1/blog/{id}", blogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.blog.id").value(blogId))
                .andExpect(jsonPath("$.message").value("Blog found successfully"));
    }

    @Test
    void getAllBlogs_success() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        List<BlogEntity> blogList = new ArrayList<>();
        blogList.add(BlogEntity.builder().id(1L).title("Test Blog 1").intro("Intro 1").content("Content 1")
                .conclusion("Conclusion 1").creationDate(LocalDateTime.now()).imageUrl("image1.jpg").build());
        blogList.add(BlogEntity.builder().id(2L).title("Test Blog 2").intro("Intro 2").content("Content 2")
                .conclusion("Conclusion 2").creationDate(LocalDateTime.now()).imageUrl("image2.jpg").build());
        Page<BlogEntity> page = new PageImpl<>(blogList, pageable, blogList.size());

        when(blogService.getAllBlogs(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/v1/blog/all-blogs")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.blogs.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.blogs.content[1].id").value(2L))
                .andExpect(jsonPath("$.message").value("Blogs found successfully"));
    }

}

