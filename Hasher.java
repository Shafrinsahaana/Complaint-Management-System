import util.PasswordUtil;

public class Hasher {
    public static void main(String[] args) {
        String[] passwords = {"Pass@1234", "Secure#567", "Citizen!999", "Officer@4321", "Admin#8765"};
        for (String p : passwords) {
            System.out.println(p + " -> " + PasswordUtil.hashPassword(p));
        }
    }
}
