package com.secureauthenticationapp.authenticationapp.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Entity
@Table(name = "BlogEntity")
public class BlogEntity {

    /**
     * Unique identifier for the blog post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blogId", nullable = false, updatable = false)
    private Long id;

    /**
     * The title of the blog post, limited to 100 characters.
     * Cannot be empty.
     */
    @Size(max = 100, message = "Title name of blog must be less than 100 characters")
    @NotBlank(message = "BlogEntity title cannot be empty")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * The introductory part of the blog post, limited to 500 characters.
     * Cannot be empty.
     */
    @Size(max = 500, message = "BlogEntity introduction cannot be longer than 500 characters")
    @NotBlank(message = "BlogEntity introduction cannot be left blank/empty")
    @Column(name = "intro", nullable = false, length = 500)
    private String intro;

    /**
     * The main content of the blog post, limited to 5000 characters.
     * Cannot be empty.
     */
    @Size(max = 5000, message = "BlogEntity content/body cannot be more than 5000 characters long")
    @NotBlank(message = "BlogEntity content cannot be left blank/empty")
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    /**
     * The concluding part of the blog post, limited to 500 characters.
     */
    @Size(max = 500, message = "BlogEntity conclusion cannot be more than 500 characters long")
    @Column(name = "conclusion", length = 500)
    private String conclusion;

    /**
     * The date and time when the blog post was created.
     */
    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}
