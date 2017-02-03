package com.yvision.model;

import java.io.Serializable;

/**
 * 添加一条wifi考勤 实体类
 *
 * Created by JackSong on 2016/9/12.
 */
public class AttendParModel3 extends AttendParModel3Builder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CompanyID;//公司ID
    private String DepartmentID;//部门ID
    private String EmployeeID;//员工ID
    private String EmployeeName;//员工名称
    private String WrokId;//员工编号
    private String CapTime;//考勤时间

    //单例模式
    private AttendParModel3(){}
    private static class AttendParModel3Container{
        private static AttendParModel3 instance = new AttendParModel3();
    }
    public static AttendParModel3 getInstance(){
        return AttendParModel3Container.instance;
    }


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

    public void setCapTime(String capTime) {
        CapTime = capTime;
    }

    @Override
    public AttendParModel3Builder buildCompanyID(String CompanyID) {
        AttendParModel3.getInstance().setCompanyID(CompanyID);
        return this;
    }

    @Override
    public AttendParModel3Builder buildDepartmentID(String DepartmentID) {
        AttendParModel3.getInstance().setDepartmentID(DepartmentID);
        return this;
    }

    @Override
    public AttendParModel3Builder buildEmployeeID(String EmployeeID) {
        AttendParModel3.getInstance().setEmployeeID(EmployeeID);
        return this;
    }

    @Override
    public AttendParModel3Builder buildEmployeeName(String EmployeeName) {
        AttendParModel3.getInstance().setEmployeeName(EmployeeName);
        return this;
    }

    @Override
    public AttendParModel3Builder buildWrokId(String WrokId) {
        AttendParModel3.getInstance().setWrokId(WrokId);
        return this;
    }

    @Override
    public AttendParModel3Builder buildCapTime(String CapTime) {
        AttendParModel3.getInstance().setCapTime(CapTime);
        return this;
    }

    @Override
    public AttendParModel3 create() {
        return AttendParModel3.getInstance();
    }
}
