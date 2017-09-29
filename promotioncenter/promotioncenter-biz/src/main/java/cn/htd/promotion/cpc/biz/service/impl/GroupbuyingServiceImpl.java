package cn.htd.promotion.cpc.biz.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.htd.common.constant.DictionaryConst;
import cn.htd.common.util.DictionaryUtils;
import cn.htd.promotion.cpc.biz.dao.GroupbuyingInfoDAO;
import cn.htd.promotion.cpc.biz.dao.GroupbuyingPriceSettingDAO;
import cn.htd.promotion.cpc.biz.dao.GroupbuyingRecordDAO;
import cn.htd.promotion.cpc.biz.dao.PromotionConfigureDAO;
import cn.htd.promotion.cpc.biz.dao.PromotionStatusHistoryDAO;
import cn.htd.promotion.cpc.biz.dao.SinglePromotionInfoDAO;
import cn.htd.promotion.cpc.biz.handle.PromotionTimelimitedRedisHandle;
import cn.htd.promotion.cpc.biz.service.GroupbuyingService;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.emums.YesNoEnum;
import cn.htd.promotion.cpc.common.exception.PromotionCenterBusinessException;
import cn.htd.promotion.cpc.common.util.GeneratorUtils;
import cn.htd.promotion.cpc.common.util.PromotionRedisDB;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoCmplReqDTO;
import cn.htd.promotion.cpc.dto.request.GroupbuyingPriceSettingReqDTO;
import cn.htd.promotion.cpc.dto.request.SinglePromotionInfoCmplReqDTO;
import cn.htd.promotion.cpc.dto.request.SinglePromotionInfoReqDTO;
import cn.htd.promotion.cpc.dto.response.PromotionConfigureDTO;
import cn.htd.promotion.cpc.dto.response.PromotionStatusHistoryDTO;

@Service("groupbuyingService")
public class GroupbuyingServiceImpl implements GroupbuyingService {

    private static final Logger logger = LoggerFactory.getLogger(GroupbuyingServiceImpl.class);

    @Resource
    private GroupbuyingInfoDAO groupbuyingInfoDAO;

    @Resource
    private GroupbuyingPriceSettingDAO groupbuyingPriceSettingDAO;

    @Resource
    private GroupbuyingRecordDAO groupbuyingRecordDAO;

    @Resource
    private SinglePromotionInfoDAO singlePromotionInfoDAO;
    
    @Resource
    private PromotionConfigureDAO promotionConfigureDAO;

    @Resource
    private PromotionRedisDB promotionRedisDB;

    @Resource
    private DictionaryUtils dictionary;
    
    @Resource
    private GeneratorUtils noGenerator;

    @Resource
    private PromotionStatusHistoryDAO promotionStatusHistoryDAO;
    
    @Resource
	private PromotionTimelimitedRedisHandle promotionTimelimitedRedisHandle;
    

    @Override
    public void addGroupbuyingInfo(GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO, String messageId) {

        // 当前时间
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();

        try {

        	SinglePromotionInfoReqDTO singlePromotionInfoReqDTO = groupbuyingInfoCmplReqDTO.getSinglePromotionInfoReqDTO();
        	
        	String promotionType = singlePromotionInfoReqDTO.getPromotionType();
        	String promotionId = noGenerator.generateGroupbuyingPromotionId(promotionType);
        	
        	// 添加活动信息
        	singlePromotionInfoReqDTO.setPromotionId(promotionId);
        	singlePromotionInfoReqDTO.setIsVip(1);//是VIP
        	setPromotionStatusInfo(singlePromotionInfoReqDTO);
        	singlePromotionInfoReqDTO.setCreateId(groupbuyingInfoCmplReqDTO.getCreateId());
        	singlePromotionInfoReqDTO.setCreateName(groupbuyingInfoCmplReqDTO.getCreateName());
        	singlePromotionInfoReqDTO.setCreateTime(currentTime);
        	singlePromotionInfoReqDTO.setModifyId(groupbuyingInfoCmplReqDTO.getModifyId());
        	singlePromotionInfoReqDTO.setModifyName(groupbuyingInfoCmplReqDTO.getModifyName());
        	singlePromotionInfoReqDTO.setModifyTime(currentTime);
        	int singlePromotionInfoRet = singlePromotionInfoDAO.insertSelective(singlePromotionInfoReqDTO);
        	if(1 != singlePromotionInfoRet){
        		throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NOT_EXIST.getCode(), "新建促销活动失败！");
        	}
        	
        	// 添加配置信息
        	SinglePromotionInfoCmplReqDTO singlePromotionInfoCmplReqDTO = (SinglePromotionInfoCmplReqDTO) singlePromotionInfoReqDTO;
            List<PromotionConfigureDTO> pclist = singlePromotionInfoCmplReqDTO.getPromotionConfigureList();
            if (pclist != null && pclist.size() > 0) {
                for (PromotionConfigureDTO pcd : pclist) {
                    pcd.setPromotionId(promotionId);
                    pcd.setCreateId(groupbuyingInfoCmplReqDTO.getCreateId());
                    pcd.setCreateName(groupbuyingInfoCmplReqDTO.getCreateName());
                    pcd.setDeleteFlag(YesNoEnum.NO.getValue());
                    promotionConfigureDAO.add(pcd);
                }
            }
        	
        	// 添加团购活动信息
        	groupbuyingInfoCmplReqDTO.setPromotionId(promotionId);
        	groupbuyingInfoCmplReqDTO.setDeleteFlag(Boolean.FALSE);
        	groupbuyingInfoCmplReqDTO.setCreateTime(currentTime);
        	groupbuyingInfoCmplReqDTO.setModifyTime(currentTime);
        	int groupbuyingInfoRet = groupbuyingInfoDAO.insert(groupbuyingInfoCmplReqDTO);
        	if(1 != groupbuyingInfoRet){
        		throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NOT_EXIST.getCode(), "新建团购促销活动失败！");
        	}
        	

        	
        	// 添加团购价格设置信息
        	List<GroupbuyingPriceSettingReqDTO> groupbuyingPriceSettingReqDTOList = groupbuyingInfoCmplReqDTO.getGroupbuyingPriceSettingReqDTOList();
        	if(null != groupbuyingPriceSettingReqDTOList && groupbuyingPriceSettingReqDTOList.size() > 0){
            	for(GroupbuyingPriceSettingReqDTO groupbuyingPriceSettingReqDTO : groupbuyingPriceSettingReqDTOList){
                	groupbuyingPriceSettingReqDTO.setPromotionId(promotionId);
            		groupbuyingPriceSettingReqDTO.setItemId(groupbuyingInfoCmplReqDTO.getItemId());
            		groupbuyingPriceSettingReqDTO.setSkuCode(groupbuyingInfoCmplReqDTO.getSkuCode());
            		groupbuyingPriceSettingReqDTO.setDeleteFlag(Boolean.FALSE);
            		groupbuyingPriceSettingReqDTO.setCreateId(groupbuyingInfoCmplReqDTO.getCreateId());
            		groupbuyingPriceSettingReqDTO.setCreateName(groupbuyingInfoCmplReqDTO.getCreateName());
            		groupbuyingPriceSettingReqDTO.setCreateTime(currentTime);
            		groupbuyingPriceSettingReqDTO.setModifyId(groupbuyingInfoCmplReqDTO.getModifyId());
            		groupbuyingPriceSettingReqDTO.setModifyName(groupbuyingInfoCmplReqDTO.getModifyName());
                	groupbuyingPriceSettingReqDTO.setModifyTime(currentTime);
                	groupbuyingPriceSettingDAO.insert(groupbuyingPriceSettingReqDTO);
            	}
        	}

        	

            // 添加团购活动履历
            addPromotionStatusHistory(singlePromotionInfoReqDTO);


            // 异步初始化秒杀活动的Redis数据
//            TimelimitedInfoResDTO timelimitedInfoResDTO =
//                    getSingleFullTimelimitedInfoByPromotionId(promotionExtendInfoReturn.getPromotionId(),TimelimitedConstants.TYPE_DATA_TIMELIMITED_REAL_REMAIN_COUNT, messageId);
//            initTimelimitedInfoRedisInfoWithThread(timelimitedInfoResDTO);

        } catch (Exception e) {
            logger.error("messageId{}:执行方法【addGroupbuyingInfo】报错：{}", messageId, e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateGroupbuyingInfo(GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO, String messageId) {

//        try {
//
//            TimelimitedInfoResDTO timelimitedInfoRes_check =
//                    getSingleTimelimitedInfoByPromotionId(timelimitedInfoReqDTO.getPromotionId(), messageId);
//            if (null == timelimitedInfoRes_check) {
//                throw new PromotionCenterBusinessException(ResultCodeEnum.NORESULT.getCode(), "秒杀促销活动不存在！");
//            }
//
//            Calendar calendar = Calendar.getInstance();
//            Date currentTime = calendar.getTime();
//
//            // 修改促销活动信息
//            PromotionExtendInfoDTO promotionExtendInfoDTO = timelimitedInfoReqDTO.getPromotionExtendInfoDTO();
//            PromotionExtendInfoDTO promotionExtendInfoReturn = baseService.updatePromotionInfo(promotionExtendInfoDTO);
//            if (null == promotionExtendInfoReturn || null == promotionExtendInfoReturn.getPromotionId() || ""
//                    .equals(promotionExtendInfoReturn.getPromotionId().trim())) {
//                throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(), "修改秒杀促销活动失败！");
//            }
//
//            // 添加秒杀活动履历
//            addPromotionStatusHistory(promotionExtendInfoReturn, timelimitedInfoReqDTO);
//
//            // 设置层级编码
//            List<? extends PromotionAccumulatyDTO> promotionAccumulatyDTOList =
//                    promotionExtendInfoReturn.getPromotionAccumulatyList();
//            if (null != promotionAccumulatyDTOList && promotionAccumulatyDTOList.size() == 1) {
//                timelimitedInfoReqDTO.setLevelCode(promotionAccumulatyDTOList.get(0).getLevelCode());
//            } else {
//                throw new PromotionCenterBusinessException(ResultCodeEnum.PROMOTION_NOT_EXIST.getCode(),
//                        "查询秒杀促销活动层级失败！");
//            }
//
//            // 伪删除商品活动的所有图片
//            TimelimitedSkuPictureReqDTO timelimitedSkuPictureReqDTO_delete = new TimelimitedSkuPictureReqDTO();
//            timelimitedSkuPictureReqDTO_delete.setPromotionId(timelimitedInfoReqDTO.getPromotionId());
//            timelimitedSkuPictureReqDTO_delete.setDeleteFlag(Boolean.TRUE);
//            timelimitedSkuPictureReqDTO_delete.setModifyId(timelimitedInfoReqDTO.getModifyId());
//            timelimitedSkuPictureReqDTO_delete.setModifyName(timelimitedInfoReqDTO.getModifyName());
//            timelimitedSkuPictureReqDTO_delete.setModifyTime(currentTime);
//            timelimitedSkuPictureDAO.pseudoDelete(timelimitedSkuPictureReqDTO_delete);
//            // 添加商品图片,返回商品主图
//            addTimelimitedSkuPictureList(timelimitedInfoReqDTO, currentTime);
//            
//            // 修改秒杀商品
////            String skuPicUrl = timelimitedInfoReqDTO.getSkuPicUrl();
////            if(null != skuPicUrl && !"".equals(skuPicUrl.trim())){
////            	if(skuPicUrl.indexOf("hl/") == -1){
////            		timelimitedInfoReqDTO.setSkuPicUrl("hl/" + skuPicUrl);
////            	}
////            }
//            timelimitedInfoReqDTO.setModifyTime(currentTime);
//            timelimitedInfoDAO.updateTimelimitedInfoByPromotionId(timelimitedInfoReqDTO);
//
//            // 伪删除商品详情的所有图片
//            TimelimitedSkuDescribeReqDTO timelimitedSkuDescribeReqDTO_delete = new TimelimitedSkuDescribeReqDTO();
//            timelimitedSkuDescribeReqDTO_delete.setPromotionId(timelimitedInfoReqDTO.getPromotionId());
//            timelimitedSkuDescribeReqDTO_delete.setDeleteFlag(Boolean.TRUE);
//            timelimitedSkuDescribeReqDTO_delete.setModifyId(timelimitedInfoReqDTO.getModifyId());
//            timelimitedSkuDescribeReqDTO_delete.setModifyName(timelimitedInfoReqDTO.getModifyName());
//            timelimitedSkuDescribeReqDTO_delete.setModifyTime(currentTime);
//            timelimitedSkuDescribeDAO.pseudoDelete(timelimitedSkuDescribeReqDTO_delete);
//            // 添加商品详情
//            addTimelimitedSkuDescribeList(timelimitedInfoReqDTO, currentTime);
//            
//            // 处理促销活动供应商规则详情
//            handlePromotionSellerDetail(timelimitedInfoReqDTO,TimelimitedConstants.SELLERDETAIL_OPERATETYPE_UPDATE);
//
//            // 异步初始化秒杀活动的Redis数据
//            TimelimitedInfoResDTO timelimitedInfoResDTO =
//                    getSingleFullTimelimitedInfoByPromotionId(timelimitedInfoReqDTO.getPromotionId(),TimelimitedConstants.TYPE_DATA_TIMELIMITED_REAL_REMAIN_COUNT, messageId);
//            initTimelimitedInfoRedisInfoWithThread(timelimitedInfoResDTO);
//
//        } catch (Exception e) {
//            logger.error("messageId{}:执行方法【updateTimelimitedInfo】报错：{}", messageId, e.toString());
//            throw new RuntimeException(e);
//        }

    }

    /**
     * 根据促销活动的有效期间设定促销活动状态
     *
     * @param promotionInfo
     * @return
     */
    public String setPromotionStatusInfo(SinglePromotionInfoReqDTO promotionInfo) {
        Date nowDt = new Date();
        String status = promotionInfo.getStatus();
        String showStatus = promotionInfo.getShowStatus();
        if (dictionary
                .getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS, DictionaryConst.OPT_PROMOTION_STATUS_DELETE)
                .equals(status)) {
            return status;
        }
        if (dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_VERIFY_STATUS,
                DictionaryConst.OPT_PROMOTION_VERIFY_STATUS_VALID).equals(showStatus) || dictionary
                .getValueByCode(DictionaryConst.TYPE_PROMOTION_VERIFY_STATUS,
                        DictionaryConst.OPT_PROMOTION_VERIFY_STATUS_PASS).equals(showStatus)) {
            if (nowDt.before(promotionInfo.getEffectiveTime())) {
                status = dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS,
                        DictionaryConst.OPT_PROMOTION_STATUS_NO_START);
            } else if (!nowDt.before(promotionInfo.getEffectiveTime()) && !nowDt
                    .after(promotionInfo.getInvalidTime())) {
                status = dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS,
                        DictionaryConst.OPT_PROMOTION_STATUS_START);
            } else {
                status = dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS,
                        DictionaryConst.OPT_PROMOTION_STATUS_END);
            }
        } else if (StringUtils.isEmpty(status)) {
            status = dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS,
                    DictionaryConst.OPT_PROMOTION_STATUS_NO_START);
        }
        promotionInfo.setStatus(status);
        return status;
    }



    /**
     * 添加秒杀活动履历
     *
     * @param promotionExtendInfo
     * @param timelimitedInfoReqDTO
     */
    private void addPromotionStatusHistory(SinglePromotionInfoReqDTO singlePromotionInfoReqDTO) {

        // 状态履历   状态 1：活动未开始，2：活动进行中，3：活动已结束，9：已删除 (暂时废弃)
//        PromotionStatusHistoryDTO historyDTO = new PromotionStatusHistoryDTO();
//        historyDTO.setPromotionId(singlePromotionInfoReqDTO.getPromotionId());
//        historyDTO.setPromotionStatus(
//                dictionary.getValueByCode(DictionaryConst.TYPE_PROMOTION_STATUS, singlePromotionInfoReqDTO.getStatus()));
//        historyDTO.setPromotionStatusText(
//                dictionary.getNameByValue(DictionaryConst.TYPE_PROMOTION_STATUS, singlePromotionInfoReqDTO.getStatus()));
//        historyDTO.setCreateId(singlePromotionInfoReqDTO.getModifyId());
//        historyDTO.setCreateName(singlePromotionInfoReqDTO.getModifyName());
//        promotionStatusHistoryDAO.add(historyDTO);

        // 促销活动展示状态履历   状态   1：待审核，2：审核通过，3：审核被驳回，4：启用，5：不启用
        PromotionStatusHistoryDTO historyDTO = new PromotionStatusHistoryDTO();
        historyDTO.setPromotionId(singlePromotionInfoReqDTO.getPromotionId());
        historyDTO.setPromotionStatus(dictionary
                .getValueByCode(DictionaryConst.TYPE_PROMOTION_VERIFY_STATUS, singlePromotionInfoReqDTO.getShowStatus()));
        historyDTO.setPromotionStatusText(dictionary
                .getNameByValue(DictionaryConst.TYPE_PROMOTION_VERIFY_STATUS, singlePromotionInfoReqDTO.getShowStatus()));
        historyDTO.setCreateId(singlePromotionInfoReqDTO.getCreateId());
        historyDTO.setCreateName(singlePromotionInfoReqDTO.getCreateName());
        promotionStatusHistoryDAO.add(historyDTO);
    }



}
