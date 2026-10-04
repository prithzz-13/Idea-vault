package com.ideavault.repository;

import com.ideavault.entity.Idea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByOrderByCreatedAtDesc();

    List<Idea> findDistinctBySkills_NameIgnoreCaseOrderByCreatedAtDesc(String skillName);
}