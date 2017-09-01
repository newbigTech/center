package cn.htd.promotion.cpc.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.htd.common.constant.DictionaryConst;
import cn.htd.promotion.cpc.biz.handle.PromotionLotteryRedisHandle;
import cn.htd.promotion.cpc.biz.service.PromotionBaseService;
import cn.htd.promotion.cpc.biz.service.PromotionLotteryService;
import cn.htd.promotion.cpc.common.constants.RedisConst;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.emums.YesNoEnum;
import cn.htd.promotion.cpc.common.exception.PromotionCenterBusinessException;
import cn.htd.promotion.cpc.common.util.DateUtil;
import cn.htd.promotion.cpc.common.util.GeneratorUtils;
import cn.htd.promotion.cpc.common.util.PromotionRedisDB;
import cn.htd.promotion.cpc.dto.request.BuyerCheckInfo;
import cn.htd.promotion.cpc.dto.request.DrawLotteryReqDTO;
import cn.htd.promotion.cpc.dto.response.DrawLotteryResDTO;
import cn.htd.promotion.cpc.dto.response.PromotionAccumulatyDTO;
import cn.htd.promotion.cpc.dto.response.PromotionAwardInfoDTO;
import cn.htd.promotion.cpc.dto.response.PromotionExtendInfoDTO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("promotionLotteryService")
public class PromotionLotteryServiceImpl implements PromotionLotteryService {

    private static final Logger logger = LoggerFactory.getLogger(PromotionLotteryServiceImpl.class);

    @Resource
    private GeneratorUtils noGenerator;

    @Resource
    private PromotionBaseService baseService;

    @Resource
    private PromotionLotteryRedisHandle lotteryRedisHandle;

    @Resource
    private PromotionRedisDB promotionRedisDB;

    /**
     * 开始抽奖处理
     *
     * @param requestDTO
     * @return
     * @throws PromotionCenterBusinessException
     * @throws Exception
     */
    @Override
    public DrawLotteryResDTO beginDrawLottery(DrawLotteryReqDTO requestDTO)
            throws PromotionCenterBusinessException, Exception {
        DrawLotteryResDTO responseDTO = new DrawLotteryResDTO();
        String buyerCode = requestDTO.getBuyerCode();
        String sellerCode = requestDTO.getSellerCode();
        String promotionId = requestDTO.getPromotionId();
        String ticket = "";
        Map<String, String> dictMap = null;
        PromotionExtendInfoDTO promotionInfoDTO = null;

        responseDTO.setMessageId(requestDTO.getMessageId());
        dictMap = baseService.initPromotionDictMap();
        promotionInfoDTO = getRedisLotteryInfo(promotionId, dictMap);
        if (checkPromotionLotteryValid(promotionInfoDTO, requestDTO, dictMap)) {
            ticket = noGenerator.generateLotteryTicket(promotionId + sellerCode + buyerCode);
            responseDTO.setTicket(ticket);
        }
        return responseDTO;
    }

    /**
     * 查询抽奖结果处理
     *
     * @param requestDTO
     * @return
     * @throws PromotionCenterBusinessException
     * @throws Exception
     */
    @Override
    public DrawLotteryResDTO getDrawLotteryResult(DrawLotteryReqDTO requestDTO)
            throws PromotionCenterBusinessException, Exception {
        return null;
    }

    /**
     * 取得Redis抽奖活动信息
     *
     * @param promotionId
     */
    public PromotionExtendInfoDTO getRedisLotteryInfo(String promotionId, Map<String, String> dictMap)
            throws PromotionCenterBusinessException {
        PromotionExtendInfoDTO lotteryInfo = null;
        String lotteryJsonStr = "";
        String validStatus = "";

        validStatus = promotionRedisDB.getHash(RedisConst.REDIS_LOTTERY_VALID, promotionId);
        if (!StringUtils.isEmpty(validStatus)
                && !dictMap.get(DictionaryConst.TYPE_PROMOTION_VERIFY_STATUS + "&"
                + DictionaryConst.OPT_PROMOTION_VERIFY_STATUS_VALID).equals(validStatus)) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NOT_VALID.getCode(),
                    "抽奖活动ID:" + promotionId + " 该活动未上架");
        }
        lotteryJsonStr = promotionRedisDB.getHash(RedisConst.REDIS_LOTTERY_INFO, promotionId);
        lotteryInfo = JSON.parseObject(lotteryJsonStr, PromotionExtendInfoDTO.class);
        if (lotteryInfo == null) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NOT_EXIST.getCode(),
                    "抽奖活动ID:" + promotionId + " 该活动不存在");
        }
        return lotteryInfo;
    }

    /**
     * 取得扭蛋促销活动信息
     *
     * @param promotionInfoDTO
     * @param requestDTO
     * @param dictMap
     * @return
     * @throws PromotionCenterBusinessException
     */
    private boolean checkPromotionLotteryValid(PromotionExtendInfoDTO promotionInfoDTO, DrawLotteryReqDTO requestDTO,
            Map<String, String> dictMap) throws PromotionCenterBusinessException {
        String promotionId = requestDTO.getPromotionId();
        String buyerCode = requestDTO.getBuyerCode();
        Date nowDt = new Date();
        BuyerCheckInfo buyerCheckInfo = new BuyerCheckInfo();
        Map<String, String> lotteryTimesInfoMap = null;
        Map<String, String> buyerTimesInfoMap = null;

        if (nowDt.before(promotionInfoDTO.getEffectiveTime())) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NO_START.getCode(),
                    "抽奖活动编号:" + promotionId + " 该活动未开始");
        }
        if (nowDt.after(promotionInfoDTO.getInvalidTime())) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_HAS_EXPIRED.getCode(),
                    "抽奖活动编号:" + promotionId + " 该活动已结束");
        }
        if (nowDt.before(DateUtil.getNowDateSpecifiedTime(promotionInfoDTO.getEachStartTime())) || nowDt.after
                (DateUtil.getNowDateSpecifiedTime(promotionInfoDTO.getEachEndTime()))) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_NOT_IN_TIME_INTERVAL.getCode(),
                    "抽奖活动编号:" + promotionId + " 时间段:" + DateUtil.format(promotionInfoDTO.getEachStartTime()) + "~" +
                            DateUtil.format(promotionInfoDTO.getEachEndTime()) + " 该活动当前不在抽奖时间段");
        }
        lotteryTimesInfoMap = promotionRedisDB.getHashOperations(RedisConst.REDIS_LOTTERY_TIMES_INFO + "_" +
                promotionId);
        if (Integer.parseInt(lotteryTimesInfoMap.get(RedisConst.REDIS_LOTTERY_AWARD_TOTAL_COUNT)) < 0) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_NO_MORE_AWARD_NUM.getCode(),
                    "抽奖活动编号:" + promotionId + " 抽奖活动目前奖品数量不足");
        }
        buyerCheckInfo.setBuyerCode(requestDTO.getBuyerCode());
        buyerCheckInfo.setIsFirstLogin(requestDTO.getIsBuyerFirstLogin());
        if (!baseService.checkPromotionBuyerRule(promotionInfoDTO, buyerCheckInfo, dictMap)) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_BUYER_NO_AUTHIORITY.getCode(),
                    "抽奖活动编号:" + promotionId + " 会员店:" + requestDTO.getSellerCode() + " 抽奖粉丝编号:" +
                            requestDTO.getBuyerCode() + " 是否首次登陆:" + requestDTO.getIsBuyerFirstLogin()
                            + " 该活动粉丝没有秒杀权限");
        }
        if (Integer.parseInt(lotteryTimesInfoMap.get(RedisConst.REDIS_LOTTERY_AWARD_TOTAL_COUNT)) <= 0) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_NO_MORE_AWARD_NUM.getCode(),
                    "抽奖活动编号:" + promotionId + " 抽奖活动目前奖品数量不足");
        }
        if (!baseService.checkPromotionSellerRule(promotionInfoDTO, requestDTO.getSellerCode(), dictMap)) {
            throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_SELLER_NO_AUTHIORITY.getCode(),
                    "抽奖活动编号:" + promotionId + " 会员店:" + requestDTO.getSellerCode() + " 抽奖粉丝编号:" +
                            requestDTO.getBuyerCode() + " 是否首次登陆:" + requestDTO.getIsBuyerFirstLogin()
                            + " 会员店没有参加本次抽奖活动");
        }
        buyerTimesInfoMap = promotionRedisDB.getHashOperations(RedisConst.REDIS_LOTTERY_BUYER_TIMES_INFO + "_" +
                buyerCode);
        if (buyerTimesInfoMap != null) {
            if (Integer.parseInt(buyerTimesInfoMap.get(RedisConst.REDIS_LOTTERY_BUYER_PARTAKE_TIMES)) <= 0) {
                if (!lotteryTimesInfoMap.containsKey(RedisConst.REDIS_LOTTERY_BUYER_TOP_EXTRA_PARTAKE_TIMES) ||
                        Integer.parseInt(
                                lotteryTimesInfoMap.get(RedisConst.REDIS_LOTTERY_BUYER_TOP_EXTRA_PARTAKE_TIMES)) <= 0) {
                    throw new PromotionCenterBusinessException(
                            ResultCodeEnum.LOTTERY_BUYER_NO_MORE_DRAW_CHANCE.getCode(),
                            "抽奖活动编号:" + promotionId + " 会员店:" + requestDTO.getSellerCode() + " 抽奖粉丝编号:" +
                                    requestDTO.getBuyerCode() + " 粉丝已经用完了所有抽奖机会，需分享获得额外抽奖机");
                } else if (!buyerTimesInfoMap.containsKey(RedisConst.REDIS_LOTTERY_BUYER_SHARE_EXTRA_PARTAKE_TIMES) ||
                        Integer.parseInt(
                                buyerTimesInfoMap.get(RedisConst.REDIS_LOTTERY_BUYER_SHARE_EXTRA_PARTAKE_TIMES)) <= 0
                        || (buyerTimesInfoMap.containsKey(RedisConst.REDIS_LOTTERY_BUYER_HAS_TOP_EXTRA_TIMES)
                        && YesNoEnum.YES.getValue() == Integer
                        .parseInt(buyerTimesInfoMap.get(RedisConst.REDIS_LOTTERY_BUYER_HAS_TOP_EXTRA_TIMES)))) {
                    throw new PromotionCenterBusinessException(
                            ResultCodeEnum.LOTTERY_BUYER_NO_MORE_EXTRA_CHANCE.getCode(),
                            "抽奖活动编号:" + promotionId + " 会员店:" + requestDTO.getSellerCode() + " 抽奖粉丝编号:" +
                                    requestDTO.getBuyerCode() + " 粉丝已经用完了自有和分享额外获取的抽奖机");
                }
            }
        }
        return true;
    }

    /**
     * 抽奖处理异步线程
     */
    private class DoPromotionLotteryDealTask extends Thread {

        private DrawLotteryReqDTO reqDTO;

        private String ticket;

        public DoPromotionLotteryDealTask(DrawLotteryReqDTO reqDTO, String ticket) {
            this.reqDTO = reqDTO;
            this.ticket = ticket;
        }

        public void run() {
            String promotionId = reqDTO.getPromotionId();
            PromotionAwardInfoDTO awardInfoDTO = new PromotionAwardInfoDTO();
            String awardPercentStr = "";
            try {
                awardPercentStr = promotionRedisDB.getHash(RedisConst.REDIS_LOTTERY_TIMES_INFO + "_" + promotionId,
                        RedisConst.REDIS_LOTTERY_AWARD_WINNING_PERCENTAGE);
                if (StringUtils.isEmpty(awardPercentStr)) {


                }
            } catch (Exception e) {

            }

        }

    }

    public static int drawLottery(List<PromotionAccumulatyDTO> accuList, int luckNo) {
//            int luckNo = noGenerator.getRandomNum();
        int low = 0;
        int high = accuList.size() - 1;
        int middle = 0;
        while (low <= high) {
            middle = (low + high) / 2;
            if (low == high) {
                return low;
            } else if (Integer.parseInt(accuList.get(middle).getLevelAmount()) > luckNo) {
                high = middle;
            } else if (Integer.parseInt(accuList.get(middle).getLevelAmount()) <= luckNo) {
                low = middle;
            }
        }
        return -1;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[] src = new int[] {1, 3, 5, 7, 8, 9};

        List<PromotionAccumulatyDTO> accuList = new ArrayList<PromotionAccumulatyDTO>();
        PromotionAccumulatyDTO accumulatyDTO = new PromotionAccumulatyDTO();
        accumulatyDTO.setLevelAmount("5");
        accuList.add(accumulatyDTO);

         accumulatyDTO = new PromotionAccumulatyDTO();
        accumulatyDTO.setLevelAmount("30");
        accuList.add(accumulatyDTO);

        accumulatyDTO = new PromotionAccumulatyDTO();
        accumulatyDTO.setLevelAmount("50");
        accuList.add(accumulatyDTO);

        accumulatyDTO = new PromotionAccumulatyDTO();
        accumulatyDTO.setLevelAmount("75");
        accuList.add(accumulatyDTO);


        accumulatyDTO = new PromotionAccumulatyDTO();
        accumulatyDTO.setLevelAmount("100");
        accuList.add(accumulatyDTO);
        int luckNo = (int)(1+Math.random()*100);
        System.out.println("luckNo:" + luckNo);
        System.out.println(drawLottery(accuList, luckNo));
    }
}
