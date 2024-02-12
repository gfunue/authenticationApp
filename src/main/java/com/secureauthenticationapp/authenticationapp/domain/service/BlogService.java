package com.secureauthenticationapp.authenticationapp.domain.service;

import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import com.secureauthenticationapp.authenticationapp.domain.exception.BlogNotFoundException;
import com.secureauthenticationapp.authenticationapp.domain.exception.BlogOperationException;
import com.secureauthenticationapp.authenticationapp.domain.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final StorageService storageService;

    @Transactional
    public BlogEntity createBlog(BlogEntity blogEntity, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = storageService.uploadFile(imageFile);
                blogEntity.setImageUrl(imageUrl);
            }
        } catch (Exception e) {
            log.error("Failed to upload image file: {}", e.getMessage());
            throw new BlogOperationException("Failed to upload image file: " + e.getMessage());
        }

        blogEntity.setCreationDate(LocalDateTime.now());
        return blogRepository.save(blogEntity);
    }

    @Transactional
    public BlogEntity updateBlog(Long id, BlogEntity updatedBlogEntity, MultipartFile imageFile) {
        try {
            return blogRepository.findById(id)
                    .map(blog -> {
                        if (imageFile != null && !imageFile.isEmpty()) {
                            String imageUrl = storageService.uploadFile(imageFile);
                            blog.setImageUrl(imageUrl);
                        }
                        blog.setTitle(updatedBlogEntity.getTitle());
                        blog.setIntro(updatedBlogEntity.getIntro());
                        blog.setContent(updatedBlogEntity.getContent());
                        blog.setConclusion(updatedBlogEntity.getConclusion());
                        return blogRepository.save(blog);
                    })
                    .orElseThrow(() -> new RuntimeException("Blog not found with id " + id));
        } catch (Exception e) {
            log.error("Failed to update blog: {}", e.getMessage());
            throw new BlogOperationException("Failed to update blog: " + e.getMessage());
        }
    }

    public Optional<BlogEntity> getBlogById(Long id) {
        try {
            return blogRepository.findById(id);
        } catch (Exception e) {
            log.error("Failed to retrieve blog: {}", e.getMessage());
            throw new BlogNotFoundException("Failed to retrieve blog: " + e.getMessage());
        }
    }

    public Page<BlogEntity> getAllBlogs(Pageable pageable) {
        try {
            return blogRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("Failed to retrieve blogs: {}", e.getMessage());
            throw new BlogNotFoundException("Failed to retrieve blogs: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteBlog(Long id) {
        BlogEntity blog = blogRepository.findById(id)
                .orElseThrow(() -> new BlogOperationException("Blog not found with id " + id));
        try {
            if (blog.getImageUrl() != null && !blog.getImageUrl().isEmpty()) {
                storageService.deleteFile(blog.getImageUrl());
            }
        } catch (Exception e) {
            log.error("Failed to delete image file: {}", e.getMessage());
            throw new BlogOperationException("Failed to delete image file: " + e.getMessage());
        }
        blogRepository.deleteById(id);
        log.info("Blog with id: {} and associated image deleted successfully", id);
    }

    public List<BlogEntity> searchBlogs(String keyword) {
        try {
            return blogRepository.searchByKeyword(keyword);
        } catch (Exception e) {
            log.error("Failed to search blogs: {}", e.getMessage());
            throw new BlogNotFoundException("Failed to search blogs: " + e.getMessage());
        }
    }
}

