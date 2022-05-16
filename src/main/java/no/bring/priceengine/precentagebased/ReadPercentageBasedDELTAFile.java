package no.bring.priceengine.precentagebased;

import no.bring.priceengine.dao.Percentagebaseddeltadump;
import no.bring.priceengine.dao.Percentagebaseddump;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ReadPercentageBasedDELTAFile {

    public List<Percentagebaseddeltadump> readFileData(String fileLocation, String filecountry){
        List<Percentagebaseddeltadump> contractdumps = new ArrayList<Percentagebaseddeltadump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheet("Sheet1");
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum()+"...row num");

                    if(currentRow.getRowNum()==0){
                        currentRow = rows.next();
//                        currentRow = rows.next();
                    }
                    if(currentRow.getRowNum()==1){
                        currentRow = rows.next();
//                        currentRow = rows.next();
                    }

                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Percentagebaseddeltadump model = new Percentagebaseddeltadump();

                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    while (cellsInRow.hasNext()) {

                        Cell currentCell = cellsInRow.next();
                        //    System.out.println("cell value..."+currentCell.getColumnIndex());
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                Integer branch = null;
                                if(currentCell.getCellType().equals(CellType.NUMERIC))
                                    branch = Integer.parseInt(new DataFormatter().formatCellValue(currentCell));
                                else if(currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue()!="")
                                    branch = Integer.parseInt(new DataFormatter().formatCellValue(currentCell));
                                model.setBranch(branch);
                                break;
                            case 1:
                                if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setParentCustomerNumber(lo.toString());
                                }
                                else if(currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue()!="")
                                    model.setParentCustomerNumber(currentCell.getStringCellValue()); ;
                                break;
                            case 2:
                                model.setParentCustomerName(currentCell.getStringCellValue()); ;
                                break;
                            case 3:
                                if(currentCell.getCellType().equals(CellType.NUMERIC)){
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setCustomerNumber(lo.toString());
                                }
                                else if(currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue()!="")
                                    model.setCustomerNumber(currentCell.getStringCellValue()); ;
                                break;
                            case 4:
                                model.setCustomerName(currentCell.getStringCellValue()); ;
                                break;

                            case 5:
                                if(currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setProdno(new Double(currentCell.getNumericCellValue()).intValue());
                                else if(currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue()!="")
                                    model.setProdno(Integer.parseInt(currentCell.getStringCellValue()));
                                break;

                            case 6:
                                if (currentCell.getCellType().equals(CellType.STRING) && !isEmpty(currentCell.getStringCellValue())) {
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = formatter.parse(currentCell.getStringCellValue());
                                    model.setStartdate(date);
                                } else {
                                    model.setStartdate(currentCell.getDateCellValue());
                                }

                                break;
                            case 7:
                                if (currentCell.getCellType().equals(CellType.STRING) && !isEmpty(currentCell.getStringCellValue())) {
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = formatter.parse(currentCell.getStringCellValue());
                                    model.setEnddate(date);
                                } else {
                                    model.setEnddate(currentCell.getDateCellValue());
                                }
                                break;
                            case 8:
                                model.setRouteType(currentCell.getStringCellValue());
                                break;

                            case 9:
                                model.setZoneType(currentCell.getStringCellValue());
                                break;

                            case 10:
                                if(currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    if(new Double(currentCell.getNumericCellValue()).toString()!="")
                                        model.setFromLocation(new Double(currentCell.getNumericCellValue()).toString());
                                    else
                                        model.setFromLocation(null);
                                }else if(currentCell.getCellType().equals(CellType.STRING)) {
                                    model.setFromLocation(currentCell.getStringCellValue());
                                }
                                break;
                            case 11:
                                if(currentCell.getCellType().equals(CellType.STRING))
                                    model.setToLocation(currentCell.getStringCellValue());
                                else if(currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setToLocation(new Double(currentCell.getNumericCellValue()).toString());
                                break;
                            case 12:
                                if(currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setPrecentageDiscount(new Double(currentCell.getNumericCellValue()).toString());
                                else if(currentCell.getCellType().equals(CellType.STRING)) {
                                    String price = currentCell.getStringCellValue();
                                    if(price.contains(","))
                                        price = price.replace(",",".");
                                    if(price.contains("-"))
                                    price = price.replace("-","");
                                    model.setPrecentageDiscount(new Double(price).toString());
                                }
                                break;
                        }

                    }
                    model.setUpdated(false);
                    model.setEnabled(true);
                    model.setCreatedDt(new Date());
                    model.setFileCountry(filecountry);
                    contractdumps.add(model);

                }
                workbook.close();
            }else{
                System.out.println("File not found, please try again.");
                System.exit(1);
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

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }
}
