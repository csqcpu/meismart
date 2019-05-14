package com.lottery.mapper.ad;

import java.util.List;
import java.util.Map;

import com.lottery.model.ad.AdContent;
import com.lottery.model.ad.AdUser;

public interface AdContentMapper {
	int insert(AdContent record);
	
	AdContent findById(Integer content_id);

	List<AdContent> findByParam(Map<String, Object> param);

	int update(AdContent adContent);

	int delByIds(List<AdContent> adContentList);

	int submitCheckByIds(List<AdContent> adContentList);

	int checkPassByIds(List<AdContent> adContentList);

	int checkFailByIds(List<AdContent> adContentList);
}