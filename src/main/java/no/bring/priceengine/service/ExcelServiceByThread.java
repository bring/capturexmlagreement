package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelServiceByThread {

    public static void main(String[] str){
        ExcelService excelService = new ExcelService();
        Scanner myObj = new Scanner(System.in);
        String fileLocation = null;
        String priceType = null;
        String fileCountry = null;

        System.out.println("Hi! This Jar will read data from the given excel sheet and insert/update the data from excelsheet to core.Contractdump table.");
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
        if(priceType.equals("1")) {
            ReadFile readFile = new ReadFile();
   //         List<Contractdump> dumps = readFile.readFileData(fileLocation, fileCountry);
  //          Boolean isDataInserted = databaseService.upsertContractData(dumps);
                Boolean isDataInserted =  true;
            if (isDataInserted) {
           //     HashMap<String, ArrayList<String>> partyMap =  filterOrgnizationAndCustomers(queryService.findAllContractdumps());
           //                     databaseService.upsertPartyDetails(partyMap);
                    List<Integer> serviceList = queryService.fetchDistinctServices(null);
                   Map<Integer, ArrayList<Integer>> servicesByThreadMap = new HashMap<Integer, ArrayList<Integer>>();
                    if(serviceList.size() >50){
                        int counter = 1;
                        while(!serviceList.isEmpty()){
                            if(serviceList.size()>=50){
                                ArrayList<Integer> subList = ( ArrayList<Integer>) serviceList.stream().limit(50).collect(Collectors.toList());
                                servicesByThreadMap.put(counter, subList);
                                serviceList.removeAll(subList);
                                counter++;
                            }else{
                                ArrayList<Integer> subList = ( ArrayList<Integer>) serviceList.stream().limit(serviceList.size()).collect(Collectors.toList());
                                servicesByThreadMap.put(counter, subList);
                                serviceList.removeAll(subList);
                                counter++;
                            }
                        }
                        if(!servicesByThreadMap.isEmpty()){
                            for(Integer key : servicesByThreadMap.keySet()) {
                                ThreadBasedDataService threadBasedDataService=new ThreadBasedDataService(servicesByThreadMap.get(key));
                                Thread thread = new Thread(threadBasedDataService);
                                thread.setName("ServiceThread"+key);
                                System.out.println("Thread executed number :: "+ key);
                                thread.start();
                            }
                        }
                    }
            }
        }

    }

    private static HashMap<String, ArrayList<String>> filterOrgnizationAndCustomers(List<Contractdump> contractdumps){
        HashMap<String, ArrayList<String>> orgMap = new HashMap<String, ArrayList<String>>();
        if(!contractdumps.isEmpty()){
            for(Contractdump contractdump: contractdumps){
                if(contractdump.getOrganizationNumber().equals(contractdump.getCustomerNumber())){
                    if(!orgMap.containsKey(contractdump.getOrganizationNumber().toString()+"~"+contractdump.getOrganizationName())){
                        ArrayList<String> customerList = new ArrayList<String>();
                        customerList.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                        orgMap.put(contractdump.getOrganizationNumber().toString()+"~"+contractdump.getOrganizationName(),customerList);
                    }
                }else{
                    if(orgMap.containsKey(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName())){
                        if(!orgMap.get(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName()).contains(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName()))
                            orgMap.get(contractdump.getOrganizationNumber()+"~"+contractdump.getOrganizationName()).add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                    }else{
                        ArrayList<String> customerList = new ArrayList<String>();
                        customerList.add(contractdump.getCustomerNumber()+"~"+contractdump.getCustomerName());
                        orgMap.put(contractdump.getOrganizationNumber().toString()+"~"+contractdump.getOrganizationName(),customerList);
                    }
                }
            }
        }
        return  orgMap;
    }

}
