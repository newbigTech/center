package cn.htd.promotion.cpc.biz.service;

import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.dto.request.ActivityPictureInfoReqDTO;
import cn.htd.promotion.cpc.dto.request.MemberActivityPictureReqDTO;
import cn.htd.promotion.cpc.dto.response.ActivityPictureInfoResDTO;
import cn.htd.promotion.cpc.dto.response.MemberActivityPictureResDTO;

public interface MaterielDownloadService {
	/**
	 * 新增
	 * 
	 * @param activityPictureInfoReqDTO
	 * @return
	 */
	public ActivityPictureInfoResDTO addMaterielDownload(ActivityPictureInfoReqDTO activityPictureInfoReqDTO);

	/**
	 * 编辑
	 * 
	 * @param activityPictureInfoReqDTO
	 * @return
	 */
	public ActivityPictureInfoResDTO editMaterielDownload(ActivityPictureInfoReqDTO activityPictureInfoReqDTO);

	/**
	 * 查看
	 * 
	 * @param activityPictureInfoReqDTO
	 * @return
	 */
	public ActivityPictureInfoResDTO viewMaterielDownload(String activityPictureInfoId);

	/**
	 * 一览
	 * 
	 * @param activityPictureInfoReqDTO
	 * @param pager
	 * @return
	 */
	public ExecuteResult<DataGrid<ActivityPictureInfoResDTO>> selectMaterielDownload(
			ActivityPictureInfoReqDTO activityPictureInfoReqDTO, Pager<ActivityPictureInfoResDTO> pager);

	public ActivityPictureInfoResDTO delMaterielDownload(String activityPictureInfoReqID);
	
	
	/**
	 * 物料历史记录查询
	 * @param memberActivityPictureReqDTO
	 * @param pager
	 * @return
	 */
	public ExecuteResult<DataGrid<MemberActivityPictureResDTO>> selectMemberActivityPicture(MemberActivityPictureReqDTO memberActivityPictureReqDTO, Pager<MemberActivityPictureReqDTO> pager);
	
	/**
	 * 物料历史记录删除
	 * @param memberActivityPictureReqDTO
	 * @param pager
	 * @return
	 */
	public MemberActivityPictureResDTO delMemberActivityPicture(Long id);

	public ExecuteResult<DataGrid<ActivityPictureInfoResDTO>> selectMaterielDownloadByMemberCode(String memberCode, String pictureType, String messageid);
}
