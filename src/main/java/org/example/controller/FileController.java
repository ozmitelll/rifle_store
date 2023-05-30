package org.example.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.Rifle;
import org.example.repository.RifleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Controller
public class FileController {
    @Autowired
    private RifleRepository repository;

    private static final int BUFFER_SIZE = 4096;
    private String filePathPDF = "PDFFile.pdf";
    private String filePathExcel = "ExcelFile.xlsx";

    private enum TypeFile {
        PDF,
        EXCEL
    }

    @GetMapping("/download/pdf")
    public void getPDFFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            doDownload(request, response, filePathPDF, TypeFile.PDF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/download/excel")
    public void getExcelFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            doDownload(request, response, filePathExcel, TypeFile.EXCEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doDownload(HttpServletRequest request,
                           HttpServletResponse response, String filePath, TypeFile typeFile) throws IOException {
        if (typeFile == TypeFile.PDF) {
            createPDFFile(filePath);
        } else if (typeFile == TypeFile.EXCEL) {
            createExcelFile(filePath);
        } else
            return;

        ServletContext context = request.getServletContext();
        String appPath = context.getRealPath("");
        String fullPath = filePath;
        File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);
        String mimeType = context.getMimeType(fullPath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outStream.close();
    }


    private void createExcelFile(String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet(" Rifle Data ");
        XSSFRow row;
        Map<String, Object[]> rifleData = new TreeMap<String, Object[]>();
        rifleData.put("1", new Object[]{"Name", "Inventory Number"});
        int i = 1;
        for (Rifle rifle : repository.findAll()) {
            rifleData.put(String.valueOf(i + 1), new Object[]{rifle.getName(), rifle.getInventory_number()});
            i++;
        }
        Set<String> keyid = rifleData.keySet();
        int rowid = 0;
        for (String key : keyid) {
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = rifleData.get(key);
            int cellid = 0;
            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }
        FileOutputStream out = new FileOutputStream(filePath);
        workbook.write(out);
        out.close();
    }

    private void createPDFFile(String filePath) throws  IOException {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            doc.open();
            String text = "Report\n" +
                    "from 07/28/23 to 08/08/23 rifles have a rest\n";
            Paragraph paragraph = new Paragraph(text);
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            paragraph.setFont(new Font(
                    Font.FontFamily.HELVETICA, 10, Font.NORMAL));
            doc.add(paragraph);
            PdfPTable table = new PdfPTable(3);
            PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
            PdfPCell cell2 = new PdfPCell(new Phrase("Inventory Number"));
            table.addCell(cell1);
            table.addCell(cell2);
            for(Rifle rifle: repository.findAll())
            {
                PdfPCell cellName = new PdfPCell(new Phrase(rifle.getName()));
                PdfPCell cellSurname = new PdfPCell(new Phrase(rifle.getInventory_number()));
                table.addCell(cellName);
                table.addCell(cellSurname);
            }
            doc.add(table);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }




}


