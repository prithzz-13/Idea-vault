package com.ideavault.repository;

import com.ideavault.entity.JoinRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    List<JoinRequest> findByIdeaId(Long ideaId);

    boolean existsByIdeaIdAndRequesterId(Long ideaId, Long requesterId);
}