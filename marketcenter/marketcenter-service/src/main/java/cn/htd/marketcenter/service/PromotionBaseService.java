package cn.htd.marketcenter.service;

import cn.htd.marketcenter.common.exception.MarketCenterBusinessException;
import cn.htd.marketcenter.domain.BuyerCheckInfo;
import cn.htd.marketcenter.dto.BuyerInfoDTO;
import cn.htd.marketcenter.dto.PromotionAccumulatyDTO;
import cn.htd.marketcenter.dto.PromotionInfoDTO;
import cn.htd.marketcenter.dto.PromotionValidDTO;
import cn.htd.membercenter.dto.MemberGroupDTO;

public interface PromotionBaseService {
    /**
     * 删除促销活动
     *
     * @param validDTO
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public void deletePromotionInfo(PromotionValidDTO validDTO) throws MarketCenterBusinessException, Exception;

    /**
     * 插入促销活动表
     *
     * @param promotionInfo
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionInfoDTO insertPromotionInfo(PromotionInfoDTO promotionInfo)
            throws MarketCenterBusinessException, Exception;


    /**
     * 查询促销活动信息
     *
     * @param promotionId
     * @param levelCodeArr
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionInfoDTO queryPromotionInfo(String promotionId, String... levelCodeArr)
            throws MarketCenterBusinessException, Exception;


    /**
     * 更新促销活动表
     *
     * @param promotionInfo
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionInfoDTO updatePromotionInfo(PromotionInfoDTO promotionInfo)
            throws MarketCenterBusinessException, Exception;

    /**
     * 将只有一个层级的促销活动转换成一个对象
     *
     * @param promotionInfo
     * @return
     */
    public PromotionAccumulatyDTO convertSingleAccumulatyPromotion2Info(PromotionInfoDTO promotionInfo);

    /**
     * 将只有一个层级的促销活动转换成层级列表形式DTO
     *
     * @param promotionAccuDTO
     * @return
     */
    public PromotionInfoDTO convertSingleAccumulatyPromotion2DTO(PromotionAccumulatyDTO promotionAccuDTO);

    /**
     * 查询促销活动信息,并将一个层级内容扁平化或者根据指定层级编码取得促销活动
     *
     * @param promotionId
     * @param levelCode
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionAccumulatyDTO querySingleAccumulatyPromotionInfo(String promotionId, String... levelCode)
            throws MarketCenterBusinessException, Exception;

    /**
     * 插入只有一个层级的促销活动信息
     *
     * @param promotionAccuDTO
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionAccumulatyDTO insertSingleAccumulatyPromotionInfo(PromotionAccumulatyDTO promotionAccuDTO)
            throws MarketCenterBusinessException, Exception;

    /**
     * 更新只有一个层级的促销活动信息
     *
     * @param promotionAccuDTO
     * @return
     * @throws MarketCenterBusinessException
     * @throws Exception
     */
    public PromotionAccumulatyDTO updateSingleAccumulatyPromotionInfo(PromotionAccumulatyDTO promotionAccuDTO)
            throws MarketCenterBusinessException, Exception;

    /**
     * 检查优惠活动会员规则
     *
     * @param promotionInfoDTO
     * @param buyerInfo
     * @return
     */
    public boolean checkPromotionBuyerRule(PromotionInfoDTO promotionInfoDTO, BuyerCheckInfo buyerInfo);


    /**
     * 根据促销活动的有效期间设定促销活动状态
     *
     * @param promotionInfo
     * @return
     */
    public String setPromotionStatusInfo(PromotionInfoDTO promotionInfo);

    /**
     * 校验会员是否在指定卖家的分组内,存在时获取分组ID
     * @param messageId
     * @param buyerInfo
     * @return
     */
    public MemberGroupDTO getBuyerGroupRelationship(String messageId, BuyerInfoDTO buyerInfo);
}
