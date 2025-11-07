package fr.upec.episen.paas.cacheloader.converter;

import fr.upec.episen.paas.cacheloader.model.Profile;
import jakarta.persistence.AttributeConverter;

import jakarta.persistence.Converter;

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