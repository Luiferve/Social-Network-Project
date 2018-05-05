package ucab.ingsw.socialnetworkproject.command;

public interface validationRules {
    int EMAIL_MIN_SIZE = 10;
    int PASSWORD_MIN_SIZE = 6;
    int FIRST_LAST_NAME_MAX_SIZE = 50;
    String FIRST_LAST_NAME_REGEX = "[a-zA-Z ]([&.'-]?[a-zA-Z ][&.'-]?)*";
}
