package com.aacm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * HostRecord class - Represents a record from network scan data.
 */
public class HostRecord {
    private String hostId;
    private String ipAddress;
    private String hostname;
    private int port;
    private String service;
    private String version;
    private String vulnId;
    private String severity;
    private double exploitabilityScore;
    private String assetCriticality;
    private List<String> reachability;

    public HostRecord() {
        this.reachability = new ArrayList<>();
    }

    // Getters and Setters
    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVulnId() {
        return vulnId;
    }

    public void setVulnId(String vulnId) {
        this.vulnId = vulnId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public double getExploitabilityScore() {
        return exploitabilityScore;
    }

    public void setExploitabilityScore(double exploitabilityScore) {
        this.exploitabilityScore = exploitabilityScore;
    }

    public String getAssetCriticality() {
        return assetCriticality;
    }

    public void setAssetCriticality(String assetCriticality) {
        this.assetCriticality = assetCriticality;
    }

    public List<String> getReachability() {
        return reachability;
    }

    public void setReachability(List<String> reachability) {
        this.reachability = reachability;
    }

    public void addReachability(String reach) {
        reachability.add(reach);
    }

    @Override
    public String toString() {
        return "HostRecord{" +
                "hostId='" + hostId + '\'' +
                ", ip='" + ipAddress + '\'' +
                ", service='" + service + '\'' +
                ", port=" + port +
                '}';
    }
}
