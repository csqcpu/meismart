package com.lottery.service.ad;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdUserMapper;
import com.lottery.model.ad.AdUser;

 
@Service
@Transactional
public class AdUserService{
	@Resource
	public AdUserMapper adUserMapper;

	public int updateAccountByName(String userName) {
		// TODO Auto-generated method stub
		//int count = adUserMapper.updateAccountByName(userName);
        return 1;
	}

    public List<AdUser> findAll(){
    	return adUserMapper.findAll();
    }
    
    public int updateByUserId(AdUser adUser){
    	return adUserMapper.updateByUserId(adUser);
    }
    public AdUser findByUserName(String userName){
    	return adUserMapper.findByUserName(userName);
    }
    
    public AdUser find(String userName){
    	return adUserMapper.findByUserName(userName);
    }
    
    public int delByUserName(String userName){
    	return adUserMapper.delByUserName(userName);
    }
    
    public int insert(AdUser adUser){
    	return adUserMapper.insert(adUser);
    }

    public int update(AdUser adUser){
    	return adUserMapper.update(adUser);
    }
    
    public List<AdUser> findByParam(Map<String,Object> paramMap){
    	return adUserMapper.findByParam(paramMap);
    }
    
    public int deleteBatch(List<AdUser> adUserList){
    	return adUserMapper.deleteBatch(adUserList);
    }
    
}