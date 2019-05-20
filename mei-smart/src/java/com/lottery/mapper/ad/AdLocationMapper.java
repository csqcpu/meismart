package com.lottery.mapper.ad;

import java.util.List;
import java.util.Map;

import com.lottery.model.ad.AdLocation;
import com.lottery.model.ad.AdLocation;

public interface AdLocationMapper {
	int insert(AdLocation record);
	
	AdLocation findById(Integer content_id);

	List<AdLocation> findByParam(Map<String, Object> param);

	int update(AdLocation adLocation);

	int delByIds(List<AdLocation> adLocationList);

	int submitCheckByIds(List<AdLocation> adLocationList);

	int checkPassByIds(List<AdLocation> adLocationList);

	int checkFailByIds(List<AdLocation> adLocationList);
}