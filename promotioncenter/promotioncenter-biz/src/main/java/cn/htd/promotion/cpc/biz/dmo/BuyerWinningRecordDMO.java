package cn.htd.promotion.cpc.biz.dmo;

import java.util.Date;

public class BuyerWinningRecordDMO {

	  private Long id;

	    private String promotionId;

	    private String levelCode;

	    private String promotionType;

	    private String promotionName;

	    private String buyerCode;

	    private String buyerName;

	    private String buyerTelephone;

	    private String sellerCode;

	    private String sellerAddress;

	    private String belongSuperiorName;

	    private String rewardType;

	    private String relevanceBargainCode;

	    private String rewardName;

	    private String awardValue;

	    private Date winningTime;

	    private String winnerName;

	    private String winningContact;

	    private String chargeTelephone;

	    private String logisticsStatus;

	    private String logisticsCompany;

	    private String logisticsNo;

	    private Long createId;

	    private String createName;

	    private Date createTime;

	    private Long modifyId;

	    private String modifyName;

	    private Date modifyTime;
	    
	    /**
		 * 开始位置
		 */
		private Integer startNo;

		/**
		 * 结束位置
		 */
		private Integer endNo;
		
		private String winningFrom;

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
	        this.promotionId = promotionId == null ? null : promotionId.trim();
	    }

	    public String getLevelCode() {
	        return levelCode;
	    }

	    public void setLevelCode(String levelCode) {
	        this.levelCode = levelCode == null ? null : levelCode.trim();
	    }

	    public String getPromotionType() {
	        return promotionType;
	    }

	    public void setPromotionType(String promotionType) {
	        this.promotionType = promotionType == null ? null : promotionType.trim();
	    }

	    public String getPromotionName() {
	        return promotionName;
	    }

	    public void setPromotionName(String promotionName) {
	        this.promotionName = promotionName == null ? null : promotionName.trim();
	    }

	    public String getBuyerCode() {
	        return buyerCode;
	    }

	    public void setBuyerCode(String buyerCode) {
	        this.buyerCode = buyerCode == null ? null : buyerCode.trim();
	    }

	    public String getBuyerName() {
	        return buyerName;
	    }

	    public void setBuyerName(String buyerName) {
	        this.buyerName = buyerName == null ? null : buyerName.trim();
	    }

	    public String getBuyerTelephone() {
	        return buyerTelephone;
	    }

	    public void setBuyerTelephone(String buyerTelephone) {
	        this.buyerTelephone = buyerTelephone == null ? null : buyerTelephone.trim();
	    }

	    public String getSellerCode() {
	        return sellerCode;
	    }

	    public void setSellerCode(String sellerCode) {
	        this.sellerCode = sellerCode == null ? null : sellerCode.trim();
	    }

	    public String getSellerAddress() {
	        return sellerAddress;
	    }

	    public void setSellerAddress(String sellerAddress) {
	        this.sellerAddress = sellerAddress == null ? null : sellerAddress.trim();
	    }

	    public String getBelongSuperiorName() {
	        return belongSuperiorName;
	    }

	    public void setBelongSuperiorName(String belongSuperiorName) {
	        this.belongSuperiorName = belongSuperiorName == null ? null : belongSuperiorName.trim();
	    }

	    public String getRewardType() {
	        return rewardType;
	    }

	    public void setRewardType(String rewardType) {
	        this.rewardType = rewardType == null ? null : rewardType.trim();
	    }

	    public String getRelevanceBargainCode() {
	        return relevanceBargainCode;
	    }

	    public void setRelevanceBargainCode(String relevanceBargainCode) {
	        this.relevanceBargainCode = relevanceBargainCode == null ? null : relevanceBargainCode.trim();
	    }

	    public String getRewardName() {
	        return rewardName;
	    }

	    public void setRewardName(String rewardName) {
	        this.rewardName = rewardName == null ? null : rewardName.trim();
	    }

	    public String getAwardValue() {
	        return awardValue;
	    }

	    public void setAwardValue(String awardValue) {
	        this.awardValue = awardValue == null ? null : awardValue.trim();
	    }

	    public Date getWinningTime() {
	        return winningTime;
	    }

	    public void setWinningTime(Date winningTime) {
	        this.winningTime = winningTime;
	    }

	    public String getWinnerName() {
	        return winnerName;
	    }

	    public void setWinnerName(String winnerName) {
	        this.winnerName = winnerName == null ? null : winnerName.trim();
	    }

	    public String getWinningContact() {
	        return winningContact;
	    }

	    public void setWinningContact(String winningContact) {
	        this.winningContact = winningContact == null ? null : winningContact.trim();
	    }

	    public String getChargeTelephone() {
	        return chargeTelephone;
	    }

	    public void setChargeTelephone(String chargeTelephone) {
	        this.chargeTelephone = chargeTelephone == null ? null : chargeTelephone.trim();
	    }

	    public String getLogisticsStatus() {
	        return logisticsStatus;
	    }

	    public void setLogisticsStatus(String logisticsStatus) {
	        this.logisticsStatus = logisticsStatus == null ? null : logisticsStatus.trim();
	    }

	    public String getLogisticsCompany() {
	        return logisticsCompany;
	    }

	    public void setLogisticsCompany(String logisticsCompany) {
	        this.logisticsCompany = logisticsCompany == null ? null : logisticsCompany.trim();
	    }

	    public String getLogisticsNo() {
	        return logisticsNo;
	    }

	    public void setLogisticsNo(String logisticsNo) {
	        this.logisticsNo = logisticsNo == null ? null : logisticsNo.trim();
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
	        this.createName = createName == null ? null : createName.trim();
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
	        this.modifyName = modifyName == null ? null : modifyName.trim();
	    }

	    public Date getModifyTime() {
	        return modifyTime;
	    }

	    public void setModifyTime(Date modifyTime) {
	        this.modifyTime = modifyTime;
	    }

		public Integer getStartNo() {
			return startNo;
		}

		public void setStartNo(Integer startNo) {
			this.startNo = startNo;
		}

		public Integer getEndNo() {
			return endNo;
		}

		public void setEndNo(Integer endNo) {
			this.endNo = endNo;
		}

		public String getWinningFrom() {
			return winningFrom;
		}

		public void setWinningFrom(String winningFrom) {
			this.winningFrom = winningFrom;
		}
		
}