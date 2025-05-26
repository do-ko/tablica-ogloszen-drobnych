package com.webdevlab.tablicabackend.constants;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    //    USER
    public static final int USER_USERNAME_MAX_LENGTH = 64;
    public static final int USER_PASSWORD_MAX_LENGTH = 64;
    public static final int USER_PASSWORD_MIN_LENGTH = 12;

    //    OFFER
    public static final int OFFER_TITLE_MAX_LENGTH = 255;
    public static final int OFFER_DESCRIPTION_MAX_LENGTH = 1500;

    public static final int OFFER_TAG_MAX_LENGTH = 64;

    //     Files
    public static final int OFFER_MAX_FILES = 5;
    public static final long OFFER_MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L;
}
