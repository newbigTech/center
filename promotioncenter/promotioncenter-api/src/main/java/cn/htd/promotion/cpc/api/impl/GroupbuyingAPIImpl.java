package cn.htd.promotion.cpc.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.htd.promotion.cpc.dto.request.GroupbuyingRecordReqDTO;
import cn.htd.promotion.cpc.dto.request.SinglePromotionInfoReqDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.common.util.DictionaryUtils;
import cn.htd.promotion.cpc.api.GroupbuyingAPI;
import cn.htd.promotion.cpc.biz.handle.PromotionGroupbuyingRedisHandle;
import cn.htd.promotion.cpc.biz.service.GroupbuyingService;
import cn.htd.promotion.cpc.common.constants.GroupbuyingConstants;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.exception.PromotionCenterBusinessException;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoCmplReqDTO;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoReqDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoCmplResDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoResDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingRecordResDTO;
import cn.htd.promotion.cpc.dto.response.PromotionConfigureDTO;

@Service("groupbuyingAPI")
public class GroupbuyingAPIImpl implements GroupbuyingAPI {
	
    private static final Logger logger = LoggerFactory.getLogger(GroupbuyingAPIImpl.class);

    @Resource
    private DictionaryUtils dictionary;

    @Resource
    private GroupbuyingService groupbuyingService;
    
    @Resource
	private PromotionGroupbuyingRedisHandle promotionGroupbuyingRedisHandle;
    

	@Override
	public ExecuteResult<?> addGroupbuyingInfo(GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO, String messageId) {
		
        ExecuteResult<?> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
			if (null == groupbuyingInfoCmplReqDTO) {
				throw new PromotionCenterBusinessException(ResultCodeEnum.PARAMETER_ERROR.getCode(), "团购促销活动参数不能为空！");
			}
			
			groupbuyingService.addGroupbuyingInfo(groupbuyingInfoCmplReqDTO, messageId);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.getMessage());
//            String msg=e.getMessage();  
//            if(e.getCause() instanceof PromotionCenterBusinessException){  
//            	PromotionCenterBusinessException be = (PromotionCenterBusinessException) e.getCause();
//                msg=be.getMessage();
//            }  
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.addGroupbuyingInfo出现异常{}", messageId,  e.toString());
        }
        return result;
	}
	
	
	@Override
	public ExecuteResult<?> updateGroupbuyingInfo(GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO, String messageId) {
		
        ExecuteResult<?> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
    		if (null == groupbuyingInfoCmplReqDTO) {
    			throw new PromotionCenterBusinessException(ResultCodeEnum.PARAMETER_ERROR.getCode(), "团购促销活动参数不能为空！");
    		}
    		
			if (null == groupbuyingInfoCmplReqDTO.getPromotionId() || "".equals(groupbuyingInfoCmplReqDTO.getPromotionId().trim())) {
				throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(), "团购促销活动编码不能为空！");
			}
    		
			groupbuyingService.updateGroupbuyingInfo(groupbuyingInfoCmplReqDTO, messageId);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.updateGroupbuyingInfo出现异常{}", messageId, groupbuyingInfoCmplReqDTO.getPromotionId() + ":" + e.toString());
        }
        return result;
	}

	@Override
	public ExecuteResult<GroupbuyingInfoCmplResDTO> getGroupbuyingInfoCmplByPromotionId(String promotionId, String messageId) {
        ExecuteResult<GroupbuyingInfoCmplResDTO> result = new ExecuteResult<GroupbuyingInfoCmplResDTO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	GroupbuyingInfoCmplResDTO groupbuyingInfoCmplResDTO = groupbuyingService.getGroupbuyingInfoCmplByPromotionId(promotionId, messageId);
        	result.setResult(groupbuyingInfoCmplResDTO);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGroupbuyingInfoCmplByPromotionId出现异常{}", messageId, e.toString());
        }
        return result;
	}
	
	@Override
	public ExecuteResult<GroupbuyingInfoResDTO> getSingleGroupbuyingInfoByPromotionId(String promotionId, String messageId) {
        ExecuteResult<GroupbuyingInfoResDTO> result = new ExecuteResult<GroupbuyingInfoResDTO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	GroupbuyingInfoResDTO groupbuyingInfoResDTO = groupbuyingService.getSingleGroupbuyingInfoByPromotionId(promotionId, messageId);
        	result.setResult(groupbuyingInfoResDTO);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getSingleGroupbuyingInfoByPromotionId出现异常{}", messageId, e.toString());
        }
        return result;
	}


	@Override
	public ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> getGroupbuyingInfoCmplForPage(Pager<GroupbuyingInfoReqDTO> page, GroupbuyingInfoReqDTO groupbuyingInfoReqDTO, String messageId) {
		ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> result = new ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>>(); 
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	DataGrid<GroupbuyingInfoCmplResDTO> groupbuyingInfoCmplResDTOData = groupbuyingService.getGroupbuyingInfoCmplForPage(page,groupbuyingInfoReqDTO, messageId);
        	result.setResult(groupbuyingInfoCmplResDTOData);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGroupbuyingInfoCmplForPage出现异常{}", messageId, e.toString());
        }
        return result;
	}
	
	@Override
	public ExecuteResult<Map<String, String>> getGBActorCountAndPriceByPromotionId(String promotionId, String messageId){
        ExecuteResult<Map<String, String>> result = new ExecuteResult<Map<String, String>>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	Map<String, String> retMap = groupbuyingService.getGBActorCountAndPriceByPromotionId(promotionId, messageId);
        	result.setResult(retMap);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGBActorCountAndPriceByPromotionId出现异常{}", messageId, e.toString());
        }
        return result;
	}

	@Override
	public ExecuteResult<GroupbuyingRecordResDTO> getSingleGroupbuyingRecord(GroupbuyingRecordReqDTO groupbuyingRecordReqDTO, String messageId) {
		
        ExecuteResult<GroupbuyingRecordResDTO> result = new ExecuteResult<GroupbuyingRecordResDTO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	GroupbuyingRecordResDTO groupbuyingRecordResDTO = groupbuyingService.getSingleGroupbuyingRecord(groupbuyingRecordReqDTO, messageId);
        	result.setResult(groupbuyingRecordResDTO);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getSingleGroupbuyingRecord出现异常{}", messageId, e.toString());
        }
        return result;
	}


	@Override
	public ExecuteResult<DataGrid<GroupbuyingRecordResDTO>> geGroupbuyingRecordForPage(Pager<GroupbuyingRecordReqDTO> page,GroupbuyingRecordReqDTO groupbuyingRecordReqDTO, String messageId) {
		
		ExecuteResult<DataGrid<GroupbuyingRecordResDTO>> result = new ExecuteResult<DataGrid<GroupbuyingRecordResDTO>>(); 
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	DataGrid<GroupbuyingRecordResDTO> groupbuyingRecordResDTOData = groupbuyingService.geGroupbuyingRecordForPage(page,groupbuyingRecordReqDTO, messageId);
        	result.setResult(groupbuyingRecordResDTOData);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.geGroupbuyingRecordForPage出现异常{}", messageId, e.toString());
        }
        return result;
	}
	
	
	@Override
	public ExecuteResult<List<PromotionConfigureDTO>> getGBPromotionConfiguresByPromotionId(String promotionId, String messageId) {
		ExecuteResult<List<PromotionConfigureDTO>> result = new ExecuteResult<List<PromotionConfigureDTO>>(); 
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
			if (null == promotionId || promotionId.length() == 0) {
				throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(), "团购促销活动编码不能为空！");
			}
			
        	List<PromotionConfigureDTO> promotionConfigureDTOlist = groupbuyingService.getGBPromotionConfiguresByPromotionId(promotionId, messageId);
        	result.setResult(promotionConfigureDTOlist);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGBPromotionConfiguresByPromotionId出现异常{}", messageId, e.toString());
        }
        return result;
	}

    @Override
    public ExecuteResult<?> addGroupbuyingRecord2HttpINTFC(GroupbuyingRecordReqDTO dto, String messageId) {
        ExecuteResult<?> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {

            String statusResult =groupbuyingService.addGroupbuyingRecord2HttpINTFC(dto,messageId);
            if(!GroupbuyingConstants.CommonStatusEnum.STATUS_SUCCESS.key().equals(statusResult)){
           	 result.setCode(statusResult);
             result.setResultMessage(GroupbuyingConstants.CommonStatusEnum.getValue(statusResult));
             }

        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.addGroupbuyingRecord2HttpINTFC 出现异常{}", messageId, dto.getPromotionId() + ":" + e.toString());
        }
        return result;
    }
    
	@Override
	public ExecuteResult<GroupbuyingInfoCmplResDTO> getGroupbuyingInfoCmpl2HttpINTFC(String promotionId, String messageId) {
        ExecuteResult<GroupbuyingInfoCmplResDTO> result = new ExecuteResult<GroupbuyingInfoCmplResDTO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
            
        	GroupbuyingInfoCmplResDTO groupbuyingInfoCmplResDTO = groupbuyingService.getGroupbuyingInfoCmplByPromotionId4Mobile(promotionId,messageId);
        	result.setResult(groupbuyingInfoCmplResDTO);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGroupbuyingInfoCmpl2HttpINTFC出现异常{}", messageId, e.toString());
        }
        return result;
	}


    @Override
    public ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> getGroupbuyingList2HttpINTFC(Pager<GroupbuyingInfoReqDTO> pager, GroupbuyingInfoReqDTO groupbuyingInfoReqDTO, String messageId) {
        ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> result = new ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
            DataGrid<GroupbuyingInfoCmplResDTO> groupbuyingInfoCmplResDTOData = groupbuyingService.getGroupbuyingInfo4MobileForPage(pager,groupbuyingInfoReqDTO, messageId);
            result.setResult(groupbuyingInfoCmplResDTOData);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGroupbuyingInfoCmplForPage出现异常{}", messageId, e.toString());
        }
        return result;
    }

    @Override
    public ExecuteResult<?> updateShowStatusByPromotionId(SinglePromotionInfoReqDTO singlePromotionInfoReqDTO, String messageId) {
        ExecuteResult<String> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	
            String statusResult = groupbuyingService.updateShowStatusByPromotionId(singlePromotionInfoReqDTO,messageId);
            if(!GroupbuyingConstants.CommonStatusEnum.STATUS_SUCCESS.key().equals(statusResult)){
            	 result.setCode(statusResult);
                 result.setResultMessage(GroupbuyingConstants.CommonStatusEnum.getValue(statusResult));
            }

        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.updateShowStatusByPromotionId 出现异常{}", messageId, singlePromotionInfoReqDTO.getPromotionId() + ":" + e.toString());
        }
        return result;
    }

    @Override
    public ExecuteResult<?> deleteGroupbuyingInfoByPromotionId(GroupbuyingInfoReqDTO groupbuyingInfoReqDTO, String messageId) {
        ExecuteResult<?> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {

        	String statusResult = groupbuyingService.deleteGroupbuyingInfoByPromotionId(groupbuyingInfoReqDTO,messageId);
            if(!GroupbuyingConstants.CommonStatusEnum.STATUS_SUCCESS.key().equals(statusResult)){
           	 result.setCode(statusResult);
             result.setResultMessage(GroupbuyingConstants.CommonStatusEnum.getValue(statusResult));
           }
            
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.deleteGroupbuyingInfoByPromotionId 出现异常{}", messageId, groupbuyingInfoReqDTO.getPromotionId() + ":" + e.toString());
        }
        return result;
    }
    
	@Override
	public ExecuteResult<Boolean> hasProductIsBeingUsedByPromotion(String skuCode,Date startTime,Date endTime, String messageId) {
        ExecuteResult<Boolean> result = new ExecuteResult<Boolean>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {

        	Boolean flag = groupbuyingService.hasProductIsBeingUsedByPromotion(skuCode,startTime,endTime,messageId);
        	result.setResult(flag);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.hasProductIsBeingUsedByPromotion 出现异常{}", messageId, skuCode + ":" + e.toString());
        }
        return result;
	}


	@Override
	public ExecuteResult<GroupbuyingInfoCmplResDTO> getGroupbuyingHomePage2HttpINTFC(GroupbuyingInfoReqDTO groupbuyingInfoReqDTO, String messageId) {
		
        ExecuteResult<GroupbuyingInfoCmplResDTO> result = new ExecuteResult<GroupbuyingInfoCmplResDTO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
        	
        	GroupbuyingInfoCmplResDTO groupbuyingInfoCmplResDTO = groupbuyingService.getGroupbuyingInfo4MobileHomePage(groupbuyingInfoReqDTO, messageId);
        	result.setResult(groupbuyingInfoCmplResDTO);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getGroupbuyingHomePage2HttpINTFC出现异常{}", messageId, e.toString());
        }
        
        return result;
		
	}


	@Override
	public ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> getMyGroupbuyingForPage2HttpINTFC(Pager<GroupbuyingInfoReqDTO> pager, GroupbuyingInfoReqDTO groupbuyingInfoReqDTO, String messageId) {
        ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>> result = new ExecuteResult<DataGrid<GroupbuyingInfoCmplResDTO>>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
            DataGrid<GroupbuyingInfoCmplResDTO> groupbuyingInfoCmplResDTOData = groupbuyingService.getMyGroupbuying4MobileForPage(pager,groupbuyingInfoReqDTO, messageId);
            result.setResult(groupbuyingInfoCmplResDTOData);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.getMyGroupbuyingForPage2HttpINTFC出现异常{}", messageId, e.toString());
        }
        return result;
	}


	@Override
	public ExecuteResult<?> updateGroupbuyingInfoByManual(GroupbuyingInfoCmplReqDTO groupbuyingInfoCmplReqDTO,String messageId) {
		
        ExecuteResult<?> result = new ExecuteResult<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());

        try {
    		if (null == groupbuyingInfoCmplReqDTO) {
    			throw new PromotionCenterBusinessException(ResultCodeEnum.PARAMETER_ERROR.getCode(), "团购促销活动参数不能为空！");
    		}
    		
			if (null == groupbuyingInfoCmplReqDTO.getPromotionId() || "".equals(groupbuyingInfoCmplReqDTO.getPromotionId().trim())) {
				throw new PromotionCenterBusinessException(ResultCodeEnum.ERROR.getCode(), "团购促销活动编码不能为空！");
			}
    		
			groupbuyingService.updateGroupbuyingInfoByManual(groupbuyingInfoCmplReqDTO, messageId);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setResultMessage(ResultCodeEnum.ERROR.getMsg());
            result.setErrorMessage(e.toString());
            logger.error("MessageId:{} 调用方法GroupbuyingAPIImpl.updateGroupbuyingInfoByManual出现异常{}", messageId, groupbuyingInfoCmplReqDTO.getPromotionId() + ":" + e.toString());
        }
        return result;
	}



}
