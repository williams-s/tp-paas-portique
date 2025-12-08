package paas.tp.entrance_cockpit_backend.models;

public enum Profile {
    ETUDIANT("etudiant"),
    ENSEIGNANT("enseignant"),
    PERSONNEL("personnel"),
    INVITE("invite"),
    EXTERNE("externe");

    private final String dbValue;

    Profile(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Profile fromDbValue(String dbValue) {
        for (Profile profile : Profile.values()) {
            if (profile.getDbValue().equalsIgnoreCase(dbValue)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Unknown Profile DB value: " + dbValue);
    }
}
