package memory_spel;

public class Speler {
    private int spelerId;
    private String username;
    private String passwordHash;
    private int globalScore;


    public Speler(String username){
        this.username = username;
        globalScore = 0;
        spelerId = -1;
    }

    public int getSpelerId() {
        return spelerId;
    }

    public void setSpelerId(int spelerId) {
        this.spelerId = spelerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(int globalScore) {
        this.globalScore = globalScore;
    }

    public void increaseGlobalScore(int increment) {
        globalScore += increment;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
