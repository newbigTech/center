package com.bjucloud.contentcenter.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * <p>
 * Description: [商城设置--左侧导航列表DTO]
 * </p>
 */
public class MallRecommNavDTO implements Serializable {

	private static final long serialVersionUID = 3193156711185490662L;
	private Integer id; //
	private Integer sortNum;// 排序顺序
	private String title; // 名称
	private String url; // 链接指向
	private Integer recId; // mall_recommend商城推荐id，即楼层id。
	private Integer status; // 启用状态。0，不启用；1，启用。
	private Date created; // 创建时间
	private Integer isImg;// 是否是图片导航 0，不是；1，是smallint(6)
	private Date modified; // 修改时间
	private String picSrc;// 图片
	private Integer nType;// 导航类型
	private String recommendName;// 楼层名称

	public String getRecommendName() {
		return recommendName;
	}

	public void setRecommendName(String recommendName) {
		this.recommendName = recommendName;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public Integer getId() {
		return id;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getRecId() {
		return recId;
	}

	public void setRecId(Integer recId) {
		this.recId = recId;
	}

	public Integer getIsImg() {
		return isImg;
	}

	public void setIsImg(Integer isImg) {
		this.isImg = isImg;
	}

	public String getPicSrc() {
		return picSrc;
	}

	public void setPicSrc(String picSrc) {
		this.picSrc = picSrc;
	}

	public Integer getnType() {
		return nType;
	}

	public void setnType(Integer nType) {
		this.nType = nType;
	}
}