package cn.htd.promotion.cpc.dto.response;

import java.io.Serializable;
import java.util.Date;

public class SinglePromotionInfoResDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3176351668008043549L;
	
	
	// 促销活动ID
	private Long id;
	// 促销活动编码
	private String promotionId;
	// 促销活动名称
	private String promotionName;
	// 促销活动描述
	private String promotionDescribe;
	// 促销活动发起方类型
	private String promotionProviderType;
	// 促销活动发起方商家编码
	private String promotionProviderSellerCode;
	// 促销活动发起方商家店铺ID
	private Long promotionProviderShopId;
	// 成本分摊类型
	private String costAllocationType;
	// 活动类型 1：优惠券，2：秒杀，3：限时购，21：扭蛋机，22：砍价，23：总部秒杀，24：刮刮乐，25：阶梯团
	private String promotionType;
	// 促销活动开始时间
	private Date effectiveTime;
	// 促销活动结束时间
	private Date invalidTime;
	// 是否是VIP会员专属标记
	private Integer isVip;
	// 状态 1：活动未开始，2：活动进行中，3：活动已结束，9：已删除
	private String status;
	// 审核状态 0：待审核，1：审核通过，2：审核被驳回，3：启用，4：不启用
	private String showStatus;
	// 审核人ID
	private Long verifierId;
	// 审核人名称
	private String verifierName;
	// 审核时间
	private Date verifyTime;
	// 审核备注
	private String verifyRemark;
	// 修改促销活动编码
	private String modifyPromotionId;
	// 是否已清理redis标记
	private Integer hasRedisClean;
	// 处理标记
	private Integer dealFlag;
	// 是否上过架
	private Integer hasUpFlag;
	// 创建人ID
	private Long createId;
	// 创建人名称
	private String createName;
	// 创建时间
	private Date createTime;
	// 更新人ID
	private Long modifyId;
	// 更新人名称
	private String modifyName;
	// 更新时间（促销活动更新时必须传入做乐观排他用）
	private Date modifyTime;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPromotionId() {
		return promotionId;
	}
	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}
	public String getPromotionName() {
		return promotionName;
	}
	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}
	public String getPromotionDescribe() {
		return promotionDescribe;
	}
	public void setPromotionDescribe(String promotionDescribe) {
		this.promotionDescribe = promotionDescribe;
	}
	public String getPromotionProviderType() {
		return promotionProviderType;
	}
	public void setPromotionProviderType(String promotionProviderType) {
		this.promotionProviderType = promotionProviderType;
	}
	public String getPromotionProviderSellerCode() {
		return promotionProviderSellerCode;
	}
	public void setPromotionProviderSellerCode(String promotionProviderSellerCode) {
		this.promotionProviderSellerCode = promotionProviderSellerCode;
	}
	public Long getPromotionProviderShopId() {
		return promotionProviderShopId;
	}
	public void setPromotionProviderShopId(Long promotionProviderShopId) {
		this.promotionProviderShopId = promotionProviderShopId;
	}
	public String getCostAllocationType() {
		return costAllocationType;
	}
	public void setCostAllocationType(String costAllocationType) {
		this.costAllocationType = costAllocationType;
	}
	public String getPromotionType() {
		return promotionType;
	}
	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}
	public Date getEffectiveTime() {
		return effectiveTime;
	}
	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	public Date getInvalidTime() {
		return invalidTime;
	}
	public void setInvalidTime(Date invalidTime) {
		this.invalidTime = invalidTime;
	}
	public Integer getIsVip() {
		return isVip;
	}
	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getShowStatus() {
		return showStatus;
	}
	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}
	public Long getVerifierId() {
		return verifierId;
	}
	public void setVerifierId(Long verifierId) {
		this.verifierId = verifierId;
	}
	public String getVerifierName() {
		return verifierName;
	}
	public void setVerifierName(String verifierName) {
		this.verifierName = verifierName;
	}
	public Date getVerifyTime() {
		return verifyTime;
	}
	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}
	public String getVerifyRemark() {
		return verifyRemark;
	}
	public void setVerifyRemark(String verifyRemark) {
		this.verifyRemark = verifyRemark;
	}
	public String getModifyPromotionId() {
		return modifyPromotionId;
	}
	public void setModifyPromotionId(String modifyPromotionId) {
		this.modifyPromotionId = modifyPromotionId;
	}
	public Integer getHasRedisClean() {
		return hasRedisClean;
	}
	public void setHasRedisClean(Integer hasRedisClean) {
		this.hasRedisClean = hasRedisClean;
	}
	public Integer getDealFlag() {
		return dealFlag;
	}
	public void setDealFlag(Integer dealFlag) {
		this.dealFlag = dealFlag;
	}
	public Integer getHasUpFlag() {
		return hasUpFlag;
	}
	public void setHasUpFlag(Integer hasUpFlag) {
		this.hasUpFlag = hasUpFlag;
	}
	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getModifyId() {
		return modifyId;
	}
	public void setModifyId(Long modifyId) {
		this.modifyId = modifyId;
	}
	public String getModifyName() {
		return modifyName;
	}
	public void setModifyName(String modifyName) {
		this.modifyName = modifyName;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	
	

}