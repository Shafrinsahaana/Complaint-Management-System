package util;

import model.Citizen;
import model.Official;

public class Session {
    private static Citizen loggedInCitizen;
    private static Official loggedInOfficial;

    public static void setCitizen(Citizen citizen) {
        loggedInCitizen = citizen;
        loggedInOfficial = null;
    }

    public static void setOfficial(Official official) {
        loggedInOfficial = official;
        loggedInCitizen = null;
    }

    public static Citizen getCitizen() { return loggedInCitizen; }
    public static Official getOfficial() { return loggedInOfficial; }

    public static void logout() {
        loggedInCitizen = null;
        loggedInOfficial = null;
    }
}
