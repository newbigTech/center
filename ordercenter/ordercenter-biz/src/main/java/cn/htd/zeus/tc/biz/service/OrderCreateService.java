package cn.htd.zeus.tc.biz.service;

import cn.htd.zeus.tc.biz.dmo.OrderCreateInfoDMO;
import cn.htd.zeus.tc.biz.dmo.TradeOrderItemsDMO;
import cn.htd.zeus.tc.biz.dmo.TradeOrderItemsDiscountDMO;
import cn.htd.zeus.tc.biz.dmo.TradeOrdersDMO;
import cn.htd.zeus.tc.dto.response.OrderCreateInfoResDTO;
import cn.htd.zeus.tc.dto.resquest.OrderCreate4huilinReqDTO;
import cn.htd.zeus.tc.dto.resquest.OrderCreateInfoReqDTO;

public interface OrderCreateService {

	/*
	 * 创建订单的时候-插入订单行表
	 */
	public int insertTradeOrderItems(TradeOrderItemsDMO tradeOrderItemsDMO);

	/*
	 * 创建订单的时候-插入订单表
	 */
	public int insertTradeOrders(TradeOrdersDMO tradeOrdersDMO);

	/*
	 * 插入订单行优惠信息表
	 */
	public int insertTradeOrderItemsDiscount(TradeOrderItemsDiscountDMO tradeOrderItemsDiscountDMO);

	/*
	 * 创建订单逻辑-支持创建内部供应商、外部供应商、京东商品、秒杀、优惠券
	 */
	public OrderCreateInfoDMO orderCreate(OrderCreateInfoReqDTO orderCreateInfoReqDTO);

	/*
	 * 创建汇掌柜订单前组装创建订单所需要的所有入参
	 */
	public OrderCreateInfoResDTO assembleOrder4huilin(String messageId,
			OrderCreate4huilinReqDTO orderCreate4huilinReqDTO,
			OrderCreateInfoResDTO orderCreateInfoResDTO);
}
