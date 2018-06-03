package ucab.ingsw.socialnetworkproject.repository;


import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ucab.ingsw.socialnetworkproject.model.User;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmailIgnoreCase(String email);
    boolean  existsByEmailIgnoreCase(String email);
}

