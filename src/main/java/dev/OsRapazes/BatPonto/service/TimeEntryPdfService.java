package dev.OsRapazes.BatPonto.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import dev.OsRapazes.BatPonto.entity.UserEntity;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import dev.OsRapazes.BatPonto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeEntryPdfService {

    private final TimeEntryService timeEntryService;
    private final UserRepository userRepository;
    private final TimeEntryReportFormatterService formatter;

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(LOCALE_PT_BR);

    public byte[] generateMyReportPdf(String authenticatedEmail, LocalDate from, LocalDate to) {
        TimeEntryReportResponseDto report = timeEntryService.getMyReport(authenticatedEmail, from, to);
        String html = buildHtml(report);
        return renderPdf(html);
    }

    public byte[] generateUserReportPdf(UUID targetUserId, String authenticatedEmail, LocalDate from, LocalDate to) {
        TimeEntryReportResponseDto report = timeEntryService.getUserReport(targetUserId, from, to, authenticatedEmail);
        String html = buildHtml(report);
        return  renderPdf(html);
    }

    public byte[] renderPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String normalized = html
                    .replace("\uFEFF", "") // remove BOM
                    .trim();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(normalized, null);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw BusinessException.unprocessable("PDF_GENERATION_FAILED", "Falha ao gerar PDF");
        }
    }

    private static String esc(String s) {
        if (s == null) return "NULL";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String buildHtml(TimeEntryReportResponseDto report) {

        String userName = userRepository.findById(report.id())
                .map(UserEntity::getName)
                .orElse("NULL");

        var rows = formatter.toDailyRows(report);

        String tableRows = rows.stream().map(r -> """
            <tr>
              <td>%s</td>
              <td>%s</td>
              <td class="center">%s</td>
              <td class="center">%s</td>
              <td class="center">%s</td>
              <td class="center">%s</td>
            </tr>
        """.formatted(
                esc(DATE_FMT.format(r.date())),
                esc(r.dayOfWeek()),
                esc(r.entry1()),
                esc(r.exit1()),
                esc(r.entry2()),
                esc(r.exit2())
        )).reduce("", String::concat);

        // placeholders MVP
        String empresa = "NULL";
        String codigoFuncionario = "NULL";
        String cpf = "NULL";

        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml" lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <style>
            body { font-family: Arial, sans-serif; font-size: 12px; color: #111; margin: 24px; }
            .header { border-bottom: 2px solid #111; padding-bottom: 10px; margin-bottom: 14px; }
            .title { font-size: 18px; font-weight: 700; margin: 0; }
            .subtitle { margin: 4px 0 0 0; color: #444; }

            .info { margin: 10px 0 14px 0; padding: 10px; border: 1px solid #ddd; border-radius: 8px; }
            .label { color: #555; font-size: 11px; margin-bottom: 2px; }
            .value { font-weight: 700; }

            table { width: 100%%; border-collapse: collapse; }
            th, td { border: 1px solid #e2e2e2; padding: 8px; }
            th { background: #f5f5f5; font-weight: 700; }
            tr:nth-child(even) { background: #fafafa; }
            .center { text-align: center; }

            /* tabela do bloco de dados do colaborador */
            .meta { width: 100%%; border-collapse: collapse; }
            .meta td { border: 0; padding: 4px 8px; vertical-align: top; }
          </style>
        </head>
        <body>
          <div class="header">
            <p class="title">Relatório de Ponto</p>
            <p class="subtitle">Período: %s a %s</p>
          </div>

          <div class="info">
            <table class="meta">
              <tr>
                <td>
                  <div class="label">Colaborador</div>
                  <div class="value">%s</div>
                </td>
                <td>
                  <div class="label">Empresa</div>
                  <div class="value">%s</div>
                </td>
                <td>
                  <div class="label">Código do Funcionário</div>
                  <div class="value">%s</div>
                </td>
                <td>
                  <div class="label">CPF</div>
                  <div class="value">%s</div>
                </td>
              </tr>
            </table>
          </div>

          <table>
            <thead>
              <tr>
                <th style="width: 15%%">Data</th>
                <th style="width: 25%%">Dia da semana</th>
                <th style="width: 15%%">Entrada 1</th>
                <th style="width: 15%%">Saída 1</th>
                <th style="width: 15%%">Entrada 2</th>
                <th style="width: 15%%">Saída 2</th>
              </tr>
            </thead>
            <tbody>
              %s
            </tbody>
          </table>

          <p style="margin-top:12px;color:#666;font-size:10px;">
            Gerado automaticamente • Fuso: %s
          </p>
        </body>
        </html>
        """.formatted(
                esc(DATE_FMT.format(report.from())),
                esc(DATE_FMT.format(report.to())),
                esc(userName),
                esc(empresa),
                esc(codigoFuncionario),
                esc(cpf),
                tableRows,
                esc(ZONE.getId())
        );
    }
}
