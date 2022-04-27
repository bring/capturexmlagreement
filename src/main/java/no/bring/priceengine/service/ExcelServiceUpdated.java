package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdumpservice;
import no.bring.priceengine.dao.Percentagebaseddump;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.precentagebased.PercentageBasedExcelService;
import no.bring.priceengine.precentagebased.PercentageDatabaseService;
import no.bring.priceengine.precentagebased.ReadPercentageBasedFile;
import org.hibernate.HibernateException;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelServiceUpdated {

    public static final String BASE_PRICE = "BasePrice";
    public static final String SLABBASED_PRICE = "SlabBasedPrice";
    public static final String PERCENTAGE_PRICE = "PercentageBasePrice";
    private static Set<Integer> unmappedItems = new HashSet<>();

    public static void main(String[] args) {
        try {

            ExcelService excelService = new ExcelService();
            Scanner myObj = new Scanner(System.in);
            String fileLocation = "C:\\Users\\POT30559\\Downloads\\Price_Lists\\PL_DK_fra_01-01-2020_NY.xlsx";
            String priceType = null;
            String fileCountry = null;
            ArrayList<Integer> excludedServiceList = new ArrayList<Integer>();
            //excludedServiceList.add(349);
            //excludedServiceList.add(34964);

            System.out.println("Hi! This Jar will read data from the given excel sheet and insert/update the data from excelsheet to core.Contractdumpservice table.");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");

            System.out.print("Please enter the XML file name along with the path location : ");
            fileLocation = myObj.nextLine();

            System.out.println("Please press 1 for slabbased and 2 for percentage based");
            Scanner myObj2 = new Scanner(System.in);
            priceType = myObj2.nextLine();

            System.out.println("Please enter country code of file : ");
            Scanner myObj3 = new Scanner(System.in);
            fileCountry = myObj3.nextLine();

            QueryService queryService = new QueryService();
            DatabaseService databaseService = new DatabaseService();
            if (priceType.equals("1")) {
                ReadFileUpdated readFile = new ReadFileUpdated();
                List<Contractdumpservice> dumps = readFile.readFileData(fileLocation, fileCountry);
                Boolean isDataInserted = databaseService.upsertUpdatedContractData(dumps);
                      isDataInserted = true;
//                if (isDataInserted) {
//                    List<Contractdumpservice> contractdumps = queryService.findAllContractdumps(fileCountry);
//                    //contractdumps = queryService.findAllContractdumpsByOrganization(fileCountry,"");
//
//                    HashMap<String, Set<String>> partyMap = filterOrgnizationAndCustomers(contractdumps);// Map of organization level
//                    ArrayList<Contractdumpservice> dumpsForDisable = new ArrayList<Contractdumpservice>();
//
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    // Uncomment only if new organization and customers identified
//                    //   databaseService.upsertPartyDetails(partyMap);
//
//                    for (String organization : partyMap.keySet()) {
//                        HashMap<String, ArrayList<Contractdumpservice>> contractdumpMapByCustomers = filterDumpByOrganization(contractdumps, partyMap.get(organization));// map at customer level
//                        HashMap<String, Set<String>> customerByOrgMap = filterCustomerByOrganization(contractdumps, partyMap.get(organization));
//                        int counter = 0;
//                        Contract persistedContract = null;
//                        ContractComponent persistedContractComponent = null;
//                        ArrayList<ContractRole> roles = new ArrayList<ContractRole>();
//                        for (String orgDetails : customerByOrgMap.keySet()) {
//                            List<Contractdumpservice> contractdumpsByCustomers = queryService.findContractdumpsByCustomer(orgDetails.split("~")[0]);
//                            if (counter == 0) {
//                                // DATA TO  CONTRACT TABLES START INSERTING HERE
//                                Party party = queryService.findPartyBySSPK(orgDetails.split("~")[0]);
//                                if (party == null)
//                                    party = queryService.findPartyByParentSSPK(orgDetails.split("~")[0]);
//                                if(party==null){
//                                    System.out.println("Party is neither present in Parent custmer NOR in child customer");
//                                    System.exit(1);
//                                }
//                                Contract contract = databaseService.buildContract(contractdumpsByCustomers.get(0), party);
//                                Long id = databaseService.insertContract(contract);
//                                persistedContract = queryService.findContract(id);// Contract inserted here
//                                ContractComponent contractComponent = new ContractComponent();
//                                contractComponent = databaseService.buildContractComponent(persistedContract, contractdumpsByCustomers.get(0), party);
//                                persistedContractComponent = databaseService.insertContractComponent(contractComponent);// ContractComponenet inserted
//                                counter++;
//                            }
//                            // INSERT ROLES
//                            Set<String> customers = customerByOrgMap.get(orgDetails);
//                            for (String customer : customers) {
//                                Party partyForContractRole = queryService.findPartyBySSPK(customer.split("~")[0]);
//                                ContractRole contractRole = databaseService.buildContractRole(persistedContractComponent, contractdumpsByCustomers.get(0), partyForContractRole, customer);
//                                ContractRole persistedContractRole = null;
//                                Long contractRoleId = null;
//                                contractRole.setContractComponent(persistedContractComponent);
//                                contractRoleId = databaseService.insertContractRole(contractRole);
//                                persistedContractRole = queryService.findContractRole(contractRoleId);
//                                roles.add(persistedContractRole);
//                            }
//                            /// *******************************************************************
//                            /// WORKING FINE TILL HERE-- NEED TO INSERT PRICE ONLY
//                        }
//                        // for pricing as per discussion
//                        HashMap<String, ArrayList<Contractdumpservice>> dumpsByCustomerIdMap = filterDumpByCustomerNumber(contractdumpMapByCustomers);
//                        Set<String> customerKeys = dumpsByCustomerIdMap.keySet();
//                        if (!customerKeys.isEmpty()) {
//                            for (String customerKey : customerKeys) {
//                                HashMap<Integer, ArrayList<Contractdumpservice>> contractDumpsByServiceMap = filterMapByService(dumpsByCustomerIdMap.get(customerKey));
//                                if (!contractDumpsByServiceMap.isEmpty()) {
//                                    Set<Integer> services = contractDumpsByServiceMap.keySet();
//                                    for (Integer serviceId : services) {
//                                        if (null != serviceId) {
//                                            if (!excludedServiceList.contains(serviceId)){
//                                                Item item = queryService.getItem(serviceId);
//                                                if (null != item) {
//                                                    ArrayList<Contractdumpservice> contractdumpsByService = contractDumpsByServiceMap.get(serviceId);
//                                                    HashMap<String, ArrayList<Contractdumpservice>> contractMapByLocation = filterCustomerBasedDumpBySourceDestination(contractdumpsByService);
//                                                    Set<String> locations = contractMapByLocation.keySet();
//                                                    for (String location : locations) {
//                                                        ArrayList<Contractdumpservice> contractdumpsByLocation = contractMapByLocation.get(location);
//                                                        HashMap<String, ArrayList<Contractdumpservice>> contractdumpsByDateMap = filterCustomerByDate(contractdumpsByLocation);
//                                                        for (String dateStr : contractdumpsByDateMap.keySet()) {
//                                                            ArrayList<Contractdumpservice> contractdumpsByDateList = contractdumpsByDateMap.get(dateStr);
//                                                            if (!contractdumpsByDateList.isEmpty()) {
//                                                                if (!contractdumpsByDateList.get(0).getCustomerNumber().equals("20000094993") && !contractdumpsByDateList.get(0).getProdNo().equals(345)){
//                                                                    HashMap<String, ArrayList<Contractdumpservice>> mapByPriceType = identifyPriceType(contractdumpsByDateList);
//                                                                    if (mapByPriceType.containsKey(BASE_PRICE)) {
//                                                                        ArrayList<Contractdumpservice> dumpsByBase = mapByPriceType.get(BASE_PRICE);
//                                                                        dumpsByBase = validateDuplicateOrgCount(dumpsByBase);
//                                                                        for (Contractdumpservice Contractdumpservice : dumpsByBase) {
//                                                                            Long priceId = databaseService.insertPrice(Contractdumpservice, item, BASE_PRICE);
//                                                                            databaseService.insertContractPrice(Contractdumpservice, item, priceId, persistedContractComponent);
//                                                                        }
//                                                                        databaseService.disableContractdumpEntry(dumpsByBase);
//                                                                    } else if (mapByPriceType.containsKey(SLABBASED_PRICE)) {
//                                                                        ArrayList<Contractdumpservice> dumpsBySlab = mapByPriceType.get(SLABBASED_PRICE);
//                                                                        dumpsBySlab = validateDuplicateOrgCount(dumpsBySlab);
//                                                                        Long priceId = databaseService.insertPrice(dumpsBySlab.get(0), item, SLABBASED_PRICE);
//                                                                        databaseService.insertContractPrice(dumpsBySlab.get(0), item, priceId, persistedContractComponent);
//                                                                        Long slabbasedId = null;
//                                                                        if (priceId != null)
//                                                                            slabbasedId = databaseService.insertSlabbasedPrice(priceId, dumpsBySlab.get(0));
//                                                                        if (slabbasedId != null) {
//                                                                            databaseService.insertSlabbasedPriceEntries(dumpsBySlab, slabbasedId);
//                                                                            databaseService.disableContractdumpEntry(dumpsBySlab);
//                                                                            dumpsForDisable.addAll(dumpsBySlab);
//                                                                        } else
//                                                                            System.out.println("########################### error occured while inserting data in  slabbasedprice");
//
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                    databaseService.disableContractdumpEntry(contractdumpsByService);
//                                                } else {
//                                                    unmappedItems.add(serviceId);
//                                                }
//                                            }else{
//                                                // coding for 349 service
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(!dumpsForDisable.isEmpty())
//                        databaseService.disableContractdumpEntry(dumpsForDisable);
//
//                }
//                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
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
                System.out.println("select * from core.price where price_id in (select price_id from core.contractprice where contractcomponent_id in (select contractcomponent_id from core.contractcomponent where contract_id in ( select distinct contract_id from core.contract where source_system_record_pk in( select ORGANIZATION_NUMBER from core.Contractdumpservice where filecountry='"+fileCountry +"' AND UPDATED=TRUE ))) and base_price is not null order by item_id, base_price");
            } else if (priceType.equals("2")) {

                ReadPercentageBasedFile readFile = new ReadPercentageBasedFile();
                PercentageDatabaseService percentageDatabaseService = new PercentageDatabaseService();
                List<Percentagebaseddump> dump = readFile.readFileData(fileLocation, fileCountry);
                Boolean isDataInserted = percentageDatabaseService.insertContractData(dump);
                //Boolean isDataInserted =  true;
                if (isDataInserted) {
                    PercentageBasedExcelService percentageBasedExcelService = new PercentageBasedExcelService();
               //     percentageBasedExcelService.performActionOnDump();

                }


            } else {
                System.out.println("Please enter the correct option.");
            }
        } catch (HibernateException he) {
            he.printStackTrace();
            //  System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, ArrayList<Contractdumpservice>> contractdumpReorderd(List<Contractdumpservice> contractdumps) {
        HashMap<String, ArrayList<Contractdumpservice>> map = new HashMap<String, ArrayList<Contractdumpservice>>();
        ArrayList<Contractdumpservice> dumpList = new ArrayList<Contractdumpservice>();
        for (Contractdumpservice Contractdumpservice : contractdumps) {
            String key = null;
            if (null != Contractdumpservice.getRouteFrom() && !Contractdumpservice.getRouteFrom().equals("")) {
                key = Contractdumpservice.getRouteFrom() + "-" + Contractdumpservice.getRouteTo();
                if (map.containsKey(key)) {
                    map.get(key).add(Contractdumpservice);
                } else {
                    dumpList = new ArrayList<Contractdumpservice>();
                    dumpList.add(Contractdumpservice);
                    map.put(key, dumpList);
                }
            } else {
                key = Contractdumpservice.getCustomerName();
                if (map.containsKey(key)) {
                    map.get(key).add(Contractdumpservice);
                } else {
                    dumpList = new ArrayList<Contractdumpservice>();
                    dumpList.add(Contractdumpservice);
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

    private HashMap<String, ArrayList<Contractdumpservice>> contractdumpReorderdByPriceList(List<Contractdumpservice> contractdumps) {
        HashMap<String, ArrayList<Contractdumpservice>> map = new HashMap<String, ArrayList<Contractdumpservice>>();
        ArrayList<Contractdumpservice> dumpList = new ArrayList<Contractdumpservice>();
        for (Contractdumpservice Contractdumpservice : contractdumps) {
            String key = null;
            // if(null!=Contractdumpservice.getRouteFrom() && !Contractdumpservice.getRouteFrom().equals("")) {
            key = Contractdumpservice.getCustomerName();
            if (map.containsKey(key)) {
                map.get(key).add(Contractdumpservice);
            } else {
                dumpList = new ArrayList<Contractdumpservice>();
                dumpList.add(Contractdumpservice);
                if (dumpList.isEmpty())
                    System.out.println("Break... list cannot be empty");
                map.put(key, dumpList);
            }
            //}
        }
        return map;
    }

    private void deteminePriceType(HashMap<String, ArrayList<Contractdumpservice>> map) {
        if (!map.isEmpty()) {
            Set<String> keySet = map.keySet();
            for (String sourceDestination : keySet) {
                ArrayList<Contractdumpservice> contractdumps = map.get(sourceDestination);
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

    private static ArrayList<Contractdumpservice> filterMixedPriceTypes(ArrayList<Contractdumpservice> contractdumps) {
        ArrayList<Contractdumpservice> filteredOut = new ArrayList<Contractdumpservice>();
        ArrayList<Contractdumpservice> filteredIn = new ArrayList<Contractdumpservice>();
        for (Contractdumpservice Contractdumpservice : contractdumps) {
            if (null == Contractdumpservice.getDiscLmtFrom()) {
                filteredOut.add(Contractdumpservice);
            } else
                filteredIn.add(Contractdumpservice);
        }
        contractdumps.clear();
        contractdumps.addAll(filteredIn);
        return filteredOut;

    }

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomers(List<Contractdumpservice> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Contractdumpservice Contractdumpservice : contractdumps) {
                if (Contractdumpservice.getOrganizationNumber().equals(Contractdumpservice.getCustomerNumber())){
                    if (!orgMap.containsKey(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName())) {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                        orgMap.put(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName(), customerList);
                    } else {
                        if (!orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).contains(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName()))
                            orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                    }
                } else {
                    if (orgMap.containsKey(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName())) {
                        if (!orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).contains(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName()))
                            orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                        orgMap.put(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomersOld(List<Contractdumpservice> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Contractdumpservice Contractdumpservice : contractdumps) {
                if (Contractdumpservice.getOrganizationNumber().equals(Contractdumpservice.getCustomerNumber())) {
                    if (!orgMap.containsKey(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName())) {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                        orgMap.put(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName(), customerList);
                    } else {
                        if (!orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).contains(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName()))
                            orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                    }
                } else {
                    if (orgMap.containsKey(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName())) {
                        if (!orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).contains(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName()))
                            orgMap.get(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName()).add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(Contractdumpservice.getCustomerNumber() + "~" + Contractdumpservice.getCustomerName());
                        orgMap.put(Contractdumpservice.getOrganizationNumber() + "~" + Contractdumpservice.getOrganizationName(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> filterDumpByOrganization(List<Contractdumpservice> contractdumps, Set<String> organizationDetails) {
        HashMap<String, ArrayList<Contractdumpservice>> dumps = new HashMap<String, ArrayList<Contractdumpservice>>();

        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String customerDetail : organizationDetails) {
                String customerId = customerDetail.split("~")[0];
                for (Contractdumpservice Contractdumpservice : contractdumps) {
                    if (Contractdumpservice.getOrganizationNumber().equals(customerId)) {
                        if (dumps.containsKey(customerDetail)) {
                            dumps.get(customerDetail).add(Contractdumpservice);
                        } else {
                            ArrayList<Contractdumpservice> dumpList = new ArrayList<Contractdumpservice>();
                            dumpList.add(Contractdumpservice);
                            dumps.put(customerDetail, dumpList);
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, Set<String>> filterCustomerByOrganization(List<Contractdumpservice> contractdumps, Set<String> organizationDetails) {
        HashMap<String, Set<String>> dumps = new HashMap<String, Set<String>>();

        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String orgDetail : organizationDetails) {
                String orgId = orgDetail.split("~")[0];
                for (Contractdumpservice Contractdumpservice : contractdumps) {
                    if (Contractdumpservice.getOrganizationNumber().equals(orgId)) {
                        if (dumps.containsKey(orgDetail)) {
                            dumps.get(orgDetail).add(Contractdumpservice.getCustomerNumber()+"~"+Contractdumpservice.getCustomerName());
                        } else {
                            Set<String> dumpList = new HashSet<String>();
                            dumpList.add(Contractdumpservice.getCustomerNumber()+"~"+Contractdumpservice.getCustomerName());
                            dumps.put(orgDetail, dumpList);
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> filterCustomerBasedDumpByServiceId(ArrayList<Contractdumpservice> contractdumps){
        HashMap<String, ArrayList<Contractdumpservice>> contractdumpMap = new HashMap<String, ArrayList<Contractdumpservice>>();
        for(Contractdumpservice Contractdumpservice : contractdumps) {
            Integer serviceId = Contractdumpservice.getProdNo();
            if (null != serviceId) {
                if (contractdumpMap.containsKey(serviceId.toString())) {
                    contractdumpMap.get(serviceId.toString()).add(Contractdumpservice);
                } else {
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(serviceId.toString(), list);
                }
            }else{
                if(contractdumpMap.containsKey(null)){
                    contractdumpMap.get(null).add(Contractdumpservice);
                }else{
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(null, list);
                }
            }
        }
        return contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> filterCustomerByDate(ArrayList<Contractdumpservice> contractdumps){
        HashMap<String, ArrayList<Contractdumpservice>> contractdumpMap = new HashMap<String, ArrayList<Contractdumpservice>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(Contractdumpservice Contractdumpservice : contractdumps){
            String startdate = sdf.format(Contractdumpservice.getFromDate());
            if(contractdumpMap.containsKey(startdate)){
                contractdumpMap.get(startdate).add(Contractdumpservice);
            }else{
                ArrayList<Contractdumpservice> dumpByDate = new ArrayList<Contractdumpservice>();
                dumpByDate.add(Contractdumpservice);
                contractdumpMap.put(startdate, dumpByDate);
            }
        }
        return  contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> filterCustomerBasedDumpBySourceDestination(ArrayList<Contractdumpservice> contractdumps){
        HashMap<String, ArrayList<Contractdumpservice>> contractdumpMap = new HashMap<String, ArrayList<Contractdumpservice>>();
        for(Contractdumpservice Contractdumpservice : contractdumps) {
            String source = Contractdumpservice.getRouteFrom();
            String destination = Contractdumpservice.getRouteTo();
            if (null != source && null!= destination) {
                String key =  source+"-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(Contractdumpservice);
                } else {
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null==destination){
                if(contractdumpMap.containsKey(null)){
                    contractdumpMap.get(null).add(Contractdumpservice);
                }else{
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(null, list);
                }
            }else if(null!=source && null == destination){
                String key =  "SOURCE-"+source;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(Contractdumpservice);
                } else {
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null != destination){
                String key =  "DESTINAITON-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(Contractdumpservice);
                } else {
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    contractdumpMap.put(key, list);
                }
            }
        }
        return contractdumpMap;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> optimizeContractdumpByServiceAndDestination(HashMap<String, ArrayList<Contractdumpservice>> map){
        HashMap<String, ArrayList<Contractdumpservice>> optimizeMap = new HashMap<String, ArrayList<Contractdumpservice>>();
        ArrayList<String> keysToRemove = new ArrayList<String>();
        Set<String> locationValidator = new HashSet<String>();
        Set<String> keys = map.keySet();
        Set<String> uniqueDetails = new HashSet<String>();
        for(String key : keys){
            ArrayList<Contractdumpservice> contractdumps = map.get(key);
            if(!contractdumps.isEmpty()){
                for(Contractdumpservice Contractdumpservice :  contractdumps){
                    String uniqueId = Contractdumpservice.getBasePrice()+"~"+Contractdumpservice.getPrice()+"~"+Contractdumpservice.getDiscLmtFrom()+"~"+Contractdumpservice.getDscLimCd()+"~"+Contractdumpservice.getPrice();
                    if(!uniqueDetails.contains(uniqueId)) {
                        if (optimizeMap.containsKey(Contractdumpservice.getCustomerNumber())) {
                            optimizeMap.get(Contractdumpservice.getCustomerNumber()).add(Contractdumpservice);
                        } else {
                            ArrayList<Contractdumpservice> dumps = new ArrayList<Contractdumpservice>();
                            dumps.add(Contractdumpservice);
                            optimizeMap.put(Contractdumpservice.getCustomerNumber(), dumps);
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


    private static HashMap<String, ArrayList<Contractdumpservice>> filterDumpByCustomerNumber(HashMap<String, ArrayList<Contractdumpservice>> contractdumpMapByCustomers){
        HashMap<String, ArrayList<Contractdumpservice>> mapByCustomer  = new HashMap<String, ArrayList<Contractdumpservice>>();
        if(!contractdumpMapByCustomers.isEmpty()){
            Set<String> orgNumbers = contractdumpMapByCustomers.keySet();
            for(String orgNumber : orgNumbers){
                mapByCustomer.put(orgNumber.split("~")[0], contractdumpMapByCustomers.get(orgNumber));
            }
        }
        return mapByCustomer;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> filterDumpByCustomerNumberOld(HashMap<String, ArrayList<Contractdumpservice>> contractdumpMapByCustomers){
        HashMap<String, ArrayList<Contractdumpservice>> mapByCustomer  = new HashMap<String, ArrayList<Contractdumpservice>>();
        ArrayList<Contractdumpservice> contractdumps = new ArrayList<Contractdumpservice>();
        if(!contractdumpMapByCustomers.isEmpty()){
            Set<String> keySet=  contractdumpMapByCustomers.keySet();
            for(String key : keySet){
                contractdumps.addAll(contractdumpMapByCustomers.get(key));
            }
        }
        if(!contractdumps.isEmpty()){
            for(Contractdumpservice Contractdumpservice : contractdumps){
                String customerNnumber =  Contractdumpservice.getCustomerNumber();
                if(mapByCustomer.containsKey(customerNnumber)){
                    mapByCustomer.get(customerNnumber).add(Contractdumpservice);
                }else{
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    mapByCustomer.put(customerNnumber , list);
                }
            }
        }

        return mapByCustomer;
    }

    private static HashMap<Integer, ArrayList<Contractdumpservice>> filterMapByService(ArrayList<Contractdumpservice> contractdumps){
        HashMap<Integer, ArrayList<Contractdumpservice>> mapByService  = new HashMap<Integer, ArrayList<Contractdumpservice>>();
        if(!contractdumps.isEmpty()){
            for(Contractdumpservice Contractdumpservice : contractdumps){
                Integer serviceId =  Contractdumpservice.getProdNo();
                if(mapByService.containsKey(serviceId)){
                    mapByService.get(serviceId).add(Contractdumpservice);
                }else{
                    ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
                    list.add(Contractdumpservice);
                    mapByService.put(serviceId , list);
                }
            }
        }
        return mapByService;
    }

    private static HashMap<String, ArrayList<Contractdumpservice>> identifyPriceType(ArrayList<Contractdumpservice> list){
        HashMap<String, ArrayList<Contractdumpservice>> mapByPriceType = new HashMap<String, ArrayList<Contractdumpservice>>();

        String type  = null;
        if(!list.isEmpty()){
            for(Contractdumpservice Contractdumpservice : list){
                if(Contractdumpservice.getDscLimCd()!=null &&  Contractdumpservice.getDscLimCd()!=0.0){
                    if(mapByPriceType.containsKey(SLABBASED_PRICE)){
                        mapByPriceType.get(SLABBASED_PRICE).add(Contractdumpservice);
                    }else{
                        ArrayList<Contractdumpservice> dumps = new ArrayList<Contractdumpservice>();
                        dumps.add(Contractdumpservice);
                        mapByPriceType.put(SLABBASED_PRICE, dumps);
                    }
                }else {
                    if(mapByPriceType.containsKey(BASE_PRICE)){
                        mapByPriceType.get(BASE_PRICE).add(Contractdumpservice);
                    }else{
                        ArrayList<Contractdumpservice> dumps = new ArrayList<Contractdumpservice>();
                        dumps.add(Contractdumpservice);
                        mapByPriceType.put(BASE_PRICE, dumps);
                    }
                }
            }
        }

        return mapByPriceType;
    }

    private static ArrayList<Contractdumpservice> validateDuplicateOrgCount(ArrayList<Contractdumpservice> dumps){
        ArrayList<Contractdumpservice> list = new ArrayList<Contractdumpservice>();
        String tempOrg = null;
        for(Contractdumpservice Contractdumpservice :  dumps){
            if(tempOrg==null) {
                tempOrg = Contractdumpservice.getCustomerNumber();
                list.add(Contractdumpservice);
            }
            else if((tempOrg!=null && Contractdumpservice.getCustomerNumber().equals(tempOrg))){
                list.add(Contractdumpservice);
            }
        }
        return list;
    }
}
