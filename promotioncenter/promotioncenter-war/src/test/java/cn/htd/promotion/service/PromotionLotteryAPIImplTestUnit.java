package cn.htd.promotion.service;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.htd.promotion.cpc.api.PromotionLotteryAPI;
import cn.htd.promotion.cpc.dto.request.LotteryActivityPageReqDTO;
import cn.htd.promotion.cpc.dto.request.ValidateLuckDrawReqDTO;
import cn.htd.promotion.cpc.dto.request.WinningRecordReqDTO;

@Transactional  
@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(locations={"classpath:applicationContext_test.xml","classpath:mybatis/sqlconfig/sqlMapConfig.xml"})  
public class PromotionLotteryAPIImplTestUnit {
	
	@Resource
	private PromotionLotteryAPI promotionLotteryAPI;
	
    @Before  
    public void setUp() throws Exception {  
    	
    }
   
    @Test
    @Rollback(false) 
    public void queryWinningRecord(){
    	WinningRecordReqDTO  dto = new WinningRecordReqDTO();
    	dto.setMemberNo("htd21343432");
    	dto.setStartNo(1);
    	dto.setEndNo(12);
    	dto.setMessageId("122121222");
    	
    	String winningRecordReqDTOJson = JSONObject.toJSONString(dto);
    	String res = promotionLotteryAPI.queryWinningRecord(winningRecordReqDTOJson);
    	System.out.println("queryWinningRecord:"+res);
    }

    @Test
    @Rollback(false) 
    public void validateLuckDrawPermission(){
    	ValidateLuckDrawReqDTO  dto = new ValidateLuckDrawReqDTO();
    	dto.setOrgId("1234");
    	dto.setMessageId("122121222");
    	
    	String validateLuckDrawReqDTOJson = JSONObject.toJSONString(dto);
    	String res = promotionLotteryAPI.validateLuckDrawPermission(validateLuckDrawReqDTOJson);
    	System.out.println("validateLuckDrawPermission:"+res);
    }
    
    @Test
    @Rollback(false) 
    public void lotteryActivityPage(){
    	LotteryActivityPageReqDTO  dto = new LotteryActivityPageReqDTO();
    	dto.setMemberNo("3253");
    	dto.setOrgId("813191");
    	dto.setPromotionId("2171730080044");
    	dto.setMessageId("122121222");
    	
    	String lotteryActivityPageReqDTOJson = JSONObject.toJSONString(dto);
    	String res = promotionLotteryAPI.lotteryActivityPage(lotteryActivityPageReqDTOJson);
    	System.out.println("validateLuckDrawPermission:"+res);
    }
}
