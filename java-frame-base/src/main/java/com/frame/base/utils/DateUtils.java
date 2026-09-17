package com.frame.base.utils;

import java.time.*;
import java.util.Date;

public class DateUtils {

    private static final ZoneId GMT_ZONE = ZoneId.of("GMT");

    /**
     * Convertit un ZonedDateTime existant en GMT.
     */
    public static ZonedDateTime convertToGmt(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withZoneSameInstant(GMT_ZONE);
    }

    /**
     * Convertit un texte de date (ex: "2026-09-17T15:30:00+02:00") en GMT.
     */
    public static ZonedDateTime parseAndConvertToGmt(String dateIsoString) {
        if (dateIsoString == null || dateIsoString.isBlank()) {
            return null;
        }
        ZonedDateTime parsedDate = ZonedDateTime.parse(dateIsoString);
        return convertToGmt(parsedDate);
    }


    /**
     * Retourne la date actuelle calée sur GMT+0 (UTC) au format java.util.Date
     */
    public static Date nowGmt() {
        return Date.from(Instant.now());
    }

    public static LocalDateTime localDateTimeGmt() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
