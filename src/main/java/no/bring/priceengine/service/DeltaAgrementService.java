package no.bring.priceengine.service;

import com.sun.xml.internal.ws.spi.db.DatabindingException;
import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.dao.Deltacontractdump;
import no.bring.priceengine.dao.Percentagebaseddeltadump;
import no.bring.priceengine.dao.Percentagebaseddump;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.precentagebased.ReadPercentageBasedDELTAFile;
import no.bring.priceengine.precentagebased.ReadPercentageBasedFile;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class DeltaAgrementService {

    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("MyLog");
    static FileHandler fh;

    public static void main(String[] str) throws ParseException {
        String fileLocation = null;
        String priceType = null;
        String fileCountry = null;
        String isZoneBased = null;
        System.out.println("Remark 5 means New agreement added in contractdump table.");
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            // This block configure the logger with handler and formatter
            fh = new FileHandler("C:\\temp\\MyLogFile-" + dtf.format(now) + ".log");    //C:\Giri\DELTA\1¨
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<String> nonCDHCustomers = new HashSet<String>();
        Scanner myObj = new Scanner(System.in);
        System.out.print("Please enter the XML file name along with the path location : ");
        fileLocation = myObj.nextLine();

        System.out.println("Please press 1 for slabbased and 2 for percentage based records and 3 for push data in price tables : ");
        Scanner myObj2 = new Scanner(System.in);
        priceType = myObj2.nextLine();

        System.out.println("Please enter country code of file : ");
        Scanner myObj3 = new Scanner(System.in);
        fileCountry = myObj3.nextLine();

        if (priceType.equals("1")) {
            System.out.println("Is addres is zone basesd  ? Press Y / N : ");
            Scanner myObj4 = new Scanner(System.in);
            isZoneBased = myObj4.nextLine();
        }

        QueryService queryService = new QueryService();
        DatabaseService databaseService = new DatabaseService();
        if (priceType.equals("1")) {

            List<Deltacontractdump> dumps = null;
            SlabbasedDeltaService slabbasedDeltaService = new SlabbasedDeltaService();

            ReadFile readFile = new ReadFile();
            if (isZoneBased.equalsIgnoreCase("N"))
                dumps = readFile.readDeltaFileData(fileLocation, fileCountry);
            else
                dumps = readFile.readZoneBasedDeltaFileData(fileLocation, fileCountry);
            Boolean isDataInserted = databaseService.upsertDeltaContracts(dumps, logger);
//            Boolean isDataInserted = true;
            if (isDataInserted) {
                slabbasedDeltaService.processDeltaAgreements(fileCountry,logger);
                System.out.println(" done ");
            }
        } else if (priceType.equals("2")) { // ReadPercentageBasedDELTAFile
            PercentagebasedDeltaService percentagebasedDeltaService = new PercentagebasedDeltaService();
//            ReadPercentageBasedDELTAFile readFile = new ReadPercentageBasedDELTAFile();
//            List<Percentagebaseddeltadump> dump = readFile.readFileData(fileLocation, fileCountry);
//             Boolean isDataInserted = databaseService.upsertPercentDeltaContracts(dump, logger);
            Boolean isDataInserted =  true;
            if (isDataInserted) {
                percentagebasedDeltaService.processDeltaAgreements(fileCountry,logger);
                System.out.println(" done ");
            }
        } else if (priceType.equals("3")) {
            PushDeltaAgreement pushDeltaAgreement = new PushDeltaAgreement();
            pushDeltaAgreement.deleteUpdatedPriceDetails(fileCountry);
            // pushDeltaAgreement.deleteUpdatedPriceDetailsPERCENT(fileCountry);


        }
    }
}

