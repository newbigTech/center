package cn.htd.promotion.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.taobao.pamirs.schedule.TaskItemDefine;

import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.promotion.cpc.api.GroupbuyingAPI;
import cn.htd.promotion.cpc.biz.dao.GroupbuyingInfoDAO;
import cn.htd.promotion.cpc.biz.handle.PromotionGroupbuyingRedisHandle;
import cn.htd.promotion.cpc.biz.service.GroupbuyingService;
import cn.htd.promotion.cpc.common.constants.GroupbuyingConstants;
import cn.htd.promotion.cpc.common.constants.RedisConst;
import cn.htd.promotion.cpc.common.emums.PromotionConfigureEnum;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.util.ExceptionUtils;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.common.util.KeyGeneratorUtils;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoCmplReqDTO;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoReqDTO;
import cn.htd.promotion.cpc.dto.request.GroupbuyingPriceSettingReqDTO;
import cn.htd.promotion.cpc.dto.request.GroupbuyingRecordReqDTO;
import cn.htd.promotion.cpc.dto.request.SinglePromotionInfoCmplReqDTO;
import cn.htd.promotion.cpc.dto.request.SinglePromotionInfoReqDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoCmplResDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoResDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingRecordResDTO;
import cn.htd.promotion.cpc.dto.response.PromotionConfigureDTO;

/**
 * Created by zf.zhang on 2017/9/1.
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext_test.xml"})
public class GroupbuyingTestUnit {

	@Resource
	private GroupbuyingAPI groupbuyingAPI;
	@Resource
    private KeyGeneratorUtils keyGeneratorUtils;
    @Resource
	private PromotionGroupbuyingRedisHandle promotionGroupbuyingRedisHandle;
    
	
    @Before
    public void setUp() throws Exception {

    }

    /**
     * 添加团购活动-测试用例
     */
	@Test
	@Rollback(false) 
    public void addGroupbuyingInfoTest(){
    	try {
            String messageId = "342453251349";
    		Long userId = 10001L;
    		String userName = "admin";
    		
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            
        	// 商品ITEMID
        	Long itemId = 20001L;
        	// 商品SKU编码
        	String skuCode = "200001";
        	
        	//团购商品信息
            GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO = new GroupbuyingInfoCmplReqDTO();
            groupbuyingInfoCmplReqDTO.setSellerCode("1001");
            groupbuyingInfoCmplReqDTO.setSellerName("测试店铺");
            groupbuyingInfoCmplReqDTO.setSellerAliasName("测试店铺-1");
            groupbuyingInfoCmplReqDTO.setItemId(itemId);
            groupbuyingInfoCmplReqDTO.setSkuCode(skuCode);
            groupbuyingInfoCmplReqDTO.setSkuName("测试商品");
            groupbuyingInfoCmplReqDTO.setSkuLabel("测试商品标签");
    		//设置主图
            groupbuyingInfoCmplReqDTO.setSkuPicUrl("/img1622/123.jpg");
    		
    		// 商品原价
    		BigDecimal skuCostPrice = new BigDecimal("50");
    		groupbuyingInfoCmplReqDTO.setSkuCostPrice(skuCostPrice);
    		// 真实参团人数
    		Integer realActorCount = 10;
    		groupbuyingInfoCmplReqDTO.setRealActorCount(realActorCount);
    		// 真实拼团价
    		BigDecimal realGroupbuyingPrice = new BigDecimal("100");
    		groupbuyingInfoCmplReqDTO.setRealGroupbuyingPrice(realGroupbuyingPrice);
    		
    		
    		// 团购开始时间
    		Date startTime = currentTime;
    		// 团购结束时间
    		Date endTime = DateUtils.addDays(currentTime, 1);
    		groupbuyingInfoCmplReqDTO.setStartTime(startTime);
    		groupbuyingInfoCmplReqDTO.setEndTime(endTime);
    		
    		// 销售区域编码
    		String salesAreaCode = "0101";
    		groupbuyingInfoCmplReqDTO.setSalesAreaCode(salesAreaCode);
    		// 销售区域名称
    		String salesAreaName = "北京";
    		groupbuyingInfoCmplReqDTO.setSalesAreaName(salesAreaName);
    		
    		
    		// 参与团购商品数量
    		Integer groupbuyingSkuCount = 100;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingSkuCount(groupbuyingSkuCount);
    		// 每人限购数量
    		Integer groupbuyingThreshold = 1;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingThreshold(groupbuyingThreshold);
    		// 团购订单有效时间（单位：分钟）
    		Integer groupbuyingValidInterval = 15;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingValidInterval(groupbuyingValidInterval);

    		groupbuyingInfoCmplReqDTO.setCreateId(userId);
    		groupbuyingInfoCmplReqDTO.setCreateName(userName);
    		groupbuyingInfoCmplReqDTO.setModifyId(userId);
    		groupbuyingInfoCmplReqDTO.setModifyName(userName);
    		
    		// 设置活动信息
    		SinglePromotionInfoCmplReqDTO singlePromotionInfoCmplReqDTO = new SinglePromotionInfoCmplReqDTO();
    		setPromotionParam(singlePromotionInfoCmplReqDTO);
    		groupbuyingInfoCmplReqDTO.setSinglePromotionInfoReqDTO(singlePromotionInfoCmplReqDTO);
    		
    		
    		// 设置团购价格
    		List<GroupbuyingPriceSettingReqDTO> groupbuyingPriceSettingReqDTOList = new ArrayList<GroupbuyingPriceSettingReqDTO>();
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO.setSortNum(1);
    		groupbuyingPriceSettingReqDTO.setActorCount(1);// 参团人数
    		BigDecimal groupbuyingPrice = new BigDecimal("100");// 拼团价
    		groupbuyingPriceSettingReqDTO.setGroupbuyingPrice(groupbuyingPrice);
    		
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO_2 = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO_2.setSortNum(2);
    		groupbuyingPriceSettingReqDTO_2.setActorCount(50);
    		groupbuyingPriceSettingReqDTO_2.setGroupbuyingPrice(new BigDecimal("90"));
    		
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO_3 = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO_3.setSortNum(3);
    		groupbuyingPriceSettingReqDTO_3.setActorCount(100);
    		groupbuyingPriceSettingReqDTO_3.setGroupbuyingPrice(new BigDecimal("70"));
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO);
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO_2);
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO_3);
    		
    		groupbuyingInfoCmplReqDTO.setGroupbuyingPriceSettingReqDTOList(groupbuyingPriceSettingReqDTOList);
    		
    		groupbuyingAPI.addGroupbuyingInfo(groupbuyingInfoCmplReqDTO, messageId);
            
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
	
	private void setPromotionParam(SinglePromotionInfoCmplReqDTO promotionInfo){

    	Date nowDt = new Date();

    	promotionInfo.setPromotionName("团购--张志峰测试");
    	promotionInfo.setPromotionProviderType("1");//促销活动发起方类型  1:平台，2:店铺
    	promotionInfo.setPromotionType("25");// 活动类型 1：优惠券，2：秒杀，3：限时购，21：扭蛋机，22：砍价，23：总部秒杀，24：刮刮乐，25：阶梯团
    	promotionInfo.setEffectiveTime(nowDt);
    	promotionInfo.setInvalidTime(DateUtils.addDays(nowDt, 1));
//    	promotionInfo.setStatus("2");//状态 1：活动未开始，2：活动进行中，3：活动已结束，9：已删除   （不需要设置，后台根据  开始时间和结束时间  自动生成）
    	promotionInfo.setShowStatus("3");//审核状态 0：待审核，1：审核通过，2：审核被驳回，3：启用，4：不启用
    	promotionInfo.setDealFlag(1);
    	List<PromotionConfigureDTO> promotionConfigureList = new ArrayList<PromotionConfigureDTO>();
    	/**
    	 * 配置类型   1：活动渠道，2：支付方式，3：配送方式
    	 * 配置值 
    	 * 活动渠道：   1：B2B商城，2：超级老板，3：汇掌柜，4：大屏；
    	 * 配送方式：   1：供应商配送  2：自提； Delivery mode
    	 * 支付方式：   1：余额帐支付，2：平台账户支付，3：在线支付 Payment method
    	 */
    	//配送方式
    	PromotionConfigureDTO deliveryMode = new PromotionConfigureDTO();
    	deliveryMode.setConfType("3");
    	deliveryMode.setConfValue("2");
    	//支付方式
    	PromotionConfigureDTO paymentMethod = new PromotionConfigureDTO();
    	paymentMethod.setConfType("2");
    	paymentMethod.setConfValue("3");
    	//活动渠道
    	PromotionConfigureDTO movable_channel = new PromotionConfigureDTO();
    	movable_channel.setConfType(PromotionConfigureEnum.MOVABLE_CHANNEL.key());
    	movable_channel.setConfValue(PromotionConfigureEnum.MovableChannelEnum.MOBILE_MALL.key());
		promotionConfigureList.add(deliveryMode);
		promotionConfigureList.add(paymentMethod);
		promotionConfigureList.add(movable_channel);
		
		promotionInfo.setPromotionConfigureList(promotionConfigureList );
		
	}

	
	
    /**
     * 修改团购活动-测试用例
     */
	@Test
	@Rollback(false) 
    public void updateGroupbuyingInfoTest(){
    	try {
            String messageId = "342453251349";
    		Long userId = 10001L;
    		String userName = "admin";
    		
    		String promotionId = "25171138110019";
    		
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            
        	// 商品ITEMID
        	Long itemId = 20001L;
        	// 商品SKU编码
        	String skuCode = "200001";
        	
        	//团购商品信息
            GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO = new GroupbuyingInfoCmplReqDTO();
//            groupbuyingInfoCmplReqDTO.setSellerCode("1001");
//            groupbuyingInfoCmplReqDTO.setSellerName("测试店铺");
            groupbuyingInfoCmplReqDTO.setSellerAliasName("测试店铺-3");
//            groupbuyingInfoCmplReqDTO.setItemId(itemId);
//            groupbuyingInfoCmplReqDTO.setSkuCode(skuCode);
//            groupbuyingInfoCmplReqDTO.setSkuName("测试商品");
//            groupbuyingInfoCmplReqDTO.setSkuLabel("测试商品标签");
    		//设置主图
//            groupbuyingInfoCmplReqDTO.setSkuPicUrl("/img1622/123.jpg");
            
            groupbuyingInfoCmplReqDTO.setPromotionId(promotionId);
    		
    		// 商品原价
    		BigDecimal skuCostPrice = new BigDecimal("50");
    		groupbuyingInfoCmplReqDTO.setSkuCostPrice(skuCostPrice);
    		// 真实参团人数
    		Integer realActorCount = 27;
    		groupbuyingInfoCmplReqDTO.setRealActorCount(realActorCount);
    		// 真实拼团价
    		BigDecimal realGroupbuyingPrice = new BigDecimal("87");
    		groupbuyingInfoCmplReqDTO.setRealGroupbuyingPrice(realGroupbuyingPrice);
    		
    		
    		// 团购开始时间
    		Date startTime = currentTime;
    		// 团购结束时间
    		Date endTime = DateUtils.addDays(currentTime, 1);
    		groupbuyingInfoCmplReqDTO.setStartTime(startTime);
    		groupbuyingInfoCmplReqDTO.setEndTime(endTime);
    		
    		// 销售区域编码
    		String salesAreaCode = "0102";
    		groupbuyingInfoCmplReqDTO.setSalesAreaCode(salesAreaCode);
    		// 销售区域名称
    		String salesAreaName = "南京";
    		groupbuyingInfoCmplReqDTO.setSalesAreaName(salesAreaName);
    		
    		
    		// 参与团购商品数量
    		Integer groupbuyingSkuCount = 100;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingSkuCount(groupbuyingSkuCount);
    		// 每人限购数量
    		Integer groupbuyingThreshold = 1;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingThreshold(groupbuyingThreshold);
    		// 团购订单有效时间（单位：分钟）
    		Integer groupbuyingValidInterval = 15;
    		groupbuyingInfoCmplReqDTO.setGroupbuyingValidInterval(groupbuyingValidInterval);

    		groupbuyingInfoCmplReqDTO.setCreateId(userId);
    		groupbuyingInfoCmplReqDTO.setCreateName(userName);
    		groupbuyingInfoCmplReqDTO.setModifyId(userId);
    		groupbuyingInfoCmplReqDTO.setModifyName(userName);
    		
    		// 设置活动信息
    		SinglePromotionInfoCmplReqDTO singlePromotionInfoCmplReqDTO = new SinglePromotionInfoCmplReqDTO();
    		setPromotionParam(singlePromotionInfoCmplReqDTO);
    		groupbuyingInfoCmplReqDTO.setSinglePromotionInfoReqDTO(singlePromotionInfoCmplReqDTO);
    		
    		
    		// 设置团购价格
    		List<GroupbuyingPriceSettingReqDTO> groupbuyingPriceSettingReqDTOList = new ArrayList<GroupbuyingPriceSettingReqDTO>();
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO.setItemId(itemId);
    		groupbuyingPriceSettingReqDTO.setSkuCode(skuCode);
    		groupbuyingPriceSettingReqDTO.setSortNum(1);
    		groupbuyingPriceSettingReqDTO.setActorCount(1);// 参团人数
    		BigDecimal groupbuyingPrice = new BigDecimal("100");// 拼团价
    		groupbuyingPriceSettingReqDTO.setGroupbuyingPrice(groupbuyingPrice);
    		
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO_2 = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO_2.setItemId(itemId);
    		groupbuyingPriceSettingReqDTO_2.setSkuCode(skuCode);
    		groupbuyingPriceSettingReqDTO_2.setSortNum(2);
    		groupbuyingPriceSettingReqDTO_2.setActorCount(50);
    		groupbuyingPriceSettingReqDTO_2.setGroupbuyingPrice(new BigDecimal("90"));
    		
    		GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO_3 = new GroupbuyingPriceSettingReqDTO();
    		groupbuyingPriceSettingReqDTO_3.setItemId(itemId);
    		groupbuyingPriceSettingReqDTO_3.setSkuCode(skuCode);
    		groupbuyingPriceSettingReqDTO_3.setSortNum(3);
    		groupbuyingPriceSettingReqDTO_3.setActorCount(100);
    		groupbuyingPriceSettingReqDTO_3.setGroupbuyingPrice(new BigDecimal("70"));
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO);
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO_2);
    		groupbuyingPriceSettingReqDTOList.add(groupbuyingPriceSettingReqDTO_3);
    		
    		groupbuyingInfoCmplReqDTO.setGroupbuyingPriceSettingReqDTOList(groupbuyingPriceSettingReqDTOList);
    		
    		groupbuyingAPI.updateGroupbuyingInfo(groupbuyingInfoCmplReqDTO, messageId);
            
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
	
	
	/**
	 * 根据promotionId获取的团购活动信息-测试用例
	 */
    @Test
    public void getGroupbuyingInfoCmplByPromotionIdTest(){
    	
        String messageId = "342453251349";
        String promotionId = "25171138110019";
        try {
        	ExecuteResult<GroupbuyingInfoCmplResDTO> executeResult = groupbuyingAPI.getGroupbuyingInfoCmplByPromotionId(promotionId, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 根据promotionId获取简单的团购活动信息-测试用例
     */
    @Test
    public void getSingleGroupbuyingInfoByPromotionIdTest(){
    	String messageId = keyGeneratorUtils.generateMessageId();
//    	 String messageId = "342453251349";
        String promotionId = "25171749290011";
        try {
        	ExecuteResult<GroupbuyingInfoResDTO> executeResult = groupbuyingAPI.getSingleGroupbuyingInfoByPromotionId(promotionId, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 根据条件分页查询团购活动信息-测试用例
     */
    @Test
    public void getGroupbuyingInfoCmplForPageTest(){
    	
        String messageId = "342453251349";
        
        String startTimeString = "2017-10-01 16:20:02";
        String endTimeString = "2017-10-31 16:20:02";
        		
        try {
        	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        	
    		Pager<GroupbuyingInfoReqDTO> page = new Pager<GroupbuyingInfoReqDTO>();
    		page.setPage(1);
    		page.setRows(20);
    		
    		GroupbuyingInfoReqDTO groupbuyingInfoReqDTO = new GroupbuyingInfoReqDTO();
    		groupbuyingInfoReqDTO.setSellerCode("1001");// 商家编码
//    		groupbuyingInfoReqDTO.setSkuName("测试商品");//商品名称
    		groupbuyingInfoReqDTO.setShowStatus("3");//审核状态 0：待审核，1：审核通过，2：审核被驳回，3：启用，4：不启用
    		groupbuyingInfoReqDTO.setActiveState("1");// 活动状态 [1.未开始,2.开团进行中,3.下单未开始,4.下单进行中,5.已结束]
    		groupbuyingInfoReqDTO.setStartTime(sdf.parse(startTimeString));
    		groupbuyingInfoReqDTO.setEndTime(sdf.parse(endTimeString));
    		
    		ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> executeResult = groupbuyingAPI.getGroupbuyingInfoCmplForPage(page, groupbuyingInfoReqDTO, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    /**
     * 根据条件获取单条参团记录-测试用例
     */
    @Test
    public void getSingleGroupbuyingRecordTest(){
        String messageId = keyGeneratorUtils.generateMessageId();
        String promotionId = "25171601240015";
        try {
        	GroupbuyingRecordReqDTO groupbuyingRecordReqDTO = new GroupbuyingRecordReqDTO();
            groupbuyingRecordReqDTO.setPromotionId(promotionId);// 促销活动编码
        	groupbuyingRecordReqDTO.setBuyerCode("2002");// 参团人账号
        	ExecuteResult<GroupbuyingRecordResDTO> executeResult = groupbuyingAPI.getSingleGroupbuyingRecord(groupbuyingRecordReqDTO, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    /**
     * 根据活动编号时时查询团购真实人数和价格-测试用例
     */
    @Test
    public void getGBActorCountAndPriceByPromotionIdTest(){
        String messageId = keyGeneratorUtils.generateMessageId();
        String promotionId = "25171629210037";
        try {
        	ExecuteResult<Map<String, String>> executeResult = groupbuyingAPI.getGBActorCountAndPriceByPromotionId(promotionId, messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult.getResult());
        	}else{
        		System.out.println("===>根据活动编号时时查询团购真实人数和价格失败！！！");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    /**
     * 分页查询参团记录-测试用例
     */
    @Test
    public void geGroupbuyingRecordForPageTest(){
    	
        String messageId = keyGeneratorUtils.generateMessageId();
        String promotionId = "25171601240015";
        		
        try {
        	
    		Pager<GroupbuyingRecordReqDTO> page = new Pager<GroupbuyingRecordReqDTO>();
    		page.setPage(1);
    		page.setRows(20);
    		
    		GroupbuyingRecordReqDTO groupbuyingRecordReqDTO = new GroupbuyingRecordReqDTO();
            groupbuyingRecordReqDTO.setPromotionId(promotionId);// 促销活动编码
    		
    		ExecuteResult<DataGrid<GroupbuyingRecordResDTO>> executeResult = groupbuyingAPI.geGroupbuyingRecordForPage(page, groupbuyingRecordReqDTO, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
	/**
	 * 活动上下架-测试用例
	 */
    @Test
    @Rollback(false) 
	public void updateShowStatusByPromotionIdTest(){
    	try{
			String messageId = "342453251349";
			String promotionId = "25171100430046";
			SinglePromotionInfoReqDTO dto = new SinglePromotionInfoReqDTO();
			dto.setPromotionId(promotionId);
			dto.setShowStatus("3");// 审核状态 0：待审核，1：审核通过，2：审核被驳回，3：启用，4：不启用
			dto.setModifyId(1L);
			dto.setModifyName("测试");
			ExecuteResult<?> executeResult = groupbuyingAPI.updateShowStatusByPromotionId(dto,messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>活动上下架失败！！！");
        	}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

    /**
     * 根据promotionId删除团购活动-测试用例
     */
	@Test
	@Rollback(false) 
	public void deleteGroupbuyingInfoByPromotionIdTest(){
		try{
			String messageId = "342453251349";
			String promotionId = "25171430390018";
			GroupbuyingInfoReqDTO dto = new GroupbuyingInfoReqDTO();
			dto.setPromotionId(promotionId);
			dto.setModifyId(1L);
			dto.setModifyName("测试");
			ExecuteResult<?> executeResult = groupbuyingAPI.deleteGroupbuyingInfoByPromotionId(dto,messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>删除团购活动失败！！！");
        	}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 商品是否被活动正在使用-测试用例
     */
	@Test
	public void hasProductIsBeingUsedByPromotionTest(){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTimeStr = "2017-11-16 15:20:00";
			String endTimeStr = "2017-11-28 15:20:00";
			
			String messageId = "342453251349";
			String skuCode = "1731665017";
		    Date startTime = sdf.parse(startTimeStr);//开始时间(开团开始时间)
		    Date endTime = sdf.parse(endTimeStr);//结束时间(下单结束时间)
		    
			ExecuteResult<Boolean> executeResult = groupbuyingAPI.hasProductIsBeingUsedByPromotion(skuCode,startTime,endTime,messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		if(Boolean.FALSE == executeResult.getResult()){ //商品没有被活动正在使用
        			System.out.println(skuCode + "商品没有被活动正在使用");
        		}else{
        			System.out.println(skuCode + "商品被活动正在使用");
        		}
        	}else{
        		System.out.println("===>检测失败！！！");
        	}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
    /**
     * 根据promotionId查询活动配置信息-测试用例
     */
    @Test
    public void getGBPromotionConfiguresByPromotionIdTest(){
    	String messageId = keyGeneratorUtils.generateMessageId();
//    	 String messageId = "342453251349";
        String promotionId = "25171622110022";
        try {
        	ExecuteResult<List<PromotionConfigureDTO>> executeResult = groupbuyingAPI.getGBPromotionConfiguresByPromotionId(promotionId, messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>查询失败！！！");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 添加团购记录(供移动端使用)-测试用例
     */
	@Test
	@Rollback(false) 
    public void addGroupbuyingRecord2HttpINTFCTest(){
		
    	try {
    		
    		String messageId = keyGeneratorUtils.generateMessageId();
    		Long userId = 10001L;
    		String userName = "admin";
    		
    		String promotionId = "25171640260010";
            
            GroupbuyingRecordReqDTO groupbuyingRecordReqDTO = new GroupbuyingRecordReqDTO();
            
            groupbuyingRecordReqDTO.setPromotionId(promotionId);// 促销活动编码
        	groupbuyingRecordReqDTO.setSellerCode("801781");// 商家编码
        	groupbuyingRecordReqDTO.setBuyerCode("2002");// 参团人账号
        	groupbuyingRecordReqDTO.setBuyerName("林寻");// 参团人姓名
        	groupbuyingRecordReqDTO.setBuyerContact("18801011008");// 参团人联系方式

        	groupbuyingRecordReqDTO.setCreateId(userId);
        	groupbuyingRecordReqDTO.setCreateName(userName);
        	groupbuyingRecordReqDTO.setModifyId(userId);
        	groupbuyingRecordReqDTO.setModifyName(userName);
    		
        	ExecuteResult<?> executeResult = groupbuyingAPI.addGroupbuyingRecord2HttpINTFC(groupbuyingRecordReqDTO, messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>参团失败！！！");
        	}
            
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
	
	/**
	 * 根据promotionId获取的团购活动信息(供移动端使用)-测试用例
	 */
    @Test
    public void getGroupbuyingInfoCmpl2HttpINTFCTest(){
    	
        String messageId = "342453251349";
        String promotionId = "25171430390018";
        try {
        	ExecuteResult<GroupbuyingInfoCmplResDTO> executeResult = groupbuyingAPI.getGroupbuyingInfoCmpl2HttpINTFC(promotionId, messageId);
        	System.out.println("===>executeResult:" + executeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据orgid获取该店铺所有团购商品(供移动端使用)-测试用例
     */
	@Test
	public void getGroupbuyingList2HttpINTFCTest(){
		String messageId = "342453251349";
		try{
			Pager<GroupbuyingInfoReqDTO> pager = new Pager<GroupbuyingInfoReqDTO>();
			pager.setPageOffset(1);
			pager.setRows(10);
			GroupbuyingInfoReqDTO dto = new GroupbuyingInfoReqDTO();
			dto.setSellerCode("801781");
//			dto.setBuyerCode("2002");
			ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> executeResult = groupbuyingAPI.getGroupbuyingList2HttpINTFC(pager,dto,messageId);
	      	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>根据orgid获取该店铺所有团购商品失败！！！");
        	}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 查询首页单个团购活动(供移动端使用)-测试用例
     */
	@Test
	public void getGroupbuyingHomePage2HttpINTFCTest(){
		String messageId = "342453251349";
		try{
			GroupbuyingInfoReqDTO dto = new GroupbuyingInfoReqDTO();
			dto.setSellerCode("801781");
			dto.setBuyerCode("2002");
			ExecuteResult<GroupbuyingInfoCmplResDTO> executeResult = groupbuyingAPI.getGroupbuyingHomePage2HttpINTFC(dto,messageId);
	      	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>查询首页单个团购活动失败！！！");
        	}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 分页查询我的团购列表(供移动端使用)-测试用例
     */
	@Test
	public void getMyGroupbuyingForPage2HttpINTFCTest(){
		String messageId = "342453251349";
		try{
			Pager<GroupbuyingInfoReqDTO> pager = new Pager<GroupbuyingInfoReqDTO>();
			pager.setPageOffset(1);
			pager.setRows(10);
			GroupbuyingInfoReqDTO groupbuyingInfoReqDTO = new GroupbuyingInfoReqDTO();
			groupbuyingInfoReqDTO.setBuyerCode("534382");
//			groupbuyingInfoReqDTO.setActiveState("1");// 活动状态，PC端状态 [1.未开始,2.开团进行中,3.下单未开始,4.下单进行中,5.已结束]
			groupbuyingInfoReqDTO.setActiveState("2");// 活动状态，仅供移动端使用 [1.全部,2.进行中,3.已结束]
			ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> executeResult = groupbuyingAPI.getMyGroupbuyingForPage2HttpINTFC(pager,groupbuyingInfoReqDTO,messageId);
	      	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){ //25171622110022 2
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>分页查询我的团购列表失败！！！");
        	}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 修改团购活动-手工跟新-测试用例
     */
	@Test
	@Rollback(false) 
    public void updateGroupbuyingInfoByManualTest(){
    	
    	try {
    		String messageId = keyGeneratorUtils.generateMessageId();
    		Long userId = 10001L;
    		String userName = "admin";
    		
    		String promotionId = "25171138110019";
    		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String effectiveTimeStr = "2017-11-16 15:20:00";//开团开始时间
			String invalidTimeStr = "2017-11-17 15:20:00";//开团结束时间
			String startTimeStr = "2017-11-18 15:20:00"; //下单开始时间
			String endTimeStr = "2017-11-20 15:20:00";//下单结束时间

			Date effectiveTime = sdf.parse(effectiveTimeStr);
			Date invalidTime = sdf.parse(invalidTimeStr);
		    Date startTime = sdf.parse(startTimeStr);
		    Date endTime = sdf.parse(endTimeStr);
        	
        	//团购商品信息
            GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO = new GroupbuyingInfoCmplReqDTO();
            groupbuyingInfoCmplReqDTO.setPromotionId(promotionId);
    		groupbuyingInfoCmplReqDTO.setStartTime(startTime);// 团购开始时间
    		groupbuyingInfoCmplReqDTO.setEndTime(endTime);// 团购结束时间
    		
    		// 设置活动信息
    		SinglePromotionInfoCmplReqDTO singlePromotionInfoCmplReqDTO = new SinglePromotionInfoCmplReqDTO();
    		singlePromotionInfoCmplReqDTO.setEffectiveTime(effectiveTime);
    		singlePromotionInfoCmplReqDTO.setInvalidTime(invalidTime);
    		groupbuyingInfoCmplReqDTO.setSinglePromotionInfoReqDTO(singlePromotionInfoCmplReqDTO);
    		
         	ExecuteResult<?> executeResult = groupbuyingAPI.updateGroupbuyingInfoByManual(groupbuyingInfoCmplReqDTO, messageId);
        	if(ResultCodeEnum.SUCCESS.getCode().equals(executeResult.getCode())){
        		System.out.println("===>executeResult:" + executeResult);
        	}else{
        		System.out.println("===>手工修改团购活动失败！！！");
        	}
            
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
	
	
	
	/**
	 * redis测试-测试用例
	 */
	@Test
	public void RedisTest() {
		try {
//			String key = "test-zzf-key";
//
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("name", "林寻88");
//			map.put("age", "228");
//			map.put("sex", "nam");
//			map.put("promotionId", "25171100430046");
//
//			boolean flag = promotionGroupbuyingRedisHandle.testHash(key, map);
//			System.out.println(flag);
			

//			boolean flag2 = promotionGroupbuyingRedisHandle.testStr("25171100430046", "");
//			System.out.println(flag2);
			
			
			boolean flag3 = promotionGroupbuyingRedisHandle.removeGroupbuyingInfoCmpl2Redis("25171005280040");
			System.out.println(flag3);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private GroupbuyingService groupbuyingService;
    @Resource
    private GroupbuyingInfoDAO groupbuyingInfoDAO;
    
	@Test
	@Rollback(false) 
	public void TaskTest() {
	String messageId = keyGeneratorUtils.generateMessageId();
//        String promotionId ="25171100430046";
//        //计算真实价格
//        Map<String, String> resultMap = groupbuyingService.getGBActorCountAndPriceByPromotionId(promotionId,messageId);
//        String realGroupbuyingPrice = resultMap.get(GroupbuyingConstants.GROUPBUYINGINFO_REAL_GROUPBUYINGPRICE_KEY);
//        String realActorCount = resultMap.get(GroupbuyingConstants.GROUPBUYINGINFO_REAL_ACTOR_COUNT_KEY);
//        //保存真实价格
//        if (!StringUtils.isEmpty(realGroupbuyingPrice) && !StringUtils.isEmpty(realActorCount)){
//        	// 1.如果Redis执行失败，数据库不执行；2.如果Redis执行成功，数据库执行失败，下次还会进行1操作
//        	
//        	boolean redisFlag = true;
//        	try {
//          	    String groupbuyingResultKey = RedisConst.PROMOTION_REDIS_GROUPBUYINGINFO_RESULT + "_" + promotionId;
//	         	// redis设置真实参团人数
//	      	    stringRedisTemplate.opsForHash().put(groupbuyingResultKey, RedisConst.PROMOTION_REDIS_GROUPBUYINGINFO_REAL_ACTOR_COUNT, realActorCount);
//	        	// redis设置真实拼团价
//	      	    stringRedisTemplate.opsForHash().put(groupbuyingResultKey, RedisConst.PROMOTION_REDIS_GROUPBUYINGINFO_REAL_GROUPBUYINGPRICE, realGroupbuyingPrice);
//            	
//			} catch (Exception e) {
//				 redisFlag = false;
//				 e.printStackTrace();
//			}
//    
//        	if(redisFlag){//redis设置成功
//                GroupbuyingInfoReqDTO groupbuyingInfoReqDTO = new GroupbuyingInfoReqDTO();
//                groupbuyingInfoReqDTO.setPromotionId(promotionId);
//                groupbuyingInfoReqDTO.setRealActorCount(Integer.valueOf(realActorCount));// 真实参团人数
//                groupbuyingInfoReqDTO.setRealGroupbuyingPrice(new BigDecimal(realGroupbuyingPrice));// 真实拼团价
//                groupbuyingInfoDAO.updateGBActorCountAndPrice(groupbuyingInfoReqDTO);
//        	}
//        }
	
	//[taskParameter:][ownSign:BASE][taskQueueNum:1][[{"parameter":"","taskItemId":"0"}]][eachFetchDataNum:500]
	
	    List<TaskItemDefine> taskQueueList = new ArrayList<TaskItemDefine>();
	    TaskItemDefine taskItemDefine = new TaskItemDefine();
	    taskItemDefine.setTaskItemId("0");
	    taskQueueList.add(taskItemDefine);
	    try {
			List<GroupbuyingInfoResDTO>  groupbuyingInfoResDTOList = selectTasks("","",1,taskQueueList,500);
			System.out.println("===>groupbuyingInfoResDTOList:" + groupbuyingInfoResDTOList);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        System.out.println("成功.......");
        
	}
	
	public List<GroupbuyingInfoResDTO> selectTasks(String taskParameter,
			String ownSign, int taskQueueNum,
			List<TaskItemDefine> taskQueueList, int eachFetchDataNum)
			throws Exception {
		GroupbuyingInfoCmplReqDTO condition = new GroupbuyingInfoCmplReqDTO();
		Pager<GroupbuyingInfoCmplReqDTO> pager = null;
		List<String> taskIdList = new ArrayList<String>();
		List<GroupbuyingInfoResDTO> groupbuyingDTOList = null;
		if (eachFetchDataNum > 0) {
			pager = new Pager<GroupbuyingInfoCmplReqDTO>();
			pager.setPageOffset(0);
			pager.setRows(eachFetchDataNum);
		}
		try {
			if (taskQueueList != null && taskQueueList.size() > 0) {
				for (TaskItemDefine taskItem : taskQueueList) {
					taskIdList.add(taskItem.getTaskItemId());
				}
				condition.setTaskQueueNum(taskQueueNum);
				condition.setTaskIdList(taskIdList);
				groupbuyingDTOList = groupbuyingInfoDAO
						.queryNeedUpdateGroupbuying4Task(condition, pager);
			}
		} catch (Exception e) {
		} finally {
		}
		return groupbuyingDTOList;
	}
	

}
