package cn.htd.promotion.cpc.biz.service;

import cn.htd.promotion.cpc.dto.request.TimelimitedInfoReqDTO;

public interface TimelimitedInfoService {
	
	public void addTimelimitedInfo(TimelimitedInfoReqDTO timelimitedInfoReqDTO,String messageId);
	
	public void updateTimelimitedInfo(TimelimitedInfoReqDTO timelimitedInfoReqDTO,String messageId);

}
