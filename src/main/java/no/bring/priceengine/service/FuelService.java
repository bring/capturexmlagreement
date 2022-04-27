package no.bring.priceengine.service;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;

import java.util.ArrayList;
import java.util.List;

public class FuelService {

    private QueryService queryService = new QueryService();
    private DatabaseService databaseService = new DatabaseService();

// GLOBAL-MARPOL-SURCHARGE-EXEMPTION-Pallet
    private static final String marpolSurchargeExemptionParcel = "GLOBAL-MARPOL-SURCHARGE-EXEMPTION-Parcel";
    private static final String marpolSurchargeExemptionPallet = "GLOBAL-MARPOL-SURCHARGE-EXEMPTION-Pallet";
    private static final String fuelSurchargeExemptionParcel = "GLOBAL-FUEL-SURCHARGE-EXEMPTION-Parcel";
    private static final String fuelSurchargeExemptionPallet = "GLOBAL-FUEL-SURCHARGE-EXEMPTION-Pallet";

    private String childCustomerLoggerMessage = "C/O : ";

    public void saveSurchargeExemptionData() throws Exception{
        List<Item> items = queryService.getSurchargeItems();
         StringBuilder logger = new StringBuilder();

         // Here you got all the items for surcharge exemption
        ArrayList<Integer> itemIDList = new ArrayList<Integer>();
        for(Item item : items){
            itemIDList.add(item.getItemId().intValue());
        }

       List<Surchargedump> dumps = queryService.findAllSurchargeDumps();
       for(Surchargedump dump :  dumps) {

           Boolean status = false;

           ContractRole contractRole = queryService.findCustomerInContractRole(dump.getCustomerNumber());
      //     Customerbranch customerBranch = queryService.findCustomerBranch(dump.getCustomerNumber());
           if(dump.getCustomerNumber().equals("20008859496"))
               System.out.println("sdfsdfsdf");
           Party parentParty = queryService.findParentParty(dump.getCustomerNumber());
           Party childParty = queryService.findChildPartyWithoutParent(dump.getCustomerNumber());

     if (null != contractRole && null != parentParty && childParty==null ) {
               List<ContractPrice> contractpriceList = queryService.getContractPriceByComponentID(contractRole.getContractComponent().getContractComponentId().intValue(), itemIDList);
               if (contractpriceList.isEmpty()) {
                   // Insert new contractprice
                   status =  saveSurchargeItemsInContractPrice(contractRole, dump, items, logger);
                   if (status)
                       databaseService.disableSurchargeDump(dump);
                   else {
                       System.out.println("Data not saved for  - Surcharge dump ID - " + dump.getId());
//                       System.exit(1);
                   }
               }
           }
     else if(null!=childParty){
                   System.out.println("Customer is already a child customer. " + childParty.getSourceSystemRecordPk());
                   childCustomerLoggerMessage = childCustomerLoggerMessage =" " + childParty.getParentSourceSystemRecordPk();
                   dump.setRemark(childCustomerLoggerMessage);
                   dump.setEnabled(false);
                   databaseService.updateSurchargeDump(dump);
           }
           else if(parentParty==null && childParty==null){

               // check if customer is not a child customer

                       Contract contract = null;
                       ContractComponent persistedContractComponent = null;
                     parentParty = databaseService.insertOrganizationParty(dump.getCustomerNumber()+"~"+ dump.getCustomerName());
                    contract = queryService.findContract(parentParty.getSourceSystemRecordPk());
                    if(null==contract) {
                        contract = databaseService.buildContractSURCHARGE(dump.getCustomerNumber(), dump.getCustomerName(), parentParty);
                        databaseService.insertContract(contract);
                    }
                       Contract persistedContract = queryService.findContract(contract.getSourceSystemRecordPk());
                       if (null != persistedContract) {
                           persistedContractComponent = queryService.findContractComponent(persistedContract);
                           if (null == persistedContractComponent) {
                               persistedContractComponent = databaseService.buildContractComponentForSurcharge(persistedContract);
                               persistedContractComponent = databaseService.insertContractComponent(persistedContractComponent);// ContractComponenet inserted
                               Contractdump contractdump = new Contractdump();
                               contractdump.setCustomerNumber(dump.getCustomerNumber());
                               contractRole = databaseService.buildContractRoleForSurcharge(persistedContractComponent, dump, parentParty);
                               Long contractRoleId = databaseService.insertContractRole(contractRole);
                               ContractRole  persistedContractRole = queryService.findContractRole(contractRoleId);
                               status = saveSurchargeItemsInContractPrice(persistedContractRole, dump, items, logger);
                               if (status)
                                   databaseService.disableSurchargeDump(dump);

                           } else {
                               ContractRole role = queryService.findContractRolesSurcharge(persistedContractComponent);
                               if (role != null) {
                                   status = saveSurchargeItemsInContractPrice(role, dump, items, logger);
                                   if (status)
                                       databaseService.disableSurchargeDump(dump);

                               }else {
                                   Party partyForContractRole = queryService.findParentParty(dump.getCustomerNumber());
                                   ContractRole persistedContractRole = databaseService.buildContractRoleForSurcharge(persistedContractComponent, dump, partyForContractRole);

                                   Long contractRoleId = null;
                                   persistedContractRole.setContractComponent(persistedContractComponent);
                                   contractRoleId = databaseService.insertContractRoleSurcharge(persistedContractRole);
                                   persistedContractRole = queryService.findContractRole(contractRoleId);
                                   // add surcharge exemption code here
                                   status = saveSurchargeItemsInContractPrice(persistedContractRole, dump, items, logger);
                                   if (status)
                                       databaseService.disableSurchargeDump(dump);


                               }
                               if (!status) {
                                   System.out.println("Surcharge data save failed" + dump.getId());
                                   System.exit(1);
                               }
                           }

                       } else {
                           Long id = databaseService.insertContract(contract);
                           if (null != id) {
                               persistedContract = queryService.findContractById(id);// Contract inserted here
                               persistedContractComponent = new ContractComponent();
                               persistedContractComponent = databaseService.buildContractComponentForSurcharge(persistedContract);
                               persistedContractComponent = databaseService.insertContractComponent(persistedContractComponent);// ContractComponenet inserted
                               // INSERT ROLES
                               Party partyForContractRole = queryService.findParentParty(dump.getCustomerNumber());
                               ContractRole persistedContractRole = databaseService.buildContractRoleForSurcharge(persistedContractComponent, dump, partyForContractRole);

                               Long contractRoleId = null;
                               persistedContractRole.setContractComponent(persistedContractComponent);
                               contractRoleId = databaseService.insertContractRole(persistedContractRole);
                               persistedContractRole = queryService.findContractRole(contractRoleId);
                               // add surcharge exemption code here
                               status = saveSurchargeItemsInContractPrice(persistedContractRole, dump, items, logger);
                               if (!status) {
                                   System.out.println("Surcharge data save failed" + dump.getId());
                                   System.exit(1);
                               }
                           } else {
                               System.out.println("Some issue occured");
                           }
                       }

                   }else{
                       databaseService.disableVoilatedSurchargeDump(dump, childParty);
                   }

               }
           }
               // END




    private Boolean upsertItem(Item item, ContractComponent contractComponent, Surchargedump dump, StringBuilder logger){
        System.out.println("VALUE DATA FOUND NULL "+ dump.getId());
        Boolean status =false;
        String sulphurParcel =  dump.getSulphurParcel();
        String sulphurParcelPallet = dump.getSulphurParcelPallet();

        String fuelParcel = dump.getFuelParcel();
        String fuelPallet = dump.getFuelPallet();

        String percentFuelParcel = dump.getPercentageFuelParcel();
        String percentFuelPallet = dump.getPercentageFuelPallet();
        // Nothing required for value Y. For value N percent value = 0
        if(item.getItemName().equals(marpolSurchargeExemptionParcel)){
            if(sulphurParcel.equals("N")){
                double percentValue = 0.00;
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            }else if (sulphurParcel.equals("Y"))
                status=true;

            // Nothing required for value Y. For value N percent value = 0
        }else if(item.getItemName().equals(marpolSurchargeExemptionPallet)){
            if(sulphurParcelPallet.equals("N")){
                double percentValue = 0.00;
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            } else if(sulphurParcelPallet.equals("Y"))
                status=true;

        }else if(item.getItemName().equals(fuelSurchargeExemptionParcel)){
            if(fuelParcel.equals("N")){
                double percentValue = 0.00;
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            }else if(fuelParcel.equals("Y") && percentFuelParcel!=null){
                double percentValue = new Double(percentFuelParcel);
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            }else
                status = true;
        }else  if(item.getItemName().equals(fuelSurchargeExemptionPallet)){
            if(fuelPallet.equals("N")){
                double percentValue = 0.00;
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            }else if(fuelPallet.equals("Y") && percentFuelPallet!=null){
                double percentValue = new Double(percentFuelPallet);
                Long priceId = databaseService.insertSurchargePrice(contractComponent, item, percentValue);
                status =  databaseService.insertContractPriceExemmption(contractComponent, item, priceId);
            }else
                status= true;
        }
        return status;
    }

    private Boolean saveSurchargeItemsInContractPrice(ContractRole contractRole,  Surchargedump dump, List<Item> items,  StringBuilder logger) {
        Boolean status = false;
        for (Item item : items) {
            Boolean isUpdated = upsertItem(item, contractRole.getContractComponent(), dump,  logger);
            status = isUpdated;
            if (!status) {
                System.out.println("wait "+ dump.getId());
                return false;
            }
        }
        return status;
    }
}
