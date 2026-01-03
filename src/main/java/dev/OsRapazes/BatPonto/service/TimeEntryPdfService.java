package dev.OsRapazes.BatPonto.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dev.OsRapazes.BatPonto.dto.TimeEntry.TimeEntryReportResponseDto;
import dev.OsRapazes.BatPonto.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TimeEntryPdfService {

    private final TimeEntryService timeEntryService;

    public byte[] generateMyReportPdf(String authenticatedEmail, java.time.LocalDate from, java.time.LocalDate to) {
        TimeEntryReportResponseDto report = timeEntryService.getMyReport(authenticatedEmail, from, to);
        String html = buildHtml(report);
        return renderPdf(html);
    }

    public byte[] generateUserReportPdf(java.util.UUID targetUserId, String authenticatedEmail, java.time.LocalDate from, java.time.LocalDate to) {
        // se você colocou bloqueio de RH no service do relatório de user, ótimo
        TimeEntryReportResponseDto report = timeEntryService.getUserReport(targetUserId, from, to, authenticatedEmail);
        String html = buildHtml(report);
        return renderPdf(html);
    }

    public byte[] renderPdf(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String normalized = html
                    .replace("\uFEFF", "")  // remove BOM
                    .trim();                 // remove espaços antes do root


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


    private String buildHtml(TimeEntryReportResponseDto report) {
        String rows = report.entries().stream()
                .map(e -> """
                <tr>
                  <td class="mono">%s</td>
                  <td>%s</td>
                  <td>%s</td>
                </tr>
            """.formatted(e.id(), e.type(), e.timestamp()))
                .reduce("", String::concat);

        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <html xmlns="http://www.w3.org/1999/xhtml" lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <style>
            body { font-family: Arial, sans-serif; font-size: 12px; }
            table { width: 100%%; border-collapse: collapse; }
            th, td { border: 1px solid #ddd; padding: 8px; }
            th { background: #f2f2f2; }
            .mono { font-family: monospace; font-size: 10px; }
          </style>
        </head>
        <body>
          <h2>Relatório de Ponto</h2>
          <p><strong>UserId:</strong> %s</p>
          <p><strong>Período:</strong> %s até %s</p>

          <h3>Colaborador</h3>
          <ul>
            <li><strong>Nome:</strong> NULL</li>
            <li><strong>CPF:</strong> NULL</li>
            <li><strong>Nascimento:</strong> NULL</li>
          </ul>

          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Tipo</th>
                <th>Timestamp</th>
              </tr>
            </thead>
            <tbody>
              %s
            </tbody>
          </table>
        </body>
        </html>
    """.formatted(report.id(), report.from(), report.to(), rows);
    }
}
