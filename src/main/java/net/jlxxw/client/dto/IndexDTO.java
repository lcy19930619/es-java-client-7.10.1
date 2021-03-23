package net.jlxxw.client.dto;

import java.util.Map;

/**
 * 索引传输对象
 * @author chunyang.leng
 * @date 2021-03-23 11:13 上午
 */
public class IndexDTO {
    /**
     * 索引名字
     */
    private String indexName;
    /**
     * 属性配置
     * key 字段名
     * value 相关的映射信息
     */
    private Map<String,AnalyzerDTO> properties;
    /**
     * 副本数
     */
    private Integer replicas;
    /**
     * 分片数
     */
    private Integer shards;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public Map<String, AnalyzerDTO> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, AnalyzerDTO> properties) {
        this.properties = properties;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public Integer getShards() {
        return shards;
    }

    public void setShards(Integer shards) {
        this.shards = shards;
    }
}
