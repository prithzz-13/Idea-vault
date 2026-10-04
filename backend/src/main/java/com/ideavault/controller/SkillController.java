package com.ideavault.controller;

import com.ideavault.dto.SkillRequest;
import com.ideavault.entity.Skill;
import com.ideavault.repository.SkillRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillRepository skillRepository;

    public SkillController(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @GetMapping
    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SkillRequest request) {
        String name = request.name().trim();
        if (skillRepository.existsByNameIgnoreCase(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Skill already exists");
        }
        Skill skill = new Skill();
        skill.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(skillRepository.save(skill));
    }
}