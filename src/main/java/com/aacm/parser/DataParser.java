package com.aacm.parser;

import com.aacm.model.HostRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser class - Reads input data from CSV or JSON files and produces HostRecords.
 */
public class DataParser {

    /**
     * Parse a CSV file containing network scan data.
     * Expected columns: HostID,IP,Hostname,Port,Service,Version,VulnID,Severity,
     *                   ExploitabilityScore,AssetCriticality,Reachability
     */
    public static List<HostRecord> parseCSV(String filename) {
        List<HostRecord> records = new ArrayList<>();

        try (FileReader reader = new FileReader(filename);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            for (CSVRecord record : csvParser) {
                try {
                    HostRecord hostRecord = new HostRecord();
                    hostRecord.setHostId(record.get("HostID"));
                    hostRecord.setIpAddress(record.get("IP"));
                    hostRecord.setHostname(record.get("Hostname"));
                    hostRecord.setPort(Integer.parseInt(record.get("Port")));
                    hostRecord.setService(record.get("Service"));
                    hostRecord.setVersion(record.get("Version"));
                    hostRecord.setVulnId(record.get("VulnID"));
                    hostRecord.setSeverity(record.get("Severity"));
                    hostRecord.setExploitabilityScore(Double.parseDouble(record.get("ExploitabilityScore")));
                    hostRecord.setAssetCriticality(record.get("AssetCriticality"));

                    // Parse reachability: comma-separated "Source->Dest" pairs
                    String reachabilityStr = record.get("Reachability");
                    if (reachabilityStr != null && !reachabilityStr.trim().isEmpty()) {
                        String[] pairs = reachabilityStr.split(",");
                        for (String pair : pairs) {
                            String trimmed = pair.trim();
                            if (!trimmed.isEmpty()) {
                                hostRecord.addReachability(trimmed);
                            }
                        }
                    }

                    records.add(hostRecord);
                } catch (NumberFormatException | NullPointerException e) {
                    System.err.println("[WARNING] Error parsing CSV record: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Could not open CSV file: " + filename);
            e.printStackTrace();
        }

        System.out.println("[INFO] Parsed " + records.size() + " records from CSV: " + filename);
        return records;
    }

    /**
     * Parse a JSON file containing network scan data using GSON.
     */
    public static List<HostRecord> parseJSON(String filename) {
        List<HostRecord> records = new ArrayList<>();
        
        try {
            String jsonContent = new String(java.nio.file.Files.readAllBytes(
                    java.nio.file.Paths.get(filename)));
            
            com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
            com.google.gson.JsonElement element = jsonParser.parse(jsonContent);
            
            if (!element.isJsonObject()) {
                System.err.println("[ERROR] JSON must be an object");
                return records;
            }
            
            com.google.gson.JsonObject jsonObject = element.getAsJsonObject();
            
            if (!jsonObject.has("hosts") || !jsonObject.get("hosts").isJsonArray()) {
                System.err.println("[ERROR] JSON must contain a 'hosts' array.");
                return records;
            }
            
            com.google.gson.JsonArray hostsArray = jsonObject.getAsJsonArray("hosts");
            
            for (com.google.gson.JsonElement hostElement : hostsArray) {
                if (!hostElement.isJsonObject()) continue;
                
                com.google.gson.JsonObject hostJson = hostElement.getAsJsonObject();
                String hostId = hostJson.has("host_id") ? hostJson.get("host_id").getAsString() : "";
                String ip = hostJson.has("ip") ? hostJson.get("ip").getAsString() : "";
                String hostname = hostJson.has("hostname") ? hostJson.get("hostname").getAsString() : "";
                String assetCriticality = hostJson.has("asset_criticality") ? 
                        hostJson.get("asset_criticality").getAsString() : "Medium";
                
                // Parse reachability
                List<String> reachability = new ArrayList<>();
                if (hostJson.has("reachability") && hostJson.get("reachability").isJsonArray()) {
                    for (com.google.gson.JsonElement r : hostJson.getAsJsonArray("reachability")) {
                        reachability.add(r.getAsString());
                    }
                }
                
                // Parse services
                if (!hostJson.has("services") || !hostJson.get("services").isJsonArray()) {
                    continue;
                }
                
                com.google.gson.JsonArray servicesArray = hostJson.getAsJsonArray("services");
                for (com.google.gson.JsonElement serviceElement : servicesArray) {
                    if (!serviceElement.isJsonObject()) continue;
                    
                    com.google.gson.JsonObject svcJson = serviceElement.getAsJsonObject();
                    int port = svcJson.has("port") ? svcJson.get("port").getAsInt() : 0;
                    String service = svcJson.has("service") ? svcJson.get("service").getAsString() : "";
                    String version = svcJson.has("version") ? svcJson.get("version").getAsString() : "";
                    
                    if (svcJson.has("vulnerabilities") && svcJson.get("vulnerabilities").isJsonArray()) {
                        com.google.gson.JsonArray vulnsArray = svcJson.getAsJsonArray("vulnerabilities");
                        for (com.google.gson.JsonElement vulnElement : vulnsArray) {
                            if (!vulnElement.isJsonObject()) continue;
                            
                            com.google.gson.JsonObject vulnJson = vulnElement.getAsJsonObject();
                            HostRecord record = new HostRecord();
                            record.setHostId(hostId);
                            record.setIpAddress(ip);
                            record.setHostname(hostname);
                            record.setPort(port);
                            record.setService(service);
                            record.setVersion(version);
                            record.setVulnId(vulnJson.has("vuln_id") ? vulnJson.get("vuln_id").getAsString() : "VULN-000");
                            record.setSeverity(vulnJson.has("severity") ? vulnJson.get("severity").getAsString() : "Low");
                            record.setExploitabilityScore(vulnJson.has("exploitability_score") ? 
                                    vulnJson.get("exploitability_score").getAsDouble() : 0.0);
                            record.setAssetCriticality(assetCriticality);
                            record.setReachability(new ArrayList<>(reachability));
                            records.add(record);
                        }
                    } else {
                        // Service with no vulnerabilities
                        HostRecord record = new HostRecord();
                        record.setHostId(hostId);
                        record.setIpAddress(ip);
                        record.setHostname(hostname);
                        record.setPort(port);
                        record.setService(service);
                        record.setVersion(version);
                        record.setVulnId("NONE");
                        record.setSeverity("None");
                        record.setExploitabilityScore(0.0);
                        record.setAssetCriticality(assetCriticality);
                        record.setReachability(new ArrayList<>(reachability));
                        records.add(record);
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Could not open JSON file: " + filename);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] JSON parse exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("[INFO] Parsed " + records.size() + " records from JSON: " + filename);
        return records;
    }
}
