package com.lottery.service.ad;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
 
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

}