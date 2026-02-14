package com.itbaizhan.farm_warehousing.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.farm_common.Utils.SecurityUtil;
import com.itbaizhan.farm_warehousing.entity.ReceiptOrderDetail;
import com.itbaizhan.farm_warehousing.mapper.ReceiptOrderDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReceiptOrderDetailService {
    @Autowired
    private ReceiptOrderDetailMapper receiptOrderDetailMapper;
    public Boolean insert(ReceiptOrderDetail receiptOrderDetail){
        String userName = SecurityUtil.getUserName();
        receiptOrderDetail.setCreateBy(userName);
        receiptOrderDetail.setUpdateBy(userName);
        receiptOrderDetail.setCreateTime(LocalDateTime.now());
        receiptOrderDetail.setUpdateTime(LocalDateTime.now());
        return receiptOrderDetailMapper.insert(receiptOrderDetail)>0;
    }
    public Boolean update(ReceiptOrderDetail receiptOrderDetail){
        String userName = SecurityUtil.getUserName();
        receiptOrderDetail.setUpdateBy(userName);
        receiptOrderDetail.setUpdateTime(LocalDateTime.now());
        return receiptOrderDetailMapper.updateById(receiptOrderDetail)>0;
    }
    public Boolean delete(List<Long> ids){
        return receiptOrderDetailMapper.deleteByIds(ids)>0;
    }
    public ReceiptOrderDetail findById(Long id){
        return receiptOrderDetailMapper.selectById(id);
    }
    public IPage<ReceiptOrderDetail> findPage(Integer page, Integer size, Long receiptOrderId){
        IPage<ReceiptOrderDetail> pageData = new Page<>(page, size);
        QueryWrapper<ReceiptOrderDetail> queryWrapper = new QueryWrapper<>();
        if(receiptOrderId != null){
            queryWrapper.eq("receipt_order_id", receiptOrderId);
        }
        queryWrapper.orderByDesc("create_time");
        return receiptOrderDetailMapper.selectPage(pageData, queryWrapper);
    }
}
