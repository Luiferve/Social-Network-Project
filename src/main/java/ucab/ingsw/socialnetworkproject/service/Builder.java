package ucab.ingsw.socialnetworkproject.service;

import org.springframework.stereotype.Component;
import ucab.ingsw.socialnetworkproject.response.AlertResponse;

import java.time.LocalDateTime;

@Component
public class Builder {

    public AlertResponse buildAlertResponse(String message){ //crea un mensaje de alerta para ser transmitido al cliente
        AlertResponse response = new AlertResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
