package com.futuro.iotdataapi.util;

public enum MenuTypeFuryEnum {
	/**
	 * {@code subheading}
	 */
	SUBHEADING(1, "subheading"),
	/**
	 * {@code Item}
	 */
	ITEM(2, "item");

	private Integer id;
	private String value;

	MenuTypeFuryEnum(Integer id, String value) {
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return this.id;
	}

	public String getValue() {
		return value;
	}
}
