package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.*;
import ucab.ingsw.socialnetworkproject.response.MessageConstants;
import ucab.ingsw.socialnetworkproject.response.UserLogInResponse;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.UserNormalResponse;
import ucab.ingsw.socialnetworkproject.response.UserProfileResponse;
import ucab.ingsw.socialnetworkproject.service.validation.DataValidation;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


@Slf4j

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataValidation dataValidation;

    @Autowired
    private Builder builder;

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

        if(!(dataValidation.validateRegistration(command))){
            return dataValidation.getResponseEntity();
        }
        else {
            User user = buildNewUser(command);
            user = userRepository.save(user);

            log.info("Registered user with ID={}", user.getId());

            return ResponseEntity.ok().body(builder.buildSuccessResponse(MessageConstants.SUCCESS, String.valueOf(user.getId())));
        }
    }

    public ResponseEntity<Object> updateUser(UserUpdateCommand command, String id) {
        log.debug("About to process [{}]", command);

        User oldUser = searchUserById(id);
        if(!(dataValidation.validateUpdateUser(oldUser, id, command))) {
            return  dataValidation.getResponseEntity();
        }
        else{
            User user = buildExistingUser(command, id);
            user.setFriends(oldUser.getFriends());
            user.setAlbums(oldUser.getAlbums());
            user = userRepository.save(user);

            log.info("Updated user with ID={}", user.getId());

            return dataValidation.getResponseEntity();
        }
    }

    public ResponseEntity<Object> loginAuthenticator(UserLoginCommand command) {
        log.debug("About to process [{}]", command);

        User user = userRepository.findByEmailIgnoreCase(command.getEmail());
        if(!(dataValidation.validateLogIn(user, command))){
            return dataValidation.getResponseEntity();
        }
        else {
            log.info("Successful login for user={}", user.getId());

            UserLogInResponse userLogInResponse = new UserLogInResponse();
            userLogInResponse.setFirstName(user.getFirstName());
            userLogInResponse.setLastName(user.getLastName());
            userLogInResponse.setEmail(user.getEmail());
            userLogInResponse.setId(user.getId());
            userLogInResponse.setDateOfBirth(user.getDateOfBirth());
            String token = getMD5(user.getEmail()) + "." + getMD5(Long.toString(System.currentTimeMillis()));
            user.setAuthToken(token);
            user = userRepository.save(user);
            userLogInResponse.setAuthToken(user.getAuthToken());
            return ResponseEntity.ok(userLogInResponse);
        }

    }

    public ResponseEntity<Object> logOut (UserLogoutCommand command){
        log.debug("About to process [{}]", command);
        User user = searchUserById(command.getId());
        if(!(dataValidation.validateLogOut(user, String.valueOf(command.getId()), command))) {
            return dataValidation.getResponseEntity();
        }
        else{
            log.info("Successful logout for user={}", user.getId());
            user.setAuthToken("0");
            userRepository.save(user);
            return dataValidation.getResponseEntity();
        }
    }


    public ResponseEntity<Object> getUserById(String id){
        log.debug("About to process [{}]", id);

        User user = searchUserById(id);
        if (!(dataValidation.validateUserId(user, id))) {
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.INVALID__ID));
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
            userProfileResponse.setAlbums(user.getAlbums());
            log.info("Returning info for user with ID={}", id);
            return ResponseEntity.ok(userProfileResponse);
        }

    }

    public ArrayList<UserNormalResponse> searchUsersByName(String search){
        log.debug("About to process search for name [{}]", search);
        ArrayList<UserNormalResponse> response = new ArrayList<>();
        userRepository.findAll().forEach(it->{
            String name = it.getFirstName();
            String lastName = it.getLastName();
            String fullName = name.concat(lastName);
            if(fullName.toLowerCase().contains(search.toLowerCase())) {
                UserNormalResponse userNormalResponse = new UserNormalResponse();
                userNormalResponse.setFirstName(it.getFirstName());
                userNormalResponse.setLastName(it.getLastName());
                userNormalResponse.setEmail(it.getEmail());
                userNormalResponse.setId(it.getId());
                userNormalResponse.setDateOfBirth(it.getDateOfBirth());

                response.add(userNormalResponse);
            }
        });
        return response;
    }

    public ResponseEntity getUsersByName(String search){
        ArrayList<UserNormalResponse> response = searchUsersByName(search);
        if(response.isEmpty()){
            log.info("Cannot find user with name={}", search);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.NO_RESULT));
        }
        else {
            log.info("Returning info for user with name={}", search);
            return ResponseEntity.ok(response);
        }
    }

    public ResponseEntity<Object> addFriend(UserFriendCommand command){
       User user = searchUserById(command.getUserId());

       if(!(dataValidation.validateAddFriend(user, command.getUserId(), command))) {
           return dataValidation.getResponseEntity();
       }
        else{
           List<Long> friends = user.getFriends();
           Long friendId = Long.parseLong(command.getFriendId());
           if(dataValidation.checkFriendInList(friends, friendId)){
               log.info("Friend ={} already in user ={} friends list", friendId, user.getId());
               return dataValidation.getResponseEntity();
           }
           else {
               boolean result = friends.add(friendId);
               if (result) {
                   log.info("Friend ={} added to user ={} friends list", friendId, user.getId());
                   user.setFriends(friends);
                   userRepository.save(user);
                   return ResponseEntity.ok().body(builder.buildAlertResponse(MessageConstants.SUCCESS));
               }
               else {
                       log.error("Error adding friend ={} to user ={} friends list", friendId, user.getId());
                       return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.ERROR_ADDING_TO_LIST));
               }
           }
       }
    }

    public ResponseEntity<Object> removeFriend(UserFriendCommand command){
        User user = searchUserById(command.getUserId());

        if(!(dataValidation.validateRemoveFriend(user, command.getUserId(), command))) {
            return dataValidation.getResponseEntity();
        }
        else{
            List<Long> friends = user.getFriends();
            Long friendId = Long.parseLong(command.getFriendId());
            boolean result = friends.remove(friendId);
            if (result) {
                log.info("Friend ={} removed from user ={} friends list", friendId, user.getId());
                user.setFriends(friends);
                userRepository.save(user);
                return ResponseEntity.ok().body(builder.buildAlertResponse("success"));
            }
            else {
                log.error("Error removing friend ={} from user ={} friends list", friendId, user.getId());
                return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.ERROR_REMOVING_FROM_LIST));
            }
        }
    }

    public ResponseEntity<Object> getFriendList(String id){
        User user = searchUserById(id);
        if (!(dataValidation.validateUserId(user, id))) {
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse(MessageConstants.INVALID__ID));
        }
        else{
            List<UserNormalResponse> friendList = createFriendList(user);
            if(friendList.isEmpty()){
                log.info("User ={} friend list is empty", id);

                return ResponseEntity.ok().body(builder.buildAlertResponse(MessageConstants.EMPTY_LIST));
            }
            else{
                log.info("Returning friend list for user id={}", id);
                return ResponseEntity.ok(friendList);
            }
        }
    }
}
