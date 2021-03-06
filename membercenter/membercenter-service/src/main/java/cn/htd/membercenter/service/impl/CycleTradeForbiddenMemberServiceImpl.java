package cn.htd.membercenter.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yiji.openapi.tool.fastjson.JSONObject;
import com.yiji.openapi.tool.util.StringUtils;

import cn.htd.common.DataGrid;
import cn.htd.common.ExecuteResult;
import cn.htd.common.Pager;
import cn.htd.common.dao.util.RedisDB;
import cn.htd.membercenter.common.constant.MemberCenterCodeEnum;
import cn.htd.membercenter.dao.CycleTradeForbiddenMemberDAO;
import cn.htd.membercenter.domain.CycleTradeForbiddenMember;
import cn.htd.membercenter.dto.CycleTradeForbiddenMemberDTO;
import cn.htd.membercenter.service.BoxRelationshipService;
import cn.htd.membercenter.service.CycleTradeForbiddenMemberService;

@Service("cycleTradeForbiddenMemberService")
public class CycleTradeForbiddenMemberServiceImpl implements CycleTradeForbiddenMemberService {

	private Logger logger = Logger.getLogger(CycleTradeForbiddenMemberServiceImpl.class);

	@Resource
	private CycleTradeForbiddenMemberDAO cycleTradeForbiddenMemberDAO;
	@Resource
	private BoxRelationshipService boxRelationshipService;
	@Resource
	private RedisDB redisDB;
	//平台公司是否禁止销售标识(reids)
	private static final String CODE_CYCLE_TRADE_FORBIDDEN_COMPANY_FLAG = "CYCLE_TRADE_FORBIDDEN_COMPANY_FLAG";

	@Override
	public ExecuteResult<String> insertCycleTradeForbiddenMember(CycleTradeForbiddenMemberDTO dto) {
		logger.info("新增互为上下游禁止交易会员信息参数:" + JSONObject.toJSONString(dto));
		ExecuteResult<String> result = new ExecuteResult<String>();
		try {
			if (null == dto) {
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			if(dto.isExport()){
				ExecuteResult<String> memberResult = boxRelationshipService.selectCompanyName(dto.getMemberCode(), dto.getMemberName());
				if(!"00000".equals(memberResult) || StringUtils.isEmpty(memberResult.getResult())){
					result.setCode(MemberCenterCodeEnum.NO_DATA_CODE.getCode());
					return result;
				}
			}
			CycleTradeForbiddenMemberDTO countDTO = new CycleTradeForbiddenMemberDTO();
			countDTO.setMemberCode(dto.getMemberCode());
			countDTO.setForbiddenType(dto.getForbiddenType());
			long count = cycleTradeForbiddenMemberDAO.selecCycleTradeForbiddenMemberCount(countDTO);
			if(count > 0){
				CycleTradeForbiddenMember member = cycleTradeForbiddenMemberDAO.queryCycleTradeForbiddenMember(dto);
				dto.setId(member.getId());
				cycleTradeForbiddenMemberDAO.updateCycleTradeForbiddenMember(dto);
				result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
				return result;
			}
			cycleTradeForbiddenMemberDAO.insertCycleTradeForbiddenMember(dto);
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>insertCycleTradeForbiddenMember=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<String> deleteCycleTradeForbiddenMember(CycleTradeForbiddenMemberDTO dto) {
		logger.info("删除互为上下游禁止交易会员信息参数:" + JSONObject.toJSONString(dto));
		ExecuteResult<String> result = new ExecuteResult<String>();
		try {
			if (null == dto || CollectionUtils.isEmpty(dto.getIds())) {
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			cycleTradeForbiddenMemberDAO.deleteCycleTradeForbiddenMember(dto);
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>deleteCycleTradeForbiddenMember=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<DataGrid<CycleTradeForbiddenMemberDTO>> selectCycleTradeForbiddenMemberList(
			CycleTradeForbiddenMemberDTO dto, Pager<CycleTradeForbiddenMemberDTO> pager) {
		logger.info(
				"查询互为上下游禁止交易会员信息(分页)参数:" + JSONObject.toJSONString(dto) + ",page=" + JSONObject.toJSONString(pager));
		ExecuteResult<DataGrid<CycleTradeForbiddenMemberDTO>> result = new ExecuteResult<DataGrid<CycleTradeForbiddenMemberDTO>>();
		try {
			if (null == dto) {
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			long count = cycleTradeForbiddenMemberDAO.selecCycleTradeForbiddenMemberCount(dto);
			if (count > 0) {
				DataGrid<CycleTradeForbiddenMemberDTO> dg = new DataGrid<CycleTradeForbiddenMemberDTO>();
				List<CycleTradeForbiddenMember> memberList = cycleTradeForbiddenMemberDAO
						.selectCycleTradeForbiddenMemberList(dto, pager);
				if (CollectionUtils.isNotEmpty(memberList)) {
					String str = JSONObject.toJSONString(memberList);
					List<CycleTradeForbiddenMemberDTO> convertList = JSONObject.parseArray(str,
							CycleTradeForbiddenMemberDTO.class);
					dg.setRows(convertList);
					dg.setTotal(count);
					result.setResult(dg);
				}
			} else {
				result.setResultMessage("暂无要禁止会员店购买的数据");
			}
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>selectCycleTradeForbiddenMemberList=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<Boolean> isCycleTradeForbiddenMember(CycleTradeForbiddenMemberDTO dto) {
		logger.info("查询该会员是否是互为上下游数据参数:" + JSONObject.toJSONString(dto));
		ExecuteResult<Boolean> result = new ExecuteResult<Boolean>();
		try {
			if (null == dto) {
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			long count = cycleTradeForbiddenMemberDAO.selecCycleTradeForbiddenMemberCount(dto);
			if (count > 0) {
				result.setResult(true);
			} else {
				result.setResult(false);
			}
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>isCycleTradeForbiddenMember=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<CycleTradeForbiddenMemberDTO> queryCycleTradeForbiddenMember(
			CycleTradeForbiddenMemberDTO dto) {
		logger.info("查询该会员数据参数:" + JSONObject.toJSONString(dto));
		ExecuteResult<CycleTradeForbiddenMemberDTO> result = new ExecuteResult<CycleTradeForbiddenMemberDTO>();
		try {
			if (null == dto) {
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			CycleTradeForbiddenMember resultDTO = cycleTradeForbiddenMemberDAO.queryCycleTradeForbiddenMember(dto);
			if(resultDTO != null){
				String str = JSONObject.toJSONString(resultDTO);
				CycleTradeForbiddenMemberDTO convertDTO = JSONObject.parseObject(str,
						CycleTradeForbiddenMemberDTO.class);
				result.setResult(convertDTO);
				result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
			}else{
				result.setCode(MemberCenterCodeEnum.NO_DATA_CODE.getCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>queryCycleTradeForbiddenMember=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<Boolean> isCycleTradeForbiddenCompany() {
		ExecuteResult<Boolean> result = new ExecuteResult<Boolean>();
		try {
			String value = redisDB.get(CODE_CYCLE_TRADE_FORBIDDEN_COMPANY_FLAG);
			if("禁止".equals(value)){
				result.setResult(true);
			}else{
				result.setResult(false);
			}
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>isCycleTradeForbiddenCompany=" + e);
		}
		return result;
	}

	@Override
	public ExecuteResult<Boolean> updateCycleTradeForbiddenCompany(String flag) {
		ExecuteResult<Boolean> result = new ExecuteResult<Boolean>();
		try {
			if(StringUtils.isEmpty(flag)){
				result.setCode(MemberCenterCodeEnum.ERROR.getCode());
				result.setResultMessage("参数错误");
				return result;
			}
			if("1".equals(flag)){
				redisDB.set(CODE_CYCLE_TRADE_FORBIDDEN_COMPANY_FLAG, "禁止");
			}else{
				redisDB.set(CODE_CYCLE_TRADE_FORBIDDEN_COMPANY_FLAG, "不禁止");
			} 
			result.setResult(true);
			result.setCode(MemberCenterCodeEnum.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(MemberCenterCodeEnum.ERROR.getCode());
			result.addErrorMessage("error");
			result.setResultMessage("异常");
			logger.error("CycleTradeForbiddenMemberServiceImpl======>updateCycleTradeForbiddenCompany=" + e);
		}
		return result;
	}

}
