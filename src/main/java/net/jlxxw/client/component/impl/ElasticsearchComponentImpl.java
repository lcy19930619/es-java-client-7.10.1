package net.jlxxw.client.component.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.jlxxw.client.utils.ObjectUtils;
import net.jlxxw.client.component.ElasticsearchComponent;
import net.jlxxw.client.dto.AnalyzerDTO;
import net.jlxxw.client.dto.IndexDTO;
import net.jlxxw.client.properties.ElasticSearchProperties;
import net.jlxxw.client.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.client.RequestOptions.DEFAULT;

/**
 * @author chunyang.leng
 * @date 2021-03-23 11:15 上午
 */
public class ElasticsearchComponentImpl implements ElasticsearchComponent {
    private static RestHighLevelClient client = null;
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchComponentImpl.class);

    public ElasticsearchComponentImpl(ElasticSearchProperties properties) {
        init(properties);
    }

    /**
     * build client
     *
     * @param properties
     * @return
     */

    private void init(ElasticSearchProperties properties) {
        if(Objects.nonNull(client)){
            return ;
        }
        List<HttpHost> nodeList = new ArrayList<>();
        Arrays.stream(properties.getNodes()).forEach(each -> {
            String[] array = each.split(":");
            nodeList.add(new HttpHost(array[0],Integer.parseInt(array[1]),properties.getSchema()));
        });
        RestClientBuilder builder = RestClient.builder(nodeList.toArray(new HttpHost[0]));

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        String username = properties.getUsername();
        String password = properties.getPassword();
        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            credentialsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }



        // 异步httpclient连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(properties.getConnectTimeOut());
            requestConfigBuilder.setSocketTimeout(properties.getSocketTimeOut());
            requestConfigBuilder.setConnectionRequestTimeout(properties.getConnectionRequestTimeOut());
            return requestConfigBuilder;
        });

        // 异步httpclient连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(properties.getMaxConnectNum());
            httpClientBuilder.setMaxConnPerRoute(properties.getMaxConnectPerRoute());
            return httpClientBuilder;
        });
        client = new RestHighLevelClient( builder );
    }

    /**
     * 创建索引
     *
     * @param indexDTO
     */
    @Override
    public CreateIndexResponse createIndex(IndexDTO indexDTO) {
        CreateIndexRequest request = new CreateIndexRequest( indexDTO.getIndexName() );
        Map<String, AnalyzerDTO> properties = indexDTO.getProperties();
        if(properties != null){
            String json = JSON.toJSONString(properties);
            JSONObject object = JSON.parseObject(json);
            request.mapping(object);
        }
        Settings settings = Settings.builder()
                .put("index.number_of_shards", ObjectUtils.defaultValue(indexDTO.getShards(),1))
                .put("index.number_of_replicas", ObjectUtils.defaultValue(indexDTO.getReplicas(),0))
                .build();
        request.settings(settings);
        try {
            return client.indices().create(request, DEFAULT);
        } catch (IOException e) {
            logger.error("创建索引出现异常,",e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 判断索引是否存在
     * @param index
     * @return
     */
    @Override
    public boolean exists(String index) {
        boolean exists;
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(index);
            getIndexRequest.humanReadable(true);
            exists = client.indices().exists(getIndexRequest, DEFAULT);
        } catch (IOException e) {
            logger.error("判断索引是否存在出现异常,index:{}",index, e);
            throw new RuntimeException(e.getMessage());
        }
        return exists;
    }

    /**
     * 批量删除索引
     * @param indexList
     * @return
     */
    @Override
    public boolean deleteIndex(String... indexList) {
        boolean acknowledged;
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexList);
            AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest,DEFAULT);
            acknowledged = delete.isAcknowledged();
        } catch (IOException e) {
            logger.error("批量删除索引出现异常,index:{}", JSON.toJSONString(indexList), e);
            throw new RuntimeException(e.getMessage());
        }
        return acknowledged;
    }

    /**
     * 保存数据
     * @param json
     * @param indexName
     * @return
     */
    @Override
    public IndexResponse insertRecord(String json, String indexName) {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.source(json, XContentType.JSON);
        try {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            if (response != null) {
                ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    logger.info("shardInfo:{}", JSONObject.toJSON(shardInfo));
                }
                // 如果有分片副本失败，可以获得失败原因信息
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason();
                        logger.info("副本失败原因,reason:{}",reason);
                    }
                }
            }
            return response;
        } catch (IOException e) {
            logger.info("保存数据出现异常,index:{},data:{}",indexName,json,e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 批量保存
     * @param batchJson
     * @param indexName
     * @return
     */
    @Override
    public BulkResponse batchInsert(List<String> batchJson, String indexName) {
        BulkRequest bulkRequest = new BulkRequest();
        batchJson.forEach(each -> {
            IndexRequest indexRequest = new IndexRequest(indexName).source(each, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        //同步
        try {
            return client.bulk(bulkRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("批量保存出现异常，index:{},data:{}",indexName,JSON.toJSONString(batchJson),e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 更新数据
     * @param indexName
     * @param id
     * @param json
     * @return
     */
    @Override
    public UpdateResponse updateRecord(String indexName, String id, String json) {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.source(json, XContentType.JSON);
        indexRequest.id(id);
        UpdateRequest updateRequest = new UpdateRequest(indexName,id);
        updateRequest.doc(indexRequest);
        try {
            return client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("更新数据出现异常,index:{},id:{},data:{}",indexName,id,json,e);
            throw new RuntimeException(e.getMessage());
        }
    }

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
    @Override
    public <T> SearchResult<T> queryCondition(String indexName, SearchSourceBuilder searchSourceBuilder) {
        // npe验证
        Objects.requireNonNull(searchSourceBuilder);
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            // 查询返回的结果
            SearchHits hits = response.getHits();
            List<T> data = new ArrayList<>();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                data.add(JSON.parseObject(sourceAsString,new TypeReference<T>(){}));
            }
            // 获取总数
            TotalHits totalHits = hits.getTotalHits();
            return SearchResult.build(totalHits.value,data);
        } catch (IOException e) {
            logger.error("搜索出现异常,index:{},query:{}",indexName,searchSourceBuilder.toString(),e);
            throw new RuntimeException("search error,"+e);
        }
    }

    @Override
    public RestHighLevelClient getClient() {
        return client;
    }

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
     * @param indexName           索引名字
     * @param searchSourceBuilder 搜索条件构建器
     * @return 搜索结果
     */
    @Override
    public SearchResponse originalQueryCondition(String indexName, SearchSourceBuilder searchSourceBuilder) {
        // npe验证
        Objects.requireNonNull(searchSourceBuilder);
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);
            return  client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("搜索出现异常,index:{},query:{}",indexName,searchSourceBuilder.toString(),e);
            throw new RuntimeException("search error,"+e);
        }
    }
}
