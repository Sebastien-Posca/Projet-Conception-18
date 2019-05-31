package fr.unice.polytech.cookieFactory.utils;

public class InvalidIdException extends IllegalArgumentException {

    public InvalidIdException(int id, String idType){
        super(id + " is not a valid " + idType);
    }

}
