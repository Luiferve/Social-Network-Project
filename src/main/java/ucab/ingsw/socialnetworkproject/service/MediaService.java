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
import ucab.ingsw.socialnetworkproject.response.UserMediaResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j

@Service("mediaService")
public class MediaService extends Validation{

    public List<UserMediaResponse> createMediaList(Album album){
        List<UserMediaResponse> mediaList = new ArrayList<>();
        List<Long> mediaIdList = album.getMedia();
        mediaRepository.findAll().forEach(it->{
            if(mediaIdList.stream().anyMatch(item->item == it.getId())){
                UserMediaResponse mediaResponse = new UserMediaResponse();
                mediaResponse.setId(it.getId());
                mediaResponse.setUrl(it.getUrl());
                mediaResponse.setType(it.getType());
                mediaList.add(mediaResponse);
            }
        });
        return mediaList;
    }

    public ResponseEntity<Object> addMedia(UserNewMediaCommand command){
        User user = userService.searchUserById(command.getUserId());
        ResponseEntity res= validAddMedia(command);
        if (res==null){
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
        else return res;
    }

    public ResponseEntity<Object> removeMedia(UserRemoveMediaCommand command){
        ResponseEntity res=validRemoveMedia(command);
        Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
        if (res==null){
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
        } else return null;
    }

    public ResponseEntity<Object> getMediaById(String id){
        if(!(mediaRepository.existsById(Long.parseLong(id)))){
            log.info("Cannot find media with id={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_media_Id."));
        }
        else{
            Media media = mediaRepository.findById(Long.parseLong(id)).get();
            UserMediaResponse mediaResponse = new UserMediaResponse();
            mediaResponse.setId(media.getId());
            mediaResponse.setUrl(media.getUrl());
            mediaResponse.setType(media.getType());
            log.info("Returning media info for media id={}", id);

            return ResponseEntity.ok(mediaResponse);
        }
    }

    public ResponseEntity<Object> getMediaList(String id){
        if(!(albumRepository.existsById(Long.parseLong(id)))){
            log.info("Cannot find album with id={}", id);

            return ResponseEntity.badRequest().body(buildAlertResponse("invalid_album_Id."));
        }
        else {
            Album album = albumRepository.findById(Long.parseLong(id)).get();
            List<UserMediaResponse> mediaList = createMediaList(album);
            if (mediaList.isEmpty()) {
                log.info("Album ={} media list is empty", id);

                return ResponseEntity.ok().body(buildAlertResponse("empty_media_list"));
            } else {
                log.info("Returning media list for album id={}", id);
                return ResponseEntity.ok(mediaList);
            }
        }
    }

}
