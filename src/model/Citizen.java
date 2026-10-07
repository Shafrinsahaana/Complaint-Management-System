package model;

public class Citizen {
    private int citizenId;
    private String name;
    private String email;
    private String password;

    public Citizen() {}
    public Citizen(int citizenId, String name, String email, String password) {
        this.citizenId = citizenId;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public int getCitizenId() { return citizenId; }
    public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
