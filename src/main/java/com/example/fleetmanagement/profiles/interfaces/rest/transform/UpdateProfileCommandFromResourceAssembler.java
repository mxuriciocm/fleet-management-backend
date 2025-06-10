package com.example.fleetmanagement.profiles.interfaces.rest.transform;

import com.example.fleetmanagement.profiles.domain.model.commands.UpdateProfileCommand;
import com.example.fleetmanagement.profiles.interfaces.rest.resources.UpdateProfileResource;

public class UpdateProfileCommandFromResourceAssembler {

    public static UpdateProfileCommand toCommandFromResource(UpdateProfileResource resource, Long userId) {
        return new UpdateProfileCommand(userId, resource.firstName(), resource.lastName(), resource.phoneNumber());
    }
}
