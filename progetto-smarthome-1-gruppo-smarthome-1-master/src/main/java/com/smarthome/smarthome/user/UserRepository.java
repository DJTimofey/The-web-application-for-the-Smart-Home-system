package com.smarthome.smarthome.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


//                                                    | Questa è la classe che voglio gestire.
//                                                    |     | Questo è il tipo della variabile che fa da id in User.
//                                                    |     |
@Repository //                                        V     V
public interface UserRepository extends JpaRepository<User, Long>
{
    @Query("SELECT u FROM User u WHERE u.name = ?1")
    Optional<User> findUserByName(String name);

}
