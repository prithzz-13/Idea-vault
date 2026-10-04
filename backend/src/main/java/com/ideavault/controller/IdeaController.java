package com.ideavault.controller;

import com.ideavault.dto.IdeaRequest;
import com.ideavault.entity.Idea;
import com.ideavault.entity.Skill;
import com.ideavault.entity.User;
import com.ideavault.repository.IdeaRepository;
import com.ideavault.repository.SkillRepository;
import com.ideavault.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ideas")
public class IdeaController {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public IdeaController(IdeaRepository ideaRepository,
                          UserRepository userRepository,
                          SkillRepository skillRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    // POST /api/ideas   (post a new idea)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody IdeaRequest request) {
        User owner = userRepository.findById(request.ownerId()).orElse(null);
        if (owner == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Owner not found"));
        }

        Set<Skill> skills = new HashSet<>();
        if (request.skillIds() != null && !request.skillIds().isEmpty()) {
            Set<Long> wanted = new HashSet<>(request.skillIds());
            List<Skill> found = skillRepository.findAllById(wanted);
            if (found.size() != wanted.size()) {
                return ResponseEntity.badRequest().body(Map.of("error", "One or more skill ids do not exist"));
            }
            skills.addAll(found);
        }

        Idea idea = new Idea();
        idea.setTitle(request.title().trim());
        idea.setDescription(request.description().trim());
        if (request.teamSize() != null) {
            idea.setTeamSize(request.teamSize());
        }
        idea.setOwner(owner);
        idea.setSkills(skills);

        return ResponseEntity.status(HttpStatus.CREATED).body(ideaRepository.save(idea));
    }

    // GET /api/ideas  or  GET /api/ideas?skill=Java
    @GetMapping
    public List<Idea> list(@RequestParam(required = false) String skill) {
        if (skill == null || skill.isBlank()) {
            return ideaRepository.findAllByOrderByCreatedAtDesc();
        }
        return ideaRepository.findDistinctBySkills_NameIgnoreCaseOrderByCreatedAtDesc(skill.trim());
    }

    // GET /api/ideas/5
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Idea idea = ideaRepository.findById(id).orElse(null);
        if (idea == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Idea not found"));
        }
        return ResponseEntity.ok(idea);
    }
}