package com.example.demo.persistence.customconverters;

import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<ArticleDocument, String> {
}
