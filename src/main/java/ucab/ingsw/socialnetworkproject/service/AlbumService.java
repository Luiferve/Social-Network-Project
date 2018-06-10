package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.UserNewAlbumCommand;
import ucab.ingsw.socialnetworkproject.command.UserRemoveAlbumCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateAlbumCommand;
import ucab.ingsw.socialnetworkproject.model.Album;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.AlbumRepository;
import ucab.ingsw.socialnetworkproject.repository.MediaRepository;
import ucab.ingsw.socialnetworkproject.repository.UserRepository;
import ucab.ingsw.socialnetworkproject.response.UserAlbumResponse;
import ucab.ingsw.socialnetworkproject.service.validation.DataValidation;

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
    private MediaRepository mediaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DataValidation dataValidation;

    @Autowired
    private Builder builder;


    private Album buildNewAlbum (UserNewAlbumCommand command){
        Album album =new Album();
        album.setId(System.currentTimeMillis());
        album.setUser_id(Long.parseLong(command.getUserId()));
        album.setName(command.getName());
        album.setDescription(command.getDescription());
        return album;
    }

    private Album buildExistingAlbum(UserUpdateAlbumCommand command){
        Album album = new Album();
        album.setUser_id(Long.parseLong(command.getUserId()));
        album.setId(Long.parseLong(command.getAlbumId()));
        album.setName(command.getName());
        album.setDescription(command.getDescription());
        return album;
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
        if(!(dataValidation.validateAddAlbum(user, command.getUserId(), command))){
            return dataValidation.getResponseEntity();
        }
        else{
            List<UserAlbumResponse> albumList = createAlbumList(user);
            List<Long> albumIdList = user.getAlbums();
            if(dataValidation.checkAlbumName(albumList, albumIdList, command.getName())){
                log.info("Album name ={} already on list", command.getName());
                return dataValidation.getResponseEntity();
            }
            else{
                Album album = buildNewAlbum(command);
                boolean result = albumIdList.add(album.getId());
                if(result){
                    log.info("Album ={} added to user ={} album list", album.getId(), user.getId());
                    user.setAlbums(albumIdList);
                    userRepository.save(user);
                    albumRepository.save(album);
                    return ResponseEntity.ok().body(builder.buildSuccessResponse("success", String.valueOf(album.getId())));
                }
                else{
                    log.error("Error adding album ={} to user ={} album list", album.getId(), user.getId());
                    return ResponseEntity.badRequest().body(builder.buildAlertResponse("error_adding_album"));
                }
            }
        }
    }

    public ResponseEntity<Object> updateAlbum(UserUpdateAlbumCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(!(dataValidation.validateUpdateAlbum(user, command.getUserId(), command))){
            return dataValidation.getResponseEntity();
        }
        else{
            Album oldAlbum = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
            List<UserAlbumResponse> albumList = createAlbumList(user);
            List<Long> albumIdList = user.getAlbums();
            if((dataValidation.checkAlbumName(albumList, albumIdList, command.getName())) && !(command.getName().toLowerCase().equals(oldAlbum.getName().toLowerCase()))){
                log.info("Album name ={} already on list", command.getName());
                return dataValidation.getResponseEntity();
            }
            else{
                Album album = buildExistingAlbum(command);
                album.setMedia(oldAlbum.getMedia());
                albumRepository.save(album);
                log.info("Updated album with ID={}", oldAlbum.getId());
                return ResponseEntity.ok().body(builder.buildAlertResponse("success"));
            }
        }
    }

    public ResponseEntity<Object> removeAlbum(UserRemoveAlbumCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(!(dataValidation.validateRemoveAlbum(user, command.getUserId(), command))){
            return dataValidation.getResponseEntity();
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
                return ResponseEntity.ok().body(builder.buildAlertResponse("success"));
            }
            else{
                log.error("Error removing album ={} from user ={} album list", command.getAlbumId(), user.getId());
                return ResponseEntity.badRequest().body(builder.buildAlertResponse("error_removing_album"));
            }
        }
    }

    public ResponseEntity<Object> getAlbumById(String id){
        if(!(dataValidation.validateAlbumId(id))){
            return ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_id"));
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
        if (!(dataValidation.validateUserId(user, id))) {
            log.info("Cannot find user with ID={}", id);

            return ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_Id."));
        }
        else {
            List<UserAlbumResponse> albumList = createAlbumList(user);
            if(albumList.isEmpty()){
                log.info("User ={} album list is empty", id);

                return ResponseEntity.ok().body(builder.buildAlertResponse("empty_album_list"));
            }
            else {
                log.info("Returning album list for user id={}", id);
                return ResponseEntity.ok(albumList);
            }
        }
    }

}
