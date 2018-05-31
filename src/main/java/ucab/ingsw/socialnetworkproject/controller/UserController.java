package ucab.ingsw.socialnetworkproject.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucab.ingsw.socialnetworkproject.command.*;
import ucab.ingsw.socialnetworkproject.service.UserService;

import javax.validation.Valid;

@Slf4j

@CrossOrigin //Permite conexion desde aplicacion externa.
@RestController
@RequestMapping(value = "/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;



    @RequestMapping(value = "/register", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity register(@Valid @RequestBody UserSingUpCommand command) {
        return userService.registerUser(command);
    }

    @RequestMapping(value = "/login", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity login(@Valid @RequestBody UserLoginCommand command) {
        return userService.loginAuthenticator(command);
    }

    @RequestMapping(value = "/logout", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity logout(@Valid @RequestBody UserLogoutCommand command) {
        return userService.logOut(command);
    }

    @RequestMapping(value = "/friend", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity friend(@Valid @RequestBody UserFriendCommand command) {
        return userService.addFriend(command);
    }

    @RequestMapping(value = "/update/{id}", consumes = "application/json", method = RequestMethod.PUT)
    public ResponseEntity update(@Valid @RequestBody UserUpdateCommand command, @PathVariable("id") String id) {
        return userService.updateUser(command, id);
    }

    @RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
    public ResponseEntity getUsersByName(@PathVariable("name") String name) {
        return userService.getUsersByName(name);
    }


    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getUser(@PathVariable("id") String id) {

        return userService.getUserById(id);
    }

}

