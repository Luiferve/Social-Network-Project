package ucab.ingsw.socialnetworkproject.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ucab.ingsw.socialnetworkproject.model.User;

import java.util.List;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByFirstNameIgnoreCaseContaining(String name);
}

