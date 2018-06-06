package ucab.ingsw.socialnetworkproject.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import ucab.ingsw.socialnetworkproject.command.*;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.UserLogInResponse;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
public class Validation {

	@Autowired
	protected UserRepository userRepository;

    protected String getMD5(String text){ //encripta string usando MD5 hash
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

    protected boolean validDate (String string){
        String[] splited=string.split("/");
        int year=Integer.parseInt(splited[0]);
        int mon=Integer.parseInt(splited[1]);
        int day=Integer.parseInt(splited[2]);
        if (!(year>0 && mon>0 && mon<13 && day>0 && day<32)) {
        	log.error("date_out_of_range");
        	return false;
        } //valores fuera de rango
        int b=0;
        if (year%400==0) b=1; //año bisiesto?
        else if (year %4==0 && year%100!=0)b=1;
        if (mon==2 && day>(28+b)) {
        	log.error("february_out_of_range");
        	return false;
        } // febrero fuera de rango
        LocalDateTime current = LocalDateTime.now();
        if (current.getYear()>year){// año pasado
            return true;
        } else if (current.getYear()==year){ //mismo año
            if (current.getMonthValue()>mon){//mes pasado
                return true;
            } else if (current.getMonthValue()==mon){// mismo mes
                if (current.getDayOfMonth()>day){ //dia pasado
                    return true;
                } else if (current.getDayOfMonth()==day){ //mismo dia
                    log.error("same_date");
                    return false;
                } else { //dia futuro
                	log.error("future_day");
                    return false;
                }
            } else { //mes futuro
            	log.error("future_month");
                return false;
            }
        } else { //año futuro
        	log.error("future_year");
            return false;
        }
    }

    protected String encrypt(String text){ //encripta string usando base64
        byte[] textBytes=text.getBytes();
        return Base64.getEncoder().encodeToString(textBytes);
    }

    protected String decrypt(String text){ //decripta string usando base64
        byte[] decoded = Base64.getDecoder().decode(text);
        return new String(decoded);
    }

    protected AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public User searchUserById(String id) {  //Busqueda de usuario por id proporcionado
        try {
            if(userRepository.findById(Long.parseLong(id)).isPresent()){ //se verifica que exista dicho id en la base de datos
                return userRepository.findById(Long.parseLong(id)).get();
            }
            else
                return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected boolean validPasswordSize(String pass){
    	if (decrypt(pass).length()>=6) return true;
    	else return false;
    }

    protected ResponseEntity<Object> validRegister(UserSingUpCommand command){
    	if(userRepository.existsByEmailIgnoreCase(command.getEmail())){ // se revisa si el email ya existe en la base de datos
            log.info("email {} already registered", command.getEmail());
            return ResponseEntity.badRequest().body(buildAlertResponse("El usuario ya se encuentra registrado en el sistema."));
        }
        else {
            if (validPasswordSize(command.getPassword())) { // se valida el tamaño de la contraseña
                if(!command.getPassword().equals(command.getConfirmationPassword())) { //se compara la contrasena con la contrasena de confirmacion
                    log.info("Mismatching passwords.");
                    return ResponseEntity.badRequest().body(buildAlertResponse("Las contraseñas no coinciden"));
                }
                else { // si el email no existe y las contrasenas coinciden se agrega el usuario a la base de datos
                    if (validDate(command.getDateOfBirth())){ //valida la fecha
                        return null;
                    }
                    else {
                        log.error("Invalid date");
                        return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_date"));
                    }
                }
            }
            else{
                log.error("Invalid password size");
                return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_password_size"));
            }

        }
    }

    protected ResponseEntity<Object> validUpdate (UserUpdateCommand command, String id){

        try {
            if (!userRepository.existsById(Long.parseLong(id))) { //se verifica si el id proporcionado es correcto
                log.info("Cannot find user with ID={}", id);

                return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
            } else {
                User oldUser=userRepository.findById(Long.parseLong(id)).get();
                String emailOriginal = oldUser.getEmail();
                String emailNuevo = command.getEmail();
                if ((userRepository.existsByEmailIgnoreCase(emailNuevo)) && !(emailNuevo.equals(emailOriginal))) { // se revisa si el email ya existe en la base de datos
                    log.info("email {} already registered", command.getEmail());
                    return ResponseEntity.badRequest().body(buildAlertResponse("El email ya se encuentra registrado en el sistema."));
                } else {
                    if (validPasswordSize(command.getPassword())) { // se valida el tamaño de la contraseña
                        if (validDate(command.getDateOfBirth())){ // se valida la fecha
                            if(command.getAuthToken().equals(oldUser.getAuthToken())){  //revisa que el usuario este iniciado
                                return null;
                            }else{
                                log.error("User with ID={} is not unauthenticated", id);
                                return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user"));
                            }
                        }
                        else{
                            log.error("Invalid date");
                            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_date"));
                        }
                    }
                    else{
                        log.error("Invalid password");
                        return ResponseEntity.badRequest().body(buildAlertResponse("invalid_password_size"));
                    }
                }
            }
        } catch(NumberFormatException e){
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_Id"));
        }
    }

    protected ResponseEntity<Object> validLogin (UserLoginCommand command){
        User user = userRepository.findByEmailIgnoreCase(command.getEmail());  //se verifica si existe el email recibido por comando
        if(user == null){
            log.info("Cannot find user with email={}", command.getEmail());

            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_mail"));
        }
        else{
            if (validPasswordSize(command.getPassword())){ //verifica el tamaño de la contraseña
                if(user.getPassword().equals(command.getPassword())) { //si las contrasenas coinciden se envia la informacion del usuario
                    if (!user.getAuthToken().equals("0")) log.info("User ={} already logged in ", user.getId());
                    return null;
                }
                else{
                    log.info("{} is not valid password for user {}", decrypt(command.getPassword()), user.getId());

                    return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_pass"));
                }
            }
            else{
                return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_password_size"));
            }

        }
    }

    protected ResponseEntity<Object> validLogout (UserLogoutCommand command){
        User user = searchUserById(command.getId());  //se verifica si existe el email recibido por comando
        if(user == null){
            log.info("Cannot find user with id={}", command.getId());

            return  ResponseEntity.badRequest().body(buildAlertResponse("invalid_id"));
        }
        else{
            if (user.getAuthToken().equals("0")){ //si el usuario no ha iniciado sesion no se permite el logout
                log.info("User with id={} already logged out", command.getId());
                return  ResponseEntity.badRequest().body(buildAlertResponse("already_logged_out"));
            }
            else if (user.getAuthToken().equals(command.getAuthToken())){

                return null;
            }
            else {
                return  ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user"));
            }
        }
    }

    protected ResponseEntity<Object> validAddFriend(UserFriendCommand command){
        User user = searchUserById(command.getUserId());
        Long friendId = Long.parseLong(command.getFriendId());
        if(user == null){
            log.info("Cannot find user with ID={}", command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else if(!userRepository.existsById(friendId)){
            log.info("Cannot find user with ID={}", friendId);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_friend_Id."));
        }
        else if(user.getId() == friendId){
            log.info("User id ={} cannot match friend id ={}", user.getId(), friendId);

            return ResponseEntity.badRequest().body(buildAlertResponse("matching_Ids."));
        }
        else if((!(command.getAuthToken().equals(user.getAuthToken()))) || (command.getAuthToken().equals("0"))){
            log.error("Wrong authentication token");

            return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user."));
        }
        else{
            return null;
        }
    }

    protected ResponseEntity<Object> validRemoveFriend (UserFriendCommand command){
        User user = searchUserById(command.getUserId());
        Long friendId = Long.parseLong(command.getFriendId());
        if(user == null){
            log.info("Cannot find user with name={}", command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else if(!userRepository.existsById(friendId)){
            log.info("Cannot find user with name={}", friendId);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_friend_Id."));
        }
        else if(!(user.getFriends().stream().anyMatch(i-> i == friendId))){
            log.info("Friend id ={} is not on user ={} friend list", friendId, command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("friend_is_not_on_list"));
        }
        else if((!(command.getAuthToken().equals(user.getAuthToken()))) || (command.getAuthToken().equals("0"))){
            log.error("Wrong authentication token");

            return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user."));
        }
        else{
            return null;
        }
    }
}