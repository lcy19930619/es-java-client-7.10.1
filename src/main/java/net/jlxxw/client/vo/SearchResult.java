package net.jlxxw.client.vo;

import java.util.List;

/**
 * @author chunyang.leng
 * @date 2021-03-23 1:29 下午
 */
public class SearchResult<T> {
    /**
     * 总数据量
     */
    private Long total;

    /**
     * 需要返回的数据
     */
    private List<T> data;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    private  SearchResult(Long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public static <T> SearchResult<T> build(Long total, List<T> data){
        return new SearchResult<>(total,data);
    }
}
