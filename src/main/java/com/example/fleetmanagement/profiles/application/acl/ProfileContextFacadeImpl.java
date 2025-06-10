package com.example.fleetmanagement.profiles.application.acl;

import com.example.fleetmanagement.profiles.domain.model.commands.CreateProfileCommand;
import com.example.fleetmanagement.profiles.domain.model.queries.GetProfileByIdQuery;
import com.example.fleetmanagement.profiles.domain.services.ProfileCommandService;
import com.example.fleetmanagement.profiles.domain.services.ProfileQueryService;
import com.example.fleetmanagement.profiles.interfaces.acl.ProfileContextFacade;
import com.example.fleetmanagement.profiles.interfaces.acl.dto.ProfileDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Profile Context Facade Implementation
 */
@Service
public class ProfileContextFacadeImpl implements ProfileContextFacade {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfileContextFacadeImpl(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @Override
    public Long createProfile(Long userId, String firstName, String lastName, String phoneNumber) {
        var createProfileCommand = new CreateProfileCommand(userId, firstName, lastName, phoneNumber);
        var profile = profileCommandService.handle(createProfileCommand);
        return profile.isEmpty() ? 0L : profile.get().getId();
    }

    @Override
    public Optional<ProfileDto> fetchProfileByUserId(Long userId) {
        var query = new GetProfileByIdQuery(userId);
        var profile = profileQueryService.handle(query);

        return profile.map(p -> new ProfileDto(
            p.getUserId(),
            p.getFullName(),
            p.getPhoneNumber()
        ));
    }
}
