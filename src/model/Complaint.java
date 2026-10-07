package model;

import java.util.Date;

public class Complaint {
    private int complaintId;
    private int citizenId;
    private Integer officialId;
    private String category;
    private String description;
    private Date complaintDate;
    private String status;
    private String officialRemarks;
    private Date resolvedDate;

    public Complaint() {}
    
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public int getCitizenId() { return citizenId; }
    public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
    public Integer getOfficialId() { return officialId; }
    public void setOfficialId(Integer officialId) { this.officialId = officialId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getComplaintDate() { return complaintDate; }
    public void setComplaintDate(Date complaintDate) { this.complaintDate = complaintDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOfficialRemarks() { return officialRemarks; }
    public void setOfficialRemarks(String officialRemarks) { this.officialRemarks = officialRemarks; }
    public Date getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(Date resolvedDate) { this.resolvedDate = resolvedDate; }
}
