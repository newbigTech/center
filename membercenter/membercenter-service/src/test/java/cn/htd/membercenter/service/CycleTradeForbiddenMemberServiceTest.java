package cn.htd.membercenter.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yiji.openapi.tool.fastjson.JSONObject;

import cn.htd.common.DataGrid;
import cn.htd.common.ExecuteResult;
import cn.htd.common.Pager;
import cn.htd.membercenter.dto.CycleTradeForbiddenMemberDTO;

public class CycleTradeForbiddenMemberServiceTest {

	ApplicationContext ctx = null;
	CycleTradeForbiddenMemberService cycleTradeForbiddenMemberService = null;

	@Before
	public void setUp() {
		try {
			ctx = new ClassPathXmlApplicationContext("classpath*:/test.xml");
			cycleTradeForbiddenMemberService = (CycleTradeForbiddenMemberService) ctx
					.getBean("cycleTradeForbiddenMemberService");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Test
	public void insertTest() {
		CycleTradeForbiddenMemberDTO dto = new CycleTradeForbiddenMemberDTO();
		dto.setMemberCode("htd0008");
		dto.setMemberName("xj");
		dto.setForbiddenType("1");
		dto.setCreateId("222");
		dto.setCreateName("sa2");
		ExecuteResult<String> result = cycleTradeForbiddenMemberService.insertCycleTradeForbiddenMember(dto);
		System.out.println(JSONObject.toJSONString(result));
	}

	@Test
	public void selectTest() {
		CycleTradeForbiddenMemberDTO dto = new CycleTradeForbiddenMemberDTO();
		dto.setMemberCode("htd0003");
		ExecuteResult<Boolean> result = cycleTradeForbiddenMemberService.isCycleTradeForbiddenMember(dto);
		System.out.println(JSONObject.toJSONString(result));
	}

	@Test
	public void deleteTest() {
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		ids.add("2");
		CycleTradeForbiddenMemberDTO dto = new CycleTradeForbiddenMemberDTO();
		dto.setIds(ids);
		dto.setModifyId("888");
		dto.setModifyName("sasa");
		ExecuteResult<String> result = cycleTradeForbiddenMemberService.deleteCycleTradeForbiddenMember(dto);
		System.out.println(JSONObject.toJSONString(result));
	}

	@Test
	public void selectPageTest() {
		CycleTradeForbiddenMemberDTO dto = new CycleTradeForbiddenMemberDTO();
		dto.setMemberName("z");
		Pager<CycleTradeForbiddenMemberDTO> pager = new Pager<CycleTradeForbiddenMemberDTO>();
		ExecuteResult<DataGrid<CycleTradeForbiddenMemberDTO>> result = cycleTradeForbiddenMemberService
				.selectCycleTradeForbiddenMemberList(dto, pager);
		System.out.println(JSONObject.toJSONString(result));
	}
}
