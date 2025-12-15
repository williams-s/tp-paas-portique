package paas.tp.entrance_cockpit_backend.converter;

import jakarta.persistence.AttributeConverter;

import jakarta.persistence.Converter;
import paas.tp.entrance_cockpit_backend.models.Profile;

@Converter(autoApply = true)
public class ProfileConverter implements AttributeConverter<Profile, String> {

    @Override
    public String convertToDatabaseColumn(Profile profile) {
        if (profile == null) {
            return null;
        }
        return profile.getDbValue();
    }

    @Override
    public Profile convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Profile.fromDbValue(dbData);
    }
}