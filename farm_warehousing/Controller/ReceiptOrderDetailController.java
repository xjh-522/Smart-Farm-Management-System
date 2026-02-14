package com.itbaizhan.farm_warehousing.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_warehousing.Service.ReceiptOrderDetailService;
import com.itbaizhan.farm_warehousing.entity.ReceiptOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/receiptOrderDetail")
public class ReceiptOrderDetailController {
    @Autowired
    private ReceiptOrderDetailService receiptOrderDetailService;
    @GetMapping("/list")
    public BaseResult<IPage<ReceiptOrderDetail>> list(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10")  Integer size,
                                                    @RequestParam (required = false)  Long receiptOrderId)
    {
        IPage<ReceiptOrderDetail> pageData = receiptOrderDetailService.findPage(page, size, receiptOrderId);
        return BaseResult.ok(pageData);
    }

    @GetMapping("/getReceiptOrderDetailById")
    public BaseResult<ReceiptOrderDetail> getById(@RequestParam Long id){
        ReceiptOrderDetail receiptOrderDetail = receiptOrderDetailService.findById(id);
        return BaseResult.ok(receiptOrderDetail);
    }

    @PostMapping("/addReceiptOrderDetail")
    public BaseResult<?> addReceiptOrderDetail(@RequestBody ReceiptOrderDetail receiptOrderDetail){
         receiptOrderDetailService.insert(receiptOrderDetail);
        return BaseResult.ok();
    }

    @PutMapping("/updateReceiptOrderDetail")
    public BaseResult<?> updateReceiptOrderDetail(@RequestBody ReceiptOrderDetail receiptOrderDetail){
        receiptOrderDetailService.update(receiptOrderDetail);
        return BaseResult.ok();
    }

    @DeleteMapping("/deleteReceiptOrderDetail")
    public BaseResult<?> deleteReceiptOrderDetail(@RequestParam String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        receiptOrderDetailService.delete(idList);
        return BaseResult.ok();
    }

}
