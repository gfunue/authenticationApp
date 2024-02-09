package com.secureauthenticationapp.authenticationapp.domain.service;

import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
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
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = storageService.uploadFile(imageFile);
            blogEntity.setImageUrl(imageUrl); // Set the image URL in the blog entity
        }
        blogEntity.setCreationDate(LocalDateTime.now());
        return blogRepository.save(blogEntity);
    }

    @Transactional
    public BlogEntity updateBlog(Long id, BlogEntity updatedBlogEntity, MultipartFile imageFile) {
        return blogRepository.findById(id)
                .map(blog -> {
                    if (imageFile != null && !imageFile.isEmpty()) {
                        String imageUrl = storageService.uploadFile(imageFile);
                        blog.setImageUrl(imageUrl); // Update the image URL if a new image is uploaded
                    }
                    blog.setTitle(updatedBlogEntity.getTitle());
                    blog.setIntro(updatedBlogEntity.getIntro());
                    blog.setContent(updatedBlogEntity.getContent());
                    blog.setConclusion(updatedBlogEntity.getConclusion());
                    return blogRepository.save(blog);
                })
                .orElseThrow(() -> new RuntimeException("Blog not found with id " + id));
    }

    public Optional<BlogEntity> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    public Page<BlogEntity> getAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Transactional
    public void deleteBlog(Long id) {
        BlogEntity blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id " + id));
        if (blog.getImageUrl() != null && !blog.getImageUrl().isEmpty()) {
            storageService.deleteFile(blog.getImageUrl());
        }
        blogRepository.deleteById(id);
        log.info("Blog with id: {} and associated image deleted successfully", id);
    }

    public List<BlogEntity> searchBlogs(String keyword) {
        return blogRepository.searchByKeyword(keyword);
    }
}

