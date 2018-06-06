package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.*;
import ucab.ingsw.socialnetworkproject.response.UserLogInResponse;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.response.UserNormalResponse;
import ucab.ingsw.socialnetworkproject.response.UserProfileResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j

@Service("userService")
public class UserService extends Validation{

    



    private User buildNewUser(UserSingUpCommand command) { //crea un usuario nuevo con los atributos recibidos por comando
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());
        user.setAuthToken("0");
        user.setAlbums(null);
        user.setFriends(null);
        return user;
    }

    private User buildExistingUser(UserUpdateCommand command, String id) { //actualiza los datos de un usuario existente
        User user = new User();
        user.setId(Long.parseLong(id));
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        user.setEmail(command.getEmail().toLowerCase());
        user.setPassword(command.getPassword());
        user.setDateOfBirth(command.getDateOfBirth());
        user.setAuthToken(command.getAuthToken());
        return user;
    }



    public List<UserNormalResponse> createFriendList(User user) {
        List<UserNormalResponse> friendList = new ArrayList<>();
        List<Long> friendIdList = user.getFriends();
        userRepository.findAll().forEach(it->{
            if(friendIdList.stream().anyMatch(item -> item == it.getId())){
                UserNormalResponse normalResponse = new UserNormalResponse();
                normalResponse.setId(it.getId());
                normalResponse.setFirstName(it.getFirstName());
                normalResponse.setLastName(it.getLastName());
                normalResponse.setEmail(it.getEmail());
                normalResponse.setDateOfBirth(it.getDateOfBirth());
                friendList.add(normalResponse);
            }
        });
        return friendList;
    }

    public ResponseEntity<Object> registerUser(UserSingUpCommand command) { //registro de usuarios
        log.debug("About to process [{}]", command);
        ResponseEntity res=validRegister(command);
        if (res==null){
            User user = buildNewUser(command);
            user = userRepository.save(user);

            log.info("Registered user with ID={}", user.getId());

            return ResponseEntity.ok().body(buildAlertResponse("Operación Exitosa."));
        }
        else return res;
    }

    public ResponseEntity<Object> updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);
        ResponseEntity res =validUpdate(command,id);
        if(res==null){
            User oldUser=userRepository.findById(Long.parseLong(id)).get();
            User user = buildExistingUser(command, id);
            user.setFriends(oldUser.getFriends());
            user.setAlbums(oldUser.getAlbums());
            user = userRepository.save(user);
            log.info("Updated user with ID={}", user.getId());
            return ResponseEntity.ok().body(buildAlertResponse("Operación Exitosa."));
        }
        else return res;
    }

    public ResponseEntity<Object> loginAuthenticator(UserLoginCommand command) {
        log.debug("About to process [{}]", command);
        ResponseEntity res =validLogin(command);
        if (res==null){
            User user = userRepository.findByEmailIgnoreCase(command.getEmail());
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
        }
        else return res;
    }

    public ResponseEntity<Object> logOut (UserLogoutCommand command){
        log.debug("About to process [{}]", command);
        ResponseEntity res=validLogout(command);
        if (res==null){
            User user = searchUserById(command.getId());
            log.info("Successful logout for user={}", user.getId());

            user.setAuthToken("0");
            user =userRepository.save(user);
            return ResponseEntity.ok().body(buildAlertResponse("Successful logout for user with ID= "+user.getId()));
        }
        else return res;
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
            userProfileResponse.setFriends(user.getFriends());
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

    public ResponseEntity<Object> addFriend(UserFriendCommand command){
        log.debug("About to process adding friend [{}]", command);
        ResponseEntity res=validAddFriend(command);
       if (res ==null){
           User user = searchUserById(command.getUserId());
           Long friendId = Long.parseLong(command.getFriendId());
           List<Long> friends = user.getFriends();
           if(friends.contains(friendId)){
               log.info("Friend ={} already in user ={} friends list", friendId, user.getId());
               return ResponseEntity.badRequest().body(buildAlertResponse("already_in_friends_list"));
           }
           else {
               boolean result = friends.add(friendId);
               if (result) {
                   log.info("Friend ={} added to user ={} friends list", friendId, user.getId());
                   user.setFriends(friends);
                   userRepository.save(user);
                   return ResponseEntity.ok().body(buildAlertResponse("success"));
               }
               else {
                   log.error("Error adding friend ={} to user ={} friends list", friendId, user.getId());
                   return ResponseEntity.badRequest().body(buildAlertResponse("error_adding_friend"));
               }
           }
       }
       else return res;
    }

    public ResponseEntity<Object> removeFriend(UserFriendCommand command){
        log.debug("About to process removing friend [{}]", command);
        ResponseEntity res = validRemoveFriend(command);
        if (res==null){
            User user = searchUserById(command.getUserId());
            Long friendId = Long.parseLong(command.getFriendId());
            List<Long> friends = user.getFriends();
            if(friends.isEmpty()){
                log.info("User ={} friends list is empty", friendId, user.getId());
                return ResponseEntity.badRequest().body(buildAlertResponse("empty_friends_list"));
            }
            else if (!(friends.contains(friendId))){
                log.info("Friend ={} is not on user ={} friends list", friendId, user.getId());
                return ResponseEntity.badRequest().body(buildAlertResponse("friend_not_in_friends_list"));
            }
            else{
                boolean result = friends.remove(friendId);
                if (result) {
                    log.info("Friend ={} removed from user ={} friends list", friendId, user.getId());
                    user.setFriends(friends);
                    userRepository.save(user);
                    return ResponseEntity.ok().body(buildAlertResponse("success"));
                }
                else {
                    log.error("Error removing friend ={} from user ={} friends list", friendId, user.getId());
                    return ResponseEntity.badRequest().body(buildAlertResponse("error_removing_friend"));
                }
            }
        }
        else return res;
    }

    public ResponseEntity<Object> getFriendList(String id){
        User user = searchUserById(id);
        if(user == null){
            log.info("Cannot find user with id={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else{
            List<UserNormalResponse> friendList = createFriendList(user);
            if(friendList.isEmpty()){
                log.info("User ={} friend list is empty", id);

                return ResponseEntity.ok().body(buildAlertResponse("empty_friend_list"));
            }
            else{
                log.info("Returning friend list for user id={}", id);
                return ResponseEntity.ok(friendList);
            }
        }
    }
}
