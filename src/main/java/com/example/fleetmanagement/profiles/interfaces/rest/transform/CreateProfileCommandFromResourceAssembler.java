package com.example.fleetmanagement.profiles.interfaces.rest.transform;

import com.example.fleetmanagement.profiles.domain.model.commands.CreateProfileCommand;
import com.example.fleetmanagement.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(resource.userId(), resource.firstName(), resource.lastName(), resource.phoneNumber());
    }
}
