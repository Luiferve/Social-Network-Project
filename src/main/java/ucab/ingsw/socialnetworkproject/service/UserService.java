package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.UserLogoutCommand;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.UserLogInResponse;
import ucab.ingsw.socialnetworkproject.command.UserSingUpCommand;
import ucab.ingsw.socialnetworkproject.command.UserLoginCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.UserNormalResponse;
import ucab.ingsw.socialnetworkproject.response.UserProfileResponse;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

@Slf4j

@Service("userService")
public class UserService {

    @Autowired //Inyecta el repositorio de usuario al momento de ejecucion.
    private UserRepository userRepository;

    private String getMD5(String text){ //encripta string usando MD5 hash
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(text));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        }
        catch (java.security.NoSuchAlgorithmException no_existe_el_algoritmo){
            System.out.println("ERROR");
        }
        return null;
    }

    private String encrypt(String text){ //encripta string usando base64
        byte[] textBytes=text.getBytes();
        return Base64.getEncoder().encodeToString(textBytes);
    }

    private String decrypt(String text){ //decripta string usando base64
        byte[] decoded = Base64.getDecoder().decode(text);
        return new String(decoded);
    }

    private User buildNewUser(UserSingUpCommand command) { //crea un usuarion nuevo con los atributos recibidos por comando
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());
        user.setAuthToken("0");

        return user;
    }

    private User buildExistingUser(UserUpdateCommand command, String id) { //actualiza los datos de un usuario existente
        User user = new User();
        user.setId(Long.parseLong(id));
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());
        user.setAuthToken(command.getAuthToken());

        return user;
    }

    private AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private User searchUserById(String id) {  //Busqueda de usuario por id proporcionado
        try {
            if(userRepository.findById(Long.parseLong(id)).isPresent()){ //se verifica que exista dicho id en la base de datos
                User user = userRepository.findById(Long.parseLong(id)).get();
                return user;
            }
            else
                return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResponseEntity<Object> registerUser(UserSingUpCommand command) { //registro de usuarios
        log.debug("About to process [{}]", command);

        if(userRepository.existsByEmail(command.getEmail())){ // se revisa si el email ya existe en la base de datos
            log.info("email {} already registered", command.getEmail());

            return ResponseEntity.badRequest().body(buildAlertResponse("El usuario ya se encuentra registrado en el sistema."));
        }
        else {
            if(!command.getPassword().equals(command.getConfirmationPassword())) { //se compara la contrasena con la contrasena de confirmacion
                log.info("Mismatching passwords.");
                return ResponseEntity.badRequest().body(buildAlertResponse("Las contraseñas no coinciden"));
            }

            else { // si el email no existe y las contrasenas coinciden se agrega el usuario a la base de datos
                User user = buildNewUser(command);
                user = userRepository.save(user);

                log.info("Registered user with ID={}", user.getId());

                return ResponseEntity.ok().body(buildAlertResponse("Operación Exitosa."));
            }
        }
    }

    public ResponseEntity<Object> updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);
        try {
            if (!userRepository.existsById(Long.parseLong(id))) { //se verifica si el id proporcionado es correcto
                log.info("Cannot find user with ID={}", id);

                return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
            } else {
                String emailOriginal = userRepository.findById(Long.parseLong(id)).get().getEmail();
                String emailNuevo = command.getEmail();
                if ((userRepository.existsByEmail(emailNuevo)) && !(emailNuevo.equals(emailOriginal))) { // se revisa si el email ya existe en la base de datos
                    log.info("email {} already registered", command.getEmail());

                    return ResponseEntity.badRequest().body(buildAlertResponse("El email ya se encuentra registrado en el sistema."));
                } else {    //se actualiza la informacion del usuario
                    User user = buildExistingUser(command, id);
                    if(command.getAuthToken().equals(userRepository.findById(Long.parseLong(id)).get().getAuthToken())){
                       user = userRepository.save(user);

                       log.info("Updated user with ID={}", user.getId());

                      return ResponseEntity.ok().body(buildAlertResponse("Operación Exitosa."));
                    }else{
                      return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user"));
                    }
                }
            }
        } catch(NumberFormatException e){
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
        }
    }

    public ResponseEntity<Object> loginAuthenticator(UserLoginCommand command) {
        log.debug("About to process [{}]", command);
        User user = userRepository.findByEmail(command.getEmail());  //se verifica si existe el email recibido por comando
        if(user == null){
            log.info("Cannot find user with email={}", command.getEmail());

            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_mail"));
        }
        else{
            if(user.getPassword().equals(command.getPassword())) { //si las contrasenas coinciden se envia la informacion del usuario
                if (user.getAuthToken().equals("0")){ //si no tiene authorization token se permite el log in
                    log.info("Successful login for user={}", user.getId());

                    UserLogInResponse userLogInResponse = new UserLogInResponse();
                    userLogInResponse.setFirstName(user.getFirstName());
                    userLogInResponse.setLastName(user.getLastName());
                    userLogInResponse.setEmail(user.getEmail());
                    userLogInResponse.setId(user.getId());
                    userLogInResponse.setDateOfBirth(user.getDateOfBirth());
                    String token=getMD5(user.getEmail())+"."+getMD5(Long.toString(System.currentTimeMillis()));
                    user.setAuthToken(token);
                    user =userRepository.save(user);
                    userLogInResponse.setAuthToken(user.getAuthToken());
                    return ResponseEntity.ok(userLogInResponse);
                } else {
                    log.info("User ={} already logged in ", user.getId());

                    return  ResponseEntity.badRequest().body(buildAlertResponse("already_logged_in"));
                }
            }
            else{
                log.info("{} is not valid password for user {}", command.getPassword(), user.getId());

                return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_pass"));
            }
        }

    }

    public ResponseEntity<Object> logOut (UserLogoutCommand command){
        log.debug("About to process [{}]", command);
        User user = userRepository.findByEmail(command.getEmail());  //se verifica si existe el email recibido por comando
        if(user == null){
            log.info("Cannot find user with email={}", command.getEmail());

            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_mail"));
        }
        else{
            if (user.getAuthToken().equals("0")){ //si el usuario no ha iniciado sesion no se permite el logout
                    return  ResponseEntity.badRequest().body(buildAlertResponse("already_logged_out"));
                } else if (user.getAuthToken().equals(command.getAuthToken())){

                    log.info("Successful logout for user={}", user.getId());

                    user.setAuthToken("0");
                    user =userRepository.save(user);
                    return ResponseEntity.badRequest().body(buildAlertResponse("Successful logout for user with ID= "+user.getId()));
                } else {
                return  ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user"));
            }
            }
    }


    public ResponseEntity<Object> getUserById(String id){
        log.debug("About to process [{}]", id);

        User user = searchUserById(id); //Se busca el usuario con el Id proporcionado
        if (user == null) {  //Se retorna mensaje si no existe
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id."));
        }
        else {
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setId(user.getId());
            userProfileResponse.setFirstName(user.getFirstName());
            userProfileResponse.setLastName(user.getLastName());
            userProfileResponse.setEmail(user.getEmail());
            userProfileResponse.setPassword(user.getPassword());
            userProfileResponse.setDateOfBirth(user.getDateOfBirth());

            log.info("Returning info for user with ID={}", id);
            return ResponseEntity.ok(userProfileResponse); //De existir usario se retorna su informacion
        }

    }

    public ArrayList<UserNormalResponse> searchUsersByName(String search){
        log.debug("About to process search for name [{}]", search);
        ArrayList<UserNormalResponse> response = new ArrayList<>();
        userRepository.findAll().forEach(it->{ //Para cada usuario registrado en la base de datos
            String name = it.getFirstName();
            String lastName = it.getLastName();
            String fullName = name.concat(lastName); // se combina su nombre y apellido
            if(fullName.toLowerCase().contains(search.toLowerCase())) { //Se verifica si la combinacion nombre completo contiene a la variable de busqueda
                UserNormalResponse userNormalResponse = new UserNormalResponse();
                userNormalResponse.setFirstName(it.getFirstName());
                userNormalResponse.setLastName(it.getLastName());
                userNormalResponse.setEmail(it.getEmail());
                userNormalResponse.setId(it.getId());
                userNormalResponse.setDateOfBirth(it.getDateOfBirth());

                response.add(userNormalResponse); //Se agrega respuesta a la lista
            }
        });
        return response;
    }

    public ResponseEntity getUsersByName(String search){
        ArrayList<UserNormalResponse> response = searchUsersByName(search);
        if(response.isEmpty()){
            log.info("Cannot find user with name={}", search);

            return ResponseEntity.badRequest().body(buildAlertResponse("No result found"));
        }
        else {
            log.info("Returning info for user with name={}", search);
            return ResponseEntity.ok(response);
        }
    }
}
