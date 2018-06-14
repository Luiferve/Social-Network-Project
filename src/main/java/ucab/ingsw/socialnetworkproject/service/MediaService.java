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
import ucab.ingsw.socialnetworkproject.model.VideoMedia;
import ucab.ingsw.socialnetworkproject.repository.AlbumRepository;
import ucab.ingsw.socialnetworkproject.repository.MediaRepository;
import ucab.ingsw.socialnetworkproject.response.UserMediaResponse;
import ucab.ingsw.socialnetworkproject.response.UserVideoMediaResponse;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaResponse;
import ucab.ingsw.socialnetworkproject.response.searchResponse.InstaVideoResponse;
import ucab.ingsw.socialnetworkproject.service.validation.DataValidation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j

@Service("mediaService")
public class MediaService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DataValidation dataValidation;

    @Autowired
    private Builder builder;

    private Media buildNewMedia(UserNewMediaCommand command){
        Media media;
        if(command.getType().equals(DataValidation.MEDIA_TYPE_VIDEO)){
            if(dataValidation.validateMissingVideoUrl(command)) {
                log.info("Missing video url");
                return null;
            }
            else {
                media = new VideoMedia();
                ((VideoMedia) media).setVideoUrl(command.getVideoUrl());
            }
        }
        else
            media = new Media();
        media.setId(System.currentTimeMillis());
        media.setAlbumId(Long.parseLong(command.getAlbumId()));
        media.setUrl(command.getUrl());
        media.setType(command.getType().toLowerCase());
        media.setLink(command.getLink());
        return media;
    }

    private UserMediaResponse buildResponse(Media media){
        UserMediaResponse mediaResponse;
        if(media.getType().equals(DataValidation.MEDIA_TYPE_IMAGE)) {
            mediaResponse = new UserMediaResponse();
            mediaResponse.setId(media.getId());
            mediaResponse.setUrl(media.getUrl());
            mediaResponse.setType(media.getType());
            mediaResponse.setLink(media.getLink());
        }
        else{
            mediaResponse = new UserVideoMediaResponse();
            VideoMedia videoMedia = (VideoMedia) media;
            mediaResponse.setId(videoMedia.getId());
            mediaResponse.setUrl(videoMedia.getUrl());
            mediaResponse.setType(videoMedia.getType());
            mediaResponse.setLink(videoMedia.getLink());
            ((UserVideoMediaResponse) mediaResponse).setVideoUrl(videoMedia.getVideoUrl());
        }
        return mediaResponse;
    }


    public List<UserMediaResponse> createMediaList(Album album){
        List<UserMediaResponse> mediaList = new ArrayList<>();
        List<Long> mediaIdList = album.getMedia();
        mediaRepository.findAll().forEach(it->{
            if(mediaIdList.stream().anyMatch(item->item == it.getId())){
               /* UserMediaResponse mediaResponse = new UserMediaResponse();
                mediaResponse.setId(it.getId());
                mediaResponse.setUrl(it.getUrl());
                mediaResponse.setType(it.getType());*/
                mediaList.add(buildResponse(it));
            }
        });
        return mediaList;
    }

    public ResponseEntity<Object> addMedia(UserNewMediaCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(!(dataValidation.validateAddMedia(user, command.getUserId(), command))){
            return dataValidation.getResponseEntity();
        }
        else{
            Media media = buildNewMedia(command);
            if(media == null)
                return ResponseEntity.badRequest().body(builder.buildAlertResponse("missing_video_url"));
            else {

                Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
                boolean result = album.getMedia().add(media.getId());
                if (result) {
                    log.info("Media ={} added to album ={} media list", album.getId(), user.getId());

                    albumRepository.save(album);
                    mediaRepository.save(media);

                    return ResponseEntity.ok().body(builder.buildSuccessResponse("success", String.valueOf(media.getId())));
                } else {
                    log.error("Error adding media ={} to album ={} media list", album.getId(), user.getId());
                    return ResponseEntity.badRequest().body(builder.buildAlertResponse("error_adding_media"));
                }
            }
        }
    }

    public ResponseEntity<Object> removeMedia(UserRemoveMediaCommand command){
        User user = userService.searchUserById(command.getUserId());
        if(!(dataValidation.validateRemoveMedia(user, command.getUserId(), command))){
            return dataValidation.getResponseEntity();
        }
        else{
            Album album = albumRepository.findById(Long.parseLong(command.getAlbumId())).get();
            if(!(dataValidation.checkMediaOnList(album,command))){
                return dataValidation.getResponseEntity();
            }
            else{
                boolean result = album.getMedia().remove(Long.parseLong(command.getMediaId()));
                if(result){
                    log.info("Media ={} removed from album ={} media list", command.getMediaId(), album.getId());

                    albumRepository.save(album);
                    mediaRepository.deleteById(Long.parseLong(command.getMediaId()));
                    return ResponseEntity.ok().body(builder.buildAlertResponse("success"));
                }
                else{
                    log.error("Error removing media ={} from album ={} media list", command.getMediaId(), album.getId());
                    return ResponseEntity.badRequest().body(builder.buildAlertResponse("error_removing_media"));
                }
            }
        }
    }

    public ResponseEntity<Object> getMediaById(String id){
        if(!(dataValidation.validateMediaId(id))){
            return ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_media_Id."));
        }
        else{
            Media media = mediaRepository.findById(Long.parseLong(id)).get();
             /*UserMediaResponse mediaResponse;
            if(media.getType().equals("image")) {
                mediaResponse = new UserMediaResponse();
                mediaResponse.setId(media.getId());
                mediaResponse.setUrl(media.getUrl());
                mediaResponse.setType(media.getType());
                mediaResponse.setLink(media.getLink());
            }
            else{
                mediaResponse = new UserVideoMediaResponse();
                VideoMedia videoMedia = (VideoMedia) media;
                mediaResponse.setId(videoMedia.getId());
                mediaResponse.setUrl(videoMedia.getUrl());
                mediaResponse.setType(videoMedia.getType());
                mediaResponse.setLink(videoMedia.getLink());
                ((UserVideoMediaResponse) mediaResponse).setVideoUrl(videoMedia.getVideoUrl());
            }*/

            log.info("Returning media info for media id={}", id);

            return ResponseEntity.ok(buildResponse(media));
        }
    }

    public ResponseEntity<Object> getMediaList(String id){
        if(!(dataValidation.validateAlbumId(id))){
            return ResponseEntity.badRequest().body(builder.buildAlertResponse("invalid_album_id"));
        }
        else {
            Album album = albumRepository.findById(Long.parseLong(id)).get();
            List<UserMediaResponse> mediaList = createMediaList(album);
            if (mediaList.isEmpty()) {
                log.info("Album ={} media list is empty", id);

                return ResponseEntity.ok().body(builder.buildAlertResponse("empty_media_list"));
            } else {
                log.info("Returning media list for album id={}", id);
                return ResponseEntity.ok(mediaList);
            }
        }
    }

}
