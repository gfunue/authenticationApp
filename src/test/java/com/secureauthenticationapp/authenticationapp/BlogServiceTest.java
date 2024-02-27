package com.secureauthenticationapp.authenticationapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.ArrayList;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import com.secureauthenticationapp.authenticationapp.domain.repository.BlogRepository;
import com.secureauthenticationapp.authenticationapp.domain.service.BlogService;
import com.secureauthenticationapp.authenticationapp.domain.service.StorageService;
import java.util.Optional;


@SpringBootTest
class BlogServiceTest {
    @Mock
    private BlogRepository blogRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private BlogService blogService;


    @Test
    public void testCreateBlog() {
        
        BlogEntity blogEntity = new BlogEntity();
        MultipartFile imageFile = new MockMultipartFile("imagefile", "Hello, World!".getBytes());

        when(storageService.uploadFile(any(MultipartFile.class))).thenReturn("imageUrl");
        when(blogRepository.save(any(BlogEntity.class))).thenReturn(blogEntity);

        BlogEntity result = blogService.createBlog(blogEntity, imageFile);

        assertNotNull(result);
        assertEquals("imageUrl", result.getImageUrl());
    }

    @Test
    public void testUpdateBlog() {
        BlogEntity blogEntity = new BlogEntity();
        MultipartFile imageFile = new MockMultipartFile("imagefile", "Hello, World!".getBytes());

        when(blogRepository.findById(any(Long.class))).thenReturn(Optional.of(blogEntity));
        when(storageService.uploadFile(any(MultipartFile.class))).thenReturn("imageUrl");
        when(blogRepository.save(any(BlogEntity.class))).thenReturn(blogEntity);

        BlogEntity result = blogService.updateBlog(1L, blogEntity, imageFile);

        assertNotNull(result);
        assertEquals("imageUrl", result.getImageUrl());
    }

    @Test
    public void testGetBlogById() {
    BlogEntity blogEntity = new BlogEntity();

    when(blogRepository.findById(any(Long.class))).thenReturn(Optional.of(blogEntity));

    Optional<BlogEntity> result = blogService.getBlogById(1L);

    assertTrue(result.isPresent());
    assertEquals(blogEntity, result.get());
    }

    @Test
    public void testGetAllBlogs() {
    Page<BlogEntity> blogPage = new PageImpl<>(new ArrayList<>());

    when(blogRepository.findAll(any(Pageable.class))).thenReturn(blogPage);

    Page<BlogEntity> result = blogService.getAllBlogs(PageRequest.of(0, 10));

    assertNotNull(result);
   }

    @Test
    public void testDeleteBlog() {
    BlogEntity blogEntity = new BlogEntity();
    blogEntity.setImageUrl("imageUrl");

    when(blogRepository.findById(any(Long.class))).thenReturn(Optional.of(blogEntity));

    blogService.deleteBlog(1L);

    verify(storageService, times(1)).deleteFile("imageUrl");
    verify(blogRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testSearchBlogs() {
    List<BlogEntity> blogs = new ArrayList<>();

    when(blogRepository.searchByKeyword(any(String.class))).thenReturn(blogs);

    List<BlogEntity> result = blogService.searchBlogs("keyword");

    assertNotNull(result);
    }


}
