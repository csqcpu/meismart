package com.lottery.service.ad;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdContentMapper;
import com.lottery.model.ad.AdContent;
import com.lottery.model.ad.AdUser;

 
@Service
@Transactional
public class AdContentService{
	@Resource
	public AdContentMapper adContentMapper;

   
	public int deleteByPrimaryKey(Integer content_id){
    	return adContentMapper.deleteByPrimaryKey(content_id);
    }

	public int insert(AdContent record){
    	return adContentMapper.insert(record);
    }

	public int insertSelective(AdContent record){
    	return adContentMapper.insertSelective(record);
    }


	public AdContent selectByPrimaryKey(Integer content_id){
    	return adContentMapper.selectByPrimaryKey(content_id);
    }

	public int updateByPrimaryKeySelective(AdContent record){
    	return adContentMapper.updateByPrimaryKeySelective(record);
    }

	public int updateByPrimaryKey(AdContent record){
    	return adContentMapper.updateByPrimaryKey(record);
	}
    	
    public List<AdContent> findByParam(Map<String,Object> param){
        	return adContentMapper.findByParam(param);    	
    }
    public int update(AdContent adContent){
    	return adContentMapper.update(adContent);
    }
    public int delByContentId(Integer contentId){
    	return adContentMapper.delByContentId(contentId);
    }
}