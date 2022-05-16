package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.dao.Deltacontractdump;
import no.bring.priceengine.dao.Surchargedump;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ReadFile {


    public List<Contractdump> readFileData(String fileLocation, String country) {
        List<Contractdump> contractdumps = new ArrayList<Contractdump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum() + "...row num");

                    if (currentRow.getRowNum() == 0)
                        currentRow = rows.next();
                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Contractdump model = new Contractdump();
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
                                System.out.println("cell type not matched ::: ROW " + currentCell.getAddress().getRow() + "  :::: COLUMN :" + currentCell.getAddress().getColumn());
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    model.setOrganizationNumber(currentCell.getStringCellValue());
                                } else {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setOrganizationNumber(lo.toString());
                                }
                                break;
                            case 2:
                                model.setOrganizationName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    model.setCustomerNumber(currentCell.getStringCellValue());
                                } else {
                                    Double d = currentCell.getNumericCellValue();
                                    Long longVal = d.longValue();
                                    model.setCustomerNumber(longVal.toString());
                                }
                                break;
                            case 4:
                                // model.setArtikelgrupp(Integer.parseInt(currentCell.getStringCellValue()));
                                model.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 5:
                                Double div = null;
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    div = new Double(currentCell.getStringCellValue());
                                    model.setDiv(div.intValue());
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    div = new Double(currentCell.getNumericCellValue());
                                    model.setDiv(div.intValue());
                                } else
                                    model.setDiv(null);

                                break;
                            case 6:
                                Double artGroup = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    artGroup = currentCell.getNumericCellValue();
                                else if (currentCell.getCellType().equals(CellType.STRING))
                                    artGroup = new Double(currentCell.getStringCellValue());
                                if (null != artGroup)
                                    model.setArtikelgrupp(artGroup.intValue());
                                break;
                            case 7:
                                model.setStatGrupp(currentCell.getStringCellValue());
                                break;
                            case 8:
                                Double prodNo = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    prodNo = currentCell.getNumericCellValue();
                                else if (currentCell.getCellType().equals(CellType.STRING))
                                    prodNo = new Double(currentCell.getStringCellValue());
                                if (null != prodNo)
                                    model.setProdNo(prodNo.intValue());
                                break;
                            case 9:
                                model.setProdDescr(currentCell.getStringCellValue());
                                break;
                            case 10:
                                if (currentCell.getStringCellValue() != "")
                                    model.setRouteFrom(currentCell.getStringCellValue());
                                else
                                    model.setRouteFrom(null);
                                break;
                            case 11:
                                if (currentCell.getStringCellValue() != "")
                                    model.setRouteTo(currentCell.getStringCellValue());
                                else
                                    model.setRouteTo(null);
                                break;

                            case 12:
                                model.setRouteType(currentCell.getStringCellValue());
                                break;

//                            case 13:
//                                if(currentCell.getCellType().equals(CellType.STRING))
//                                model.setZoneType(currentCell.getStringCellValue());
//                                else if(currentCell.getCellType().equals(CellType.NUMERIC)) {
//                                    Double ds = new Double(currentCell.getNumericCellValue());
//                                    model.setZoneType(ds.toString());
//                                }
//                                break;

                            case 13:
                                if (currentCell.getCellType().equals(CellType.STRING))
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
                                if (currentCell.getCellType().equals(CellType.NUMERIC) && (Double) currentCell.getNumericCellValue() != null)
                                    model.setDscLimCd(((Double) currentCell.getNumericCellValue()).intValue());
                                else if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != null
                                        && currentCell.getStringCellValue() != "")
                                    model.setDscLimCd(Integer.parseInt(currentCell.getStringCellValue()));
                                break;
                            case 19:
                                model.setKgTill(currentCell.getStringCellValue());
                                break;
                            case 20:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "")
                                    model.setDiscLmtFrom(new Double(currentCell.getStringCellValue()));
                                else if (currentCell.getCellType().equals(CellType.NUMERIC) && (Double) currentCell.getNumericCellValue() != null)
                                    model.setDiscLmtFrom(currentCell.getNumericCellValue());
                                else
                                    model.setDiscLmtFrom(null);
                                break;
                            case 21:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "")
                                    model.setPrice(new Double(currentCell.getStringCellValue()));
                                else if (currentCell.getCellType().equals(CellType.NUMERIC) && (Double) currentCell.getNumericCellValue() != null)
                                    model.setPrice(currentCell.getNumericCellValue());
                                else
                                    model.setPrice(null);
                                break;
//                            case 22:
//                                if(((Double)currentCell.getNumericCellValue())!=null)
//                                model.setADsc(currentCell.getNumericCellValue());

                        }

                    }
                    model.setUpdated(false);
                    model.setFileCountry(country);
                    model.setEnabled(true);
                    contractdumps.add(model);

                }
                workbook.close();
            } else {
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return contractdumps;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return contractdumps;
    }


    public List<Contractdump> readZoneBasesFileData(String fileLocation, String country) {
        List<Contractdump> contractdumps = new ArrayList<Contractdump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum() + "...row num");

                    if (currentRow.getRowNum() == 0)
                        currentRow = rows.next();
                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Contractdump model = new Contractdump();
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
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setOrganizationNumber(lo.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    Long lo = (new Double(currentCell.getStringCellValue())).longValue();
                                    model.setOrganizationNumber(lo.toString());
                                }

                                break;
                            case 2:
                                model.setOrganizationName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setCustomerNumber(lo.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    Long lo = (new Double(currentCell.getStringCellValue())).longValue();
                                    model.setCustomerNumber(lo.toString());
                                }
                                break;
                            case 4:
                                // model.setArtikelgrupp(Integer.parseInt(currentCell.getStringCellValue()));
                                model.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 5:
                                Double div = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    div = currentCell.getNumericCellValue();
                                } else {
                                    div = new Double(currentCell.getStringCellValue());
                                }
                                model.setDiv(div.intValue());
                                break;
                            case 6:
                                Double artGroup = null;
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    artGroup = new Double(currentCell.getStringCellValue());
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    artGroup = currentCell.getNumericCellValue();
                                }
                                if (null != artGroup)
                                    model.setArtikelgrupp(artGroup.intValue());
                                break;
                            case 7:
                                model.setStatGrupp(currentCell.getStringCellValue());
                                break;
                            case 8:
                                Double prodNo = null;
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    prodNo = new Double(currentCell.getStringCellValue());
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    prodNo = currentCell.getNumericCellValue();
                                }
                                if (null != prodNo)
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
                                if (currentCell.getCellType().equals(CellType.STRING))
                                    model.setZoneType(currentCell.getStringCellValue());
                                else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Double ds = new Double(currentCell.getNumericCellValue());
                                    model.setZoneType(ds.toString());
                                }
                                break;

                            case 14:
                                if (currentCell.getCellType().equals(CellType.STRING))
                                    System.out.println(currentCell.getStringCellValue());
                                model.setFromDate(currentCell.getDateCellValue());
                                break;
                            case 15:
                                model.setToDate(currentCell.getDateCellValue());
                                break;
                            case 16:
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    String basePrice = currentCell.getStringCellValue();
                                    Double d = new Double(basePrice);
                                    model.setBasePrice(d);
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    model.setBasePrice(currentCell.getNumericCellValue());
                                }
                                break;
                            case 17:
                                model.setCurr(currentCell.getStringCellValue());
                                break;
                            case 18:
                                model.setPrUM(currentCell.getStringCellValue());
                                break;
                            case 19:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    if (null != ((Double) currentCell.getNumericCellValue()))
                                        model.setDscLimCd(((Double) currentCell.getNumericCellValue()).intValue());
                                } else if ((currentCell.getStringCellValue()) != null && (currentCell.getStringCellValue()) != "")
                                    model.setDscLimCd(Integer.parseInt(currentCell.getStringCellValue()));
                                break;
                            case 20:
                                model.setKgTill(currentCell.getStringCellValue());
                                break;
                            case 21:
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setDiscLmtFrom(currentCell.getNumericCellValue());
                                else if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "") {
                                    model.setDiscLmtFrom(new Double(currentCell.getStringCellValue()));
                                }
                                break;
                            case 22:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "") {
                                    String price = currentCell.getStringCellValue();
                                    model.setPrice(new Double(price));
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setPrice(currentCell.getNumericCellValue());
                                break;
//                            case 22:
//                                if(((Double)currentCell.getNumericCellValue())!=null)
//                                model.setADsc(currentCell.getNumericCellValue());

                        }

                    }
                    model.setUpdated(false);
                    model.setFileCountry(country);
                    model.setEnabled(true);
                    contractdumps.add(model);

                }
                workbook.close();
            } else {
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return contractdumps;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return contractdumps;
    }

    public List<Deltacontractdump> readZoneBasedDeltaFileData(String fileLocation, String country) {
        List<Deltacontractdump> deltacontractdumps = new ArrayList<Deltacontractdump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum() + "...row num");

                    if (currentRow.getRowNum() == 0)
                        currentRow = rows.next();
                    Deltacontractdump deltaModel = new Deltacontractdump();
                    //    System.out.println("color ################################## "+ currentRow.getCell(3).getCellStyle().getFillBackgroundColorColor());
                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    while (cellsInRow.hasNext()) {

                        Cell currentCell = cellsInRow.next();
                        switch (currentCell.getColumnIndex()) {
                            case 0:
                                //model.setId((int)currentCell.getNumericCellValue());
                                break;
                            case 1:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    deltaModel.setOrganizationNumber(lo.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    Long lo = (new Double(currentCell.getStringCellValue())).longValue();
                                    deltaModel.setOrganizationNumber(lo.toString());
                                }

                                break;
                            case 2:
                                deltaModel.setOrganizationName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    deltaModel.setCustomerNumber(lo.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    Long lo = (new Double(currentCell.getStringCellValue())).longValue();
                                    deltaModel.setCustomerNumber(lo.toString());
                                }
                                break;
                            case 4:
                                deltaModel.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 5:
                                Double div = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    div = currentCell.getNumericCellValue();
                                } else {
                                    div = new Double("9999");
                                }
                                deltaModel.setDiv(div.intValue());
                                break;
                            case 6:
                                Double artGroup = new Double(9999);
                                break;
                            case 7:
                                deltaModel.setStatGrupp("9999");
                                break;
                            case 8:
                                Double prodNo = null;
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    prodNo = new Double(currentCell.getStringCellValue());
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    prodNo = currentCell.getNumericCellValue();
                                }
                                if (null != prodNo)
                                    deltaModel.setProdNo(prodNo.intValue());
                                break;
                            case 9:
                                deltaModel.setProdDescr(isEmpty(currentCell.getStringCellValue()) ? "NULL" : currentCell.getStringCellValue());
                                break;
                            case 10:
                                deltaModel.setRouteFrom(isEmpty(currentCell.getStringCellValue()) ? "NULL" : currentCell.getStringCellValue());
                                break;
                            case 11:
                                deltaModel.setRouteTo(isEmpty(currentCell.getStringCellValue()) ? "NULL" : currentCell.getStringCellValue());
                                break;
                            case 12:
                                deltaModel.setRouteType(isEmpty(currentCell.getStringCellValue()) ? "NULL" : currentCell.getStringCellValue());
                                break;

                            case 13:
                                if (currentCell.getCellType().equals(CellType.STRING))
                                    deltaModel.setZoneType(currentCell.getStringCellValue());
                                else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Double ds = new Double(currentCell.getNumericCellValue());
                                    deltaModel.setZoneType(ds.toString());
                                }
                                break;

                            case 14:
                                if (currentCell.getCellType().equals(CellType.STRING) && !isEmpty(currentCell.getStringCellValue())) {
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = formatter.parse(currentCell.getStringCellValue());
                                    deltaModel.setFromDate(date);
                                } else {
                                    deltaModel.setFromDate(currentCell.getDateCellValue());
                                }
                                break;
                            case 15:
                                if (currentCell.getCellType().equals(CellType.STRING) && !isEmpty(currentCell.getStringCellValue())) {
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = formatter.parse(currentCell.getStringCellValue());
                                    deltaModel.setToDate(date);
                                } else {
                                    deltaModel.setToDate(currentCell.getDateCellValue());
                                }
                                break;
                            case 16:
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    String basePrice = currentCell.getStringCellValue();
                                    Double d = new Double(basePrice);
                                    deltaModel.setBasePrice(d);
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    deltaModel.setBasePrice(currentCell.getNumericCellValue());
                                }
                                break;
                            case 17:
                                deltaModel.setCurr(currentCell.getStringCellValue());
                                break;
                            case 18:
                                deltaModel.setPrUM(currentCell.getStringCellValue());
                                break;
                            case 19:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    if (null != ((Double) currentCell.getNumericCellValue()))
                                        deltaModel.setDscLimCd(((Double) currentCell.getNumericCellValue()).intValue());
                                } else if ((currentCell.getStringCellValue()) != null && (currentCell.getStringCellValue()) != "")
                                    deltaModel.setDscLimCd(Integer.parseInt(currentCell.getStringCellValue()));
                                break;
                            case 20:
                                deltaModel.setKgTill(currentCell.getStringCellValue());
                                break;
                            case 21:
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    deltaModel.setDiscLmtFrom(currentCell.getNumericCellValue());
                                else if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "") {
                                    deltaModel.setDiscLmtFrom(new Double(currentCell.getStringCellValue() == null ? "-1" : currentCell.getStringCellValue()));
                                }
                                break;
                            case 22:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "") {
                                    String price = currentCell.getStringCellValue();
                                    deltaModel.setPrice(new Double(price));
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    deltaModel.setPrice(currentCell.getNumericCellValue());
                                break;

                        }
                    }
                    deltaModel.setUpdated(false);
                    deltaModel.setFileCountry(country);
                    deltaModel.setEnabled(true);
                    deltaModel.setCreateddate(new Date());
                    deltaModel.setRemark(null);
                    deltacontractdumps.add(deltaModel);

                }
                workbook.close();
            } else {
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return deltacontractdumps;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return deltacontractdumps;
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public List<Surchargedump> readSurchargeFileData(String fileLocation) {
        List<Surchargedump> surchargedumps = new ArrayList<Surchargedump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum() + "...row num");

                    if (currentRow.getRowNum() == 0)
                        currentRow = rows.next();
                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Surchargedump model = new Surchargedump();
                    //    System.out.println("color ################################## "+ currentRow.getCell(3).getCellStyle().getFillBackgroundColorColor());
                    Iterator<Cell> cellsInRow = currentRow.iterator();
                    while (cellsInRow.hasNext()) {

                        Cell currentCell = cellsInRow.next();
                        //    System.out.println("cell value..."+currentCell.getColumnIndex());
                        switch (currentCell.getColumnIndex()) {

                            case 0:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    model.setCustomerNumber(lo.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    model.setCustomerNumber(currentCell.getStringCellValue());
                                }
                                break;

                            case 1:
                                model.setCustomerName(currentCell.getStringCellValue());
                                break;

                            case 2:
                                model.setSulphurParcel(currentCell.getStringCellValue());
                                break;
                            case 3:
                                model.setSulphurParcelPallet(currentCell.getStringCellValue());
                                break;
                            case 4:
                                model.setFuelParcel(currentCell.getStringCellValue());
                                break;
                            case 5:
                                model.setFuelPallet(currentCell.getStringCellValue());
                                break;
                            case 6:
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Double d = new Double(currentCell.getNumericCellValue());
                                    d = d * 100;
                                    BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_DOWN);
                                    model.setPercentageFuelParcel(bd.toString());
                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    if (currentCell.getStringCellValue().toCharArray().length > 0)
                                        model.setPercentageFuelParcel(currentCell.getStringCellValue());

                                } else
                                    model.setPercentageFuelParcel(null);
                                break;
                            case 7://
                                if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    Double d = new Double(currentCell.getNumericCellValue());
                                    d = d * 100;
                                    BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_DOWN);
                                    model.setPercentageFuelPallet(bd.toString());

                                } else if (currentCell.getCellType().equals(CellType.STRING)) {
                                    if (currentCell.getStringCellValue().toCharArray().length > 0)
                                        model.setPercentageFuelPallet(currentCell.getStringCellValue());

                                } else
                                    model.setPercentageFuelPallet(null);
                                break;
                            case 8:
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    model.setBranch((new Double(currentCell.getNumericCellValue())).intValue());

                        }
                    }
                    model.setUpdated(false);
                    model.setEnabled(true);
                    surchargedumps.add(model);

                }
                workbook.close();
            } else {
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return surchargedumps;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surchargedumps;
    }


    public List<Deltacontractdump> readDeltaFileData(String fileLocation, String country) {
        List<Deltacontractdump> deltacontractdumps = new ArrayList<Deltacontractdump>();
        try {
            FileInputStream file = new FileInputStream(new File(fileLocation));
            //Create Workbook instance holding reference to .xlsx file
            if (null != file) {
                Workbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                while (rows.hasNext()) {
                    Row currentRow = rows.next();
                    System.out.println(currentRow.getRowNum() + "...row num");

                    if (currentRow.getRowNum() == 0)
                        currentRow = rows.next();
                    //    System.out.println(currentRow.getRowNum()+"...new row num");
                    Deltacontractdump deltaModel = new Deltacontractdump();
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
                                System.out.println("cell type not matched ::: ROW " + currentCell.getAddress().getRow() + "  :::: COLUMN :" + currentCell.getAddress().getColumn());
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    deltaModel.setOrganizationNumber(currentCell.getStringCellValue());
                                } else {
                                    Long lo = (new Double(currentCell.getNumericCellValue())).longValue();
                                    deltaModel.setOrganizationNumber(lo.toString());
                                }
                                break;
                            case 2:
                                deltaModel.setOrganizationName(currentCell.getStringCellValue());
                                break;

                            case 3:
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    deltaModel.setCustomerNumber(currentCell.getStringCellValue());
                                } else {
                                    Double d = currentCell.getNumericCellValue();
                                    Long longVal = d.longValue();
                                    deltaModel.setCustomerNumber(longVal.toString());
                                }
                                break;
                            case 4:
                                deltaModel.setCustomerName(currentCell.getStringCellValue());
                                break;
                            case 5:
                                Double div = null;
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    div = new Double(currentCell.getStringCellValue());
                                    deltaModel.setDiv(div.intValue());
                                } else if (currentCell.getCellType().equals(CellType.NUMERIC)) {
                                    div = new Double(currentCell.getNumericCellValue());
                                    deltaModel.setDiv(div.intValue());
                                } else
                                    deltaModel.setDiv(null);

                                break;
                            case 6:
                                Double artGroup = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    artGroup = currentCell.getNumericCellValue();
                                else if (currentCell.getCellType().equals(CellType.STRING))
                                    artGroup = new Double(currentCell.getStringCellValue());
                                if (null != artGroup)
                                    deltaModel.setArtikelgrupp(artGroup.intValue());
                                break;
                            case 7:
                                deltaModel.setStatGrupp(currentCell.getStringCellValue());
                                break;
                            case 8:
                                Double prodNo = null;
                                if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    prodNo = currentCell.getNumericCellValue();
                                else if (currentCell.getCellType().equals(CellType.STRING))
                                    prodNo = new Double(currentCell.getStringCellValue());
                                if (null != prodNo)
                                    deltaModel.setProdNo(prodNo.intValue());
                                break;
                            case 9:
                                deltaModel.setProdDescr(currentCell.getStringCellValue());
                                break;
                            case 10:
                                if (currentCell.getStringCellValue() != "")
                                    deltaModel.setRouteFrom(currentCell.getStringCellValue());
                                else
                                    deltaModel.setRouteFrom("NULL");
                                break;
                            case 11:
                                if (currentCell.getStringCellValue() != "")
                                    deltaModel.setRouteTo(currentCell.getStringCellValue());
                                else
                                    deltaModel.setRouteTo("NULL");
                                break;

                            case 12:
                                deltaModel.setRouteType(currentCell.getStringCellValue());
                                break;

//                            case 13:
//                                if(currentCell.getCellType().equals(CellType.STRING))
//                                model.setZoneType(currentCell.getStringCellValue());
//                                else if(currentCell.getCellType().equals(CellType.NUMERIC)) {
//                                    Double ds = new Double(currentCell.getNumericCellValue());
//                                    model.setZoneType(ds.toString());
//                                }
//                                break;

                            case 13:
                                if (currentCell.getCellType().equals(CellType.STRING) && !isEmpty(currentCell.getStringCellValue())) {
                                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = formatter.parse(currentCell.getStringCellValue());
                                    deltaModel.setFromDate(date);
                                } else {
                                    deltaModel.setFromDate(currentCell.getDateCellValue());
                                }
                                break;
                            case 14:
                                if (currentCell.getCellType().equals(CellType.STRING)) {
                                    String value = currentCell.getStringCellValue().replace(".", "-");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    deltaModel.setToDate(sdf.parse(value));
                                } else
                                    deltaModel.setToDate(currentCell.getDateCellValue());
                                break;
                            case 15:
                                    deltaModel.setBasePrice(currentCell.getNumericCellValue());
                                break;
                            case 16:
                                deltaModel.setCurr(currentCell.getStringCellValue());
                                break;
                            case 17:
                                deltaModel.setPrUM(currentCell.getStringCellValue());
                                break;
                            case 18:
                                if (currentCell.getCellType().equals(CellType.NUMERIC) && (Double) currentCell.getNumericCellValue() != null)
                                    deltaModel.setDscLimCd(((Double) currentCell.getNumericCellValue()).intValue());
                                else if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != null
                                        && currentCell.getStringCellValue() != "")
                                    deltaModel.setDscLimCd(Integer.parseInt(currentCell.getStringCellValue()));
                                break;
                            case 19:
                                deltaModel.setKgTill(currentCell.getStringCellValue());
                                break;
                            case 20:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "")
                                    deltaModel.setDiscLmtFrom(new Double(currentCell.getStringCellValue()));
                                else if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    deltaModel.setDiscLmtFrom(currentCell.getNumericCellValue());
                                else
                                    deltaModel.setDiscLmtFrom(Double.valueOf("-1"));
                                break;
                            case 21:
                                if (currentCell.getCellType().equals(CellType.STRING) && currentCell.getStringCellValue() != "")
                                    deltaModel.setPrice(new Double(currentCell.getStringCellValue()));
                                else if (currentCell.getCellType().equals(CellType.NUMERIC))
                                    deltaModel.setPrice(currentCell.getNumericCellValue());
                                else
                                    deltaModel.setPrice(null);
                                break;
//                            case 22:
//                                if(((Double)currentCell.getNumericCellValue())!=null)
//                                model.setADsc(currentCell.getNumericCellValue());

                        }
                    }
                    deltaModel.setUpdated(false);
                    deltaModel.setFileCountry(country);
                    deltaModel.setEnabled(true);
                    deltaModel.setRemark(null);
                    deltaModel.setCreateddate(new Date());
                    deltacontractdumps.add(deltaModel);

                }
                workbook.close();
            } else {
                System.out.println("File not found, please try again.");
                System.exit(0);
            }
            return deltacontractdumps;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return deltacontractdumps;
    }


}