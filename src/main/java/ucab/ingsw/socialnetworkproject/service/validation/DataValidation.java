package ucab.ingsw.socialnetworkproject.service.validation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ucab.ingsw.socialnetworkproject.command.*;
import ucab.ingsw.socialnetworkproject.model.Album;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.AlbumRepository;
import ucab.ingsw.socialnetworkproject.repository.MediaRepository;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.UserAlbumResponse;
import ucab.ingsw.socialnetworkproject.service.Builder;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j

@Data
@Component
public class DataValidation {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private Builder builder;


    private ResponseEntity<Object> responseEntity;


    private String decrypt(String text){ //decripta string usando base64
        byte[] decoded = Base64.getDecoder().decode(text);
        return new String(decoded);
    }

    private static boolean validDate (String string){
        String[] splited=string.split("/");
        int year=Integer.parseInt(splited[0]);
        int mon=Integer.parseInt(splited[1]);
        int day=Integer.parseInt(splited[2]);
        if (!(year>0 && mon>0 && mon<13 && day>0 && day<32)) return false; //valores fuera de rango
        int b=0;
        if (year%400==0) b=1; //año bisiesto?
        else if (year %4==0 && year%100!=0)b=1;
        if (mon==2 && day>(28+b)) return false; // febrero fuera de rango
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
                    return false;
                } else { //dia futuro
                    return false;
                }
            } else { //mes futuro
                return false;
            }
        } else { //año futuro
            return false;
        }
    }

    private boolean checkForExistingEmail(String email){
        if(userRepository.existsByEmailIgnoreCase(email)){
            log.info("Email {} already registered", email);

            return false;
        }
        else return true;
    }

    private boolean validateUserEmail(User user, String email){
        if(user == null){
            log.info("Cannot find user with email={}", email);

            return false;
        }
        else return true;
    }

    private boolean validatePasswordLength(String password){
        if(!(decrypt(password).length()>=6)){
            log.info("Invalid password size");

            return false;
        }
        else return true;
    }

    private boolean validateConfirmationPassword(String password, String confirmationPassword){
        if(!(password.equals(confirmationPassword))){
            log.info("Mismatching passwords");

            return false;
        }
        else return true;
    }

    private boolean validateUserPassword(String commandPassword, String userPassword, String id){
        if(!(commandPassword.equals(userPassword))){
            log.info("{} is not valid password for user {}", decrypt(commandPassword), id);

            return false;
        }
        else return true;
    }

    public boolean validateUserId(User user, String id){
            if (user == null) {
                log.info("Cannot find user with ID={}", id);

                return false;
            } else return true;
    }

    private boolean validateUserId(String id){
        try {
            if (!userRepository.existsById(Long.parseLong(id))) {
                log.info("Cannot find user with id={}", id);
                return false;
            } else return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateAlbumId(String id){
        try {
            if (!albumRepository.existsById(Long.parseLong(id))) {
                log.info("Cannot find album with id={}", id);
                return false;
            } else return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateMediaId(String id){
        try {
            if (!mediaRepository.existsById(Long.parseLong(id))) {
                log.info("Cannot find media with id={}", id);
                return false;
            } else return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateUserToken(String commandToken, String userToken){
        if((!(commandToken.equals(userToken))) || (commandToken.equals("0"))){
            log.error("Invalid token", commandToken, userToken);

            return false;
        }
        else return true;
    }


    public boolean validateRegistration(UserSingUpCommand command){
        if(!(validDate(command.getDateOfBirth()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_date"));
            return false;
        }
        else if(!(checkForExistingEmail(command.getEmail()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("email_already_registered"));
            return false;
        }
        else if(!(validatePasswordLength(command.getPassword()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_password_size"));
            return false;
        }
        else if(!(validateConfirmationPassword(command.getPassword(), command.getConfirmationPassword()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("mismatching passwords"));
            return false;
        }
        else{
            return true;
        }
    }

    public boolean validateLogIn(User user, UserLoginCommand command){
        if(!(validateUserEmail(user, command.getEmail()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_mail"));
            return false;
        }
        else if(!(validatePasswordLength(command.getPassword()))) {
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_password_size"));
            return false;
        }
        else if(!(validateUserPassword(command.getPassword(), user.getPassword(), String.valueOf(user.getId())))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_pass"));
            return false;
        }
        else{
            return true;
        }
    }

    public boolean validateUpdateUser(User user, String id, UserUpdateCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else if(!(validDate(command.getDateOfBirth()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_date"));
            return false;
        }
        else if(!(checkForExistingEmail(command.getEmail())) && !(command.getEmail().equals(user.getEmail()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("email_already_registered"));
            return false;
        }
        else if(!(validatePasswordLength(command.getPassword()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_password_size"));
            return false;
        }
        else{
            responseEntity = ResponseEntity.ok().body(builder.buildAlertResponse("success"));
            return true;
        }
    }

    public boolean validateLogOut(User user, String id, UserLogoutCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else{
            responseEntity = ResponseEntity.ok().body(builder.buildAlertResponse("success"));
            return true;
        }
    }

    public boolean checkFriendInList(List<Long> friends, Long friendId){
        if(friends.contains(friendId)){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("already_in_friends_list"));
            return true;
        }
        else return false;
    }


    public boolean validateAddFriend(User user, String id, UserFriendCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserId(command.getFriendId()))){
            log.info("Cannot find friend with id={}", command.getFriendId());
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_friend_id"));
            return false;
        }
        else if(user.getId() == Long.parseLong(command.getFriendId())){
            log.info("User ={} cannot be friend with self", user.getId());
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("matching_ids"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else return true;
    }

    public boolean validateRemoveFriend(User user, String id, UserFriendCommand command){
        if(!(validateAddFriend(user, id, command))){
            return false;
        }
        else if(!(checkFriendInList(user.getFriends(),Long.parseLong(command.getFriendId())))){
            log.info("Friend ={} not on user ={} friends list", command.getFriendId(), user.getId());
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("friend_id_not_on_list"));
            return false;
        }

        else return true;
    }

    public boolean checkAlbumName(List<UserAlbumResponse> albumList, List<Long> albumIdLis, String name){
        if(albumList.stream().anyMatch(i -> i.getName().toLowerCase().equals(name.toLowerCase()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("album_name_already_used"));
            return true;
        }
        return false;
    }

    public boolean validateAddAlbum(User user, String id, UserNewAlbumCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else return true;
    }

    public boolean validateRemoveAlbum(User user, String id, UserRemoveAlbumCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else if(!(validateAlbumId(command.getAlbumId()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_Id"));
            return false;
        }
        else if(!(user.getAlbums().stream().anyMatch(i-> i == Long.parseLong(command.getAlbumId())))){
            log.info("Album id ={} is not on user ={} album list", command.getAlbumId(), command.getUserId());

            responseEntity =  ResponseEntity.badRequest().body(builder.buildAlertResponse("album_id_not_on_list"));
            return  false;
        }
        else return true;
    }

    public boolean validateUpdateAlbum(User user, String id, UserUpdateAlbumCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else if(!(validateAlbumId(command.getAlbumId()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_Id"));
            return false;
        }
        else if(!(user.getAlbums().stream().anyMatch(i-> i == Long.parseLong(command.getAlbumId())))){
            log.info("Album id ={} is not on user ={} album list", command.getAlbumId(), command.getUserId());

            responseEntity =  ResponseEntity.badRequest().body(builder.buildAlertResponse("album_id_not_on_list"));
            return  false;
        }

        else return true;
    }

    public boolean validateAddMedia(User user, String id, UserNewMediaCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else if(!(validateAlbumId(command.getAlbumId()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_Id"));
            return false;
        }
        else if(!(user.getAlbums().stream().anyMatch(i-> i == Long.parseLong(command.getAlbumId())))){
            log.info("Album id ={} is not on user ={} album list", command.getAlbumId(), command.getUserId());

            responseEntity =  ResponseEntity.badRequest().body(builder.buildAlertResponse("album_id_not_on_list"));
            return  false;
        }
        else return true;
    }

    public boolean validateRemoveMedia(User user, String id, UserRemoveMediaCommand command){
        if(!(validateUserId(user,id))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_id"));
            return false;
        }
        else if(!(validateUserToken(command.getAuthToken(),user.getAuthToken()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("unauthenticated_user"));
            return false;
        }
        else if(!(validateAlbumId(command.getAlbumId()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_Id"));
            return false;
        }
        else if(!(user.getAlbums().stream().anyMatch(i-> i == Long.parseLong(command.getAlbumId())))){
            log.info("Album id ={} is not on user ={} album list", command.getAlbumId(), command.getUserId());

            responseEntity =  ResponseEntity.badRequest().body(builder.buildAlertResponse("album_id_not_on_list"));
            return  false;
        }
        else if(!(validateMediaId(command.getMediaId()))){
            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_media_Id"));
            return false;
        }
        else return true;
    }

    public boolean checkMediaOnList(Album album, UserRemoveMediaCommand command){
        if(!(album.getMedia().stream().anyMatch(i-> i == Long.parseLong(command.getMediaId())))){
            log.info("Media id ={} is not on album ={} media list", command.getAlbumId(), command.getMediaId());

            responseEntity = ResponseEntity.badRequest().body(builder.buildAlertResponse("media_id_not_on_list"));
            return false;
        }
        else return true;
    }
}
