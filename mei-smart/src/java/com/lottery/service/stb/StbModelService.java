package com.lottery.service.stb;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.stb.StbModelMapper;
import com.lottery.mapper.stb.StbModelMapper;
import com.lottery.model.stb.StbModel;
import com.lottery.model.stb.StbModel;

@Service
@Transactional
public class StbModelService {
	@Resource
	public StbModelMapper stbModelMapper;

    public List<StbModel> findAll(){
    	return stbModelMapper.findAll();
    }
    
	public int insert(StbModel record) {
		return stbModelMapper.insert(record);
	}

	public StbModel findById(Integer stb_id) {
		return stbModelMapper.findById(stb_id);
	}

	public List<StbModel> findByParam(Map<String, Object> param) {
		return stbModelMapper.findByParam(param);
	}

	public int update(StbModel stbModel) {
		return stbModelMapper.update(stbModel);
	}

	public int delByIds(List<StbModel> stbModelList) {
		return stbModelMapper.delByIds(stbModelList);
	}

	public int submitCheckByIds(List<StbModel> stbModelList) {
		return stbModelMapper.submitCheckByIds(stbModelList);
	}

	public int checkPassByIds(List<StbModel> stbModelList) {
		return stbModelMapper.checkPassByIds(stbModelList);
	}

	public int checkFailByIds(List<StbModel> stbModelList) {
		return stbModelMapper.checkFailByIds(stbModelList);
	}
}