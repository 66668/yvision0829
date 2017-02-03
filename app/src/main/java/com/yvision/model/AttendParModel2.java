package com.yvision.model;

import java.io.Serializable;

/**
 *地图考勤 实体类
 * Created by JackSong on 2016/9/12.
 */
public class AttendParModel2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CompanyID;//公司ID
    private String DepartmentID;//部门ID
    private String EmployeeID;//员工ID
    private String EmployeeName;//员工名称
    private String WrokId;//员工编号
    private String Longitude;//精度
    private String Latitude;//纬度
    private String CapTime;//考勤时间

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public void setDepartmentID(String departmentID) {
        DepartmentID = departmentID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public void setWrokId(String wrokId) {
        WrokId = wrokId;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setCapTime(String capTime) {
        CapTime = capTime;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public String getDepartmentID() {
        return DepartmentID;
    }

    public String getEmployeeID() {
        return EmployeeID;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public String getWrokId() {
        return WrokId;
    }

    public String getLongitude() {
        return Longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getCapTime() {
        return CapTime;
    }
}
