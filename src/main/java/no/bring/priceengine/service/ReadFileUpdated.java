package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdumpservice;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadFileUpdated {



    public List<Contractdumpservice> readFileData(String fileLocation, String country){
        List<Contractdumpservice> contractdumps = new ArrayList<Contractdumpservice>();
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

                    if(currentRow.getRowNum()==0)
                        currentRow = rows.next();
                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Contractdumpservice model = new Contractdumpservice();
                    //    System.out.println("color ################################## "+ currentRow.getCell(3).getCellStyle().getFillBackgroundColorColor());
                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    while (cellsInRow.hasNext()) {

                        Cell currentCell = cellsInRow.next();
                        //    System.out.println("cell value..."+currentCell.getColumnIndex());
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                //model.setId((int)currentCell.getNumericCellValue());
                                break;
                            case 1:
//                                Double organizationNumber = currentCell.getNumericCellValue();
//                                Long longOrgNumber = organizationNumber.longValue();
//                                model.setOrganizationNumber(longOrgNumber.toString());
                                Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                model.setOrganizationNumber(lo.toString());

                                break;
                            case 2:
                                model.setOrganizationName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                Double d = currentCell.getNumericCellValue();
                                Long longVal = d.longValue();
                                model.setCustomerNumber(longVal.toString());
                                break;
                            case 4:
                                // model.setArtikelgrupp(Integer.parseInt(currentCell.getStringCellValue()));
                                model.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 5:
                                Double div = currentCell.getNumericCellValue();
                                model.setDiv(div.intValue());
                                break;
                            case 6:
                                Double artGroup  = currentCell.getNumericCellValue();
                                if(null!=artGroup)
                                    model.setArtikelgrupp(artGroup.intValue());
                                break;
                            case 7:
                                model.setStatGrupp(currentCell.getStringCellValue());
                                break;
                            case 8:
                                Double prodNo  = currentCell.getNumericCellValue();
                                if(null!=prodNo)
                                    model.setProdNo(prodNo.intValue());
                                break;
                            case 9:
                                model.setProdDescr(currentCell.getStringCellValue());
                                break;
                            case 10:
                                model.setRouteFrom(currentCell.getStringCellValue());
                                break;
                            case 11:
                                model.setRouteTo(currentCell.getStringCellValue());
                                break;

                            case 12:
                                model.setRouteType(currentCell.getStringCellValue());
                                break;

                            case 13:
                                if(currentCell.getCellType().equals(CellType.STRING))
                                    System.out.println(currentCell.getStringCellValue());
                                model.setFromDate(currentCell.getDateCellValue());
                                break;
                            case 14:
                                model.setToDate(currentCell.getDateCellValue());
                                break;
                            case 15:
                                model.setBasePrice(currentCell.getNumericCellValue());
                                break;
                            case 16:
                                model.setCurr(currentCell.getStringCellValue());
                                break;
                            case 17:
                                model.setPrUM(currentCell.getStringCellValue());
                                break;
                            case 18:
                                if(((Double)currentCell.getNumericCellValue())!=null)
                                    model.setDscLimCd(((Double)currentCell.getNumericCellValue()).intValue());
                                break;
                            case 19:
                                model.setKgTill(currentCell.getStringCellValue());
                                break;
                            case 20:
                                model.setDiscLmtFrom(currentCell.getNumericCellValue());
                                break;
                            case 21:
                                model.setPrice(currentCell.getNumericCellValue());
                                break;
//                            case 22:
//                                if(((Double)currentCell.getNumericCellValue())!=null)
//                                model.setADsc(currentCell.getNumericCellValue());

                        }

                    }
                    model.setUpdated(false);
                    model.setFileCountry(country);
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
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return contractdumps;
    }

}
