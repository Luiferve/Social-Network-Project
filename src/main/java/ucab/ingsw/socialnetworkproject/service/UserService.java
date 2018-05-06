package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.Response.ErrorResponse;
import ucab.ingsw.socialnetworkproject.Response.UserResponse;
import ucab.ingsw.socialnetworkproject.command.UserCommand;
import ucab.ingsw.socialnetworkproject.command.UserLoginCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;

import java.time.LocalDateTime;
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

    private ErrorResponse buildErrorResponse(String message){
        ErrorResponse response = new ErrorResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public boolean registerUser(UserCommand command) {
        log.debug("About to process [{}]", command);

        User user = buildNewUser(command);
        user =  userRepository.save(user);

        log.info("Registered user with ID={}", user.getId());

        return true;
    }

    public ResponseEntity<Object> updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);

        if (!userRepository.existsById(Long.parseLong(id))) {
            log.info("Cannot user with ID={}", id);

            return ResponseEntity.badRequest().body(buildErrorResponse("Id no encontrado."));
        } else {
            User user = buildExistingUser(command, id);
            user = userRepository.save(user);

            log.info("Updated user with ID={}", user.getId());

            return ResponseEntity.ok().body(buildErrorResponse("Operacion Exitosa."));
        }
    }

    public List<User> findUserByName(String name){
        List<User> users = userRepository.findByFirstNameIgnoreCaseContaining(name);

        log.info("Found {} records with the partial email address={}", users.size(), name);

        return users;
    }

    public ResponseEntity<Object> loginAuthenticator(UserLoginCommand command) {
        log.debug("About to process [{}]", command);
        User user = userRepository.findByEmail(command.getEmail());
        if(user == null){
            log.info("Cannot find user with email={}", command.getEmail());

            return  ResponseEntity.badRequest().body(buildErrorResponse("Email no encontrado."));
        }
        else{
            if(user.getPassword().equals(command.getPassword())) {
                log.info("Successful login for user ={}", user.getId());

                UserResponse userResponse = new UserResponse();
                userResponse.setFirstName(user.getFirstName());
                userResponse.setLastName(user.getLastName());
                userResponse.setEmail(user.getEmail());
                userResponse.setId(user.getId());
                return ResponseEntity.ok(userResponse);
            }
            else{
                log.info("{} is not valid password for user {}", command.getPassword(), user.getId());

                return  ResponseEntity.badRequest().body(buildErrorResponse("Contrasena Icorrecta."));
            }
        }

    }
}
