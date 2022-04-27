package no.bring.priceengine.service;

import no.bring.priceengine.dao.Customervolfactordump;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadvolFactorFile {

    public List<Customervolfactordump> readFileData(String fileLocation){
        List<Customervolfactordump> contractdumps = new ArrayList<Customervolfactordump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum()+"...row num");

                    if(currentRow.getRowNum()==0){
                        currentRow = rows.next();
                    }
                    Customervolfactordump model = new Customervolfactordump();
                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    while (cellsInRow.hasNext()) {

                        Cell currentCell = cellsInRow.next();
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                model.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 1:
                                if(currentCell.getCellType().equals(CellType.STRING))
                                    model.setBranch(Integer.parseInt(currentCell.getStringCellValue()));
                                else {
                                    Double branch = currentCell.getNumericCellValue();
                                    if (null != branch)
                                        model.setBranch(branch.intValue());
                                }
                                break;
                            case 2:
                                if(currentCell.getCellType().equals(CellType.STRING))
                                model.setCustomerNumber(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                    Double service = currentCell.getNumericCellValue();
                                    model.setService(service.intValue());
                                }
                                break;
                            case 4:
                                if(currentCell.getCellType().equals(CellType.STRING))
                                    model.setDestinaiton(currentCell.getStringCellValue());
                                break;
                            case 5:
                                if(currentCell.getCellType().equals(CellType.NUMERIC))
                                model.setVolumetricFactor(currentCell.getNumericCellValue());
                                else if(currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue()!=""){
                                    Double d= new Double(currentCell.getStringCellValue().toString());
                                    model.setVolumetricFactor(d);
                                }

                                break;
                        }
                    }
                    model.setUpdated(false);
                    contractdumps.add(model);

                }
                workbook.close();
            }else{
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return contractdumps;
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            System.exit(1);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return contractdumps;
    }

}
