package net.jlxxw.client.holder;

import com.alibaba.fastjson.JSON;
import net.jlxxw.client.component.ElasticsearchComponent;
import net.jlxxw.client.component.impl.ElasticsearchComponentImpl;
import net.jlxxw.client.properties.ElasticSearchProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Elasticsearch 客户端持有者
 *
 * @author chunyang.leng
 * @date 2021-03-23 2:25 下午
 */
public class ElasticsearchClientHolder {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchClientHolder.class);
    private static ElasticsearchComponent elasticsearchComponent = null;

    /**
     * 配置文件路径
     */
    private static final String FILENAME = "/Users/lcy/IdeaProjects/es-java-client/src/main/resources/application.properties";

    static {
        Properties properties = new Properties();
        try (InputStream in = new BufferedInputStream(new FileInputStream(FILENAME))){
            properties.load(in);

            String schema = properties.getProperty("elasticsearch.schema");
            String connectTimeOut = properties.getProperty("elasticsearch.connectTimeOut");
            String socketTimeOut = properties.getProperty("elasticsearch.socketTimeOut");
            String connectionRequestTimeOut = properties.getProperty("elasticsearch.connectionRequestTimeOut");
            String maxConnectNum = properties.getProperty("elasticsearch.maxConnectNum");
            String maxConnectPerRoute = properties.getProperty("elasticsearch.maxConnectPerRoute");
            String nodes = properties.getProperty("elasticsearch.nodes");
            String username = properties.getProperty("elasticsearch.username");
            String password = properties.getProperty("elasticsearch.password");

            ElasticSearchProperties elasticSearchProperties = new ElasticSearchProperties();
            if(StringUtils.isNotBlank(schema)) {
                elasticSearchProperties.setSchema(schema);
            }
            if(StringUtils.isNotBlank(connectionRequestTimeOut)) {
                elasticSearchProperties.setConnectionRequestTimeOut(Integer.parseInt(connectionRequestTimeOut));
            }
            if(StringUtils.isNotBlank(connectTimeOut)){
                elasticSearchProperties.setConnectTimeOut(Integer.parseInt(connectTimeOut));
            }
            if(StringUtils.isNotBlank(socketTimeOut)){
                elasticSearchProperties.setSocketTimeOut(Integer.parseInt(socketTimeOut));
            }
            if(StringUtils.isNotBlank(maxConnectNum)){
                elasticSearchProperties.setMaxConnectNum(Integer.parseInt(maxConnectNum));
            }
            if(StringUtils.isNotBlank(maxConnectPerRoute)){
                elasticSearchProperties.setMaxConnectPerRoute(Integer.parseInt(maxConnectPerRoute));
            }

            if(StringUtils.isNotBlank(nodes)){
                elasticSearchProperties.setNodes(nodes.split(","));
            }

            if(StringUtils.isNotBlank(username)){
                elasticSearchProperties.setUsername(username);
            }
            if(StringUtils.isNotBlank(password)){
                elasticSearchProperties.setPassword(password);
            }
            elasticsearchComponent = new ElasticsearchComponentImpl(elasticSearchProperties);
            logger.info("初始化 elasticsearch 配置文件完毕");
            logger.debug("载入 elasticsearch 配置文件\n"+ JSON.toJSONString(properties));
        } catch (IOException e) {
            logger.error("初始化 elasticsearch 配置文件失败",e);
            throw new RuntimeException("读取配置文件 " + FILENAME + " 错误!!!!");
        }
    }

    public static ElasticsearchComponent getElasticsearchComponent() {
        return elasticsearchComponent;
    }
}
