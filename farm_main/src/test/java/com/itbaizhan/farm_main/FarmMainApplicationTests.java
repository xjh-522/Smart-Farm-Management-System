package com.itbaizhan.farm_main;

import com.itbaizhan.farm_common.Utils.OrderNumberUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FarmMainApplicationTests {
    @Autowired
    private OrderNumberUtil orderNumberUtil;

    @Test
    void contextLoads() {
        System.out.println(orderNumberUtil.getOrderNumber());
        System.out.println(orderNumberUtil.getOrderNumber());

    }

}
