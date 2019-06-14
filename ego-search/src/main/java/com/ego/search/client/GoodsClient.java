package com.ego.search.client;

import com.ego.common.pojo.PageResult;
import com.ego.item.api.GoodsApi;
import com.ego.item.bo.SpuBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

}

