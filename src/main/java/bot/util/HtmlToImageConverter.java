package bot.util;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Component
public class HtmlToImageConverter {

    /**
     * Конвертирует HTML в изображение PNG
     *
     * @param html   HTML-контент
     * @param width  ширина изображения в пикселях
     * @param height высота изображения в пикселях
     * @return массив байтов изображения PNG
     * @throws IOException
     */
    public byte[] convertHtmlToImage(String html, int width, int height) throws IOException {
        // Создаем временный PDF файл
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        // Генерируем PDF из HTML
        generatePdfFromHtml(html, pdfOutputStream);

        // Конвертируем PDF в изображение
        BufferedImage image = convertPdfToImage(new ByteArrayInputStream(pdfOutputStream.toByteArray()), width, height);

        // Сохраняем изображение в байтовый массив
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return baos.toByteArray();

    }

    /**
     * Генерирует PDF из HTML
     */
    private void generatePdfFromHtml(String html, OutputStream outputStream) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(outputStream);  // Исправлено: используем поток напрямую
        FSSupplier<InputStream> fontStream = () -> getClass().getResourceAsStream("/font/DejaVuSans.ttf");
        builder.useFont(fontStream, "DejaVu Sans");

        try {
            builder.run();
        } catch (Exception e) {
            throw new IOException("Ошибка при генерации PDF из HTML", e);
        } finally {
            try {
                outputStream.close(); // Закрываем здесь, но убедимся, что не закрыли раньше
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Конвертирует первую страницу PDF в BufferedImage
     */
    private BufferedImage convertPdfToImage(ByteArrayInputStream pdfFile, int width, int height) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        try {
            // Рендерим первую страницу с указанным DPI
            float dpi = 300f; // DPI для хорошего качества

            return pdfRenderer.renderImageWithDPI(0, dpi, ImageType.RGB);
        } finally {
            document.close();
        }
    }

    /**
     * Пример создания таблицы результатов турнира
     */
    public String generateTournamentTableHtml(String[][] results) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'></meta>\n");
        html.append("    <title>Таблица результатов</title>\n");
        html.append("    <style>\n");
        html.append("        @page {\n");
        html.append("            size: A4 landscape; /* Горизонтальная ориентация */\n");
        html.append("            margin: 10mm;\n");
        html.append("        }\n");
        html.append("        body { font-family: 'DejaVu Sans', sans-serif; margin: 5px; }\n");
        html.append("        table {\n");
        html.append("            border-collapse: collapse;\n");
        html.append("            width: 90%;\n");
        html.append("            font-size: 8px; /* Уменьшаем размер шрифта */\n");
        html.append("        }\n");
        html.append("        th, td {\n");
        html.append("            border: 1px solid #ddd;\n");
        html.append("            padding: 3px 5px; /* Уменьшаем отступы */\n");
        html.append("            text-align: center;\n");
        html.append("            white-space: nowrap; /* Запрещаем перенос текста */\n");
        html.append("        }\n");
        html.append("        th {\n");
        html.append("            background-color: #4CAF50;\n");
        html.append("            color: white;\n");
        html.append("        }\n");
        html.append("        @media print {\n");
        html.append("            table { page-break-inside: avoid; }\n");
        html.append("        }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <h3 style='font-size: 14px; margin-bottom: 8px;'>Результаты кругового турнира</h3>\n");
        html.append("    <table>\n");

        // Заголовки таблицы
        html.append("        <tr>\n");
//        html.append("            <th>Очки</th>\n");
        html.append("        </tr>\n");

        // Данные таблицы
        for (String[] row : results) {
            html.append("        <tr>\n");
            for (String cell : row) {
                html.append("            <td>").append(cell).append("</td>\n");
            }
            html.append("        </tr>\n");
        }

        html.append("    </table>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }
}