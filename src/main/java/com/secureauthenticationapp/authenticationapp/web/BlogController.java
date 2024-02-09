package com.secureauthenticationapp.authenticationapp.web;

import com.secureauthenticationapp.authenticationapp.domain.entity.BlogEntity;
import com.secureauthenticationapp.authenticationapp.domain.service.BlogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blogs")
@AllArgsConstructor
public class BlogController {

    private final BlogService blogService;
    @GetMapping("/search")
    public ResponseEntity<List<BlogEntity>> searchBlogs(@RequestParam String keyword) {
        List<BlogEntity> result = blogService.searchBlogs(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<BlogEntity>> getAllBlogs(@PageableDefault(size = 5) Pageable pageable) {
        Page<BlogEntity> page = blogService.getAllBlogs(pageable);
        return ResponseEntity.ok(page);
    }
}

