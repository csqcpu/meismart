package com.lottery.service.ad;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdContentMapper;
import com.lottery.model.ad.AdContent;

@Service
@Transactional
public class AdContentService {
	@Resource
	public AdContentMapper adContentMapper;

	public int insert(AdContent record) {
		return adContentMapper.insert(record);
	}

	public AdContent findById(Integer content_id) {
		return adContentMapper.findById(content_id);
	}

	public List<AdContent> findByParam(Map<String, Object> param) {
		return adContentMapper.findByParam(param);
	}

	public int update(AdContent adContent) {
		return adContentMapper.update(adContent);
	}

	public int delByIds(List<AdContent> adContentList) {
		return adContentMapper.delByIds(adContentList);
	}

	public int submitCheckByIds(List<AdContent> adContentList) {
		return adContentMapper.submitCheckByIds(adContentList);
	}

	public int checkPassByIds(List<AdContent> adContentList) {
		return adContentMapper.checkPassByIds(adContentList);
	}

	public int checkFailByIds(List<AdContent> adContentList) {
		return adContentMapper.checkFailByIds(adContentList);
	}
}