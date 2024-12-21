package com.app.librarymgtsystem.data.repositories;

import com.app.librarymgtsystem.data.models.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends MongoRepository<Member, String> {
    Member findByEmail(String emailAddy);
    Member findByAccessLevel(int accessLevel);
    Member findBySessionStatus(Boolean sessionStatus);
    Optional<Member> findBySessionStatusTrue();


    //Optional<Member> findByEmail(String email);


}
