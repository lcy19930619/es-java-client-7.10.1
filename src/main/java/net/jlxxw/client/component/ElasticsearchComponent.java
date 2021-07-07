package net.jlxxw.client.component;

import net.jlxxw.client.dto.IndexDTO;
import net.jlxxw.client.vo.SearchResult;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

/**
 * @author chunyang.leng
 * @date 2021-03-23 11:11 上午
 */
public interface ElasticsearchComponent {

    /**
     * 创建索引
     * @param indexDTO
     * @return
     */
    CreateIndexResponse createIndex(IndexDTO indexDTO);



    /**
     * 判断索引是否存在
     * @param index
     * @return
     */
     boolean exists(String index);
      

    /**
     * 批量删除索引
     * @param indexList
     * @return
     */
     boolean deleteIndex(String... indexList);
     

    /**
     * 保存数据
     * @param json
     * @param indexName
     * @return
     */
     IndexResponse insertRecord(String json, String indexName);
    

    /**
     * 批量保存
     * @param batchJson
     * @param indexName
     * @return
     */
     BulkResponse batchInsert(List<String> batchJson, String indexName);
     

    /**
     * 更新数据
     * @param indexName
     * @param id
     * @param json
     * @return
     */
     UpdateResponse updateRecord(String indexName, String id, String json);
    

    /**
     * 根据条件进行搜索
     * <pre>
     *      精确等于查询：age = 10
     *      searchSourceBuilder.query(QueryBuilders.termQuery("age", 10));
     *
     *      分词查询: 名字包含张三
     *      searchSourceBuilder.query(QueryBuilders.matchQuery("name","张三"))
     *
     *      纠错检索，让输入条件有容错性
     *      例如：你输入个“邓子棋”，也能把“邓紫棋”查出来，有一定的纠错能力
     *      searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name","张三"));
     *
     *      通配符检索
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","*张三"));  // 右匹配
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","张三*"));  // 左匹配
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","*张三*")); // 中匹配
     *
     *      范围查询：age 1 - 10
     *      searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("age").from(1).to(10)));
     *
     *      分页大小: 每页10条
     *      searchSourceBuilder.size(10);
     *
     *      设置分页起始位置：从0开始
     *      searchSourceBuilder.from(0);
     *
     *      排序
     *      searchSourceBuilder.sort("age", SortOrder.ASC);
     * </pre>
     *
     * @param indexName 索引名字
     * @param searchSourceBuilder 搜索条件构建器
     * @param <T> 泛型
     * @return 搜索结果
     */
    <T> SearchResult<T> queryCondition(String indexName, SearchSourceBuilder searchSourceBuilder);

    /**
     * 获取es链接客户端
     * @return
     */
    RestHighLevelClient getClient();



    /**
     * 原始查询条件进行搜索
     * <pre>
     *      精确等于查询：age = 10
     *      searchSourceBuilder.query(QueryBuilders.termQuery("age", 10));
     *
     *      分词查询: 名字包含张三
     *      searchSourceBuilder.query(QueryBuilders.matchQuery("name","张三"))
     *
     *      纠错检索，让输入条件有容错性
     *      例如：你输入个“邓子棋”，也能把“邓紫棋”查出来，有一定的纠错能力
     *      searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name","张三"));
     *
     *      通配符检索
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","*张三"));  // 右匹配
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","张三*"));  // 左匹配
     *      searchSourceBuilder.query(QueryBuilders.wildcardQuery("name","*张三*")); // 中匹配
     *
     *      范围查询：age 1 - 10
     *      searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("age").from(1).to(10)));
     *
     *      分页大小: 每页10条
     *      searchSourceBuilder.size(10);
     *
     *      设置分页起始位置：从0开始
     *      searchSourceBuilder.from(0);
     *
     *      排序
     *      searchSourceBuilder.sort("age", SortOrder.ASC);
     * </pre>
     *
     * @param indexName 索引名字
     * @param searchSourceBuilder 搜索条件构建器
     * @return 搜索结果
     */
    SearchResponse originalQueryCondition(String indexName, SearchSourceBuilder searchSourceBuilder);
}
