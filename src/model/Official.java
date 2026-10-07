package model;

public class Official {
    private int officialId;
    private String name;
    private String email;
    private String password;

    public Official() {}
    public Official(int officialId, String name, String email, String password) {
        this.officialId = officialId;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public int getOfficialId() { return officialId; }
    public void setOfficialId(int officialId) { this.officialId = officialId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
