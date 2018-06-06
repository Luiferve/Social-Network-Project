package ucab.ingsw.socialnetworkproject.service;

import ucab.ingsw.socialnetworkproject.command.UserNewAlbumCommand;
import ucab.ingsw.socialnetworkproject.command.UserNewMediaCommand;
import ucab.ingsw.socialnetworkproject.command.UserSingUpCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.model.Album;
import ucab.ingsw.socialnetworkproject.model.Media;
import ucab.ingsw.socialnetworkproject.model.User;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;

import java.time.LocalDateTime;

public class Builder {

    protected AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    protected User buildNewUser(UserSingUpCommand command) { //crea un usuario nuevo con los atributos recibidos por comando
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

    protected User buildExistingUser(UserUpdateCommand command, String id) { //actualiza los datos de un usuario existente
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

    protected Album buildNewAlbum (UserNewAlbumCommand command){
        Album album =new Album();
        album.setId(System.currentTimeMillis());
        album.setUser_id(Long.parseLong(command.getUserId()));
        album.setName(command.getName());
        album.setDescription(command.getDescription());
        return album;
    }

    protected Media buildNewMedia(UserNewMediaCommand command){
        Media media = new Media();
        media.setId(System.currentTimeMillis());
        media.setAlbumId(Long.parseLong(command.getAlbumId()));
        media.setUrl(command.getUrl());
        media.setType(command.getType());
        return media;
    }
}
