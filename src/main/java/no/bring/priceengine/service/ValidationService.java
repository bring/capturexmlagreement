package no.bring.priceengine.service;

import cdh.CustomerCacheServiceImpl;
import cdh.CustomerModel;
import no.bring.priceengine.dao.Price;
import no.bring.priceengine.dao.SlabBasedPriceEntry;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.JPAUtil;
import no.bring.priceengine.util.PriceEngineConstants;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

public class ValidationService {
	
    enum  ContractProcessing {
        PURE_COUNTRY_CODE,
        COUNTRY_CODE_FULL_POSTAL_CODE,
        COUNTRY_CODE_PARTIAL_POSTAL_CODE,
        STANDARD_ZONE,
        CUSTOM_ZONE,
        SURCHARE_EXEMPTION,
        COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE,
        ALL_SOURCE_ALL_DESTINATION
    }

    private static DatabaseService databaseService = new DatabaseService();

    public static void main(String[] str){
        // check data exist in select * from core.cdcustomcountryroutetp
        ValidationService validationService = new ValidationService();
        ValidationService.disableNonCDHCustomers("NO");
        validationService.fixItemInPrice();
        validationService.updateAllContractPriceForProcessing();

    }

    private static void disableNonCDHCustomers(String fileCountry){
        CustomerCacheServiceImpl impl = new CustomerCacheServiceImpl();
        Map<String, CustomerModel> cdhCustomerRecords  =  impl.refreshCDH();
        databaseService.disableNonCDHCustomers(fileCountry, cdhCustomerRecords);
    }


    public void fixItemInPrice(){
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createNativeQuery("SELECT price.price_id, price.item_id,  cp.item_id_dup FROM core.price price inner join CORE.CONTRACTPRICE cp on price.price_id = cp.price_id and price.item_id!= cp.item_id_dup").getResultList();
        Iterator listIterator = query.listIterator();
        while(listIterator.hasNext()) {
            Object[] price = (Object[]) listIterator.next();
            Integer price_id = Integer.parseInt(price[0].toString());
            Integer priceItemId=   Integer.parseInt(price[1].toString());
            Integer contractPriceItemId =  Integer.parseInt(price[2].toString());
            if(!priceItemId.equals(contractPriceItemId))
                updateItemInPriceTable(price_id, contractPriceItemId);
        }
        System.out.println("Data inserted/updated successfully....");
        entityManager.close();

    }

    @Transactional
    public void updateItemInPriceTable(Integer priceId, Integer contractPriceItemId){
        try {
            Class.forName(PriceEngineConstants.DB_CONNECTION_DRIVER);
            Connection connection = DriverManager.getConnection(PriceEngineConstants.DB_CONNECTION_URL, PriceEngineConstants.DB_CONNECTION_USERNAME, PriceEngineConstants.DB_CONNECTION_PASSWORD);
            PreparedStatement stmt = connection.prepareStatement("update core.price set item_id=? where price_id = ?");
            stmt.setInt(1, contractPriceItemId);
            stmt.setInt(2,  priceId);
            stmt.executeUpdate();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateAllContractPriceForProcessing(){

        List<SlabBasedPriceEntry> entries   = new ArrayList<SlabBasedPriceEntry>();
        StringBuilder builder = new StringBuilder();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside getAllContractPrice() ");
        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();
        List<Query> query = entityManager.createNativeQuery("select * from core.contractprice WHERE  contractprice_processing_tp_cd is null").getResultList();
        Iterator listIterator = query.listIterator();
        while(listIterator.hasNext()){

            Object[] object = (Object[]) listIterator.next();
            String fromCountry = null;
            String toCountry = null;
            String fromPostalCode = null;
            String toPostalCode = null;
            String fromZone = null;
            String toZone = null;
            String zoneIdCustom = null;
            String priceId = null;

            if(object[0]!=null)
                priceId =  object[0].toString();
            if(object[11]!=null)
                fromCountry = object[11].toString();
            if(object[12]!=null)
                fromPostalCode = object[12].toString();
            if(object[13]!=null)
                toCountry = object[13].toString();
            if(object[14]!=null)
                toPostalCode = object[14].toString();
            if(object[15]!=null)
                fromZone = object[15].toString();
            if(object[16]!=null)
                toZone = object[16].toString();
            if(object[19]!=null)
                zoneIdCustom = object[19].toString();

            String processingType = filterRouteByProcesingTypes(fromCountry, fromPostalCode, toCountry, toPostalCode, fromZone, toZone, zoneIdCustom);
            if(null!=processingType && !processingType.equals("NOT IN ANY TYPE")) {
                updateContractPriceProcessingDetails(object[0].toString(), Integer.parseInt(processingType));
                builder.append(object[0].toString()+",");
            }else{
                System.out.println("PROCESSING TYPE NOT FOUND FOR PRICE_ID -------------- "+ priceId );
                System.exit(1);
            }
            System.out.println("Price ID - "+ object[0].toString() +" :: Processing type - "+ processingType);
        }
        System.out.println(builder.toString());
        entityManager.close();

    }


    private String filterRouteByProcesingTypes(String fromCountry, String fromPostalCode,  String toCountry,
                                               String toPostalCode, String fromZone, String toZone, String customZone){

        String processingType = null;
        Boolean isFULLFromPostalCodeAvailable =false;
        Boolean isFULLToPostalCodeAvailable =false;

        Boolean isPARTIALFromPostalCodeAvailable =false;
        Boolean isPARTIALToPostalCodeAvailable =false;
        ///  TYPE 8 ALL TO ALL
        if(null==fromCountry && null==toCountry && null==fromPostalCode && null==toPostalCode &&
                null==fromZone && null==toZone && null==customZone) {
            processingType = ValidationService.ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        } else if(null!=fromCountry && null==toCountry && null==fromPostalCode && null==toPostalCode &&
                null==fromZone && null==toZone && null==customZone) {
            processingType = ValidationService.ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        } else if(null==fromCountry && null!=toCountry && null==fromPostalCode && null==toPostalCode &&
                null==fromZone && null==toZone && null==customZone) {
            processingType = ValidationService.ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        // TYPE 1 --  PURE COUNTRY CODE
        else if(null!=fromCountry && null!=toCountry && null==fromPostalCode && null==toPostalCode &&
                null==fromZone && null==toZone && null==customZone) {
            processingType = ValidationService.ContractProcessing.PURE_COUNTRY_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode==null && toPostalCode!=null && toPostalCode.toCharArray().length>3){
            isFULLFromPostalCodeAvailable= true;
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode==null && fromPostalCode.toCharArray().length>3){
            isFULLFromPostalCodeAvailable= true;
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode!=null && fromPostalCode.toCharArray().length>3
                && toPostalCode.toCharArray().length>3){
            isFULLFromPostalCodeAvailable= true;
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();

        }

        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode==null && toPostalCode!=null && toPostalCode.toCharArray().length<4){

            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode==null && fromPostalCode.toCharArray().length<4){
            isFULLFromPostalCodeAvailable= true;
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode!=null && fromPostalCode.toCharArray().length<4
                && toPostalCode.toCharArray().length<4){
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }

        /////// TYPE 7 COUNTRY CODE AND COMBINED FULL AND PARTIAL POSTAL CODE

        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode!=null && fromPostalCode.toCharArray().length>3 && toPostalCode.toCharArray().length<4){
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        else if(null!=fromCountry && null!=toCountry && fromPostalCode!=null && toPostalCode!=null && fromPostalCode.toCharArray().length<4 && toPostalCode.toCharArray().length>3){
            processingType = ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        // END TYPE 7

        else if(fromZone!=null || toZone!=null || customZone!=null) {
            if(customZone!=null) {
                processingType = ValidationService.ContractProcessing.CUSTOM_ZONE.toString();
                processingType = getProcessingId().get(processingType).toString();
            }else if (fromZone!=null && toZone!=null) {
                processingType = ValidationService.ContractProcessing.STANDARD_ZONE.toString();
                processingType = getProcessingId().get(processingType).toString();
            }
        }
        else
            processingType = "NOT IN ANY TYPE";

        return  processingType;

    }

    @Transactional
    private void updateContractPriceProcessingDetails(String priceId, Integer processingId){
        try {

            Class.forName(PriceEngineConstants.DB_CONNECTION_DRIVER);
            Connection connection = DriverManager.getConnection(PriceEngineConstants.DB_CONNECTION_URL, PriceEngineConstants.DB_CONNECTION_USERNAME, PriceEngineConstants.DB_CONNECTION_PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(DatabaseService.UPDATE_CONTRACTPRICE_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1,processingId);
            stmt.setLong(2, new Long(priceId));
            int i = stmt.executeUpdate();
            connection.close();
            return;

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    private HashMap<String, Integer> getProcessingId(){
        HashMap<String, Integer> processingMap = new HashMap<>();

        processingMap.put(ValidationService.ContractProcessing.PURE_COUNTRY_CODE.toString(),1);
        processingMap.put(ValidationService.ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString(),2);
        processingMap.put(ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString(),3);
        processingMap.put(ValidationService.ContractProcessing.STANDARD_ZONE.toString(),4);
        processingMap.put(ValidationService.ContractProcessing.CUSTOM_ZONE.toString(),5);
        processingMap.put(ValidationService.ContractProcessing.SURCHARE_EXEMPTION.toString(),6);
        processingMap.put(ValidationService.ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString(),7);
        processingMap.put(ValidationService.ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString(),8);
        return processingMap;
    }

}
