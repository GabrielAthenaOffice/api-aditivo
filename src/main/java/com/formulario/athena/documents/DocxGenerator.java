package com.formulario.athena.documents;

import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Map;

public class DocxGenerator {

    public static byte[] generateDoc(String templatePath, Map<String, String> values) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        for (Map.Entry<String, String> entry : values.entrySet()) {
                            String placeholder = "{{" + entry.getKey() + "}}";
                            if (text.contains(placeholder)) {
                                text = text.replace(placeholder, entry.getValue());
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }

            document.write(bos);
            return bos.toByteArray();
        }
    }
}
