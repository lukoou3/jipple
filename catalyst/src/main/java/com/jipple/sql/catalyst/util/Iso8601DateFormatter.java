package com.jipple.sql.catalyst.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Iso8601DateFormatter implements DateFormatter {
    private static final long serialVersionUID = 1L;
    private final String pattern;
    private final Locale locale;
    private transient DateTimeFormatter formatter;

    public Iso8601DateFormatter(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }

    @Override
    public int parse(String s) throws DateTimeParseException {
        LocalDate localDate = LocalDate.parse(s, formatter);
        return Math.toIntExact(localDate.toEpochDay());
    }

    @Override
    public String format(int days) {
        return format(LocalDate.ofEpochDay(days));
    }

    @Override
    public String format(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        return format(localDate);
    }

    @Override
    public String format(LocalDate localDate) {
        return localDate.format(formatter);
    }

    @Override
    public void validatePatternString() {
        DateTimeFormatter.ofPattern(pattern, locale);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.formatter = DateTimeFormatter.ofPattern(pattern, locale);
    }
}
