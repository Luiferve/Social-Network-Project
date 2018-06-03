package ucab.ingsw.socialnetworkproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucab.ingsw.socialnetworkproject.command.UserNewMediaCommand;
import ucab.ingsw.socialnetworkproject.command.UserRemoveMediaCommand;
import ucab.ingsw.socialnetworkproject.service.MediaService;

import javax.validation.Valid;

@Slf4j

@CrossOrigin //Permite conexion desde aplicacion externa.
@RestController
@RequestMapping(value = "/media", produces = "application/json")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @RequestMapping(value = "/add", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity register(@Valid @RequestBody UserNewMediaCommand command) {
        return mediaService.addMedia(command);
    }

    @RequestMapping(value = "/remove", consumes = "application/json", method = RequestMethod.DELETE)
    public ResponseEntity removeFriend(@Valid @RequestBody UserRemoveMediaCommand command) {
        return mediaService.removeMedia(command);
    }
}
