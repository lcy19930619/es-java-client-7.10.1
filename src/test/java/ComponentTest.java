import net.jlxxw.client.component.ElasticsearchComponent;
import net.jlxxw.client.dto.IndexDTO;
import net.jlxxw.client.holder.ElasticsearchClientHolder;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void createIndex(){
        IndexDTO indexDTO = new IndexDTO();
        indexDTO.setIndexName("test111");
        elasticsearchComponent.createIndex(indexDTO);
    }
}
