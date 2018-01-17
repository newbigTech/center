package com.bjucloud.contentcenter.enums;

/**
 * 弹屏广告终端定义
 */
public enum PopupAdTerminalTypeEnums {
	HZG("hzg", "汇掌柜"),
	SUPER_BOSS_APP("bossapp", "超级老板APP"),
	SUPER_BOSS_PC("bosspc", "超级老板PC"),
	SUPER_MANAGER("manager", "超级经理人"),
	B2B_MALL("mallpc", "汇通达商城PC");

	private String code;
	private String typeDesc;

	PopupAdTerminalTypeEnums(String code, String typeDesc) {
		this.code = code;
		this.typeDesc = typeDesc;
	}

	public String getCode() {
		return code;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	// 获取枚举value对应名字方法
	public static String getTypeDesc(String code) {
		for (PopupAdTerminalTypeEnums enumObj : PopupAdTerminalTypeEnums.values()) {
			if (code.equals(enumObj.getCode())) {
				return enumObj.typeDesc;
			}
		}
		return "";
	}
}