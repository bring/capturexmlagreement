package no.bring.priceengine.service;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.repository.BeanRepository;

import java.util.*;

public class VolumetricFactorService {

    public static void main(String[] args) throws Exception {
        try {

            ExcelService excelService = new ExcelService();
            QueryService queryService = new QueryService();
            BeanRepository beanRepository = new BeanRepository();
            Scanner myObj = new Scanner(System.in);
            String fileLocation = null;
            String priceType = null;
            String fileCountry = null;

            System.out.println("Hi! This Jar will read data from the given excel sheet and insert/update the data from excelsheet to core.Customervolfactordump table.");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------");

            System.out.print("Please enter the XML file name along with the path location : ");
            fileLocation = myObj.nextLine();
            DatabaseService databaseService = new DatabaseService();

//                ReadvolFactorFile readFile = new ReadvolFactorFile();
//               List<Customervolfactordump> dumps = readFile.readFileData(fileLocation);
//                Boolean isDataInserted = databaseService.upsertCustomerVolumetricFactorData(dumps);

                Boolean isDataInserted = true;
            System.out.println(isDataInserted + "  DATA COPIED");
                if(isDataInserted) {
                        int counter = 0;
                    ArrayList<Customervolfactordump> customervolfactordumps = queryService.findVolMetricDump();
                    ArrayList<Service>  services = queryService.findAllServices();
                        for(Customervolfactordump customervolfactordump : customervolfactordumps){
                            counter++;
                             isDataInserted = false;
                            if(null!=customervolfactordump.getService()){
                                services.clear();
                                services.add(queryService.findServiceByID(customervolfactordump.getService()));
                            }else{
                                services.clear();
                                services.addAll(queryService.findAllServices());
                            }
                            ArrayList<ContractRole> contractRoles = new  ArrayList<ContractRole>();
                            Set<ContractComponent> contractComponents = new HashSet<ContractComponent>();
                            Contract contract = queryService.findContractBySSPK(customervolfactordump.getCustomerNumber());
                            if(null ==contract){
                                contractRoles =  queryService.findContractRolesBySSPK(customervolfactordump.getCustomerNumber());
                                contractComponents = getContractComponents(contractRoles);
                            } else{
                                contractComponents.addAll(contract.getContractComponents());
                            }
                            for(Service service :  services){
                                if(!contractComponents.isEmpty() && service!=null){
                                    for(ContractComponent contractComponent : contractComponents){
                                        if(contractComponent!=null){
                                        if(!BeanRepository.validateCustomProdAvailable(contractComponent, service.getItemId())) {
                                      System.out.println("Contract component ID  ::: " + contractComponent.getContractComponentId());
                                            System.out.println("ITEM ID  ::: " + service.getItemId());

                                            CustomProductAttribute customProductAttribute = BeanRepository.buildCustomProductAttribute(customervolfactordump, contractComponent, service);
                                            beanRepository.insertCustomerProdAttribute(customProductAttribute);
                                            isDataInserted = true;
                                        }
                                        }else{
                                            System.out.println("wait");

                                        }
                                    }
                                } else {
                                    System.out.println(" Agreement details are not available . ");
                                    isDataInserted = false;

                                }
                            }
                            if(isDataInserted)
                                DatabaseService.updateDumpData(customervolfactordump);
                            else
                                System.out.println("data not saved" +  customervolfactordump);
                            System.out.println("Counter -- "+ counter);

                        }

                }else
                    System.out.println("Data insertion failed. Please check logs");




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static HashMap<String, CustomProductAttribute> productAttributeByCustomerMap(ArrayList<CustomProductAttribute> attributes) {
        HashMap<String, CustomProductAttribute> map = new HashMap<String, CustomProductAttribute>();
        if(null!= attributes && !attributes.isEmpty()){
            for(CustomProductAttribute customProductAttribute :  attributes){
                if(map.containsKey(customProductAttribute.getContractComponent().getContract().getSourceSystemRecordPk()+"~"+customProductAttribute.getItemId().toString())){
                    System.out.println("Details already exist --  Please verify #####################################");
                }else{
                    map.put(customProductAttribute.getContractComponent().getContract().getSourceSystemRecordPk()+"~"+customProductAttribute.getItemId().toString(),customProductAttribute);
                }
            }
        }
        return map;
    }


    private static void createCustomrProdAttribute(ContractComponent contractComponent, Customervolfactordump customervolfactordump, Service service) throws Exception{
        BeanRepository beanRepository = new BeanRepository();

        BeanRepository.buildCustomProductAttribute(customervolfactordump,contractComponent,service);
    }

    private static Set<ContractComponent> getContractComponents(ArrayList<ContractRole> roles){
        Set<ContractComponent> contractComponents = new HashSet<ContractComponent>();

        for(ContractRole contractRole : roles){
            contractComponents.add(contractRole.getContractComponent());
        }

        return contractComponents;
    }

}

