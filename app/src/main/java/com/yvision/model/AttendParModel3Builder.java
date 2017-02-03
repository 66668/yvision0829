package com.yvision.model;

/**
 * Created by JackSong on 2016/9/16.
 */
public abstract class AttendParModel3Builder {
    public abstract AttendParModel3Builder buildCompanyID(String CompanyID);
    public abstract AttendParModel3Builder buildDepartmentID(String DepartmentID);
    public abstract AttendParModel3Builder buildEmployeeID(String EmployeeID);
    public abstract AttendParModel3Builder buildEmployeeName(String EmployeeName);
    public abstract AttendParModel3Builder buildWrokId(String WrokId);
    public abstract AttendParModel3Builder buildCapTime(String CapTime);
    public abstract AttendParModel3 create();
}
