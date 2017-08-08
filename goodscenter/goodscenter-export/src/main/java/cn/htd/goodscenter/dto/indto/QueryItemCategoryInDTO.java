package cn.htd.goodscenter.dto.indto;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Description: [商品品牌]
 * </p>
 */
public class QueryItemCategoryInDTO implements Serializable {

	private Date startTime;

	private Date endTime;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
