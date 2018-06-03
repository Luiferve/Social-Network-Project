package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.UserNewAlbumCommand;
import ucab.ingsw.socialnetworkproject.command.UserRemoveAlbumCommand;
import ucab.ingsw.socialnetworkproject.model.Album;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.AlbumRepository;
import ucab.ingsw.socialnetworkproject.repository.MediaRepository;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.UserAlbumResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j

@Service("albumService")
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MediaRepository mediaRepository;


    private Album buildNewAlbum (UserNewAlbumCommand command){
        Album album =new Album();
        album.setId(System.currentTimeMillis());
        album.setUser_id(Long.parseLong(command.getUserId()));
        album.setName(command.getName());
        album.setDescription(command.getDescription());
        return album;
    }

    private AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }


    public List<UserAlbumResponse> createAlbumList(User user){
        List<UserAlbumResponse> albumList = new ArrayList<>();
        List<Long> albumIdList = user.getAlbums();
        albumRepository.findAll().forEach(it->{
            if(albumIdList.stream().anyMatch(item -> item == it.getId())){
                UserAlbumResponse albumResponse = new UserAlbumResponse();
                albumResponse.setId(it.getId());
                albumResponse.setName(it.getName());
                albumResponse.setDescription(it.getDescription());
                albumResponse.setMedia(it.getMedia());
                albumList.add(albumResponse);
            }
        });
        return albumList;
    }

    public ResponseEntity<Object> addAlbum(UserNewAlbumCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(user == null){
            log.info("Cannot find user with name={}", command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else if((!(command.getAuthToken().equals(user.getAuthToken()))) || (command.getAuthToken().equals("0"))) {
            log.error("Wrong authentication token");

            return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user."));
        }
        else{
            List<UserAlbumResponse> albumList = createAlbumList(user);
            List<Long> albumIdList = user.getAlbums();
            if(albumList.stream().anyMatch(i -> i.getName().equals(command.getName()))){
                log.info("Album name ={} already on list", command.getName());

                return ResponseEntity.badRequest().body(buildAlertResponse("album_name_already_used"));
            }
            else{
                Album album = buildNewAlbum(command);
                boolean result = albumIdList.add(album.getId());
                if(result){
                    log.info("Album ={} added to user ={} album list", album.getId(), user.getId());
                    user.setAlbums(albumIdList);
                    userRepository.save(user);
                    albumRepository.save(album);
                    return ResponseEntity.ok().body(buildAlertResponse("success"));
                }
                else{
                    log.error("Error adding album ={} to user ={} album list", album.getId(), user.getId());
                    return ResponseEntity.badRequest().body(buildAlertResponse("error_adding_album"));
                }
            }
        }
    }

    public ResponseEntity<Object> removeAlbum(UserRemoveAlbumCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(user == null){
            log.info("Cannot find user with name={}", command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else if((!(command.getAuthToken().equals(user.getAuthToken()))) || (command.getAuthToken().equals("0"))) {
            log.error("unauthenticated_user.");

            return ResponseEntity.badRequest().body(buildAlertResponse("unauthenticated_user."));
        }
        else if(!(albumRepository.existsById(Long.parseLong(command.getAlbumId())))){
            log.info("Cannot find album with id={}", command.getAlbumId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_album_Id."));
        }
        else if(!(user.getAlbums().stream().anyMatch(i-> i == Long.parseLong(command.getAlbumId())))){
            log.info("Album id ={} is not on user ={} album list", command.getAlbumId(), command.getUserId());

            return ResponseEntity.badRequest().body(buildAlertResponse("album_id_not_on_list"));
        }
        else{
            Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
            album.getMedia().forEach(it->{
                mediaRepository.deleteById(it);
            });
            boolean result = user.getAlbums().remove(Long.parseLong(command.getAlbumId()));
            if(result){
                log.info("Album ={} removed from user ={} album list", command.getAlbumId(), user.getId());

                userRepository.save(user);
                albumRepository.deleteById(Long.parseLong(command.getAlbumId()));
                return ResponseEntity.ok().body(buildAlertResponse("success"));
            }
            else{
                log.error("Error removing album ={} from user ={} album list", command.getAlbumId(), user.getId());
                return ResponseEntity.badRequest().body(buildAlertResponse("error_removing_album"));
            }
        }
    }

    public ResponseEntity<Object> getAlbumById(String id){
        if(!(albumRepository.existsById(Long.parseLong(id)))){
            log.info("Cannot find album with id={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_album_Id."));
        }
        else {
            Album album = albumRepository.findById(Long.parseLong(id)).get();
            UserAlbumResponse albumResponse = new UserAlbumResponse();
            albumResponse.setId(album.getId());
            albumResponse.setName(album.getName());
            albumResponse.setDescription(album.getDescription());
            albumResponse.setMedia(album.getMedia());
            log.info("Returning album info for album id={}", id);

            return ResponseEntity.ok(albumResponse);
        }
    }

    public ResponseEntity<Object> getAlbumList(String id){
        User user = userService.searchUserById(id);
        if(user == null){
            log.info("Cannot find user with id={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_user_Id."));
        }
        else {
            List<UserAlbumResponse> albumList = createAlbumList(user);
            if(albumList.isEmpty()){
                log.info("User ={} album list is empty", id);

                return ResponseEntity.ok().body(buildAlertResponse("empty_album_list"));
            }
            else {
                log.info("Returning album list for user id={}", id);
                return ResponseEntity.ok(albumList);
            }
        }
    }

}
