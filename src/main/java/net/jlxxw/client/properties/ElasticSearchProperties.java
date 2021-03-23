package net.jlxxw.client.properties;

/**
 * @author chunyang.leng
 * @date 2021-03-23 11:46 上午
 */
public class ElasticSearchProperties {
    /**
     * 使用的协议
     */
    private String schema = "http";
    /**
     * 连接超时时间
     */
    private int connectTimeOut = 1000;
    /**
     * 连接超时时间
     */
    private int socketTimeOut = 30000;
    /**
     * 获取连接的超时时间
     */
    private int connectionRequestTimeOut = 500;
    /**
     * 最大连接数
     */
    private int maxConnectNum = 100;
    /**
     * 最大路由连接数
     */
    private int maxConnectPerRoute = 100;
    /**
     * demo 127.0.0.1:9092 127.0.0.19093
     */
    private String[] nodes;

    private String username;

    private String password;


    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectionRequestTimeOut() {
        return connectionRequestTimeOut;
    }

    public void setConnectionRequestTimeOut(int connectionRequestTimeOut) {
        this.connectionRequestTimeOut = connectionRequestTimeOut;
    }

    public int getMaxConnectNum() {
        return maxConnectNum;
    }

    public void setMaxConnectNum(int maxConnectNum) {
        this.maxConnectNum = maxConnectNum;
    }

    public int getMaxConnectPerRoute() {
        return maxConnectPerRoute;
    }

    public void setMaxConnectPerRoute(int maxConnectPerRoute) {
        this.maxConnectPerRoute = maxConnectPerRoute;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
