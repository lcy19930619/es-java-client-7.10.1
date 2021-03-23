package net.jlxxw.client.dto;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author chunyang.leng
 * @date 2021-03-23 11:19 上午
 */
public class AnalyzerDTO {
    /**
     * 字段类型
     */
    @JSONField(name = "type")
    private String type;
    /**
     * 创建索引分词器
     */
    @JSONField(name = "analyzer")
    private String analyzer;

    /**
     * 搜索分词器
     */
    @JSONField(name = "search_analyzer")
    private String searchAnalyzer;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }
}
