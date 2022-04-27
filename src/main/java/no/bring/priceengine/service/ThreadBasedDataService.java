package no.bring.priceengine.service;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public  class ThreadBasedDataService implements  Runnable{

    public static final String BASE_PRICE = "BasePrice";
    public static final String SLABBASED_PRICE = "SlabBasedPrice";
    public static final String PERCENTAGE_PRICE = "PercentageBasePrice";
    private static ArrayList<Integer> unmappedItems = new ArrayList<Integer>();
    public static  ArrayList<Integer> serviceList = new ArrayList<Integer>();


    public ThreadBasedDataService(ArrayList<Integer> serviceListConstrctor ){
        this.serviceList = serviceListConstrctor;
    }

    public ThreadBasedDataService(){}

    public void run() {
        try {
            System.out.println("Thread has been started");
            processSlabbasedData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public  void processSlabbasedData() {
        try {

            ThreadBasedDataService excelService = new ThreadBasedDataService();
            String priceType = null;

            QueryService queryService = new QueryService();
            DatabaseService databaseService = new DatabaseService();
            System.out.println("service list size this :: " + this.serviceList.size());
            System.out.println("service list size  :: " + serviceList.size());
              //      String contractPKID = queryService.findMaxContractSourceSystemRecordPKID();
//                    System.out.println("contractPKID value - "+contractPKID);
//                    if (null != contractPKID)
//                        contractPKID = createNewPKID(contractPKID);
                    if (!serviceList.isEmpty()) {
                        for (Integer serviceId : serviceList) {
                            if (null != serviceId) {
                                Item item = queryService.getItem(serviceId);
                                if(null!=item ) {
                                    List<Contractdump> contractdumps = queryService.findContractByService(serviceId,null);
                                    HashMap<String, ArrayList<Contractdump>> dumpMap = excelService.contractdumpReorderd(contractdumps);
                                    if (!dumpMap.isEmpty()) {
                                        //Item item = queryService.getItem(serviceId);// ADD CODE SYSTEM.EXIT IF ITEM IS NULL
                                        //if(null!=item  &&  !unmappedItems.contains(item.getItemId())) {
                                        for (String key : dumpMap.keySet()) {
                                            ArrayList<Contractdump> contractdumpList = dumpMap.get(key);
                                            if (!contractdumpList.isEmpty()) {
                                                HashMap<String, ArrayList<Contractdump>> dumpMapByPriceList = excelService.contractdumpReorderdByPriceList(contractdumpList);
                                                if (!dumpMapByPriceList.isEmpty()) {
                                                    Set<String> keys = dumpMapByPriceList.keySet();
                                                    for (String priceKey : keys) {
                                                        ArrayList<Contractdump> contractdumpByPriceList = dumpMapByPriceList.get(priceKey);

                                                        ContractComponent persistedContractComponent = null;
                                                        ArrayList<Long> priceIdList = new ArrayList<Long>();
                                                        Contractdump contractdump = contractdumpByPriceList.get(0);
                                                        Party party =null ; // queryService.findPartyBySSPK(contractdump.getCustomerNumber());
                                                        Contract contract = databaseService.buildContract(contractdump,  party);

                                                   //     contractPKID = contract.getSourceSystemRecordPk(); // till here
                                                  //      contractPKID = createNewPKID(contractPKID);
                                                        List<ContractComponent> contractComponentsToSave = new ArrayList<ContractComponent>();

                                                        Long id = databaseService.insertContract(contract);
                                                        Contract persistedContract = null ; //queryService.findContract(id);
                                                        if (null != persistedContract) {
                                                            Set<ContractComponent> components = null; // databaseService.buildContractComponent(persistedContract);
                                                            for (ContractComponent contractComponent : components) {

                                                                contractComponent.setContract(persistedContract);
                                                                persistedContractComponent = databaseService.insertContractComponent(contractComponent);
                                                                contractComponentsToSave.add(persistedContractComponent);
                                                                // Insert contract roles
                                                                //     Set<ContractRole> roles = persistedContractComponent.getContractRolesSet();
                                                                persistedContract.getContractComponents().add(persistedContractComponent);
                                                                Set<ContractRole> roles = null; // databaseService.buildContractRole(persistedContract, contractdump, party, null);
                                                                for (ContractRole contractRole : roles) {
                                                                    ContractRole persistedContractRole = null;
                                                                    Long contractRoleId = null;
                                                                    contractRole.setContractComponent(persistedContractComponent);
                                                                    contractRoleId = databaseService.insertContractRole(contractRole);
                                                                    persistedContractRole = queryService.findContractRole(contractRoleId);
                                                                }
                                                            }
                                                            if (null != item) {
                                                                if (contractdumpByPriceList.size() == 1) {
                                                                    Long priceId = databaseService.insertPrice(contractdump, item, BASE_PRICE);
                                                                //    databaseService.insertContractPrice(contractdump, item, priceId, persistedContractComponent);
                                                                    databaseService.disableContractdumpEntry(contractdumpByPriceList);
                                                                }
                                                                if (contractdumpByPriceList.size() > 1) {
                                                                    ArrayList<Contractdump> subListFixPrice = filterMixedPriceTypes(contractdumpByPriceList);
                                                                    if (!contractdumpByPriceList.isEmpty() && contractdumpByPriceList.size() > 1) {
                                                                        Long priceId = databaseService.insertPrice(contractdump, item, SLABBASED_PRICE);
                                                               //         databaseService.insertContractPrice(contractdump, item, priceId, persistedContractComponent);
                                                                        Long slabbasedId = null; // databaseService.insertSlabbasedPrice(priceId, contractdumpByPriceList.get(0).getDscLimCd());
                                                                        if (slabbasedId != null) {
                                                                            databaseService.insertSlabbasedPriceEntries(contractdumpByPriceList, slabbasedId);
                                                                            databaseService.disableContractdumpEntry(contractdumpByPriceList);
                                                                        } else
                                                                            System.out.println("########################### error occured while inserting data in  slabbasedprice");
                                                                    }
                                                                    if (!subListFixPrice.isEmpty()) {
                                                                        for (Contractdump contractdump1 : subListFixPrice) {
                                                                            Long priceId1 = databaseService.insertPrice(contractdump1, item, BASE_PRICE);
                                                                          //  databaseService.insertContractPrice(contractdump1, item, priceId1, persistedContractComponent);
                                                                        }
                                                                        databaseService.disableContractdumpEntry(subListFixPrice);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            } // till here

                                            // currently dealing with multiple contract entries as it must be for slabbased pricing as per my understanding

                                        }
                                    }
                                }else {
                                    System.out.println("Item not found for service ID - " + serviceId);
                                    unmappedItems.add(serviceId);
                                }

                            }
                        }
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    System.out.println(" ");
                    for(Integer unmappedId : unmappedItems) {
                        System.out.print(unmappedId + ",");
                    }
                }
        }catch (HibernateException he){
            he.printStackTrace();
            //  System.exit(1);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private HashMap<String, ArrayList<Contractdump>> contractdumpReorderd(List<Contractdump> contractdumps){
        HashMap<String, ArrayList<Contractdump>> map = new HashMap<String, ArrayList<Contractdump>>();
        ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
        for (Contractdump contractdump : contractdumps) {
            String key = null;
            if(null!=contractdump.getRouteFrom() && !contractdump.getRouteFrom().equals("")) {
                key = contractdump.getRouteFrom() + "-" + contractdump.getRouteTo();
                if (map.containsKey(key)) {
                    map.get(key).add(contractdump);
                } else {
                    dumpList = new ArrayList<Contractdump>();
                    dumpList.add(contractdump);
                    map.put(key, dumpList);
                }
            }else{
                key = contractdump.getCustomerName().toString();
                if (map.containsKey(key)) {
                    map.get(key).add(contractdump);
                } else {
                    dumpList = new ArrayList<Contractdump>();
                    dumpList.add(contractdump);
                    map.put(key, dumpList);
                }
            }
        }
        if(map.isEmpty())
            System.out.println("IT SHOULD NOT BE EMPTY");
        return  map;
    }

    private HashMap<String, ArrayList<Contractdump>> contractdumpReorderdByPriceList(List<Contractdump> contractdumps){
        HashMap<String, ArrayList<Contractdump>> map = new HashMap<String, ArrayList<Contractdump>>();
        ArrayList<Contractdump> dumpList = new ArrayList<Contractdump>();
        for (Contractdump contractdump : contractdumps) {
            String key = null;
            // if(null!=contractdump.getRouteFrom() && !contractdump.getRouteFrom().equals("")) {
            key = contractdump.getCustomerName().toString();
            if (map.containsKey(key)) {
                map.get(key).add(contractdump);
            } else {
                dumpList = new ArrayList<Contractdump>();
                dumpList.add(contractdump);
                if(dumpList.isEmpty())
                    System.out.println("Break... list cannot be empty");
                map.put(key, dumpList);
            }
            //}
        }
        return  map;
    }

    private void deteminePriceType(HashMap<String, ArrayList<Contractdump>>  map){
        if(!map.isEmpty()){
            Set<String> keySet = map.keySet();
            for(String sourceDestination : keySet){
                ArrayList<Contractdump> contractdumps = map.get(sourceDestination);
                if(contractdumps.size()>1){

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

    private static String createNewPKID(String contractPKID){
        Integer a = Integer.parseInt(contractPKID.replaceAll("CB",""));
        a=a+1;
        String finalVal = "CB"+a;
        return finalVal;
    }

    private static ArrayList<Contractdump> filterMixedPriceTypes(ArrayList<Contractdump> contractdumps){
        ArrayList<Contractdump> filteredOut = new ArrayList<Contractdump>();
        ArrayList<Contractdump> filteredIn = new ArrayList<Contractdump>();
        for(Contractdump contractdump : contractdumps){
            if(null==contractdump.getDiscLmtFrom()){
                filteredOut.add(contractdump);
            }else
                filteredIn.add(contractdump);
        }
        contractdumps.clear();
        contractdumps.addAll(filteredIn);
        return filteredOut;

    }


}
