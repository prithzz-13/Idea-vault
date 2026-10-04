package com.ideavault.controller;

import com.ideavault.dto.JoinRequestBody;
import com.ideavault.dto.StatusRequest;
import com.ideavault.entity.Idea;
import com.ideavault.entity.JoinRequest;
import com.ideavault.entity.User;
import com.ideavault.repository.IdeaRepository;
import com.ideavault.repository.JoinRequestRepository;
import com.ideavault.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinRequestController {

    private final JoinRequestRepository joinRequestRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    public JoinRequestController(JoinRequestRepository joinRequestRepository,
                                 IdeaRepository ideaRepository,
                                 UserRepository userRepository) {
        this.joinRequestRepository = joinRequestRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
    }

    // POST /api/ideas/5/requests   (a student asks to join idea 5)
    @PostMapping("/api/ideas/{ideaId}/requests")
    public ResponseEntity<?> send(@PathVariable Long ideaId, @Valid @RequestBody JoinRequestBody body) {
        Idea idea = ideaRepository.findById(ideaId).orElse(null);
        if (idea == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Idea not found"));
        }
        User requester = userRepository.findById(body.requesterId()).orElse(null);
        if (requester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
        if (idea.getOwner().getId().equals(requester.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot request to join your own idea"));
        }
        if (!"OPEN".equals(idea.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "This idea is closed"));
        }
        if (joinRequestRepository.existsByIdeaIdAndRequesterId(ideaId, requester.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "You already requested this idea"));
        }

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setIdea(idea);
        joinRequest.setRequester(requester);
        joinRequest.setMessage(body.message());

        return ResponseEntity.status(HttpStatus.CREATED).body(joinRequestRepository.save(joinRequest));
    }

    // GET /api/ideas/5/requests   (the owner sees who wants to join)
    @GetMapping("/api/ideas/{ideaId}/requests")
    public List<JoinRequest> forIdea(@PathVariable Long ideaId) {
        return joinRequestRepository.findByIdeaId(ideaId);
    }

    // PUT /api/requests/9   body: {"status":"ACCEPTED"} or {"status":"REJECTED"}
    @PutMapping("/api/requests/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest body) {
        JoinRequest joinRequest = joinRequestRepository.findById(id).orElse(null);
        if (joinRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Request not found"));
        }
        String status = body.status().trim().toUpperCase();
        if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status must be ACCEPTED or REJECTED"));
        }
        joinRequest.setStatus(status);
        return ResponseEntity.ok(joinRequestRepository.save(joinRequest));
    }
}