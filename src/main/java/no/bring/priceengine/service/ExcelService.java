package no.bring.priceengine.service;

import cdh.CustomerCacheService;
import cdh.CustomerCacheServiceImpl;
import cdh.CustomerModel;
import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.precentagebased.PercentageBasedExcelService;
import no.bring.priceengine.precentagebased.PercentageDatabaseService;
import no.bring.priceengine.precentagebased.ReadPercentageBasedFile;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelService {

    public static final String BASE_PRICE = "BasePrice";
    public static final String SLABBASED_PRICE = "SlabBasedPrice";
    public static final String PERCENTAGE_PRICE = "PercentageBasePrice";
    public static final String SERCHARGE_EXEMPTION = "SurchargeExemption";
    private static Set<Integer> unmappedItems = new HashSet<>();
    private static CustomerCacheService customerCacheService;


    public static void main(String[] args) {
        try {
            CustomerCacheServiceImpl impl = new CustomerCacheServiceImpl();
            ExcelService excelService = new ExcelService();

//            Map<String, CustomerModel> cdhCustomerRecords  =  impl.refreshCDH();
          //  CustomerModel customerModel =  cdhCustomerRecords.get("20001270527");
           // System.out.println(customerModel.getOrganizationNumber());
            Scanner myObj = new Scanner(System.in);
            String fileLocation = "C:\\Users\\POT30559\\Downloads\\Price_Lists\\PL_DK_fra_01-01-2020_NY.xlsx";
            String priceType = null;
            String fileCountry = null;
            String isZoneBased = null;

            StringBuilder errorLog = new StringBuilder();

            System.out.println("Hi! This Jar will read data from the given excel sheet and insert/update the data from excelsheet to core.Contractdump table.");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");

            System.out.print("Please enter the XML file name along with the path location : ");
            fileLocation = myObj.nextLine();

            System.out.println("Please press 1 for slabbased and 2 for percentage based  and Press 3 for surcharge exemption : ");
            Scanner myObj2 = new Scanner(System.in);
            priceType = myObj2.nextLine();

            System.out.println("Please enter country code of file , left blank for surcharge exemption : ");
            Scanner myObj3 = new Scanner(System.in);
            fileCountry = myObj3.nextLine();
            Set<String> nonCDHCustomers = new HashSet<String>();
            if (!priceType.equals("3")) {
                System.out.println("Is addres is zone basesd  ? Press Y / N : ");
                Scanner myObj4 = new Scanner(System.in);
                isZoneBased = myObj4.nextLine();
            }

            QueryService queryService = new QueryService();
            DatabaseService databaseService = new DatabaseService();
            if (priceType.equals("1")) {

//                ReadFile readFile = new ReadFile();
//                List<Contractdump> dumps = null;
//                if(isZoneBased.equalsIgnoreCase("N"))
//                    dumps = readFile.readFileData(fileLocation, fileCountry);
//                else
//                    dumps = readFile.readZoneBasesFileData(fileLocation, fileCountry);
//               Boolean isDataInserted = databaseService.upsertContractData(dumps);
              Boolean isDataInserted = true;
                if (isDataInserted) {

             //       databaseService.disableNonCDHCustomers(fileCountry, cdhCustomerRecords);

                     List<Contractdump> contractdumpsAll = queryService.findAllContractdumps(fileCountry);// currently zone type check is enabled
                   //  List<Contractdump> contractdumpsInserted = queryService.findContractByCustomer("20003449996");

                    HashMap<String, Set<String>> partyMap = filterOrgnizationAndCustomers(contractdumpsAll);// Map of organization level


                    ArrayList<Contractdump> dumpsForDisable = new ArrayList<Contractdump>();

                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // fIRST TIME ENTRY OF NEW CUSTOMERS-
                    databaseService.upsertPartyDetailsNEW(partyMap);

                            ArrayList<String>  organizationList = queryService.filterOrganizations(partyMap.keySet());
                    for (String organization : organizationList) {
    //                    if(cdhCustomerRecords.containsKey(organization)){
                        HashMap<String, ArrayList<Contractdump>> contractdumpMapByCustomers =  new HashMap<String, ArrayList<Contractdump>>();
                        ArrayList<Contractdump> contractdumps = queryService.findContractdumpByOrganization(organization);
                                contractdumpMapByCustomers.put(organization, contractdumps);
                        HashMap<String, Set<String>> customerByOrgMap = filterCustomerByOrganizationUPDATED(organization, contractdumps);
                        int counter = 0;

                        Contract persistedContract = null;
                        ContractComponent persistedContractComponent = null;
                        ArrayList<ContractRole> roles = new ArrayList<ContractRole>();
                            List<Contractdump> contractdumpsByCustomers = queryService.findContractdumpsByOrganization(organization);
                            if (!contractdumpsByCustomers.isEmpty()){
                                if (counter == 0) {
                                    // DATA TO  CONTRACT TABLES START INSERTING HERE
                                    Party party = queryService.findChildPartyBySSPK(organization);
                                    if (party == null)
                                     party = queryService.findPartyByParentSSPK(organization);
                                    if (party == null) {
                                        System.out.println("Party is not a Parent custmer.  Check database...... " +  organization );
                                        System.exit(1);
                                    }
                                    Contract contract = null;

                                    try {
                                        if (null == party)
                                            System.out.println("wait.. Party not available");
                                            contract = databaseService.buildContract(contractdumpsByCustomers.get(0), party);
                                    } catch (IndexOutOfBoundsException iob) {
                                        iob.printStackTrace();
                                        System.exit(1);
                                    }
                                    persistedContract = queryService.findContract(contract.getSourceSystemRecordPk());
                                    if(null!=persistedContract){
                                        persistedContractComponent = queryService.findContractComponent(persistedContract);
                                        if(persistedContractComponent.getEndDt()==null)
                                            databaseService.addEndDateContractComponent(persistedContractComponent, contractdumpsByCustomers.get(0));
                                            // check what end date is getting set and its correct ???

                                    }else{
                                        Long id = databaseService.insertContract(contract);
                                        if(null!=id){
                                            persistedContract = queryService.findContractById(id);// Contract inserted here
                                            ContractComponent contractComponent = new ContractComponent();
                                            contractComponent = databaseService.buildContractComponent(persistedContract, contractdumpsByCustomers.get(0), party);
                                            persistedContractComponent = databaseService.insertContractComponent(contractComponent);// ContractComponenet inserted
                                            counter++;
                                        }else{
                                            System.out.println("Some issue occured");
                                        }
                                    }
                                }
                            // INSERT ROLES
                                if(organization.contains("20003449996"))
                                    System.out.println("break");

                            Set<String> customers = customerByOrgMap.get(organization);
                                List<ContractRole> existingContractRoles =  queryService.findContractRoles(persistedContractComponent);
                                if(existingContractRoles.isEmpty()) {
                                    for (String customer : customers) {
                                        Party partyForContractRole = queryService.findPartyBySSPK(customer.split("~")[0]);// THIS METHOD HAS CHANGED.. CONDITION OF PARENT = NULL IS REMOVE .. BE CAREFUL AND VALIDATE
                                        ContractRole contractRole = databaseService.buildContractRole(persistedContractComponent, contractdumpsByCustomers.get(0), partyForContractRole, customer);
                                        ContractRole persistedContractRole = null;
                                        Long contractRoleId = null;
                                        contractRole.setContractComponent(persistedContractComponent);

                                        contractRoleId = databaseService.insertContractRole(contractRole);
                                        persistedContractRole = queryService.findContractRole(contractRoleId);
                                        roles.add(persistedContractRole);
                                    }
                                }
                                else if(existingContractRoles.size()< customers.size()){
                                    for(String customer  :  customers){
                                        Boolean status = false;
                                        for(ContractRole role :  existingContractRoles){
                                            if(role.getPartySourceSystemRecordPk().equals(customer.split("~")[0]))
                                                status = true;
                                        }
                                        if(status==false){
                                            Party partyForContractRole = queryService.findPartyBySSPK(customer.split("~")[0]);
                                            if(partyForContractRole!=null) {
                                                System.out.println("waits");
                                                ContractRole contractRole = databaseService.buildContractRole(persistedContractComponent, contractdumpsByCustomers.get(0), partyForContractRole, customer);
                                                ContractRole persistedContractRole = null;
                                                Long contractRoleId = null;
                                                contractRole.setContractComponent(persistedContractComponent);
                                                contractRoleId = databaseService.insertContractRole(contractRole);
                                                persistedContractRole = queryService.findContractRole(contractRoleId);
                                                roles.add(persistedContractRole);
                                            }else{
                                                errorLog.append("Party rule voilates for sspk =" + customer +" and " +  organization +"  ......");
                                            }

                                        }
                                    }
                                }else if(existingContractRoles.size()> customers.size()){
                                    System.out.println("Some issue  is here for Party ,... ");
                                }
                            /// *******************************************************************
                            /// WORKING FINE TILL HERE-- NEED TO INSERT PRICE ONLY
                        }else{
                                System.out.println("updated already");
                            }
                        // for pricing as per discussion
                                HashMap<Integer, ArrayList<Contractdump>> contractDumpsByServiceMap = filterMapByService(contractdumps);
                                if (!contractDumpsByServiceMap.isEmpty()) {
                                    Set<Integer> services = contractDumpsByServiceMap.keySet();
                                    for (Integer serviceId : services) {
                                        if (null != serviceId) {
                                                Item item = queryService.getItem(serviceId);
                                                Boolean isServicePassiveReturned = false;
                                                Integer serviceIdData = null;
                                                if(item==null){
                                                    if(serviceId.toString().substring(serviceId.toString().length()-2, serviceId.toString().length()).equals("64")){
                                                        isServicePassiveReturned = true;
                                                        serviceId  = Integer.parseInt(serviceId.toString().substring(0, serviceId.toString().length()-2));
                                                        item  = queryService.getItem(serviceId);
                                                        serviceIdData = Integer.parseInt(serviceId.toString()+"64");
                                                    }
                                                }
                                                if (null != item) {
                                                    ArrayList<Contractdump> contractdumpsByService = new ArrayList<Contractdump>();
                                                    if(serviceIdData!=null)
                                                        contractdumpsByService = contractDumpsByServiceMap.get(serviceIdData);
                                                        else
                                                            contractdumpsByService = contractDumpsByServiceMap.get(serviceId);
                                                        if(contractdumpsByService.get(0).getCustomerNumber().equals("20011576004") && contractdumpsByService.get(0).getProdNo().equals(331))
                                                            System.out.println("wait");

                                                    HashMap<String, ArrayList<Contractdump>> contractMapByLocation = filterCustomerBasedDumpBySourceDestination(contractdumpsByService);
                                                    Set<String> locations = contractMapByLocation.keySet();
                                                    for (String location : locations) {
                                                        ArrayList<Contractdump> contractdumpsByLocation = contractMapByLocation.get(location);
                                                        HashMap<String, ArrayList<Contractdump>> contractdumpsByDateMap = filterCustomerByDate(contractdumpsByLocation);
                                                        for (String dateStr : contractdumpsByDateMap.keySet()) {
                                                            ArrayList<Contractdump> contractdumpsByDateList = contractdumpsByDateMap.get(dateStr);
                                                            if (!contractdumpsByDateList.isEmpty()) {
                                                                HashMap<String, ArrayList<Contractdump>> mapByPriceType = identifyPriceType(contractdumpsByDateList);
                                                                if (mapByPriceType.containsKey(BASE_PRICE)) {
                                                                    ArrayList<Contractdump> dumpsByBase = mapByPriceType.get(BASE_PRICE);
                                                                    dumpsByBase = validateDuplicateOrgCount(dumpsByBase);
                                                                    for (Contractdump contractdump : dumpsByBase) {
                                                                        Long priceId = databaseService.insertPrice(contractdump, item, BASE_PRICE);
                                                                        try {
                                                                            databaseService.insertContractPrice(contractdump, item, priceId, persistedContractComponent, isServicePassiveReturned);
                                                                        } catch (NullPointerException ne) {
                                                                            ne.printStackTrace();
                                                                            System.out.println(" contractdump table id :  " + contractdump.getId());
                                                                            System.exit(1);
                                                                        }
                                                                    }
                                                                    databaseService.disableContractdumpEntry(dumpsByBase);
                                                                    databaseService.fixItemInPrice();
                                                                } else if (mapByPriceType.containsKey(SLABBASED_PRICE)) {
                                                                    ArrayList<Contractdump> dumpsBySlab = mapByPriceType.get(SLABBASED_PRICE);
                                                                    dumpsBySlab = validateDuplicateOrgCount(dumpsBySlab);
                                                                    Long priceId = databaseService.insertPrice(dumpsBySlab.get(0), item, SLABBASED_PRICE);
                                                                    databaseService.insertContractPrice(dumpsBySlab.get(0), item, priceId, persistedContractComponent, isServicePassiveReturned);
                                                                    Long slabbasedId = null;
                                                                    if (priceId != null)
                                                                        slabbasedId = databaseService.insertSlabbasedPrice(priceId, dumpsBySlab.get(0));
                                                                    if (slabbasedId != null) {
//                                                                        if (dumpsBySlab.size() > 1) {
//                                                                            databaseService.insertSlabbasedPriceEntries(dumpsBySlab, slabbasedId);
//                                                                        } else {
                                                                        if(null!=dumpsBySlab.get(0).getDscLimCd() && dumpsBySlab.get(0).getDscLimCd()==3)
                                                                            databaseService.insertSlabbasedPriceEntries(dumpsBySlab, slabbasedId);
                                                                        else if(null!=dumpsBySlab.get(0).getDscLimCd() && dumpsBySlab.get(0).getDscLimCd()==2)
                                                                           databaseService.insertSlabbasedPriceEntriesForItem(dumpsBySlab, slabbasedId);
//                                                                          }

                                                                        databaseService.disableContractdumpEntry(dumpsBySlab);
                                                                        dumpsForDisable.addAll(dumpsBySlab);
                                                                        databaseService.fixItemInPrice();
                                                                    } else
                                                                        System.out.println("########################### error occured while inserting data in  slabbasedprice");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    databaseService.fixItemInPrice();
                                                           databaseService.disableContractdumpEntry(contractdumpsByService);
                                                    databaseService.fixItemInPrice();
                                                        } else {
                                                    if(serviceIdData!=null)
                                                        unmappedItems.add(serviceIdData);
                                                        else
                                                            unmappedItems.add(serviceId);
                                                        }
                                                    }
                                                }
                                            }else{
                                    System.out.println("contractDumpsByServiceMap found enpty .. check here  " + contractDumpsByServiceMap);
                                }

                                /// loop for cdh
//                                        }else{
//                                    nonCDHCustomers.add(organization);

                        //}

                                        }

                                  //  }
                                //}
                            //}//
                if(!dumpsForDisable.isEmpty())
                    databaseService.disableContractdumpEntry(dumpsForDisable);
                    databaseService.updateAllContractPriceForProcessing();
                    databaseService.fixItemInPrice();
                }
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");

                System.out.println(" VIOLATION IN PARTY " + errorLog.toString());
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" Unmapped SERVICE IDS-   ");
                for (Integer unmappedId : unmappedItems) {
                    System.out.print(unmappedId + ",");
                }
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" REMINDER :::: Also run an update end_dt to infinity for all entries for contractcomponent and contractrole tables");
                System.out.println("Run below query after updating FILECOUNTRY");
                System.out.println("select * from core.price where price_id in (select price_id from core.contractprice where contractcomponent_id in (select contractcomponent_id from core.contractcomponent where contract_id in ( select distinct contract_id from core.contract where source_system_record_pk in( select ORGANIZATION_NUMBER from core.contractdump where filecountry='"+fileCountry +"' AND UPDATED=TRUE ))) and base_price is not null order by item_id, base_price");


            }
            else if (priceType.equals("2")) {

//                ReadPercentageBasedFile readFile = new ReadPercentageBasedFile();
//                PercentageDatabaseService percentageDatabaseService = new PercentageDatabaseService();
//                List<Percentagebaseddump> dump = readFile.readFileData(fileLocation, fileCountry);
//                Boolean isDataInserted = percentageDatabaseService.insertContractData(dump);
                Boolean isDataInserted =  true;
                if (isDataInserted) {
                    List<Percentagebaseddump> contractdumps = queryService.findAllPercentageBasedContractdumps(fileCountry);

        //            databaseService.disableNonCDHCustomersPercent(fileCountry, cdhCustomerRecords);
               //     List<Percentagebaseddump> contractdumps = queryService.findPercentageBasedContractdumpsByCustomer("20000195881");
                     HashMap<String, Set<String>> partyMap = filterOrgnizationAndCustomersPercentageBased(contractdumps);
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    // Uncomment only if new organization and customers identified
                    databaseService.upsertPartyDetailsNEW(partyMap);

                    PercentageBasedExcelService percentageBasedExcelService = new PercentageBasedExcelService();
                            percentageBasedExcelService.performActionOnDump(contractdumps, partyMap);

                }

            }
            else if (priceType.equals("3")){
                ReadFile readFile = new ReadFile();

                List<Surchargedump> dump = readFile.readSurchargeFileData(fileLocation);
               Boolean isDataInserted = databaseService.upsertSurchargeData(dump);
//                Boolean isDataInserted =  true;
      //          databaseService.disableNonCDHCustomersPercent(fileCountry, cdhCustomerRecords);
                if(isDataInserted){
                    FuelService fuelService = new FuelService();
                    fuelService.saveSurchargeExemptionData();
                }
            }
        } catch (HibernateException he) {
            he.printStackTrace();
              System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Remarks if incase data not saved - 1 -2 ");
            System.out.println("--------------------------------------------------------");
            System.out.println("Remark 1 - Customer NOT available in CDH ");
            System.out.println("--------------------------------------------------------");
            System.out.println("Remark 2 - Service VAS Combination doest not exist. ");
            System.out.println("---------------------------------------------------------");
        }

    }

    private HashMap<String, ArrayList<Contractdump>> contractdumpReorderd(List<Contractdump> contractdumps) {
        HashMap<String, ArrayList<Contractdump>> map = new HashMap<String, ArrayList<Contractdump>>();
        ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
        for (Contractdump contractdump : contractdumps) {
            String key = null;
            if (null != contractdump.getRouteFrom() && !contractdump.getRouteFrom().equals("")) {
                key = contractdump.getRouteFrom() + "-" + contractdump.getRouteTo();
                if (map.containsKey(key)) {
                    map.get(key).add(contractdump);
                } else {
                    dumpList = new ArrayList<Contractdump>();
                    dumpList.add(contractdump);
                    map.put(key, dumpList);
                }
            } else {
                key = contractdump.getCustomerName();
                if (map.containsKey(key)) {
                    map.get(key).add(contractdump);
                } else {
                    dumpList = new ArrayList<Contractdump>();
                    dumpList.add(contractdump);
                    map.put(key, dumpList);
                }
            }
        }
        if (!map.isEmpty()) {
            for (String destinations : map.keySet()) {

            }
        }

        return map;
    }

    private HashMap<String, ArrayList<Contractdump>> contractdumpReorderdByPriceList(List<Contractdump> contractdumps) {
        HashMap<String, ArrayList<Contractdump>> map = new HashMap<String, ArrayList<Contractdump>>();
        ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
        for (Contractdump contractdump : contractdumps) {
            String key = null;
            // if(null!=contractdump.getRouteFrom() && !contractdump.getRouteFrom().equals("")) {
            key = contractdump.getCustomerName();
            if (map.containsKey(key)) {
                map.get(key).add(contractdump);
            } else {
                dumpList = new ArrayList<Contractdump>();
                dumpList.add(contractdump);
                if (dumpList.isEmpty())
                    System.out.println("Break... list cannot be empty");
                map.put(key, dumpList);
            }
            //}
        }
        return map;
    }

    private void deteminePriceType(HashMap<String, ArrayList<Contractdump>> map) {
        if (!map.isEmpty()) {
            Set<String> keySet = map.keySet();
            for (String sourceDestination : keySet) {
                ArrayList<Contractdump> contractdumps = map.get(sourceDestination);
                if (contractdumps.size() > 1) {

                }
            }
        }
    }

//    private String createNewPKID(String contractPKID){
//        Integer id = Integer.parseInt(contractPKID.substring(2, contractPKID.length()));
//        int zeros = contractPKID.substring(2, contractPKID.length()).length() - contractPKID.substring(2, contractPKID.length()).replaceAll("0", "").length();
//        String finalVal =  "CB";
//        id = id++;
//        int length =  id.toString().length();
//        for(int i=0;i<zeros;i++){
//            finalVal = finalVal+"0";
//        }
//        finalVal = finalVal+id;
//       return finalVal;
//    }

    private static String createNewPKID(String contractPKID) {
        Integer a = Integer.parseInt(contractPKID.replaceAll("CB", ""));
        a = a + 1;
        String finalVal = "CB" + a;
        return finalVal;
    }

    private static ArrayList<Contractdump> filterMixedPriceTypes(ArrayList<Contractdump> contractdumps) {
        ArrayList<Contractdump> filteredOut = new ArrayList<Contractdump>();
        ArrayList<Contractdump> filteredIn = new ArrayList<Contractdump>();
        for (Contractdump contractdump : contractdumps) {
            if (null == contractdump.getDiscLmtFrom()) {
                filteredOut.add(contractdump);
            } else
                filteredIn.add(contractdump);
        }
        contractdumps.clear();
        contractdumps.addAll(filteredIn);
        return filteredOut;

    }

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomers(List<Contractdump> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Contractdump contractdump : contractdumps) {
                if (contractdump.getOrganizationNumber().equals(contractdump.getCustomerNumber())){
                    if (!orgMap.containsKey(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName())) {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                        orgMap.put(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName(), customerList);
                    } else {
                        if (!orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName()))
                            orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                    }
                } else {
                    if (orgMap.containsKey(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName())) {
                        if (!orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName()))
                            orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                        orgMap.put(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomersPercentageBased(List<Percentagebaseddump> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Percentagebaseddump contractdump : contractdumps) {
                if (contractdump.getParentCustomerNumber().equals(contractdump.getCustomerNumber())){
                    if (!orgMap.containsKey(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName())) {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                        orgMap.put(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName(), customerList);
                    } else {
                        if (!orgMap.get(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName()))
                            orgMap.get(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                    }
                } else {
                    if (orgMap.containsKey(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName())) {
                        if (!orgMap.get(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName()))
                            orgMap.get(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());
                        orgMap.put(contractdump.getParentCustomerNumber() + "~" + contractdump.getParentCustomerName(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }

    private static HashMap<String, ArrayList<Contractdump>> filterDumpByOrganization(List<Contractdump> contractdumps, Set<String> organizationDetails) {
        HashMap<String, ArrayList<Contractdump>> dumps = new HashMap<String, ArrayList<Contractdump>>();

        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String customerDetail : organizationDetails) {
                String customerId = customerDetail.split("~")[0];
                for (Contractdump contractdump : contractdumps) {
                    if (!contractdump.isUpdated() && contractdump.isEnabled()) {
                        if (contractdump.getOrganizationNumber().equals(customerId)) {
                            if (dumps.containsKey(customerDetail)) {
                                dumps.get(customerDetail).add(contractdump);
                            } else {
                                ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
                                dumpList.add(contractdump);
                                dumps.put(customerDetail, dumpList);
                            }
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, ArrayList<Contractdump>> filterDumpByOrganizationUPDATED(List<Contractdump> contractdumps, Set<String> customerDetails, String organizationNumber) {
        HashMap<String, ArrayList<Contractdump>> dumps = new HashMap<String, ArrayList<Contractdump>>();

        if (!contractdumps.isEmpty() && !customerDetails.isEmpty()) {
            for (String customerDetail : customerDetails) {
                String customerId = customerDetail.split("~")[0];
                for (Contractdump contractdump : contractdumps) {
                    if (!contractdump.isUpdated() && contractdump.isEnabled()) {
                        if (contractdump.getCustomerNumber().equals(customerId) && contractdump.getOrganizationNumber().equals(organizationNumber)) {
                            if (dumps.containsKey(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName())) {
                                dumps.get(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName()).add(contractdump);
                            } else {
                                ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
                                dumpList.add(contractdump);
                                dumps.put(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName() , dumpList);
                            }
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, Set<String>> filterCustomerByOrganizationUPDATED(String organizationNumber , ArrayList<Contractdump> contractdumps) {
        HashMap<String, Set<String>> dumps = new HashMap<String, Set<String>>();
        Set<String> customerSet = new HashSet<String>();

        if (!contractdumps.isEmpty()) {
                for (Contractdump contractdump : contractdumps) {
                        customerSet.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                    }
               }
        dumps.put(organizationNumber, customerSet);

        return dumps;
    }

    private static HashMap<String, Set<String>> filterCustomerByOrganization(List<Contractdump> contractdumps, Set<String> organizationDetails) {
        HashMap<String, Set<String>> dumps = new HashMap<String, Set<String>>();

        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String orgDetail : organizationDetails) {
                String orgId = orgDetail.split("~")[0];
                for (Contractdump contractdump : contractdumps) {
                              if (contractdump.getOrganizationNumber().equals(orgId)) {
                    if (dumps.containsKey(orgDetail)) {
                        dumps.get(orgDetail).add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                    } else {
                        Set<String> dumpList = new HashSet<String>();
                        dumpList.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                        dumps.put(orgDetail, dumpList);
                            }
                        }
                     }
            }
        }
        return dumps;
    }

    private static HashMap<String, ArrayList<Contractdump>> filterCustomerBasedDumpByServiceId(ArrayList<Contractdump> contractdumps){
        HashMap<String, ArrayList<Contractdump>> contractdumpMap = new HashMap<String, ArrayList<Contractdump>>();
        for(Contractdump contractdump : contractdumps) {
            Integer serviceId = contractdump.getProdNo();
            if (null != serviceId) {
                if (contractdumpMap.containsKey(serviceId.toString())) {
                    contractdumpMap.get(serviceId.toString()).add(contractdump);
                } else {
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(serviceId.toString(), list);
                }
            }else{
                if(contractdumpMap.containsKey(null)){
                    contractdumpMap.get(null).add(contractdump);
                }else{
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(null, list);
                }
            }
        }
        return contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdump>> filterCustomerByDate(ArrayList<Contractdump> contractdumps){
        HashMap<String, ArrayList<Contractdump>> contractdumpMap = new HashMap<String, ArrayList<Contractdump>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(Contractdump contractdump : contractdumps){
            String startdate = sdf.format(contractdump.getFromDate());
            if(contractdumpMap.containsKey(startdate)){
                contractdumpMap.get(startdate).add(contractdump);
            }else{
                ArrayList<Contractdump> dumpByDate = new ArrayList<Contractdump>();
                dumpByDate.add(contractdump);
                contractdumpMap.put(startdate, dumpByDate);
            }
        }
        return  contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdump>> filterCustomerBasedDumpBySourceDestination(ArrayList<Contractdump> contractdumps){
        HashMap<String, ArrayList<Contractdump>> contractdumpMap = new HashMap<String, ArrayList<Contractdump>>();
        for(Contractdump contractdump : contractdumps) {
            String source = contractdump.getRouteFrom();
            String destination = contractdump.getRouteTo();
            if (null != source && null!= destination) {
                String key =  source+"-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null==destination){
                if(contractdumpMap.containsKey(null)){
                    contractdumpMap.get(null).add(contractdump);
                }else{
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(null, list);
                }
            }else if(null!=source && null == destination){
                String key =  "SOURCE-"+source;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null != destination){
                String key =  "DESTINAITON-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }
        }
        return contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdump>> optimizeContractdumpByServiceAndDestination(HashMap<String, ArrayList<Contractdump>> map){
        HashMap<String, ArrayList<Contractdump>> optimizeMap = new HashMap<String, ArrayList<Contractdump>>();
        ArrayList<String> keysToRemove = new ArrayList<String>();
        Set<String> locationValidator = new HashSet<String>();
        Set<String> keys = map.keySet();
        Set<String> uniqueDetails = new HashSet<String>();
        for(String key : keys){
            ArrayList<Contractdump> contractdumps = map.get(key);
            if(!contractdumps.isEmpty()){
                for(Contractdump contractdump :  contractdumps){
                    String uniqueId = contractdump.getBasePrice()+"~"+contractdump.getPrice()+"~"+contractdump.getDiscLmtFrom()+"~"+contractdump.getDscLimCd()+"~"+contractdump.getPrice();
                    if(!uniqueDetails.contains(uniqueId)) {
                        if (optimizeMap.containsKey(contractdump.getCustomerNumber())) {
                            optimizeMap.get(contractdump.getCustomerNumber()).add(contractdump);
                        } else {
                            ArrayList<Contractdump> dumps = new ArrayList<Contractdump>();
                            dumps.add(contractdump);
                            optimizeMap.put(contractdump.getCustomerNumber(), dumps);
                        }
                    }
                }
            }
        }
        // filter by location
        if(!optimizeMap.isEmpty()){

            Set<String> keysByCustomerId = optimizeMap.keySet();
            for(String key : keysByCustomerId ){
                if(!locationValidator.contains(optimizeMap.get(key).get(0).getRouteFrom()+"-"+optimizeMap.get(key).get(0).getRouteTo())){
                    locationValidator.add(optimizeMap.get(key).get(0).getRouteFrom()+"-"+optimizeMap.get(key).get(0).getRouteTo());
                }else{
                    keysToRemove.add(key);
                }
            }
        }
        if(!keysToRemove.isEmpty()){
            for(String str :  keysToRemove){
                optimizeMap.remove(str);
            }
        }
        return optimizeMap;
    }


    private static HashMap<String, ArrayList<Contractdump>> filterDumpByCustomerNumber(HashMap<String, ArrayList<Contractdump>> contractdumpMapByCustomers){
        HashMap<String, ArrayList<Contractdump>> mapByCustomer  = new HashMap<String, ArrayList<Contractdump>>();
        if(!contractdumpMapByCustomers.isEmpty()){
         Set<String> orgNumbers = contractdumpMapByCustomers.keySet();
         for(String orgNumber : orgNumbers){
             mapByCustomer.put(orgNumber.split("~")[0], contractdumpMapByCustomers.get(orgNumber));
            }
        }
        return mapByCustomer;
    }



    private static HashMap<Integer, ArrayList<Contractdump>> filterMapByService(ArrayList<Contractdump> contractdumps){
        HashMap<Integer, ArrayList<Contractdump>> mapByService  = new HashMap<Integer, ArrayList<Contractdump>>();
        if(!contractdumps.isEmpty()){
            for(Contractdump contractdump : contractdumps){
                Integer serviceId =  contractdump.getProdNo();
                if(mapByService.containsKey(serviceId)){
                    mapByService.get(serviceId).add(contractdump);
                }else{
                    ArrayList<Contractdump> list = new ArrayList<Contractdump>();
                    list.add(contractdump);
                    mapByService.put(serviceId , list);
                }
            }
        }
        return mapByService;
    }

    private static HashMap<String, ArrayList<Contractdump>> identifyPriceType(ArrayList<Contractdump> list){
        HashMap<String, ArrayList<Contractdump>> mapByPriceType = new HashMap<String, ArrayList<Contractdump>>();

        String type  = null;
            if(!list.isEmpty()){
                for(Contractdump contractdump : list){
                    if(contractdump.getDscLimCd()!=null &&  contractdump.getDscLimCd()!=0.0 ){
                        if(mapByPriceType.containsKey(SLABBASED_PRICE)){
                            if(mapByPriceType.get(SLABBASED_PRICE).get(0).getCustomerNumber().equals(contractdump.getCustomerNumber()))
                            mapByPriceType.get(SLABBASED_PRICE).add(contractdump);
                        }else{
                            ArrayList<Contractdump> dumps = new ArrayList<Contractdump>();
                            dumps.add(contractdump);
                            mapByPriceType.put(SLABBASED_PRICE, dumps);
                        }
                    }else {
                        if(mapByPriceType.containsKey(BASE_PRICE)){
                            if(mapByPriceType.get(BASE_PRICE).get(0).getCustomerNumber().equals(contractdump.getCustomerNumber()))
                            mapByPriceType.get(BASE_PRICE).add(contractdump);
                        }else{
                            ArrayList<Contractdump> dumps = new ArrayList<Contractdump>();
                            dumps.add(contractdump);
                            mapByPriceType.put(BASE_PRICE, dumps);
                        }
                    }
                }
            }

        return mapByPriceType;
    }

    private static ArrayList<Contractdump> validateDuplicateOrgCount(ArrayList<Contractdump> dumps){
        ArrayList<Contractdump> list = new ArrayList<Contractdump>();
        String tempOrg = null;
        for(Contractdump contractdump :  dumps){
            if(tempOrg==null) {
                tempOrg = contractdump.getCustomerNumber();
                list.add(contractdump);
            }
            else if((tempOrg!=null && contractdump.getCustomerNumber().equals(tempOrg))){
                list.add(contractdump);
            }
        }
        return list;
    }

}