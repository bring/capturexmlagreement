package no.bring.priceengine.precentagebased;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.service.ExcelService;

import java.text.SimpleDateFormat;
import java.util.*;

public class PercentageBasedExcelService {

    public static final String BASE_PRICE = "BasePrice";
    public static final String PERCENTAGE_PRICE = "PercentageBasePrice";
    private static Set<Integer> unmappedItems = new HashSet<>();

    public void performActionOnDump(List<Percentagebaseddump> contractdumps, HashMap<String, Set<String>> partyMap){
        try {
            QueryService queryService = new QueryService();
            DatabaseService databaseService = new DatabaseService();
            ExcelService excelService = new ExcelService();
            for (String organization : partyMap.keySet()) {

                HashMap<String, ArrayList<Percentagebaseddump>> contractdumpMapByCustomers = filterDumpByOrganization(contractdumps, partyMap.get(organization), organization.split("~")[0]);// map at customer level
                HashMap<String, Set<String>> customerByOrgMap = filterCustomerByOrganization(contractdumps, partyMap.get(organization), organization.split("~")[0]);
                int counter = 0;
                Contract persistedContract = null;
                ContractComponent persistedContractComponent = null;
                ArrayList<ContractRole> roles = new ArrayList<ContractRole>();
                for (String orgDetails : customerByOrgMap.keySet()) { // add contractdumpsByCustomers is empty condition here
                    List<Percentagebaseddump> contractdumpsByCustomers = contractdumpMapByCustomers.get(orgDetails); // queryService.findPercentageBasedContractdumpsByCustomer(orgDetails.split("~")[0]);
                    if (null !=contractdumpsByCustomers && !contractdumpsByCustomers.isEmpty()){
                        if (counter == 0) {
                            // DATA TO  CONTRACT TABLES START INSERTING HERE
                            Party party = queryService.findPartyBySSPK(orgDetails.split("~")[0]);
                            if (party == null)
                                party = queryService.findPartyByParentSSPK(orgDetails.split("~")[0]);
                            if (party == null) {
                                System.out.println("Party is neither present in Parent custmer NOR in child customer "+ orgDetails.split("~")[0] +" : "+ orgDetails.split("~")[1]);
                                System.exit(1);
                            }
                            Contract contract = null;

                            try {
                                if (null == party)
                                    System.out.println("wait.. Party not available");

                                contract = databaseService.buildContractPercentageBased(contractdumpsByCustomers.get(0), party);
                            } catch (IndexOutOfBoundsException iob) {
                                iob.printStackTrace();
                                System.exit(1);
                            }
                            persistedContract = queryService.findContract(contract.getSourceSystemRecordPk());
                            if(null!=persistedContract){
                                persistedContractComponent = queryService.findContractComponent(persistedContract);
                            }else{
                                Long id = databaseService.insertContract(contract);
                                if(null!=id){
                                    persistedContract = queryService.findContractById(id);// Contract inserted here
                                    ContractComponent contractComponent = new ContractComponent();
                                    contractComponent = databaseService.buildPercentageBasedContractComponent(persistedContract, contractdumpsByCustomers.get(0), party);
                                    persistedContractComponent = databaseService.insertContractComponent(contractComponent);// ContractComponenet inserted
                                    counter++;
                                }else{
                                    System.out.println("Some issue occured");
                                }
                            }
                        }
                        // INSERT ROLES
                        Set<String> customers = customerByOrgMap.get(orgDetails);
                        List<ContractRole> existingContractRoles =  queryService.findContractRoles(persistedContractComponent);
                        if(existingContractRoles.isEmpty()) {
                            for (String customer : customers) {
                                Party partyForContractRole = queryService.findPartyBySSPK(customer.split("~")[0]);
                                ContractRole contractRole = databaseService.buildContractRolePercentageBased(persistedContractComponent, contractdumpsByCustomers.get(0), partyForContractRole, customer);
                                ContractRole persistedContractRole = null;
                                Long contractRoleId = null;
                                contractRole.setContractComponent(persistedContractComponent);
                                contractRoleId = databaseService.insertContractRole(contractRole);
                                persistedContractRole = queryService.findContractRole(contractRoleId);
                                roles.add(persistedContractRole);
                            }
                        }
                        /// *******************************************************************
                        /// WORKING FINE TILL HERE-- NEED TO INSERT PRICE ONLY
                    }else{
                        System.out.println("updated already");
                    }
                    // for pricing as per discussion
                    HashMap<String, ArrayList<Percentagebaseddump>> dumpsByParentCustomerIdMap = filterDumpByCustomerNumber(contractdumpMapByCustomers);
                    Set<String> parentCustomerKeys = dumpsByParentCustomerIdMap.keySet();
                    ArrayList<Percentagebaseddump> dumpsByParentCustomer = new ArrayList<Percentagebaseddump>();
                    for (String customerKey : parentCustomerKeys) {
                        dumpsByParentCustomer.addAll(dumpsByParentCustomerIdMap.get(customerKey));
                    }
                    if (!parentCustomerKeys.isEmpty()) {
                        //for (String customerKey : parentCustomerKeys) {
                            HashMap<Integer, ArrayList<Percentagebaseddump>> contractDumpsByServiceMap = filterMapByService(dumpsByParentCustomer);
                            if (!contractDumpsByServiceMap.isEmpty()) {
                                Set<Integer> services = contractDumpsByServiceMap.keySet();
                                for (Integer serviceId : services) {
                                    if (null != serviceId) {
                                        Item item = queryService.getItem(serviceId);
                                        if (null != item) {

                                            ArrayList<Percentagebaseddump> contractdumpsByService = contractDumpsByServiceMap.get(serviceId);
                                            HashMap<String, ArrayList<Percentagebaseddump>> contractMapByLocation = filterCustomerBasedDumpBySourceDestination(contractdumpsByService);
                                            Set<String> locations = contractMapByLocation.keySet();
                                            for (String location : locations) {
                                                ArrayList<Percentagebaseddump> contractdumpsByLocation = contractMapByLocation.get(location);
                                                HashMap<String, ArrayList<Percentagebaseddump>> contractdumpsByDateMap = filterCustomerByDate(contractdumpsByLocation);
                                                for (String dateStr : contractdumpsByDateMap.keySet()) {
                                                    ArrayList<Percentagebaseddump> contractdumpsByDateList = contractdumpsByDateMap.get(dateStr);
                                                    if (!contractdumpsByDateList.isEmpty()) {
                                                        contractdumpsByDateList = removeDuplicateFromFilteredDump(contractdumpsByDateList);
                                                            for (Percentagebaseddump contractdump : contractdumpsByDateList) {
                                                                Long priceId = databaseService.insertPercentageBasedPrice(contractdump, item, PERCENTAGE_PRICE);
                                                                try {
                                                                    databaseService.insertPercentageBasedContractPrice(contractdump, item, priceId, persistedContractComponent);
                                                                } catch (NullPointerException ne) {
                                                                    ne.printStackTrace();
                                                                    System.out.println(" contractdump table id :  " + contractdump.getId());
                                                                    System.exit(1);
                                                                }
                                                            }
                                                            databaseService.disablePercentageBasedContractdumpEntry(contractdumpsByDateList);
                                                    }
                                                }
                                            }
                                            databaseService.disablePercentageBasedContractdumpEntry(contractdumpsByService);
                                        } else {
                                            unmappedItems.add(serviceId);
                                        }
//                                            } else {
//                                                // coding for 349 service
//
//                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            //}

        System.out.println(" ");
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
   //     System.out.println("select * from core.price where price_id in (select price_id from core.contractprice where contractcomponent_id in (select contractcomponent_id from core.contractcomponent where contract_id in ( select distinct contract_id from core.contract where source_system_record_pk in( select ORGANIZATION_NUMBER from core.contractdump where filecountry='"+contractdumps.get(0).getFileCountry() +"' AND UPDATED=TRUE ))) and base_price is not null order by item_id, base_price");


    }catch (Exception e){
            e.printStackTrace();
        }

    }

    private HashMap<String, ArrayList<Percentagebaseddump>> contractdumpReorderdPercentageBased(List<Percentagebaseddump> contractdumps){
        HashMap<String, ArrayList<Percentagebaseddump>> map = new HashMap<String, ArrayList<Percentagebaseddump>>();
        ArrayList<Percentagebaseddump> dumpList = new ArrayList<Percentagebaseddump>();
        for (Percentagebaseddump contractdump : contractdumps) {
            if (!contractdump.getPrecentageDiscount().equals("-100")) {
                String key = null;
                if (null != contractdump.getParentCustomerNumber()) {
                    key = contractdump.getParentCustomerNumber();
                    if (map.containsKey(key)) {
                        map.get(key).add(contractdump);
                    } else {
                        dumpList = new ArrayList<Percentagebaseddump>();
                        dumpList.add(contractdump);
                        map.put(key, dumpList);
                    }
                }
            }
        }
        return  map;
    }

    private static HashMap<String, ArrayList<Percentagebaseddump>> filterDumpByOrganization(List<Percentagebaseddump> contractdumps, Set<String> organizationDetails, String organizationNumber) {
        HashMap<String, ArrayList<Percentagebaseddump>> dumps = new HashMap<String, ArrayList<Percentagebaseddump>>();

        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String customerDetail : organizationDetails) {
                String customerId = customerDetail.split("~")[0];
                for (Percentagebaseddump contractdump : contractdumps) {
                    if (contractdump.getEnabled()) {
                        if (contractdump.getParentCustomerNumber().equals(customerId)) {
                            if (dumps.containsKey(customerDetail.split("~")[0])) {
                                dumps.get(customerDetail.split("~")[0]).add(contractdump);
                            } else {
                                ArrayList<Percentagebaseddump> dumpList = new ArrayList<Percentagebaseddump>();
                                dumpList.add(contractdump);
                                dumps.put(customerDetail.split("~")[0], dumpList);
                            }
                        }else if(contractdump.getParentCustomerNumber().equals(organizationNumber)){
                            if (dumps.containsKey(organizationNumber)) {
                                dumps.get(organizationNumber).add(contractdump);
                            } else {
                                ArrayList<Percentagebaseddump> dumpList = new ArrayList<Percentagebaseddump>();
                                dumpList.add(contractdump);
                                dumps.put(organizationNumber, dumpList);
                            }
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, Set<String>> filterCustomerByOrganization(List<Percentagebaseddump> contractdumps, Set<String> organizationDetails, String organizationNumber) {
        HashMap<String, Set<String>> dumps = new HashMap<String, Set<String>>();
        if(organizationNumber.equals("20000200400"))
            System.out.println("wait");
// CHECK HERE NEW
        if (!contractdumps.isEmpty() && !organizationDetails.isEmpty()) {
            for (String orgDetail : organizationDetails) {
                String orgId = orgDetail.split("~")[0];
                for (Percentagebaseddump contractdump : contractdumps) {
                    if (contractdump.getParentCustomerNumber().equals(orgId)) {
                        if (dumps.containsKey(orgDetail.split("~")[0])) {
                            dumps.get(orgDetail.split("~")[0]).add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                        } else {
                            Set<String> dumpList = new HashSet<String>();
                            dumpList.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                            dumps.put(orgDetail.split("~")[0], dumpList);
                        }
                    }else if(contractdump.getParentCustomerNumber().equals(organizationNumber)){
                        if (dumps.containsKey(organizationNumber)) {
                            dumps.get(organizationNumber).add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                        } else {
                            Set<String> dumpList = new HashSet<String>();
                            dumpList.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                            dumps.put(organizationNumber, dumpList);
                        }
                    }
                }
            }
        }
        return dumps;
    }

    private static HashMap<String, ArrayList<Percentagebaseddump>> filterDumpByCustomerNumber(HashMap<String, ArrayList<Percentagebaseddump>> contractdumpMapByCustomers){
        HashMap<String, ArrayList<Percentagebaseddump>> mapByCustomer  = new HashMap<String, ArrayList<Percentagebaseddump>>();
        if(!contractdumpMapByCustomers.isEmpty()){
            Set<String> orgNumbers = contractdumpMapByCustomers.keySet();
            for(String orgNumber : orgNumbers){
                mapByCustomer.put(orgNumber.split("~")[0], contractdumpMapByCustomers.get(orgNumber));
            }
        }
        return mapByCustomer;
    }

    private static HashMap<Integer, ArrayList<Percentagebaseddump>> filterMapByService(ArrayList<Percentagebaseddump> contractdumps){
        HashMap<Integer, ArrayList<Percentagebaseddump>> mapByService  = new HashMap<Integer, ArrayList<Percentagebaseddump>>();
        if(!contractdumps.isEmpty()){
            for(Percentagebaseddump contractdump : contractdumps){
                Integer serviceId =  contractdump.getProdno();
                if(mapByService.containsKey(serviceId)){
                    mapByService.get(serviceId).add(contractdump);
                }else{
                    ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
                    list.add(contractdump);
                    mapByService.put(serviceId , list);
                }
            }
        }
        return mapByService;
    }



    private static ArrayList<Percentagebaseddump> validateDuplicateOrgCount(ArrayList<Percentagebaseddump> dumps){
        ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
        String tempOrg = null;
        for(Percentagebaseddump contractdump :  dumps){
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

    private static HashMap<String, ArrayList<Percentagebaseddump>> filterCustomerBasedDumpBySourceDestination(ArrayList<Percentagebaseddump> contractdumps){
        HashMap<String, ArrayList<Percentagebaseddump>> contractdumpMap = new HashMap<String, ArrayList<Percentagebaseddump>>();
        for(Percentagebaseddump contractdump : contractdumps) {
            String source = contractdump.getFromLocation();
            String destination = contractdump.getToLocation();
            if (null != source && null!= destination) {
                String key =  source+"-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null==destination){
                if(contractdumpMap.containsKey(null)){
                    contractdumpMap.get(null).add(contractdump);
                }else{
                    ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
                    list.add(contractdump);
                    contractdumpMap.put(null, list);
                }
            }else if(null!=source && null == destination){
                String key =  "SOURCE-"+source;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }else if(null==source && null != destination){
                String key =  "DESTINAITON-"+destination;
                if (contractdumpMap.containsKey(key)) {
                    contractdumpMap.get(key).add(contractdump);
                } else {
                    ArrayList<Percentagebaseddump> list = new ArrayList<Percentagebaseddump>();
                    list.add(contractdump);
                    contractdumpMap.put(key, list);
                }
            }
        }
        return contractdumpMap;
    }

    private static HashMap<String, ArrayList<Percentagebaseddump>> filterCustomerByDate(ArrayList<Percentagebaseddump> contractdumps){
        HashMap<String, ArrayList<Percentagebaseddump>> contractdumpMap = new HashMap<String, ArrayList<Percentagebaseddump>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(Percentagebaseddump contractdump : contractdumps){
            String startdate = sdf.format(contractdump.getStartdate());
            if(contractdumpMap.containsKey(startdate)){
                contractdumpMap.get(startdate).add(contractdump);
            }else{
                ArrayList<Percentagebaseddump> dumpByDate = new ArrayList<Percentagebaseddump>();
                dumpByDate.add(contractdump);
                contractdumpMap.put(startdate, dumpByDate);
            }
        }
        return  contractdumpMap;
    }
     private ArrayList<Percentagebaseddump> removeDuplicateFromFilteredDump(ArrayList<Percentagebaseddump> dumps){

        if(!dumps.isEmpty() && dumps.size()>1){
            Percentagebaseddump temp = null;
            Iterator itr = dumps.iterator();
            while(itr.hasNext()){
                Percentagebaseddump percentagebaseddump = (Percentagebaseddump) itr.next();
                if(temp==null)
                    temp = percentagebaseddump;
                else{
                    if(temp.getZoneType()==null && percentagebaseddump.getZoneType()==null){
                        if(temp.getPrecentageDiscount().equals(percentagebaseddump.getPrecentageDiscount()))
                            itr.remove();
                    }else if(temp.getZoneType()!=null && percentagebaseddump.getZoneType()!=null){
                        if(temp.getPrecentageDiscount().equals(percentagebaseddump.getPrecentageDiscount()))
                            itr.remove();
                    }
                    temp = percentagebaseddump;
                }
            }
        }
        return dumps;
    }



}
