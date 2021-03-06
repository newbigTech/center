//package cn.htd.promotion.service;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import cn.htd.common.DataGrid;
//import cn.htd.common.Pager;
//import cn.htd.promotion.cpc.biz.service.BuyerLaunchBargainInfoService;
//import cn.htd.promotion.cpc.common.util.ExecuteResult;
//import cn.htd.promotion.cpc.dto.request.BuyerBargainLaunchReqDTO;
//import cn.htd.promotion.cpc.dto.response.BuyerLaunchBargainInfoResDTO;
//
//import com.alibaba.fastjson.JSON;
//
//@Transactional  
//@RunWith(SpringJUnit4ClassRunner.class)  
//@ContextConfiguration(locations={"classpath:applicationContext_test.xml","classpath:mybatis/sqlconfig/sqlMapConfig.xml"}) 
//public class BuyerLauchBargainTestUnit {
//	
//	@Resource
//	private BuyerLaunchBargainInfoService buyerLaunchBargainInfoService;
//	
//	@Test
//	@Rollback(false) 
//	public void addBuyerBargainLaunch() {
//		BuyerLaunchBargainInfoResDTO barfainDTO = new BuyerLaunchBargainInfoResDTO();
//		barfainDTO.setPromotionId("22172032410074");
//		barfainDTO.setLevelCode("2217203241007490");
//		barfainDTO.setBuyerCode("13913037054");
//		barfainDTO.setBuyerName("小龙");
//		barfainDTO.setBuyerTelephone("13913037054");
//		barfainDTO.setCreateId(111);
//		barfainDTO.setCreateName("xxoo");
//		barfainDTO.setCreateTime(new Date());
//		ExecuteResult<BuyerLaunchBargainInfoResDTO> result = buyerLaunchBargainInfoService.addBuyerBargainLaunch(barfainDTO, "337788");
//		System.out.println(result.getErrorMessage());
//	}
//	
////	@Test
////	@Rollback(false) 
////	public void addRedis(){
////		String price = promotionRedisDB.tailPop("XMZ");
////		System.out.println(price);
////	}
//	
//	@Test
//	@Rollback(false)
//	public void queryLaunchBargainInfoList(){
//		BuyerBargainLaunchReqDTO buyerBargainLaunch = new BuyerBargainLaunchReqDTO();
//		buyerBargainLaunch.setSellerCode("801781");
//		Pager<BuyerBargainLaunchReqDTO> page = new Pager<BuyerBargainLaunchReqDTO>();
//		page.setPageOffset(1);
//		page.setRows(10);
//		ExecuteResult<DataGrid<BuyerLaunchBargainInfoResDTO>> result = buyerLaunchBargainInfoService.queryLaunchBargainInfoList(buyerBargainLaunch, page);
//		System.out.println(JSON.toJSONString(result));
//	}
//	
//	
//}
