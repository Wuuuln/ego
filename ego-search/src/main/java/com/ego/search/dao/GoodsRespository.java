package com.ego.search.dao;

import com.ego.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRespository extends ElasticsearchRepository<Goods,Long> {
}
