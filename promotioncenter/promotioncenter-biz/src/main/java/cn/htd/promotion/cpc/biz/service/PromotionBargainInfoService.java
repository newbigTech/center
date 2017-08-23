package cn.htd.promotion.cpc.biz.service;

import java.util.List;
import cn.htd.promotion.cpc.common.exception.PromotionCenterBusinessException;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.dto.request.BuyerBargainLaunchReqDTO;
import cn.htd.promotion.cpc.dto.response.PromotionBargainInfoResDTO;
import cn.htd.promotion.cpc.dto.response.PromotionValidDTO;

public interface PromotionBargainInfoService {

	/**
	 * 查询砍价商品详情
	 * 
	 * @param buyerBargainLaunch
	 * @return
	 */
	public PromotionBargainInfoResDTO getPromotionBargainInfoDetail(
			BuyerBargainLaunchReqDTO buyerBargainLaunch);

	/**
	 * 新增砍价商品
	 * 
	 * @param promotionBargainInfoResDTO
	 */
	public ExecuteResult<List<PromotionBargainInfoResDTO>> addPromotionBargainInfoRedis(
			List<PromotionBargainInfoResDTO> promotionBargainInfoList)throws PromotionCenterBusinessException;
	
	/**
	 * 删除砍价活动
	 * @param validDTO
	 * @return
	 * @throws PromotionCenterBusinessException
	 */
	public ExecuteResult<String> deleteBargainInfo(PromotionValidDTO validDTO)throws PromotionCenterBusinessException;

	/**
	 * 获取砍价活动信息
	 * @param messageId
	 * @param promotionId
	 * @return
	 * @throws PromotionCenterBusinessException
	 */
	public ExecuteResult<List<PromotionBargainInfoResDTO>> getPromotionBargainInfoList(
			String messageId, String promotionId) throws PromotionCenterBusinessException;
	
	/**
	 * 修改砍价活动信息
	 * @param bargainInfoList
	 * @return
	 * @throws PromotionCenterBusinessException
	 */
	public ExecuteResult<List<PromotionBargainInfoResDTO>> updateBargainInfo(List<PromotionBargainInfoResDTO> bargainInfoList)
	    	throws PromotionCenterBusinessException;

}
