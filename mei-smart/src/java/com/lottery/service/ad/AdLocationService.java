package com.lottery.service.ad;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdLocationMapper;
import com.lottery.model.ad.AdLocation;

@Service
@Transactional
public class AdLocationService {
	@Resource
	public AdLocationMapper adLocationMapper;

	public int insert(AdLocation record) {
		return adLocationMapper.insert(record);
	}

	public AdLocation findById(Integer content_id) {
		return adLocationMapper.findById(content_id);
	}

	public List<AdLocation> findByParam(Map<String, Object> param) {
		return adLocationMapper.findByParam(param);
	}

	public int update(AdLocation adLocation) {
		return adLocationMapper.update(adLocation);
	}

	public int delByIds(List<AdLocation> adLocationList) {
		return adLocationMapper.delByIds(adLocationList);
	}

	public int submitCheckByIds(List<AdLocation> adLocationList) {
		return adLocationMapper.submitCheckByIds(adLocationList);
	}

	public int checkPassByIds(List<AdLocation> adLocationList) {
		return adLocationMapper.checkPassByIds(adLocationList);
	}

	public int checkFailByIds(List<AdLocation> adLocationList) {
		return adLocationMapper.checkFailByIds(adLocationList);
	}
}