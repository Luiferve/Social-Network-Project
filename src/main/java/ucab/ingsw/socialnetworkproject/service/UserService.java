package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.UserResponse;
import ucab.ingsw.socialnetworkproject.command.UserSingUpCommand;
import ucab.ingsw.socialnetworkproject.command.UserLoginCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j

@Service("userService")
public class UserService {

    @Autowired //Inyecta el repositorio de usuario al momento de ejecucion.
    private UserRepository userRepository;



    private User buildNewUser(UserSingUpCommand command) {
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());

        return user;
    }

    private User buildExistingUser(UserUpdateCommand command, String id) {
        User user = new User();
        user.setId(Long.parseLong(id));
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());

        return user;
    }

    private AlertResponse buildAlertResponse(String message){
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public ResponseEntity<Object> registerUser(UserSingUpCommand command) {
        log.debug("About to process [{}]", command);

        if(userRepository.existsByEmail(command.getEmail())){
            log.info("email {} already registered", command.getEmail());

            return ResponseEntity.badRequest().body(buildAlertResponse("El usuario ya se encuentra registrado en el sistema."));
        }
        else {
            if(!command.getPassword().equals(command.getConfirmationPassword())) {
                log.info("Mismatching passwords.");
                return ResponseEntity.badRequest().body(buildAlertResponse("Las contrasenas no coinciden"));
            }

            else {
                User user = buildNewUser(command);
                user = userRepository.save(user);

                log.info("Registered user with ID={}", user.getId());

                return ResponseEntity.ok().body(buildAlertResponse("Operacion Exitosa."));
            }
        }
    }

    public ResponseEntity<Object> updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);

        if (!userRepository.existsById(Long.parseLong(id))) {
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
        } else {
            User user = buildExistingUser(command, id);
            user = userRepository.save(user);

            log.info("Updated user with ID={}", user.getId());

            return ResponseEntity.ok().body(buildAlertResponse("Operacion Exitosa."));
        }
    }

    public List<User> findUserByName(String name){
        List<User> users = userRepository.findByFirstNameIgnoreCaseContaining(name);

        log.info("Found {} records with the partial name={}", users.size(), name);

        return users;
    }

    public ResponseEntity<Object> loginAuthenticator(UserLoginCommand command) {
        log.debug("About to process [{}]", command);
        User user = userRepository.findByEmail(command.getEmail());
        if(user == null){
            log.info("Cannot find user with email={}", command.getEmail());

            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_mail"));
        }
        else{
            if(user.getPassword().equals(command.getPassword())) {
                log.info("Successful login for user ={}", user.getId());

                UserResponse userResponse = new UserResponse();
                userResponse.setFirstName(user.getFirstName());
                userResponse.setLastName(user.getLastName());
                userResponse.setEmail(user.getEmail());
                userResponse.setId(user.getId());
                userResponse.setDateOfBirth(user.getDateOfBirth());
                return ResponseEntity.ok(userResponse);
            }
            else{
                log.info("{} is not valid password for user {}", command.getPassword(), user.getId());

                return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_pass "));
            }
        }

    }

    public ResponseEntity<Object> getUserById(String id){
        log.debug("About to process [{}]", id);

        if (!userRepository.existsById(Long.parseLong(id))) {
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
        }
        else {
            User user = userRepository.findById(Long.parseLong(id)).get();
            UserResponse userResponse = new UserResponse();
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setId(user.getId());
            userResponse.setDateOfBirth(user.getDateOfBirth());
            log.info("Returning info for user with ID={}", id);
            return ResponseEntity.ok(userResponse);
        }

    }
}
