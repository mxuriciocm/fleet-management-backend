package com.example.fleetmanagement.profiles.interfaces.rest.resources;

/**
 * Resource for updating a user's profile.
 * This record holds the fields that can be updated in a user's profile.
 */
public record UpdateProfileResource(
    String firstName,
    String lastName,
    String phoneNumber
) {}
