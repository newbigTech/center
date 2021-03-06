package cn.htd.promotion.cpc.dto.response;

import java.io.Serializable;
import java.util.Date;

public class VoteActivityFansVoteResDTO implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 534632546886733101L;

	private Long voteFansId;

    private Long voteMemberId;

    private Long fansId;

    private Date fansSignUpTime;

    private Long createId;

    private String createName;

    private Date createTime;

    private Long modifyId;

    private String modifyName;

    private Date modifyTime;

    public Long getVoteFansId() {
        return voteFansId;
    }

    public void setVoteFansId(Long voteFansId) {
        this.voteFansId = voteFansId;
    }

    public Long getVoteMemberId() {
        return voteMemberId;
    }

    public void setVoteMemberId(Long voteMemberId) {
        this.voteMemberId = voteMemberId;
    }

    public Long getFansId() {
        return fansId;
    }

    public void setFansId(Long fansId) {
        this.fansId = fansId;
    }

    public Date getFansSignUpTime() {
        return fansSignUpTime;
    }

    public void setFansSignUpTime(Date fansSignUpTime) {
        this.fansSignUpTime = fansSignUpTime;
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
}