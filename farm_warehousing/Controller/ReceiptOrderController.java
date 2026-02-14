package com.itbaizhan.farm_warehousing.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_warehousing.Service.ReceiptOrderService;
import com.itbaizhan.farm_warehousing.entity.ReceiptOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receiptOrder")
public class ReceiptOrderController {
    @Autowired
    private ReceiptOrderService receiptOrderService;
    @GetMapping("/list")
    public BaseResult<IPage<ReceiptOrder>> findPage(@RequestParam(defaultValue = "1") Integer orderPage,
                                             @RequestParam (defaultValue = "10") Integer orderNumber,
                                             @RequestParam (required = false) String receiptOrderNo,
                                             @RequestParam (required = false) Integer receiptOrderStatus)
    {
        IPage<ReceiptOrder> list = receiptOrderService.findPage(orderPage, orderNumber, receiptOrderNo, receiptOrderStatus);
        return BaseResult.ok(list);
    }
    @GetMapping("/getReceiptOrderById")
    public BaseResult<ReceiptOrder> findById(@RequestParam Long id){
        ReceiptOrder receiptOrder = receiptOrderService.findById(id);
        return BaseResult.ok(receiptOrder);
    }
    @PostMapping("/addReceiptOrder")
    public BaseResult<String> addReceiptOrder(@RequestBody ReceiptOrder receiptOrder){
        String receiptOrderNo = receiptOrderService.addReceiptOrder(receiptOrder);
        return BaseResult.ok(receiptOrderNo);
    }
    /**
     * 修改入库单
     * @param receiptOrder 入库单信息
     * @return 修改结果
     */
    @PutMapping("/updateReceiptOrder")
    public BaseResult<String> updateReceiptOrder(@RequestBody ReceiptOrder receiptOrder){
        receiptOrderService.updateReceiptOrder(receiptOrder);
        return BaseResult.ok();
    }
    @DeleteMapping("/deleteReceiptOrder")
    public BaseResult<?> deleteReceiptOrder(@RequestParam Long id){
        receiptOrderService.deleteReceiptOrder(id);
        return BaseResult.ok();
    }
    @PostMapping("/completeReceipt")
    public BaseResult<Boolean> completeReceipt(@RequestParam Long id){
        receiptOrderService.completeReceipt(id);
        return BaseResult.ok();
    }
}
