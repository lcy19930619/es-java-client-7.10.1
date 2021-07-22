package net.jlxxw.client.db;

import net.jlxxw.client.domain.CsdnDO;
import net.jlxxw.client.holder.ElasticsearchClientHolder;
import net.jlxxw.client.util.HtmlToText;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author chunyang.leng
 * @date 2021-07-21 5:23 下午
 */
public class DataBaseClient {
    private static final String FILENAME = "application.properties";
    private static String url = "";
    private static String username="";
    private static String password="";

    public DataBaseClient() throws IOException {
        init();
    }
    public static void init() throws IOException{
        Properties properties = new Properties();
        try (InputStream in = ElasticsearchClientHolder.class.getClassLoader().getResourceAsStream(FILENAME)) {
            properties.load(in);
            url = properties.getProperty("mysql.url");
            username = properties.getProperty("mysql.username");
            password =  properties.getProperty("mysql.password");
        }
    }



    public List<CsdnDO> getData(){
        Properties info = new Properties();  //定义Properties对象
        info.setProperty("user",username);  //设置Properties对象属性
        info.setProperty("password",password);
        List<CsdnDO> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,info)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id,title ,content,url,`group`,`view`,md5,create_time,pdf_path from csdn");
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                String url = resultSet.getString("url");
                Long view = resultSet.getLong("view");
                String md5 = resultSet.getString("md5");
                Date createTime = resultSet.getDate("create_time");
                String pdfPath = resultSet.getString("pdf_path");
                CsdnDO domain = new CsdnDO();
                domain.setContent(HtmlToText.toText(content));
                domain.setId(id);
                domain.setTitle(title);
                domain.setUrl(url);
                domain.setView(view);
                domain.setMd5(md5);
                domain.setCreateTime(createTime);
                domain.setPdfPath(pdfPath);
                list.add(domain);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }


}
