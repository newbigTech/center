package cn.htd.promotion.cpc.api.impl;

import javax.annotation.Resource;

import cn.htd.common.DataGrid;
import cn.htd.promotion.cpc.api.AwardRecordAPI;
import cn.htd.promotion.cpc.biz.service.AwardRecordService;
import cn.htd.promotion.cpc.biz.service.TimelimitedInfoService;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.dto.request.PromotionAwardReqDTO;
import cn.htd.promotion.cpc.dto.request.SeckillOrderReqDTO;
import cn.htd.promotion.cpc.dto.response.ImportResultDTO;
import cn.htd.promotion.cpc.dto.response.PromotionAwardDTO;
import cn.htd.promotion.cpc.dto.response.TimelimitedInfoResDTO;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tangjiayong on 2017/8/22.
 */
@Service("awardRecordAPI")
public class AwardRecordAPIImpl implements AwardRecordAPI {

    private static final Logger logger = LoggerFactory.getLogger(AwardRecordAPIImpl.class);

    @Resource
    AwardRecordService awardRecordService;

    @Resource
    TimelimitedInfoService timelimitedInfoService;

    @Override
    public ExecuteResult<DataGrid<PromotionAwardDTO>> getAwardRecordByPromotionId(PromotionAwardReqDTO dto,
            String messageId) {
        ExecuteResult<DataGrid<PromotionAwardDTO>> result = new ExecuteResult<DataGrid<PromotionAwardDTO>>();

        try {
            if (!StringUtils.isEmpty(messageId)) {
                DataGrid<PromotionAwardDTO> awardRecordList =
                        awardRecordService.getAwardRecordByPromotionId(dto, messageId);
                result.setResult(awardRecordList);
                result.setCode(ResultCodeEnum.SUCCESS.getCode());
                if (awardRecordList.getTotal() == 0 || awardRecordList == null) {
                    result.setResultMessage(ResultCodeEnum.NORESULT.getMsg());
                } else {
                    result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());
                }

            } else {
                result.setCode(ResultCodeEnum.PROMOTION_AWARD_IS_NULL.getCode());
                result.setErrorMessage(ResultCodeEnum.PROMOTION_AWARD_IS_NULL.getMsg());
            }

            logger.info("promotionId{}:AwardRecordAPIImpl.getAwardRecordByPromotionId（）方法,出参{}", messageId,
                    dto.getPromotionId(), JSON.toJSONString(result));
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法AwardRecordAPIImpl.getAwardRecordByPromotionId出现异常{}", messageId,
                    dto.getPromotionId() + ":" + e.toString());
        }
        return result;
    }

    @Override
    public ExecuteResult<ImportResultDTO> importWinningRecord(List<PromotionAwardReqDTO> dtos, String messageId) {
        logger.info("messageId{} : AwardRecordAPIImpl--->importWinningRecord--->parmas:" +messageId +":"+ JSONObject.toJSONString(dtos));
        ExecuteResult<ImportResultDTO> result = new ExecuteResult<ImportResultDTO>();
        ImportResultDTO importResult = new ImportResultDTO();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());
        List<PromotionAwardReqDTO> list = new ArrayList<PromotionAwardReqDTO>();
        List<PromotionAwardReqDTO> successlist = new ArrayList<PromotionAwardReqDTO>();
        int failCount = 0;
        int successCount = 0;
        try {
            for(PromotionAwardReqDTO dto : dtos){
                if("23".equals(dto.getPromotionType())){//秒杀订单导入
                    if (!StringUtils.isEmpty(dto.getOrderNo())) {
                        if (awardRecordService.updateOrderLogisticsInfo(dto, messageId) > 0) {
                            successCount++;
                            successlist.add(dto);
                        } else {
                            failCount++;
                            list.add(dto);
                        }
                    } else {
                        failCount++;
                        list.add(dto);
                    }
                }else {//中奖记录
                    if (!StringUtils.isEmpty("" + dto.getId())) {
                        if (awardRecordService.updateLogisticsInfo(dto, messageId) > 0) {
                            successCount++;
                        } else {
                            failCount++;
                            list.add(dto);
                        }
                    } else {
                        failCount++;
                        list.add(dto);
                    }
                }
            }
            importResult.setFailCount(failCount);
            importResult.setSuccessCount(successCount);
            importResult.setPromotionAwardList(list);
            importResult.setSuccessAwardList(successlist);
            result.setResult(importResult);
        } catch (Exception e) {
            logger.error("\n 方法[{}]，异常：[{}]", "messageId : AwardRecordAPIImpl-importWinningRecord", messageId +" : "+ e.toString());
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            result.setResult(null);
        }
        return result;
    }

    @Override
    public ExecuteResult insertUpdateSeckillOrder(SeckillOrderReqDTO dto ,String messageId)  {
        logger.info("messageId{} : AwardRecordAPIImpl--->importWinningRecord--->parmas:" +messageId +" : "+ JSONObject.toJSONString(dto));
        ExecuteResult result = new ExecuteResult();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        Integer i;
        Boolean exist = awardRecordService.checkOrder(dto.getOrderNo());
        PromotionAwardReqDTO awardReqDTO = getPromotionAwardReqDTO(dto);
        if(exist){
            i = awardRecordService.updateOrder(awardReqDTO);
        }else{
            i = awardRecordService.insertOrder(awardReqDTO);
        }

        if(i < 0){
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage("insert or update failed!");
        }

        return result;
    }

    private PromotionAwardReqDTO getPromotionAwardReqDTO(SeckillOrderReqDTO dto) {
        PromotionAwardReqDTO awardReqDTO = new PromotionAwardReqDTO();
        awardReqDTO.setOrderNo(dto.getOrderNo());
        awardReqDTO.setRewardName(StringUtils.isNotEmpty(dto.getProductName()) ? dto.getProductName():"");
        awardReqDTO.setAwardValue(StringUtils.isNotEmpty(dto.getTotalMoeny()) ? dto.getTotalMoeny():"");
        awardReqDTO.setWinningContact(StringUtils.isNotEmpty(dto.getFanCode()) ? dto.getFanCode():"");
        awardReqDTO.setBelongSuperiorName(StringUtils.isNotEmpty(dto.getMemberBossName()) ? dto.getMemberBossName():"");
        awardReqDTO.setBuyerTelephone(StringUtils.isNotEmpty(dto.getBossTelphone()) ? dto.getBossTelphone():"");
        awardReqDTO.setBuyerName(StringUtils.isNotEmpty(dto.getMemberName()) ? dto.getMemberName():"");
        awardReqDTO.setSellerAddress(StringUtils.isNotEmpty(dto.getMemberAddress()) ? dto.getMemberAddress():"");
        awardReqDTO.setOrderStatus(StringUtils.isNotEmpty(dto.getOrderStatus()) ? dto.getOrderStatus():"");
        awardReqDTO.setWinningTime(dto.getOrderTime() != null ? dto.getOrderTime(): new Date());
        awardReqDTO.setPromotionId(StringUtils.isNotEmpty(dto.getPromotionId()) ? dto.getPromotionId():"");
        awardReqDTO.setSellerCode(StringUtils.isNotEmpty(dto.getOrgid())? dto.getOrgid():"");
        awardReqDTO.setWinnerName(StringUtils.isNotEmpty(dto.getFanName())?dto.getFanName():"");
        awardReqDTO.setPromotionName("总部秒杀");
        return awardReqDTO;
    }
}
