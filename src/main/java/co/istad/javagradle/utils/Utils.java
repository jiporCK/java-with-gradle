package co.istad.javagradle.utils;

import java.util.UUID;

public class Utils {


    public static String generateUuid() {

        String uuid = UUID.randomUUID().toString();

        String[] newUuid = uuid.split("-");

        return newUuid[0];
    }

}
