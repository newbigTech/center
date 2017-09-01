package cn.htd.promotion.cpc.biz.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.htd.common.constant.DictionaryConst;
import cn.htd.promotion.cpc.biz.handle.PromotionLotteryRedisHandle;
import cn.htd.promotion.cpc.biz.service.PromotionBaseService;
import cn.htd.promotion.cpc.biz.service.PromotionLotteryCommonService;
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
    
    @Resource
	private PromotionLotteryCommonService promotionLotteryCommonService;

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
        promotionInfoDTO = promotionLotteryCommonService.getRedisLotteryInfo(promotionId, dictMap);
        if (promotionLotteryCommonService.checkPromotionLotteryValid(promotionInfoDTO, requestDTO, dictMap)) {
            ticket = noGenerator.generateLotteryTicket(promotionId + sellerCode + buyerCode);
            responseDTO.setTicket(ticket);
            new Thread(new DoPromotionLotteryDealTask(requestDTO, ticket)).start();
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
            PromotionAwardInfoDTO awardInfoDTO = null;
            String awardPercentStr = "";
            List<PromotionAccumulatyDTO> accuList = null;
            try {
                awardPercentStr = promotionRedisDB.getHash(RedisConst.REDIS_LOTTERY_TIMES_INFO + "_" + promotionId,
                        RedisConst.REDIS_LOTTERY_AWARD_WINNING_PERCENTAGE);
                if (StringUtils.isEmpty(awardPercentStr)) {
                    throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(),
                            "抽奖活动编号:" + promotionId + " 奖项设置异常没有设置奖项");
                }
                accuList = JSON.parseArray(awardPercentStr, PromotionAccumulatyDTO.class);
                awardInfoDTO = drawLotteryAward(promotionId, accuList);
                if (awardInfoDTO == null) {
                    throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_NO_MORE_AWARD_NUM.getCode(),
                            "抽奖活动编号:" + promotionId + " 抽奖活动目前奖品数量不足");
                }
            } catch (PromotionCenterBusinessException pcbe) {
                if (awardInfoDTO == null) {
                    awardInfoDTO = new PromotionAwardInfoDTO();
                }
                awardInfoDTO.setResponseCode(pcbe.getCode());
                awardInfoDTO.setResponseMsg(pcbe.getMessage());
            }
            promotionRedisDB.setHash(RedisConst.REDIS_LOTTERY_BUYER_AWARD_INFO,
                    promotionId + "_" + reqDTO.getSellerCode() + "_" + reqDTO.getBuyerCode() + "_" + ticket,
                    JSON.toJSONString(awardInfoDTO));
        }

        private PromotionAwardInfoDTO drawLotteryAward(String promotionId, List<PromotionAccumulatyDTO> accuList)
                throws PromotionCenterBusinessException {
            int luckNo = 0;
            PromotionAccumulatyDTO goalAccuDTO = null;
            String lotteryKey = "";
            String awardJsonStr = "";
            PromotionAwardInfoDTO awardInfoDTO = null;
            int loopSize = 0;

            if (accuList == null || !accuList.isEmpty()) {
                throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(),
                        "抽奖活动编号:" + promotionId + " 奖项设置异常没有设置奖项");
            } else if ("100".equals(accuList.get(accuList.size() - 1).getLevelAmount())) {
                throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(),
                        "奖项设置异常没有合适奖项 luckNo:" + luckNo + " level:" + JSON.toJSONString(accuList));
            }
            try {
                if (promotionRedisDB.decrHash(RedisConst.REDIS_LOTTERY_TIMES_INFO + "_" + promotionId, RedisConst
                        .REDIS_LOTTERY_AWARD_TOTAL_COUNT).longValue() < 0) {
                    throw new PromotionCenterBusinessException(ResultCodeEnum.LOTTERY_NO_MORE_AWARD_NUM.getCode(),
                            "抽奖活动编号:" + promotionId + " 抽奖活动目前奖品数量不足");
                }
                while (loopSize++ < 10) {
                    if (accuList.size() == 1) {
                        goalAccuDTO = accuList.get(0);
                    } else {
                        luckNo = noGenerator.getRandomNum();
                        for (PromotionAccumulatyDTO accuDTO : accuList) {
                            if (luckNo <= Integer.parseInt(accuDTO.getLevelAmount())) {
                                goalAccuDTO = accuDTO;
                                break;
                            }
                        }
                    }
                    lotteryKey =
                            RedisConst.REDIS_LOTTERY_AWARD_PREFIX + goalAccuDTO.getPromotionId() + "_" + goalAccuDTO
                                    .getLevelCode();
                    awardJsonStr = promotionRedisDB.headPop(lotteryKey);
                    if (StringUtils.isEmpty(awardJsonStr)) {
                        continue;
                    }
                    awardInfoDTO = JSON.parseObject(awardJsonStr, PromotionAwardInfoDTO.class);
                    break;
                }
            } catch (PromotionCenterBusinessException pcbe) {
                promotionRedisDB.incrHash(RedisConst.REDIS_LOTTERY_TIMES_INFO + "_" + promotionId, RedisConst
                        .REDIS_LOTTERY_AWARD_TOTAL_COUNT);
            }
            return awardInfoDTO;
        }
    }
}
