package cn.htd.promotion.cpc.dto.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 促销活动供应商规则
 */
public class PromotionSellerRuleDTO extends GenricResDTO {
	/**
	 * ID
	 */
	private Long id;
	/**
	 * 促销活动编码
	 */
	private String promotionId;
	/**
	 * 规则ID
	 */
	private Long ruleId;
	/**
	 * 规则名称
	 */
	private String ruleName;
	/**
	 * 卖家规则对象种类 ：1：指定供应商类型，2：部分供应商
	 */
	private String ruleTargetType;
	/**
	 * 卖家规则对象种类为1时使用 0：全部通用 1（平台公司），2（商品+），3（外部供应商）
	 */
	private String targetSellerType;
	/**
	 * 删除标记
	 */
	private int deleteFlag;
	/**
	 * 创建人ID
	 */
	private Long createId;
	/**
	 * 创建人名称
	 */
	private String createName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新人ID
	 */
	private Long modifyId;
	/**
	 * 更新人名称
	 */
	private String modifyName;
	/**
	 * 更新时间
	 */
	private Date modifyTime;
	/**
	 * 指定卖家信息
	 */
	private List<PromotionSellerDetailDTO> sellerDetailList;

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

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleTargetType() {
		return ruleTargetType;
	}

	public void setRuleTargetType(String ruleTargetType) {
		this.ruleTargetType = ruleTargetType;
	}

	public String getTargetSellerType() {
		return targetSellerType;
	}

	public void setTargetSellerType(String targetSellerType) {
		this.targetSellerType = targetSellerType;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
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

	public List<PromotionSellerDetailDTO> getSellerDetailList() {
		return sellerDetailList;
	}

	public void setSellerDetailList(List<PromotionSellerDetailDTO> sellerDetailList) {
		this.sellerDetailList = sellerDetailList;
	}
}