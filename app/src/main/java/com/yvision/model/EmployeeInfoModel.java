package com.yvision.model;

import java.io.Serializable;

/**
 * 根据employeeId查看登录人信息 实体类
 * Created by JackSong on 2016/9/12.
 */
public class EmployeeInfoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String EmployeeID;//员工ID
    private String StoreID;//公司ID
    private String DepartmentID;//部门ID
    private String WrokId;//员工工号
    private String EmployeeName;//员工姓名
    private String EmployeeGender;//员工性别

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public void setStoreID(String storeID) {
        StoreID = storeID;
    }

    public void setDepartmentID(String departmentID) {
        DepartmentID = departmentID;
    }

    public void setWrokId(String wrokId) {
        WrokId = wrokId;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public void setEmployeeGender(String employeeGender) {
        EmployeeGender = employeeGender;
    }

    public String getEmployeeID() {

        return EmployeeID;
    }

    public String getStoreID() {
        return StoreID;
    }

    public String getDepartmentID() {
        return DepartmentID;
    }

    public String getWrokId() {
        return WrokId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public String getEmployeeGender() {
        return EmployeeGender;
    }
}
