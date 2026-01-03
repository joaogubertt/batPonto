package dev.OsRapazes.BatPonto.service;

import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimeEntryReportFormatterService {

    public record DayRow(
            LocalDate date,
            String dayOfWeek,
            String entry1,
            String exit1,
            String entry2,
            String exit2
    ) {}

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm").withLocale(LOCALE_PT_BR);

    public List<DayRow> toDailyRows(TimeEntryReportResponseDto report) {

        Map<LocalDate, List<TimeEntryReportResponseDto.EntryItem>> grouped =
                report.entries().stream()
                        .collect(Collectors.groupingBy(e ->
                                e.timestamp().atZone(ZONE).toLocalDate()
                        ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> buildRow(entry.getKey(), entry.getValue()))
                .toList();
    }

    private DayRow buildRow(LocalDate date, List<TimeEntryReportResponseDto.EntryItem> items) {
        List<TimeEntryReportResponseDto.EntryItem> sorted = items.stream()
                .sorted(Comparator.comparing(TimeEntryReportResponseDto.EntryItem::timestamp))
                .toList();

        String e1 = null, s1 = null, e2 = null, s2 = null;

        for (var it : sorted) {
            String type = it.type() == null ? "" : it.type().toUpperCase();
            String time = TIME_FMT.format(it.timestamp().atZone(ZONE));

            if (type.equals("ENTRADA")) {
                if (e1 == null) e1 = time;
                else if (s1 != null && e2 == null) e2 = time;
            } else if (type.equals("SAIDA") || type.equals("SAÍDA")) {
                if (e1 != null && s1 == null) s1 = time;
                else if (e2 != null && s2 == null) s2 = time;
            }
        }

        String dow = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, LOCALE_PT_BR);

        return new DayRow(
                date,
                capitalize(dow),
                nvl(e1), nvl(s1), nvl(e2), nvl(s2)
        );
    }

    private static String nvl(String v) { return v == null ? "—" : v; }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.substring(0, 1).toUpperCase(LOCALE_PT_BR) + s.substring(1);
    }
}