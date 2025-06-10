package com.example.fleetmanagement.profiles.interfaces.acl;

import com.example.fleetmanagement.profiles.interfaces.acl.dto.ProfileDto;
import java.util.Optional;

/**
 * UserProfileContextFacade
 */
public interface ProfileContextFacade {
    Long createProfile(Long userId, String firstName, String lastName, String phoneNumber);

    Optional<ProfileDto> fetchProfileByUserId(Long userId);
}
