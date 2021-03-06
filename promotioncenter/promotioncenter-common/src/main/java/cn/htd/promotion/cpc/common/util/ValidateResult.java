package cn.htd.promotion.cpc.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.github.pagehelper.StringUtil;

public class ValidateResult {

	// 校验结果是否有错
	private boolean hasErrors = false;

	// 校验错误信息
	private Map<String, String> errorMsg = new HashMap<String, String>();
	
	/*
	 * add by 张丁 开始
	 */
	private Boolean isPass=true;
	
	/**
	 * 返回码
	 */
	private String reponseMsg = null;
	
    /*
     * add by 张丁 结束
     */

	public boolean isHasErrors() {
		return hasErrors;
	}

	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public String getErrorMsg() {
		String errorKey = "";
		String errorValue = "";
		StringBuffer errorMessage = new StringBuffer();
		if (this.hasErrors) {
			Iterator<String> it = this.errorMsg.keySet().iterator();
			while (it.hasNext()) {
				errorKey = it.next();
				errorValue = this.errorMsg.get(errorKey);
				errorMessage.append("," + errorKey + ":" + errorValue + "\r\n");
			}
		}
		return StringUtil.isEmpty(errorMessage.toString()) ? "" : errorMessage.toString().substring(1);
	}

	public void addErrorMsg(String property, String errorMsg) {
		if (this.errorMsg == null) {
			this.errorMsg = new HashMap<String, String>();
		}
		this.errorMsg.put(property, errorMsg);
	}

	public void setErrorMsg(Map<String, String> errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Boolean isPass() {
		return isPass;
	}
	public void setIsPass(Boolean isPass) {
		this.isPass = isPass;
	}

	public String getReponseMsg() {
		return reponseMsg;
	}

	public void setReponseMsg(String reponseMsg) {
		this.reponseMsg = reponseMsg;
	}

}