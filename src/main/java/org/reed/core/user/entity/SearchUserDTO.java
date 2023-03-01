package org.reed.core.user.entity;

import java.util.Map;

public class SearchUserDTO {
    private OrgUser user;
    private Map<String, Object> extAttrMap;

    public SearchUserDTO() {
    }

    public OrgUser getUser() {
        return user;
    }

    public void setUser(OrgUser user) {
        this.user = user;
    }

    public Map<String, Object> getExtAttrMap() {
        return this.extAttrMap;
    }

    public void setExtAttrMap(Map<String, Object> extAttrMap) {
        this.extAttrMap = extAttrMap;
    }
}