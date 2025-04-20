package com.futuro.iotdataapi.exception;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ExceptionResponse {

	private Date date;
	private String message;
	private String error;
	private int status;

}
