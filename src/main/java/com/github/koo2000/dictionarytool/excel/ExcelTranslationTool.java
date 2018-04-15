package com.github.koo2000.dictionarytool.excel;

import com.github.koo2000.dictionarytool.util.TranslationDictionary;
import com.github.koo2000.dictionarytool.util.WordNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelTranslationTool {

    private File dictionaryFile;
    private File inFile;
    private File outFile;

    private String dictionaryFileSheetName = "単語辞書";
    private int dictionaryFileStartRow = 1;
    private int dictionaryFileJapaneseCol = 0;
    private int dictionaryFileEnglishCol = 1;

    private String targetFileSheetName = "変換シート";
    private int targetFileStartRow = 1;
    private int targetFileJapaneseCol = 0;
    private int targetFileEnglishCol = 1;
    private int targetFileErrorCol = 2;

    public ExcelTranslationTool(File dictionaryFile, File inFile, File outFile) {
        this.dictionaryFile = dictionaryFile;
        this.inFile = inFile;
        this.outFile = outFile;
    }

    public int getDictionaryFileStartRow() {
        return dictionaryFileStartRow;
    }

    public void setDictionaryFileStartRow(int dictionaryFileStartRow) {
        this.dictionaryFileStartRow = dictionaryFileStartRow;
    }

    public int getDictionaryFileJapaneseCol() {
        return dictionaryFileJapaneseCol;
    }

    public void setDictionaryFileJapaneseCol(int dictionaryFileJapaneseCol) {
        this.dictionaryFileJapaneseCol = dictionaryFileJapaneseCol;
    }

    public int getDictionaryFileEnglishCol() {
        return dictionaryFileEnglishCol;
    }

    public void setDictionaryFileEnglishCol(int dictionaryFileEnglishCol) {
        this.dictionaryFileEnglishCol = dictionaryFileEnglishCol;
    }

    public String getTargetFileSheetName() {
        return targetFileSheetName;
    }

    public void setTargetFileSheetName(String targetFileSheetName) {
        this.targetFileSheetName = targetFileSheetName;
    }

    public int getTargetFileStartRow() {
        return targetFileStartRow;
    }

    public void setTargetFileStartRow(int targetFileStartRow) {
        this.targetFileStartRow = targetFileStartRow;
    }

    public int getTargetFileJapaneseCol() {
        return targetFileJapaneseCol;
    }

    public void setTargetFileJapaneseCol(int targetFileJapaneseCol) {
        this.targetFileJapaneseCol = targetFileJapaneseCol;
    }

    public int getTargetFileEnglishCol() {
        return targetFileEnglishCol;
    }

    public void setTargetFileEnglishCol(int targetFileEnglishCol) {
        this.targetFileEnglishCol = targetFileEnglishCol;
    }

    public int getTargetFileErrorCol() {
        return targetFileErrorCol;
    }

    public void setTargetFileErrorCol(int targetFileErrorCol) {
        this.targetFileErrorCol = targetFileErrorCol;
    }

    public int translate() {
        TranslationDictionary dictionary = new TranslationDictionary(loadDictionary());

        return translate(dictionary);
    }

    private int translate(TranslationDictionary dictionary) {
        Workbook workbook;

        try {
            workbook = WorkbookFactory.create(inFile);
        } catch (IOException e) {
            throw new RuntimeException("can't load input file [" + inFile + "]", e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("can't load input file [" + inFile + "]", e);
        }

        int errorLines = processWorkbook(workbook, dictionary);

        try (OutputStream os = new FileOutputStream(outFile)) {
            workbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException("write failed [" + outFile + "");
        }

        return errorLines;
    }

    private int processWorkbook(Workbook workbook, TranslationDictionary dictionary) {
        int errorLines = 0;

        Sheet inSheet = workbook.getSheet(targetFileSheetName);
        if (inSheet == null) {
            throw new RuntimeException("can't find sheet [" + targetFileSheetName + "]");
        }

        for (Row row :
                inSheet) {
            if (row.getRowNum() < targetFileStartRow) {
                continue;
            }

            Cell cell = row.getCell(targetFileJapaneseCol);
            Cell errorCell;
            errorCell = row.getCell(targetFileErrorCol);
            if (errorCell == null) {
                errorCell = row.createCell(targetFileErrorCol);
            }

            if (cell.getCellTypeEnum() != CellType.STRING) {
                errorCell.setCellValue("cell value is not type string.");
            }
            String japanese = cell.getStringCellValue();

            Cell englishCell;
            englishCell = row.getCell(targetFileEnglishCol);
            if (englishCell == null) {
                englishCell = row.createCell(targetFileEnglishCol);
            }

            try {
                englishCell.setCellValue(dictionary.translateToSnakeCase(japanese));
            } catch (WordNotFoundException e) {
                errorCell.setCellValue(e.getMessage());
                errorLines++;
            }
        }
        return errorLines;
    }

    private Map<String, String> loadDictionary() {
        Map<String, String> dictionary = new HashMap<>();

        Workbook dictionaryBook;
        try {
            dictionaryBook = WorkbookFactory.create(dictionaryFile);
        } catch (IOException e) {
            throw new RuntimeException("can't load dictionary file [" + dictionaryFile + "]", e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("can't load dictionary file [" + dictionaryFile + "]", e);
        }

        Sheet inSheet = dictionaryBook.getSheet(dictionaryFileSheetName);

        if (inSheet == null) {
            throw new RuntimeException("can't find sheet [" + dictionaryFileSheetName + "]");
        }

        for (Row row : inSheet) {
            if (row.getRowNum() < dictionaryFileStartRow) {
                continue;
            }
            Cell japaneseCell = row.getCell(dictionaryFileJapaneseCol);

            if (japaneseCell == null || japaneseCell.getCellTypeEnum() != CellType.STRING) {
                continue;
            }

            Cell englishCell = row.getCell(dictionaryFileEnglishCol);
            if (japaneseCell == null || japaneseCell.getCellTypeEnum() != CellType.STRING) {
                continue;
            }

            dictionary.put(japaneseCell.getStringCellValue(), englishCell.getStringCellValue());
        }
        return dictionary;
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("usage " + ExcelTranslationTool.class + " <dictionaryFile> <inputFile> <outputFile>");
            System.exit(1);
        }

        ExcelTranslationTool tool = new ExcelTranslationTool(
                new File(args[0]),
                new File(args[1]),
                new File(args[2])
        );

        int errorLines = tool.translate();

        if (errorLines == 0) {
            System.out.println("translation success!!");
            System.exit(0);
        } else {
            System.out.println("translation end with " + errorLines + " lines.");
            System.exit(1);
        }
    }
}
