import com.alibaba.fastjson.JSON;
import net.jlxxw.client.component.ElasticsearchComponent;
import net.jlxxw.client.db.DataBaseClient;
import net.jlxxw.client.domain.CsdnDO;
import net.jlxxw.client.dto.IndexDTO;
import net.jlxxw.client.holder.ElasticsearchClientHolder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author chunyang.leng
 * @date 2021-07-21 6:08 下午
 */

public class ProcessTest {
    private static final String INDEX_NAME = "csdn";
    private ElasticsearchComponent elasticsearchComponent;

    @Before
    public void before(){
        elasticsearchComponent = ElasticsearchClientHolder.getElasticsearchComponent();
    }

    @Test
    public void test() throws InterruptedException {
        List<CsdnDO> data = new DataBaseClient().getData();
        if(elasticsearchComponent.exists(INDEX_NAME)){
            // 存在索引
            elasticsearchComponent.deleteIndex(INDEX_NAME);
            System.out.println("index exists ");
        }
        IndexDTO indexDTO = new IndexDTO();
        indexDTO.setIndexName(INDEX_NAME);
        indexDTO.setReplicas(1);
        indexDTO.setShards(1);
        elasticsearchComponent.createIndex(indexDTO);
        System.out.println("create index ");

        data.forEach(o->{
            elasticsearchComponent.insertRecord(JSON.toJSONString(o),INDEX_NAME);
        });
        System.out.println("--------------------------------done--------------------------------");
    }
}
