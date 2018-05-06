package ucab.ingsw.socialnetworkproject.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ucab.ingsw.socialnetworkproject.Response.UserResponse;
import ucab.ingsw.socialnetworkproject.command.UserCommand;
import ucab.ingsw.socialnetworkproject.command.UserLoginCommand;
import ucab.ingsw.socialnetworkproject.command.UserUpdateCommand;
import ucab.ingsw.socialnetworkproject.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/user", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;



    @RequestMapping(value = "/register", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity register(@Valid @RequestBody UserCommand command) {
        boolean result = userService.registerUser(command);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/login", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity login(@Valid @RequestBody UserLoginCommand command) {
        return userService.loginAuthenticator(command);
    }

    @RequestMapping(value = "/update/{id}", consumes = "application/json", method = RequestMethod.PUT)
    public ResponseEntity update(@Valid @RequestBody UserUpdateCommand command, @PathVariable("id") String id) {
        return userService.updateUser(command, id);
    }

    @RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> getUsersByName(@PathVariable("name") String name) {
        ArrayList<UserResponse> response = new ArrayList<>();
        userService.findUserByName(name).forEach(it ->{
            UserResponse userResponse = new UserResponse();
            userResponse.setFirstName(it.getFirstName());
            userResponse.setLastName(it.getLastName());
            userResponse.setEmail(it.getEmail());
            userResponse.setId(it.getId());

            response.add(userResponse);
        });

        return ResponseEntity.ok(response);
    }

}

