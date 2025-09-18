package com.cmp.agent.model;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/** 文档实体：最小字段以演示加密/索引流程 */
public final class Document {
    public final int docId;
    public final String text;
    public final List<String> keywords; // 已提取的关键词集合（数据源侧）
    public Document(int docId, String text, List<String> keywords){
        this.docId = docId;
        this.text = Objects.requireNonNull(text);
        this.keywords = Objects.requireNonNull(keywords);
    }
    public byte[] bytes(){ return text.getBytes(StandardCharsets.UTF_8); }
}
