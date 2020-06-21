package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvertFormat {

    //Reference to the jobOverviewController.
    private JobOverviewController jobOverviewController;

    //Constructor, gives a reference back to jobOverviewController
    public ConvertFormat(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }

    /**
     * Is called by jobOverviewController to give a reference back to itself.
     *
     * @param jobOverviewController
     */
    public void setJobOverviewController(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }


    //Helper function
    public void echoAsCSV(Sheet sheet, File convertedFile, Workbook wb) {

        //Deletes the actual convertedUpdateFile if it exists, because the convert appends to the File.
        if (convertedFile.exists()) {
            convertedFile.delete();
        }

        DataFormatter objDefaultFormat = new DataFormatter();
        FormulaEvaluator objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) wb);

        Row row = null;

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {

                Cell cell = row.getCell(j);

                // This will evaluate the cell, And any type of cell will return string value.
                objFormulaEvaluator.evaluate(cell);
                String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);

                try {
                    //Write the row to the .csv file, by appending it.
                    FileUtils.writeStringToFile(convertedFile, "\"" + cellValueStr + "\",", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                //Writes the new line character into the .csv file, by appending it.
                FileUtils.writeStringToFile(convertedFile, "\n", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Reads the ConvertFormat File and translates it to csv.
    public void convertUpdateFileToCsv(Job job) {

        System.out.println("[Enter] convertUpdateFileToCsv");
        Platform.runLater(() -> job.getTextArea().appendText("Enter processing update file" + System.getProperty("line.separator")));


        File updateFile = job.getUpdateFile();

        //Extract input filename without extension.
        String updateFileNameWithoutExt = FilenameUtils.removeExtension(updateFile.toString());

        //Name the convertedFile to csv extension.
        File convertedUpdateFile = new File(updateFileNameWithoutExt + ".csv");

        InputStream inp = null;

        try {
            inp = new FileInputStream(updateFile);
            Workbook wb = WorkbookFactory.create(inp);

            //Only allow one sheet.
            for (int i = 0; i < 1; i++) {
                System.out.println(wb.getSheetAt(i).getSheetName());
                echoAsCSV(wb.getSheetAt(i), convertedUpdateFile, wb);
            }

        } catch (InvalidFormatException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inp.close();
            } catch (IOException ex) {
                Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        job.setUpdateFile(convertedUpdateFile);

        System.out.println("[Exit] convertUpdateFileToCsv");
        Platform.runLater(() -> job.getTextArea().appendText("Exit processing update file" + System.getProperty("line.separator")));

    }


    //Reads the ConvertFormat File and translates it to csv.
    public void convertDiscountFileToCsv(Job job) {

        System.out.println("[Enter] convertDiscountFileToCsv");
        Platform.runLater(() -> job.getTextArea().appendText("Enter processing discount file" + System.getProperty("line.separator")));


        File discountFile = job.getDiscountFile();

        //Extract input filename without extension.
        String updateFileNameWithoutExt = FilenameUtils.removeExtension(discountFile.toString());

        //Name the convertedFile to csv extension.
        File convertedDiscountFile = new File(updateFileNameWithoutExt + ".csv");

        InputStream inp = null;

        try {
            inp = new FileInputStream(discountFile);
            Workbook wb = WorkbookFactory.create(inp);

            //Only allow one sheet.
            for (int i = 0; i < 1; i++) {
                System.out.println(wb.getSheetAt(i).getSheetName());
                echoAsCSV(wb.getSheetAt(i), convertedDiscountFile, wb);
            }
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inp.close();
            } catch (IOException ex) {
                Logger.getLogger(ConvertFormat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        job.setDiscountFile(convertedDiscountFile);
        System.out.println("[Exit] convertDiscountFileToCsv");
        Platform.runLater(() -> job.getTextArea().appendText("Exit processing discount file" + System.getProperty("line.separator")));
    }
}
