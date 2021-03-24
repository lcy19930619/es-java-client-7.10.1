import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.jlxxw.client.component.ElasticsearchComponent;
import net.jlxxw.client.dto.AnalyzerDTO;
import net.jlxxw.client.dto.IndexDTO;
import net.jlxxw.client.holder.ElasticsearchClientHolder;
import net.jlxxw.client.vo.SearchResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chunyang.leng
 * @date 2021-03-23 11:32 上午
 */
public class ComponentTest {
    ElasticsearchComponent elasticsearchComponent;
    @Before
    public void before(){
        elasticsearchComponent = ElasticsearchClientHolder.getElasticsearchComponent();
    }
    String indexName = "test111";
    @Test
    public void createIndex(){
        elasticsearchComponent.deleteIndex(indexName);
        IndexDTO indexDTO = new IndexDTO();
        indexDTO.setIndexName(indexName);
        Map<String, AnalyzerDTO> properties = new HashMap<>();
        AnalyzerDTO nameProperties = new AnalyzerDTO();
        nameProperties.setType("text");
        properties.put("name",nameProperties);
        AnalyzerDTO addressProperties = new AnalyzerDTO();
        addressProperties.setType("text");
        properties.put("address",addressProperties);
        indexDTO.setProperties(properties);
        elasticsearchComponent.createIndex(indexDTO);
    }

    @Test
    public void insertTest(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","testName");
        jsonObject.put("address","testAddress");
        elasticsearchComponent.insertRecord(jsonObject.toJSONString(),indexName);
    }

    @Test
    public void selectTest(){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name","testName"));
        SearchResult<JSONObject> objectSearchResult = elasticsearchComponent.queryCondition(indexName, searchSourceBuilder);
        System.out.println(JSON.toJSONString(objectSearchResult));
    }


}
