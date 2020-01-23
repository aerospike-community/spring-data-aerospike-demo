package com.example.demo.persistence.article;

import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<ArticleDocument, String> {
}
