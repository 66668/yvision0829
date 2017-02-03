package com.yvision.model;

import java.io.Serializable;

/**
 *  升级信息
 * 
 */
public class UpgradeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isexistsnewversion;

    private String Version;

    private String Message;

    private String PackageUrl;

    public boolean isIsexistsnewversion() {
        return isexistsnewversion;
    }

    public void setIsexistsnewversion(boolean isexistsnewversion) {
        this.isexistsnewversion = isexistsnewversion;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getPackageUrl() {
        return PackageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        PackageUrl = packageUrl;
    }
}
