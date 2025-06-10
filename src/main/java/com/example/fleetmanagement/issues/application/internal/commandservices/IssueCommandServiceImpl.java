package com.example.fleetmanagement.issues.application.internal.commandservices;

import com.example.fleetmanagement.issues.domain.model.aggregates.Issue;
import com.example.fleetmanagement.issues.domain.model.commands.CreateIssueCommand;
import com.example.fleetmanagement.issues.domain.model.commands.UpdateIssueCommand;
import com.example.fleetmanagement.issues.domain.services.IssueCommandService;
import com.example.fleetmanagement.issues.infrastructure.persistence.jpa.repositories.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IssueCommandServiceImpl implements IssueCommandService {
    private final IssueRepository issueRepository;

    public IssueCommandServiceImpl(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Override
    public Issue handle(CreateIssueCommand command) {
        var report = new Issue(command);
        return issueRepository.save(report);
    }

    @Override
    public Optional<Issue> handle(Long reportId, UpdateIssueCommand command) {
        return issueRepository.findById(reportId)
                .map(report -> {
                    if (command.title() != null) { report.updateTitle(command.title()); }
                    if (command.content() != null) { report.updateContent(command.content()); }
                    if (command.type() != null) { report.updateType(command.type()); }
                    return issueRepository.save(report);
                });
    }

    @Override
    public boolean deleteIssue(Long reportId){
        if (!issueRepository.existsById(reportId)) { return false; }
        try {
            issueRepository.deleteById(reportId);
            return true;
        } catch (Exception e) {
            // Log the exception if necessary
            return false;
        }
    }

}
