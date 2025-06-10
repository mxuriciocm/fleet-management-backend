package com.example.fleetmanagement.issues.interfaces.rest;

import com.example.fleetmanagement.iam.interfaces.acl.IamContextFacade;

import com.example.fleetmanagement.issues.domain.model.queries.GetIssueByIdQuery;
import com.example.fleetmanagement.issues.domain.model.queries.GetIssuesByCarrierIdQuery;
import com.example.fleetmanagement.issues.domain.model.queries.GetIssuesByManagerIdQuery;
import com.example.fleetmanagement.issues.domain.model.queries.GetIssuesByTypeQuery;
import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;
import com.example.fleetmanagement.issues.domain.services.IssueCommandService;
import com.example.fleetmanagement.issues.domain.services.IssueQueryService;
import com.example.fleetmanagement.issues.interfaces.rest.resources.CreateIssueResource;
import com.example.fleetmanagement.issues.interfaces.rest.resources.IssueResource;
import com.example.fleetmanagement.issues.interfaces.rest.transform.CreateIssueCommandFromResourceAssembler;
import com.example.fleetmanagement.issues.interfaces.rest.transform.IssueResourceFromEntityAssembler;
import com.example.fleetmanagement.vehicles.interfaces.acl.VehicleContextFacade;
import com.example.fleetmanagement.vehicles.interfaces.acl.dto.VehicleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/issues", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Issues", description = "Issues Management Endpoints")
public class IssuesController {
    private final IssueCommandService issueCommandService;
    private final IssueQueryService issueQueryService;
    private final IamContextFacade iamContextFacade;
    private final VehicleContextFacade vehicleContextFacade;

    public IssuesController(IssueCommandService issueCommandService, IssueQueryService issueQueryService, IamContextFacade iamContextFacade, VehicleContextFacade vehicleContextFacade) {
        this.issueCommandService = issueCommandService;
        this.issueQueryService = issueQueryService;
        this.iamContextFacade = iamContextFacade;
        this.vehicleContextFacade = vehicleContextFacade;
    }

    /**
     * Create a new issue.
     * @param resource the resource containing the issue details
     * @return ResponseEntity with the created issue resource
     */
    @PostMapping
    @Operation(summary = "Create a new issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Issue created successfully"),
            @ApiResponse(responseCode = "400", description = "User not authenticated or not a carrier")
    })
    public ResponseEntity<IssueResource> createIssue(@Valid @RequestBody CreateIssueResource resource){
        Long carrierId = getCurrentUserId();
        if (carrierId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        var user = iamContextFacade.fetchUserById(carrierId);
        if (user.isEmpty() || !user.get().roles().contains("ROLE_CARRIER")) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        Long managerId = getManagerIdForCarrier(carrierId);
        var command = CreateIssueCommandFromResourceAssembler.toCommandFromResource(resource, carrierId, managerId);
        var issue = issueCommandService.handle(command);
        var issueResource = IssueResourceFromEntityAssembler.toResourceFromEntity(issue);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueResource);
    }

    /**
     * Get an issue by its ID.
     * @param issueId the ID of the issue to retrieve
     * @return ResponseEntity with the issue resource if found, or appropriate error status
     */
    @GetMapping("/{issueId}")
    @Operation(summary = "Get an issue by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Issue not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access this issue")
    })
    public ResponseEntity<IssueResource> getIssueById(@PathVariable Long issueId) {
        var issueOptional = issueQueryService.handle(new GetIssueByIdQuery(issueId));
        if (issueOptional.isEmpty()) { return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }
        var issue = issueOptional.get();
        if (!canAccessIssue(issue.getCarrierId(), issue.getManagerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(IssueResourceFromEntityAssembler.toResourceFromEntity(issue));
    }

    /**
     * Get all issues created by the authenticated carrier.
     * @return ResponseEntity with a list of issue resources if found, or no content status
     */
    @GetMapping("/carrier/issues")
    @Operation(summary = "Get all issues created by the authenticated carrier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access these issues")
    })
    public ResponseEntity<List<IssueResource>> getAllIssues() {
        Long carrierId = getCurrentUserId();
        if (carrierId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        var user = iamContextFacade.fetchUserById(carrierId);
        if (user.isEmpty() || !user.get().roles().contains("ROLE_CARRIER")) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        var issues = issueQueryService.handle(new GetIssuesByCarrierIdQuery(carrierId));
        if (issues.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = IssueResourceFromEntityAssembler.toResourceFromEntities(issues);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get all issues created by a specific carrier.
     * @param carrierId the ID of the carrier whose issues to retrieve
     * @return ResponseEntity with a list of issue resources if found, or appropriate error status
     */
    @GetMapping("/carrier/{carrierId}")
    @Operation(summary = "Get all issues created by a specific carrier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues found successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access these issues"),
            @ApiResponse(responseCode = "404", description = "No issues found for the specified carrier")
    })
    public ResponseEntity<List<IssueResource>> getIssuesByCarrierId(@PathVariable Long carrierId) {
        if (!canAccessIssuesByCarrier(carrierId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var issues = issueQueryService.handle(new GetIssuesByCarrierIdQuery(carrierId));
        if (issues.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = IssueResourceFromEntityAssembler.toResourceFromEntities(issues);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get all issues created by a specific manager.
     * @param managerId the ID of the manager whose issues to retrieve
     * @return ResponseEntity with a list of issue resources if found, or appropriate error status
     */
    @GetMapping("/manager/{managerId}")
    @Operation(summary = "Get all issues created by a specific manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues found successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access these issues"),
            @ApiResponse(responseCode = "404", description = "No issues found for the specified manager")
    })
    public ResponseEntity<List<IssueResource>> getIssuesByManagerId(@PathVariable Long managerId) {
        if (!canAccessIssuesByManager(managerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var issues = issueQueryService.handle(new GetIssuesByManagerIdQuery(managerId));
        if (issues.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = IssueResourceFromEntityAssembler.toResourceFromEntities(issues);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get all issues by type.
     * @param type the type of issues to retrieve
     * @return ResponseEntity with a list of issue resources if found, or appropriate error status
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Get all issues by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues found successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access these issues"),
            @ApiResponse(responseCode = "404", description = "No issues found for the specified type")
    })
    public ResponseEntity<List<IssueResource>> getIssuesByType(@PathVariable IssueType type) {
        Long userId = getCurrentUserId();
        if (userId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        var issues = issueQueryService.handle(new GetIssuesByTypeQuery(type));
        var filteredIssues = issues.stream()
                .filter(issue -> canAccessIssue(issue.getCarrierId(), issue.getManagerId()))
                .toList();
        if (filteredIssues.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = IssueResourceFromEntityAssembler.toResourceFromEntities(filteredIssues);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get the ID of the current authenticated user.
     * @return the user ID or 0 if isn't authenticated
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { return 0L; }
        String email = authentication.getName();
        return iamContextFacade.fetchUserIdByEmail(email);
    }

    /**
     * Check if a user has a specific role.
     * @param userId the ID of the user to check
     * @param role the role to check for
     * @return true if the user has the role, false otherwise
     */
    private boolean hasRole(Long userId, String role) {
        var user = iamContextFacade.fetchUserById(userId);
        return user.isPresent() && user.get().roles().contains(role);
    }

    /**
     * Check if the current user can access an issue.
     *
     * @param issueCarrierId the ID of the carrier who created the issue
     * @param issueManagerId the ID of the manager associated with the issue
     * @return true if the current user can access the issue, false otherwise
     */
    private boolean canAccessIssue(Long issueCarrierId, Long issueManagerId) {
        Long userId = getCurrentUserId();
        if (userId == 0) { return false; }
        if (hasRole(userId, "ROLE_ADMIN")) { return true; }
        if (hasRole(userId, "ROLE_MANAGER") && issueManagerId != null && issueManagerId.equals(userId)) { return true; }
        return hasRole(userId, "ROLE_CARRIER") && issueCarrierId.equals(userId);
    }

    private boolean canAccessIssuesByCarrier(Long carrierId){
        Long userId = getCurrentUserId();
        if (userId == 0) { return false; }
        if (hasRole(userId, "ROLE_ADMIN")) { return true; }
        if (hasRole(userId, "ROLE_MANAGER")) {
            Long managerIdOfCarrier = getManagerIdForCarrier(carrierId);
            return managerIdOfCarrier != 0L && managerIdOfCarrier.equals(userId);
        }
        return hasRole(userId, "ROLE_CARRIER") && carrierId.equals(userId);
    }

    private boolean canAccessIssuesByManager(Long managerId) {
        Long userId = getCurrentUserId();
        if (userId == 0) { return false; }
        if (hasRole(userId, "ROLE_ADMIN")) { return true; }
        return hasRole(userId, "ROLE_MANAGER") && managerId.equals(userId);
    }

    private Long getManagerIdForCarrier(Long carrierId) {
        Optional<VehicleDto> vehicle = vehicleContextFacade.fetchVehicleByCarrierId(carrierId);
        if (vehicle.isPresent()) {
            return vehicle.get().managerId();
        }
        throw new IllegalArgumentException("No manager found for carrier with ID: " + carrierId);
    }
}
