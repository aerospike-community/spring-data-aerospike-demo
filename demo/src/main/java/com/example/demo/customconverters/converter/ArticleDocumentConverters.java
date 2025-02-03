package com.example.demo.customconverters.converter;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.example.demo.customconverters.entity.ArticleDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.aerospike.convert.AerospikeReadData;
import org.springframework.data.aerospike.convert.AerospikeWriteData;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.Collection;
import java.util.List;

public class ArticleDocumentConverters {

    @WritingConverter
    @RequiredArgsConstructor
    public static class ArticleDocumentToAerospikeWriteDataConverter implements Converter<ArticleDocument, AerospikeWriteData> {

        private static final int TEN_SECONDS = 10;
        private static final int NEVER_EXPIRE = -1;
        private final String namespace;
        private final String setName;

        @Override
        public AerospikeWriteData convert(ArticleDocument source) {
            Key key = new Key(namespace, setName, source.getId());
            int expiration = source.isDraft() ? TEN_SECONDS : NEVER_EXPIRE;
            Integer version = null; // not versionable document
            Collection<Bin> bins = List.of(
                    new Bin("author", source.getAuthor()),
                    new Bin("content", source.getContent()),
                    new Bin("draft", source.isDraft())
            );
            return new AerospikeWriteData(key, bins, expiration, version);
        }
    }

    @ReadingConverter
    public enum AerospikeReadDataToArticleDocumentToConverter implements Converter<AerospikeReadData, ArticleDocument> {
        INSTANCE;

        @Override
        public ArticleDocument convert(AerospikeReadData source) {
            String id = (String) source.getKey().userKey.getObject();
            String author = (String) source.getValue("author");
            String content = (String) source.getValue("content");
            boolean draft = (boolean) source.getValue("draft");
            return new ArticleDocument(id, author, content, draft);
        }
    }
}