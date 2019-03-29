package com.lottery.mapper.ad;

import com.lottery.model.ad.AdPay;

public interface AdPayMapper {
    int deleteByPrimaryKey(Integer pay_id);

    int insert(AdPay record);

    int insertSelective(AdPay record);

    AdPay selectByPrimaryKey(Integer pay_id);

    int updateByPrimaryKeySelective(AdPay record);

    int updateByPrimaryKey(AdPay record);
}