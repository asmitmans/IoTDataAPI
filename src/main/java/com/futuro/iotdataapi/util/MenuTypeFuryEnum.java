package com.futuro.iotdataapi.util;

public enum MenuTypeFuryEnum {
	/**
	 * {@code VENTA}
	 */
	SUBHEADING(1, "subheading"),
	/**
	 * {@code RECHAZO}
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
