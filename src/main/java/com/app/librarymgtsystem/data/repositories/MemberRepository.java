package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

    Optional<Member> findByEmail(String emailAddy);
    Member findByAccessLevel(int accessLevel);
    Optional<Member> findBySessionEmail(String sessionEmail);

    Member findBySessionStatus(Boolean sessionStatus);

}
