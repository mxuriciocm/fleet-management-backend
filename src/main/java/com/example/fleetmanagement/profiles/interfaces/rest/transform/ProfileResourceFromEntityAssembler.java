package com.example.fleetmanagement.profiles.interfaces.rest.transform;

import com.example.fleetmanagement.profiles.domain.model.aggregates.Profile;
import com.example.fleetmanagement.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile entity) {
        return new ProfileResource(entity.getUserId(), entity.getFullName(), entity.getPhoneNumber());
    }
}
