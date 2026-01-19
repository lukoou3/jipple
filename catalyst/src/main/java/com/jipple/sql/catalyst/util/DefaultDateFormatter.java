package com.jipple.sql.catalyst.util;

import java.util.Locale;

public class DefaultDateFormatter extends Iso8601DateFormatter {
    public DefaultDateFormatter(Locale locale) {
        super(DateFormatter.defaultPattern(), locale);
    }

    public DefaultDateFormatter() {
        this(DateFormatter.DEFAULT_LOCALE);
    }
}
