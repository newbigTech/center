package cn.htd.promotion.cpc.biz.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import cn.htd.common.Pager;
import cn.htd.promotion.cpc.dto.request.GroupbuyingInfoReqDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoCmplResDTO;
import cn.htd.promotion.cpc.dto.response.GroupbuyingInfoResDTO;

public interface GroupbuyingInfoDAO {
	
    int deleteByPrimaryKey(Long groupbuyingId);
    
    int deleteByPromotionId(GroupbuyingInfoReqDTO record);

    int insert(GroupbuyingInfoReqDTO record);

    int insertSelective(GroupbuyingInfoReqDTO record);

    GroupbuyingInfoResDTO selectByPrimaryKey(Long groupbuyingId);
    
    GroupbuyingInfoResDTO selectByPromotionId(String promotionId);

    int updateByPrimaryKeySelective(GroupbuyingInfoReqDTO record);

    int updateByPrimaryKey(GroupbuyingInfoReqDTO record);
    
    int updateGroupbuyingInfo(GroupbuyingInfoReqDTO record);
    
    
	/**
	 * 根据条件查询团购活动总数
	 * @param record
	 * @return
	 */
	int getGroupbuyingInfoCmplCount( @Param("dto")GroupbuyingInfoReqDTO record);
	
	/**
	 * 分页查询团购活动
	 * @param page
	 * @param record
	 * @return
	 */
	List<GroupbuyingInfoCmplResDTO> getGroupbuyingInfoCmplForPage(@Param("page")Pager<GroupbuyingInfoReqDTO> page, @Param("dto")GroupbuyingInfoReqDTO record);
	
	/**
	 * 根据活动编码查询单个团购活动
	 * @return
	 */
	GroupbuyingInfoCmplResDTO getGroupbuyingInfoCmplByPromotionId(String promotionId);
	
	
    
}