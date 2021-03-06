package cn.htd.promotion.cpc.biz.handle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.common.constant.DictionaryConst;
import cn.htd.common.util.DictionaryUtils;
import cn.htd.promotion.cpc.biz.dmo.BuyerUseTimelimitedLogDMO;
import cn.htd.promotion.cpc.common.constants.PromotionCenterConst;
import cn.htd.promotion.cpc.common.constants.RedisConst;
import cn.htd.promotion.cpc.common.exception.PromotionCenterBusinessException;
import cn.htd.promotion.cpc.common.util.PromotionRedisDB;
import cn.htd.promotion.cpc.dto.response.PromotionInfoDTO;
import cn.htd.promotion.cpc.dto.response.PromotionOrderItemDTO;
import cn.htd.promotion.cpc.dto.response.PromotionTimelimitedShowDTO;
import cn.htd.promotion.cpc.dto.response.TimelimitedInfoResDTO;
import cn.htd.promotion.cpc.dto.response.TimelimitedResultDTO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("promotionTimelimitedRedisHandle")
public class PromotionTimelimitedRedisHandle {

    protected static transient Logger logger = LoggerFactory.getLogger(PromotionTimelimitedRedisHandle.class);

    @Resource
    private DictionaryUtils dictionary;

    @Resource
    private PromotionRedisDB promotionRedisDB;
    
    public TimelimitedInfoResDTO getTimelitedInfoByPromotionId(String promotionId){
    	String timelimitedJsonStr = "";
    	 TimelimitedInfoResDTO timelimitedInfoDTO = null;
		timelimitedJsonStr = promotionRedisDB.getHash(RedisConst.PROMOTION_REDIS_TIMELIMITED, promotionId);
		timelimitedInfoDTO = JSON.parseObject(timelimitedJsonStr, TimelimitedInfoResDTO.class);
		if(null !=timelimitedInfoDTO){
			return timelimitedInfoDTO;
		}
		return null;
    }

    /**
     * 秒杀 - 保存秒杀活动的启用状态
     * 
     * @param skuCodeList 商品编码集合
     * @return
     */
    public void saveTimelimitedValidStatus2Redis(TimelimitedInfoResDTO timelimitedInfoResDTO) {
    	  String jsonObj = JSON.toJSONString(timelimitedInfoResDTO);
    	  promotionRedisDB.setHash(RedisConst.PROMOTION_REDIS_TIMELIMITED, timelimitedInfoResDTO.getPromotionId(), jsonObj);
    }
    
    
    /**
     * 秒杀 - 根据促销活动id修改秒杀活动的启用状态
     * 
     * @param skuCodeList 商品编码集合
     * @return
     */
    public void updateTimelimitedValidStatus2Redis(String promotionId,String showStatus) {
    	 TimelimitedInfoResDTO timelimitedInfoDTO = null;
	     String timelimitedJsonStr = "";
	     if(StringUtils.isNotBlank(promotionId) && StringUtils.isNotBlank(showStatus)){
			 timelimitedJsonStr = promotionRedisDB.getHash(RedisConst.PROMOTION_REDIS_TIMELIMITED, promotionId);
			 timelimitedInfoDTO = JSON.parseObject(timelimitedJsonStr, TimelimitedInfoResDTO.class);
			 timelimitedInfoDTO.getPromotionExtendInfoDTO().setShowStatus(showStatus);
	    	 String jsonObj = JSON.toJSONString(timelimitedInfoDTO);
	    	 promotionRedisDB.setHash(RedisConst.PROMOTION_REDIS_TIMELIMITED, timelimitedInfoDTO.getPromotionId(), jsonObj);
	     }
    }


    /**
     * 秒杀 - 查询Redis秒杀活动展示结果信息
     *
     * @param promotionId
     * @return
     */
    public TimelimitedResultDTO getRedisTimelimitedResult(String promotionId) throws PromotionCenterBusinessException {
        Map<String, String> resultMap = null;
        TimelimitedResultDTO resultDTO = null;
        String timelimitedResultKey = RedisConst.PROMOTION_REDIS_TIMELIMITED_RESULT + "_" + promotionId;
        resultMap = promotionRedisDB.getHashOperations(timelimitedResultKey);
        if (resultMap == null || resultMap.isEmpty()) {
            throw new PromotionCenterBusinessException(PromotionCenterConst.TIMELIMITED_RESULT_NOT_EXIST,
                    "该秒杀活动的结果数据不存在!");
        }
        resultDTO = new TimelimitedResultDTO();
        resultDTO.setPromotionId(promotionId);
        resultDTO.setTotalSkuCount(Integer.valueOf(resultMap.get(RedisConst.PROMOTION_REDIS_TIMELIMITED_TOTAL_COUNT)));
        resultDTO.setShowRemainSkuCount(Integer.valueOf(resultMap.get(RedisConst.PROMOTION_REDIS_TIMELIMITED_SHOW_REMAIN_COUNT)));
        resultDTO.setShowTimelimitedActorCount(
                Integer.valueOf(resultMap.get(RedisConst.PROMOTION_REDIS_TIMELIMITED_SHOW_ACTOR_COUNT)));
        resultDTO.setRealRemainSkuCount(Integer.valueOf(resultMap.get(RedisConst.PROMOTION_REDIS_TIMELIMITED_REAL_REMAIN_COUNT)));
        resultDTO.setRealTimelimitedActorCount(
                Integer.valueOf(resultMap.get(RedisConst.PROMOTION_REDIS_TIMELIMITED_REAL_ACTOR_COUNT)));
        return resultDTO;
    }

	/**
	 * 秒杀 -  从Redis中取得秒杀log信息
	 * 
	 * @param orderTimelimiteDTO
	 * @return
	 */
    public BuyerUseTimelimitedLogDMO getRedisReleaseBuyerTimelimitedUseLog(PromotionOrderItemDTO orderTimelimiteDTO)
            throws PromotionCenterBusinessException {
        String promotionId = orderTimelimiteDTO.getPromotionId();
        String orderNo = orderTimelimiteDTO.getOrderNo();
        String buyerCode = orderTimelimiteDTO.getBuyerCode();
        BuyerUseTimelimitedLogDMO useLog = null;
        useLog = getRedisBuyerTimelimitedUseLog(orderTimelimiteDTO);
        if (useLog == null) {
            return null;
        }
        if (orderTimelimiteDTO.getPromoitionChangeType().equals(useLog.getUseType())) {
            return null;
        }
        if (!dictionary.getValueByCode(DictionaryConst.TYPE_BUYER_PROMOTION_STATUS,
                DictionaryConst.OPT_BUYER_PROMOTION_STATUS_REVERSE).equals(useLog.getUseType())) {
            throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_TIMELIMITED_NO_REVERSE, "订单号:" + orderNo
                    + " 会员编号:" + buyerCode + " 促销活动ID:" + promotionId + " 该订单已被"
                    + dictionary.getNameByValue(DictionaryConst.TYPE_BUYER_PROMOTION_STATUS, useLog.getUseType()));
        }
        useLog.setUseType(orderTimelimiteDTO.getPromoitionChangeType());
        useLog.setModifyId(orderTimelimiteDTO.getOperaterId());
        useLog.setModifyName(orderTimelimiteDTO.getOperaterName());
        useLog.setModifyTime(new Date());
        return useLog;
    }
    
	/**
	 * 秒杀 - 从Redis中取得秒杀log信息
	 * 
	 * @param orderTimelimiteDTO
	 * @return
	 */
    private BuyerUseTimelimitedLogDMO getRedisBuyerTimelimitedUseLog(PromotionOrderItemDTO orderTimelimiteDTO)
            throws PromotionCenterBusinessException {
        String buyerCode = orderTimelimiteDTO.getBuyerCode();
        String promotionId = orderTimelimiteDTO.getPromotionId();
        String useLogRedisKey = buyerCode + "&" + promotionId;
        String useLogJsonStr = "";
        BuyerUseTimelimitedLogDMO useLog = null;
        useLogJsonStr = promotionRedisDB.getHash(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_USELOG, useLogRedisKey);
        useLog = JSON.parseObject(useLogJsonStr, BuyerUseTimelimitedLogDMO.class);
        return useLog;
    }
    
	/**
	 * 秒杀 - 根据状态（锁定、释放）处理会员秒杀
	 *  @param dealType
	 * @param orderTimelimiteDTO
	 * @return
	 */
    public void dealRedisBuyerTimelimitedList(List<PromotionOrderItemDTO> timelimitedList, String dealType)
            throws PromotionCenterBusinessException {
        List<PromotionOrderItemDTO> rollbackTimelimitedList = new ArrayList<PromotionOrderItemDTO>();
        try {
            for (PromotionOrderItemDTO timelimitedInfo : timelimitedList) {
                if (DictionaryConst.OPT_BUYER_PROMOTION_STATUS_REVERSE.equals(dealType)) {
                    dealRedisReverseBuyerTimelimitedInfo(timelimitedInfo);
                    rollbackTimelimitedList.add(timelimitedInfo);
                } else if (DictionaryConst.OPT_BUYER_PROMOTION_STATUS_RELEASE.equals(dealType)) {
                    dealRedisReleaseBuyerTimelimitedInfo(timelimitedInfo);
                }
            }
        } catch (PromotionCenterBusinessException bcbe) {
            if (!rollbackTimelimitedList.isEmpty()) {
                for (PromotionOrderItemDTO rollbackTimelimited : rollbackTimelimitedList) {
                    dealRedisReleaseBuyerTimelimitedInfo(rollbackTimelimited);
                }
            }
            throw bcbe;
        }
    }
    
    /**
     * 锁定会员参加秒杀活动商品数量
     *
     * @param buyerTimelimitedInfo
     */
    private void dealRedisReverseBuyerTimelimitedInfo(PromotionOrderItemDTO buyerTimelimitedInfo)
            throws PromotionCenterBusinessException {
    }

    /**
     * 释放会员参加秒杀活动商品数量
     *
     * @param buyerTimelimitedInfo
     */
    private void dealRedisReleaseBuyerTimelimitedInfo(PromotionOrderItemDTO buyerTimelimitedInfo) {
        String promotionId = buyerTimelimitedInfo.getPromotionId();
        String buyerCode = buyerTimelimitedInfo.getBuyerCode();
        Integer skuCount = buyerTimelimitedInfo.getQuantity();
        String timelimitedResultKey = RedisConst.PROMOTION_REDIS_TIMELIMITED_RESULT + "_" + promotionId;
        String buyerTimelimitedKey = buyerCode + "&" + promotionId;
        long buyerTimelimitedCount = 0;

        promotionRedisDB.incrHashBy(timelimitedResultKey, RedisConst.PROMOTION_REDIS_TIMELIMITED_REAL_REMAIN_COUNT, skuCount);
        promotionRedisDB.incrHashBy(timelimitedResultKey, RedisConst.PROMOTION_REDIS_TIMELIMITED_SHOW_REMAIN_COUNT, skuCount);
        buyerTimelimitedCount = promotionRedisDB.incrHashBy(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_COUNT, buyerTimelimitedKey,
                skuCount * -1);
        if (buyerTimelimitedCount < 0) {
        	promotionRedisDB.setHash(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_COUNT, buyerTimelimitedKey, "0");
        }
    }
    
	/**
	 * 秒杀 - 秒杀活动参加记录保存进Redis,并更新DB
	 * 
	 * @param useLogList
	 * @return
	 */
    public void updateRedisUseTimelimitedLog(List<BuyerUseTimelimitedLogDMO> useLogList) {
        String useLogRedisKey = "";
        if (useLogList == null || useLogList.isEmpty()) {
            return;
        }
        for (BuyerUseTimelimitedLogDMO useLog : useLogList) {
            useLogRedisKey = useLog.getBuyerCode() + "&" + useLog.getPromotionId();
            promotionRedisDB.setHash(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_USELOG, useLogRedisKey, JSON.toJSONString(useLog));
            promotionRedisDB.tailPush(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_NEED_SAVE_USELOG, JSON.toJSONString(useLog));
        }
    }
    
    /**
     * 秒杀 - 从Redis中取得锁定时订单行秒杀log信息
     *
     * @param orderTimelimiteDTO
     */
    public BuyerUseTimelimitedLogDMO getRedisReverseBuyerTimelimitedUseLog(PromotionOrderItemDTO orderTimelimiteDTO)
            throws PromotionCenterBusinessException {
        String seckillLockNo = orderTimelimiteDTO.getSeckillLockNo();
        String orderNo = orderTimelimiteDTO.getOrderNo();
        String buyerCode = orderTimelimiteDTO.getBuyerCode();
        String promotionId = orderTimelimiteDTO.getPromotionId();
        BuyerUseTimelimitedLogDMO useLog = null;
        useLog = getRedisBuyerTimelimitedUseLog(orderTimelimiteDTO);
        if (useLog != null) {
            if (!orderTimelimiteDTO.getPromoitionChangeType().equals(useLog.getUseType())) {
                throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_IMELIMITED_STATUS_ERROR, " 会员编号:"
                        + buyerCode + " 促销活动ID:" + promotionId + " 该会员已参加秒杀活动秒杀活动次数已被"
                        + dictionary.getNameByValue(DictionaryConst.TYPE_BUYER_PROMOTION_STATUS, useLog.getUseType()));
            }
            if (!StringUtils.isEmpty(useLog.getOrderNo())) {
                if (!StringUtils.isEmpty(orderNo)) {
                    if (orderNo.equals(useLog.getOrderNo())) {
                        return null;
                    }
                    throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_TIMELIMITED_DOUBLE_REVERSE,
                            " 会员编号:" + buyerCode + " 促销活动ID:" + promotionId + " 秒杀订单号:" + useLog.getOrderNo()
                                    + " 该会员已参加该秒杀活动生成了秒杀订单");
                }
                throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_HAS_TIMELIMITED_ERROR,
                        " 会员编号:" + buyerCode + " 促销活动ID:" + promotionId + " 该会员已参加该秒杀活动");
            } else {
                if (seckillLockNo.equals(useLog.getSeckillLockNo())) {
                    if (StringUtils.isEmpty(orderNo)) {
                        return null;
                    }
                    useLog.setSeckillLockNo(seckillLockNo);
                    useLog.setOrderNo(orderNo);
                    useLog.setModifyId(orderTimelimiteDTO.getOperaterId());
                    useLog.setModifyName(orderTimelimiteDTO.getOperaterName());
                    useLog.setModifyTime(new Date());
                    return useLog;
                }
                throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_TIMELIMITED_INFO_ERROR,
                        " 会员编号:" + buyerCode + " 促销活动ID:" + promotionId + " 会员预占秒杀活动信息未释放");
            }
        } else if (!StringUtils.isEmpty(seckillLockNo) && !StringUtils.isEmpty(orderNo)) {
            throw new PromotionCenterBusinessException(PromotionCenterConst.BUYER_TIMELIMITED_NO_PREREVERSE,
                    " 会员编号:" + buyerCode + " 促销活动ID:" + promotionId + " 秒杀订单号:" + orderNo + " 该会员生成秒杀订单时没有秒杀库存预锁数据");

        }
        useLog = new BuyerUseTimelimitedLogDMO();
        useLog.setBuyerCode(buyerCode);
        useLog.setSeckillLockNo(seckillLockNo);
        useLog.setOrderNo(orderNo);
        useLog.setPromotionId(promotionId);
        useLog.setUseType(orderTimelimiteDTO.getPromoitionChangeType());
        useLog.setUsedCount(orderTimelimiteDTO.getQuantity());
        useLog.setCreateId(orderTimelimiteDTO.getOperaterId());
        useLog.setCreateName(orderTimelimiteDTO.getOperaterName());
        useLog.setCreateTime(new Date());
        useLog.setModifyId(orderTimelimiteDTO.getOperaterId());
        useLog.setModifyName(orderTimelimiteDTO.getOperaterName());
        useLog.setModifyTime(new Date());
        return useLog;
    }
    
	/**
	 * 秒杀 - 清除未提交订单（预占订单）的Redis中的秒杀活动记录，并更新DB
	 *
	 * @param useLogList
	 * @return
	 */
    public void updateNoSubmitRedisUseTimelimitedLog(List<BuyerUseTimelimitedLogDMO> useLogList) {
        String useLogRedisKey = "";
        String useType = dictionary.getValueByCode(DictionaryConst.TYPE_BUYER_PROMOTION_STATUS,
                DictionaryConst.OPT_BUYER_PROMOTION_STATUS_RELEASE);
        if (useLogList == null || useLogList.isEmpty()) {
            return;
        }
        for (BuyerUseTimelimitedLogDMO useLog : useLogList) {
            useLog.setUseType(useType);
            useLogRedisKey = useLog.getBuyerCode() + "&" + useLog.getPromotionId();
            promotionRedisDB.delHash(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_USELOG, useLogRedisKey);
            promotionRedisDB.tailPush(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_NEED_SAVE_USELOG, JSON.toJSONString(useLog));
        }
    }

	/**
	 * 秒杀 - 立即抢购，生成预占订单，未提交生成正式订单
	 * 
	 * @param orderTimelimiteDTO
	 * @return
	 */
    public BuyerUseTimelimitedLogDMO getNoSubmitBuyerTimelimitedUseLog(PromotionOrderItemDTO orderTimelimiteDTO) {
        String seckillLockNo = orderTimelimiteDTO.getSeckillLockNo();
        String buyerCode = orderTimelimiteDTO.getBuyerCode();
        String promotionId = orderTimelimiteDTO.getPromotionId();
        String useLogRedisKey = buyerCode + "&" + promotionId;
        String useLogJsonStr = "";
        BuyerUseTimelimitedLogDMO useLog = null;
        useLogJsonStr = promotionRedisDB.getHash(RedisConst.PROMOTION_REDIS_BUYER_TIMELIMITED_USELOG, useLogRedisKey);
        useLog = JSON.parseObject(useLogJsonStr, BuyerUseTimelimitedLogDMO.class);
        if (useLog == null) {
            return null;
        }
        if (StringUtils.isEmpty(useLog.getOrderNo()) && !seckillLockNo.equals(useLog.getSeckillLockNo())) {
            return useLog;
        }
        return null;
    }
    
    /**
     * 更新秒杀变化活动状态
     *
     * @param promotionInfo
     */
    public void updateRedisTimeilimitedStatus(PromotionInfoDTO promotionInfo) {

    }
}
