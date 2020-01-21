package com.example.demo.persistence;

import lombok.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

@Value
public class CommentsKey {

    public final long pageId;
    public final long threadId;

    @WritingConverter
    public enum CommentsKeyToStringConverter implements Converter<CommentsKey, String> {
        INSTANCE;

        @Override
        public String convert(CommentsKey source) {
            return "comments::" + source.pageId + "::" + source.threadId;
        }
    }

    @ReadingConverter
    public enum StringToCommentsKeyConverter implements Converter<String, CommentsKey> {
        INSTANCE;

        @Override
        public CommentsKey convert(String source) {
            String[] split = source.split("::");
            if (split.length != 3) {
                throw new IllegalArgumentException("Key can not be parsed: " + source);
            }
            long pageId = Long.parseLong(split[1]);
            long threadId = Long.parseLong(split[2]);
            return new CommentsKey(pageId, threadId);
        }
    }

}