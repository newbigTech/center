package cn.htd.promotion.cpc.api;

import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.dto.request.PromotionInfoReqDTO;
import cn.htd.promotion.cpc.dto.response.PromotionInfoDTO;

public interface PromotionLotteryAPI {
	
	/**
	 * 查询扭蛋机活动列表
	 * PromotionInfoDTO 取参数数据值如下
	 * @param promotion_name 活动名称
	 * @param status 活动状态：1：活动未开始，2：活动进行中，3：活动已结束，9：已删除',
	 * @param effective_time 活动开始时间
	 * @param invalid_time 活动结束时间
	 * @param modify_time 最后操作时间状态：
	 * @return
	 */
	public ExecuteResult<DataGrid<PromotionInfoDTO>> getPromotionGashaponByInfo(PromotionInfoReqDTO promotionInfoDTO,Pager<PromotionInfoReqDTO> pager);
	
	/**
	 * 判断汇掌柜首页是否要显示扭蛋机图标
	 * 
	 * @param validateLuckDrawReqDTOJson
	 * @return
	 */
	public String validateLuckDrawPermission(String validateLuckDrawReqDTOJson);

	/**
	 * 抽奖(扭蛋)活动页
	 * 
	 * @param request
	 * @return
	 */
	public String lotteryActivityPage(String lotteryActivityPageReqDTOJson);

	/**
	 * 抽奖(扭蛋)活动规则页
	 * 
	 * @param request
	 * @return
	 */
	public String lotteryActivityRulePage(String lotteryActivityRulePageReqDTOJson);
	
	/**
	 * 粉丝查询自己的中讲过记录
	 * @param winningRecordReqDTOJson
	 * @return
	 */
	public String queryWinningRecord(String winningRecordReqDTOJson);
	
	/**
	 * 分享链接点击处理
	 * @param shareLinkHandleReqDTOJson
	 * @return
	 */
	public String shareLinkHandle(String shareLinkHandleReqDTOJson);

	/**
	 * 开始抽奖处理
	 * @param drawLotteryParam
	 * @return
	 */
	public String beginDrawLottery(String drawLotteryParam);

	/**
	 * 查询抽奖结果
	 * @param drawLotteryParam
	 * @return
	 */
	public String getDrawLotteryResult(String drawLotteryParam);

}