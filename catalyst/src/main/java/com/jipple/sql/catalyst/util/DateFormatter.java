package com.jipple.sql.catalyst.util;

import com.jipple.collection.Option;

import java.io.Serializable;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public interface DateFormatter extends Serializable {
    Locale DEFAULT_LOCALE = Locale.US;

    /**
     * Parses a date string and returns days since epoch.
     */
    int parse(String s) throws DateTimeParseException;

    String format(int days);

    String format(Date date);

    String format(LocalDate localDate);

    void validatePatternString();

    static String defaultPattern() {
        return "yyyy-MM-dd";
    }

    static DateFormatter getFormatter() {
        return getFormatter(Option.none(), DEFAULT_LOCALE);
    }

    static DateFormatter getFormatter(Option<String> format) {
        return getFormatter(format, DEFAULT_LOCALE);
    }

    static DateFormatter getFormatter(Option<String> format, Locale locale) {
        if (format.isDefined()) {
            return new Iso8601DateFormatter(format.get(), locale);
        }
        return new DefaultDateFormatter(locale);
    }
}
