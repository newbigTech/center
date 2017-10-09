package cn.htd.marketcenter.service;
import cn.htd.common.ExecuteResult;
import cn.htd.marketcenter.dto.TimelimitedInfoDTO;

/**
 * 限时购活动服务
 */
public interface TimelimitedPurchaseService {

	/**
	 * 限时购  -  新增限时购活动信息
	 * 
	 * @param timelimitedInfo
	 * @return
	 */
	public ExecuteResult<TimelimitedInfoDTO> addTimelimitedInfo(TimelimitedInfoDTO timelimitedInfo);

	/**
	 * 限时购	  －  获取对应的限时购信息
	 * @param skuCode
	 * @return
	 */
	public ExecuteResult<TimelimitedInfoDTO> getTimelimitedInfo(String skuCode);

}
