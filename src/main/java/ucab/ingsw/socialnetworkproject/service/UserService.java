package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.UserCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;

import java.util.List;

@Slf4j

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;



    private User buildNewUser(UserCommand command) {
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());

        return user;
    }

    private User buildExistingUser(UserUpdateCommand command, String id) {
        User user = new User();
        user.setId(Long.parseLong(id));
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());

        return user;
    }

    public boolean registerUser(UserCommand command) {
        log.debug("About to process [{}]", command);

        User user = buildNewUser(command);
        user =  userRepository.save(user);

        log.info("Registered user with ID={}", user.getId());

        return true;
    }

    public boolean updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);

        User user = buildExistingUser(command, id);
        user =  userRepository.save(user);

        log.info("Updated user with ID={}", user.getId());

        return true;
    }

    public List<User> findUserByName(String name){
        List<User> users = userRepository.findByFirstNameIgnoreCaseContaining(name);

        log.info("Found {} records with the partial email address={}", users.size(), name);

        return users;
    }
}
