package org.reed.core.user.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UserResultEntity implements Serializable {

    private static final long serialVersionUID = -3123782983219342L;

    private long total;
    private List<Map<String, Object>> users;
    private List<Map<String, String>> mappings;
    private Double version = 1.0;

    public UserResultEntity() {
    }

    public UserResultEntity(long total, List<Map<String, Object>> users, List<Map<String, String>> mappings, Double version) {
        this.total = total;
        this.users = users;
        this.mappings = mappings;
        this.version = version;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Map<String, Object>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<String, Object>> users) {
        this.users = users;
    }

    public List<Map<String, String>> getMappings() {
        return mappings;
    }

    public void setMappings(List<Map<String, String>> mappings) {
        this.mappings = mappings;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }
}
