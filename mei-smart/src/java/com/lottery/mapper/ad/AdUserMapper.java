package com.lottery.mapper.ad;

import java.util.List;
import java.util.Map;

import com.lottery.model.ad.AdUser;

public interface AdUserMapper {
	int deleteByPrimaryKey(Integer user_id);

	int insert(AdUser record);

	int insertSelective(AdUser record);

	AdUser selectByPrimaryKey(Integer user_id);

	int updateByPrimaryKeySelective(AdUser record);

	int updateByPrimaryKey(AdUser record);

	List<AdUser> findAll();

	int updateByUserId(AdUser adUser);
}