package ucab.ingsw.socialnetworkproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ucab.ingsw.socialnetworkproject.command.UserNewMediaCommand;
import ucab.ingsw.socialnetworkproject.command.UserRemoveMediaCommand;
import ucab.ingsw.socialnetworkproject.model.Album;
import ucab.ingsw.socialnetworkproject.model.Media;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.repository.AlbumRepository;
import ucab.ingsw.socialnetworkproject.repository.MediaRepository;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;

import java.time.LocalDateTime;

@Slf4j

@Service("mediaService")
public class MediaService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private UserService userService;

    private Media buildNewMedia(UserNewMediaCommand command){
        Media media = new Media();
        media.setId(System.currentTimeMillis());
        media.setAlbumId(Long.parseLong(command.getAlbumId()));
        media.setUrl(command.getUrl());
        media.setType(command.getType());
        return media;
    }

    private AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public ResponseEntity<Object> addMedia(UserNewMediaCommand command){
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
            Media media = buildNewMedia(command);
            Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
            boolean result = album.getMedia().add(media.getId());
            if(result ) {
                log.info("Meida ={} added to album ={} media list", album.getId(), user.getId());

                albumRepository.save(album);
                mediaRepository.save(media);

                return ResponseEntity.ok().body(buildAlertResponse("success"));
            }
            else{
                log.error("Error adding media ={} to album ={} media list", album.getId(), user.getId());
                return ResponseEntity.badRequest().body(buildAlertResponse("error_adding_media"));
            }
        }
    }

    public ResponseEntity<Object> removeMedia(UserRemoveMediaCommand command){
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
        else if(!(mediaRepository.existsById(Long.parseLong(command.getMediaId())))){
            log.info("Cannot find media with id={}", command.getMediaId());

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_media_Id."));
        }
        else{
            Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
            if(!(album.getMedia().stream().anyMatch(i-> i == Long.parseLong(command.getMediaId())))){
                log.info("Media id ={} is not on album ={} media list", command.getAlbumId(), command.getMediaId());

                return ResponseEntity.badRequest().body(buildAlertResponse("media_id_not_on_list"));
            }
            else{
                boolean result = album.getMedia().remove(Long.parseLong(command.getMediaId()));
                if(result){
                    log.info("Media ={} removed from album ={} media list", command.getMediaId(), album.getId());

                    albumRepository.save(album);
                    mediaRepository.deleteById(Long.parseLong(command.getMediaId()));
                    return ResponseEntity.ok().body(buildAlertResponse("success"));
                }
                else{
                    log.error("Error removing media ={} from album ={} media list", command.getMediaId(), album.getId());
                    return ResponseEntity.badRequest().body(buildAlertResponse("error_removing_media"));
                }
            }
        }
    }
}
