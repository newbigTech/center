package cn.htd.membercenter.domain;

import java.io.Serializable;
import java.util.Date;

public class OMeetingEvaluate implements Serializable {

	private static final long serialVersionUID = 3894822842078032905L;

	private Long id;
	private String meetingNo;// 会议序号
	private Long sellerId;// 平台公司id
	private String sellerName;// 平台公司名称
	private String sellerCode;// 平台公司帐号
	private String meetingTitle;// 标题
	private Date meetingStateTime;// 会议开始时间
	private Date meetingEndTime;// 会议结束时间
	private String meetingAddr;// 会议地点
	private Long memberId;// 会员ID
	private String memberCode;// 会员店帐号
	private String memberName;// 会员店名称
	private String artificialPersonName;// 会员店法人姓名
	private Integer evaluate1;// 行动计划评价
	private Integer evaluate2;// 数据分析评价
	private Integer evaluate3;// 盈利帮助评价
	private Long createId;// 创建人ID
	private String createName;// 创建人名称
	private Date createTime;// 创建时间

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMeetingNo() {
		return meetingNo;
	}

	public void setMeetingNo(String meetingNo) {
		this.meetingNo = meetingNo;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getMeetingTitle() {
		return meetingTitle;
	}

	public void setMeetingTitle(String meetingTitle) {
		this.meetingTitle = meetingTitle;
	}

	public Date getMeetingStateTime() {
		return meetingStateTime;
	}

	public void setMeetingStateTime(Date meetingStateTime) {
		this.meetingStateTime = meetingStateTime;
	}

	public Date getMeetingEndTime() {
		return meetingEndTime;
	}

	public void setMeetingEndTime(Date meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}

	public String getMeetingAddr() {
		return meetingAddr;
	}

	public void setMeetingAddr(String meetingAddr) {
		this.meetingAddr = meetingAddr;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getArtificialPersonName() {
		return artificialPersonName;
	}

	public void setArtificialPersonName(String artificialPersonName) {
		this.artificialPersonName = artificialPersonName;
	}

	public Integer getEvaluate1() {
		return evaluate1;
	}

	public void setEvaluate1(Integer evaluate1) {
		this.evaluate1 = evaluate1;
	}

	public Integer getEvaluate2() {
		return evaluate2;
	}

	public void setEvaluate2(Integer evaluate2) {
		this.evaluate2 = evaluate2;
	}

	public Integer getEvaluate3() {
		return evaluate3;
	}

	public void setEvaluate3(Integer evaluate3) {
		this.evaluate3 = evaluate3;
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

}
