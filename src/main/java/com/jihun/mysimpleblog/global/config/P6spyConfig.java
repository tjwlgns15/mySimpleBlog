package com.jihun.mysimpleblog.global.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Configuration
public class P6spyConfig implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        Date currentDate = new Date();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(currentDate) + " | "+ elapsed + "ms | " + category + " | connection " + connectionId + "\n"
                + sql + ";";
    }

    private String formatSql(String category, String sql) {
        if (sql == null || sql.trim().isEmpty()) return sql;

        // Only format Statement, PreparedStatement and Batch
        if ("statement".equalsIgnoreCase(category)
                || "prepared".equalsIgnoreCase(category)
                || "batch".equalsIgnoreCase(category)) {
            String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter")
                    || trimmedSQL.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            return sql;
        }

        return sql;
    }
}