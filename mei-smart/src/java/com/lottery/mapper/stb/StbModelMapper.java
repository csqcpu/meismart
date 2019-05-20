package com.lottery.mapper.stb;

import java.util.List;
import java.util.Map;

import com.lottery.model.stb.StbModel;

public interface StbModelMapper {
    List<StbModel> findAll();
    
	int insert(StbModel record);
	
	StbModel findById(Integer content_id);

	List<StbModel> findByParam(Map<String, Object> param);

	int update(StbModel stbModel);

	int delByIds(List<StbModel> stbModelList);

	int submitCheckByIds(List<StbModel> stbModelList);

	int checkPassByIds(List<StbModel> stbModelList);

	int checkFailByIds(List<StbModel> stbModelList);
}