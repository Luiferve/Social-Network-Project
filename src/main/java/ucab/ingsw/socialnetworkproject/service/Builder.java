package ucab.ingsw.socialnetworkproject.service;

import org.springframework.stereotype.Component;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;
import ucab.ingsw.socialnetworkproject.response.SuccessResponse;

import java.time.LocalDateTime;

@Component
public class Builder {

    public AlertResponse buildAlertResponse(String message){
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public SuccessResponse buildSuccessResponse(String message, String id){
        SuccessResponse response = new SuccessResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        response.setId(id);
        return response;
    }

}
