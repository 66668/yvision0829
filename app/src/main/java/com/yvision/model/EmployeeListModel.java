package com.yvision.model;

import java.io.Serializable;

/**
 * 受访人信息
 * @author JackSong
 *
 */
public class EmployeeListModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String EmployeeId;
	private String WrokId;
	private String EmployeeName;
	public String getEmployeeId() {
		return EmployeeId;
	}
	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}
	public String getWrokId() {
		return WrokId;
	}
	public void setWrokId(String wrokId) {
		WrokId = wrokId;
	}
	public String getEmployeeName() {
		return EmployeeName;
	}
	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}
	
}
