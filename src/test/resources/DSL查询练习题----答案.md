#第一题
基于数据库表csdn，创建索引 csdn，并设置title、content字段为中文分词字段，
并且title和content存储使用ik最大分词算法，搜索采用最小分词算法
```

PUT csdn
{
  "settings": {
    "index.analysis.analyzer.default.type": "ik_max_word"
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "long",
        "index": true
      },
      "title": {
        "type": "text",
        "index": true,
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fielddata": true
      },
      "content": {
        "type": "text",
        "index": true,
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "url": {
        "type": "text"
      },
      "group": {
        "type": "text"
      },
      "view": {
        "type": "long"
      },
      "md5": {
        "type": "text"
      },
      "createTime": {
        "type": "date"
      },
      "pdfPath": {
        "type": "text"
      }
    }
  }
}


```


#第二题
查询es中，title字段分词匹配关键字 '数据库'的数据
```
POST /csdn/_search
{
    "query":{
        "match":{
            "title":"数据库"
        }
    }
}

```

#第三题
查询es中，title字段精准匹配关键字 '小米手机开发时非要sim卡才能调试怎么办' 的数据

```
POST /csdn/_search
{
    "query":{
        "term":{
            "title":"小米手机开发时非要sim卡才能调试怎么办"
        }
    }
}
```


#第四题
查询es中，create_time 在 2010-01-01 00：00：00 和 2021-12-31 23：59：59 的数据
```
POST /csdn/_search
{
  "query": {
    "range": {
      "createTime": {
        "gte": "2010-01-01",
        "lte": "2021-12-31",
        "format": "yyyy-MM-dd"
      }
    }
  }
}
```


#第五题
查询es中，view浏览量在 500-10000之间的数据
```
POST /csdn/_search
{
  "query": {
    "range": {
      "view": {
        "gte": 500,
        "lte": 10000
      }
    }
  }
}
```

#第六题
查询es中，不包含url的数据
```
POST /csdn/_search
{
  "query": {
    "bool": {
      "must_not": [
        {
          "exists": {
            "field": "url"
          }
        }
      ]
    }
  }
}
```

#第七题
查询es中，view大于100，并且，create_time在2021-07-01 00：00：00之前的数据
```
POST /csdn/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "range": {
            "view": {
              "gte": 100
            }
          }
        },
        {
          "range": {
            "createTime": {
              "lt": "2020-12-01"
            }
          }   
        }
      ]
    }
  }
}
```

#第八题
查询es中，content分词包含"redis",并且view大于200的数据
```

POST /csdn/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "content": "redis"
          }
          
        },
        {
          "range": {
            "view": {
              "gte": 200
            }
          }
        }
      ]
    }
  }
}
```

#第九题(不理想)
查询es中，title包含关键字，"数据库"或者"redis" 的数据分别有多少
```

POST /csdn/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "term": {
            "title": "redis"
          }
        },
        {
          "term": {
            "title": "数据库"
          }
        }
      ]
    }
  },
  "aggs": {
    "redis_count": {
      "terms": {"field":"title"}
    }
  },
  "size": 0
}
```

#第十题
查询es中，id为奇数的数据

#第十一题
查询es中，title是否存在为"你好啊"的数据

#第十二题
查询es中，title包含关键字"redis"或者"mysql"，并且view大于200，
并且create_time在2001-01-01 到 2021-12-31之间的数据，
并按照 view 降序排序

