package cn.htd.promotion.cpc.biz.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.htd.common.Pager;
import cn.htd.common.dao.orm.BaseDAO;
import cn.htd.promotion.cpc.dto.response.PromotionInfoDTO;

@Repository("cn.htd.promotion.cpc.biz.dao.promotionInfoDAO")
public interface PromotionInfoDAO extends BaseDAO<PromotionInfoDTO> {

    /**
     * 根据促销活动ID列表查询促销活动信息
     *
     * @param promotionIdList
     * @return
     */
    public List<PromotionInfoDTO> queryPromotionInfoByIdList(List<String> promotionIdList);

    /**
     * 修改促销活动展示状态(启用,不启用)
     *
     * @param record
     * @return
     */
    public Integer savePromotionValidStatus(PromotionInfoDTO record);

    /**
     * 取得定时任务处理对象促销活动
     *
     * @param condition
     * @param page
     * @return
     */
    public List<PromotionInfoDTO> queryPromotionList4Task(@Param("entity") PromotionInfoDTO condition,
            @Param("page") Pager<PromotionInfoDTO> page);

    /**
     * 更新促销活动状态
     *
     * @param promotionInfo
     * @return
     */
    public Integer updatePromotionStatusById(PromotionInfoDTO promotionInfo);

    /**
     * 更新促销活动审核结果
     *
     * @param promotionInfo
     * @return
     */
    public Integer updatePromotionVerifyInfo(PromotionInfoDTO promotionInfo);

    /**
     * 根据促销活动类型查询所有未开始／进行中的促销活动信息
     *
     * @param condition
     * @return
     */
    public List<PromotionInfoDTO> queryPromotionListByType(PromotionInfoDTO condition);

    /**
     * 根据是否已清除Redis标记，查询需要清除的促销活动信息
     *
     * @param condition
     * @param page
     * @return
     */
    public List<PromotionInfoDTO> queryNeedCleanRedisPromotion4Task(@Param("entity") PromotionInfoDTO condition,
            @Param("page") Pager<PromotionInfoDTO> page);

    /**
     * 更新促销活动的清除Redis标记
     *
     * @param promotionInfoDTO
     * @return
     */
    public Integer updateCleanedRedisPromotionStatus(PromotionInfoDTO promotionInfoDTO);

}
