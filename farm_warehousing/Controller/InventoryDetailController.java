package com.itbaizhan.farm_warehousing.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_warehousing.Service.InventoryDetailService;
import com.itbaizhan.farm_warehousing.entity.InventoryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存详情Controller
 */
@RestController
@RequestMapping("/inventoryDetail")
public class InventoryDetailController {
    @Autowired
    private InventoryDetailService inventoryDetailService;

    /**
     * 分页查询库存详情
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param warehouseId 仓库ID
     * @param areaId 库区ID
     * @param orderNo 入库单号，模糊查询
     * @return 查询结果
     */
    @GetMapping("/list")
    public BaseResult<IPage<InventoryDetail>> selectPage(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "warehouseId", required = false) Long warehouseId,
            @RequestParam(value = "areaId", required = false) Long areaId,
            @RequestParam(value = "orderNo", required = false) String orderNo){
        IPage<InventoryDetail> inventoryDetailPage = inventoryDetailService.selectPage(pageNum, pageSize, warehouseId, areaId, orderNo);
        return BaseResult.ok(inventoryDetailPage);
    }

    /**
     * 根据ID查询库存详情
     * @param id 库存详情ID
     * @return 查询结果
     */
    @GetMapping("/getInventoryDetailById")
    public BaseResult<InventoryDetail> getInventoryDetailById(@RequestParam Long id){
        InventoryDetail inventoryDetail = inventoryDetailService.getInventoryDetailById(id);
        return BaseResult.ok(inventoryDetail);
    }
}
