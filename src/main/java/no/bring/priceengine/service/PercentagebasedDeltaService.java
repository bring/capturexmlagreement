package no.bring.priceengine.service;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import no.bring.priceengine.precentagebased.PercentageDatabaseService;

import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

public class PercentagebasedDeltaService {

    private DatabaseService databaseService = new DatabaseService();
    private DeltaServiceImpl deltaServiceImpl = new DeltaServiceImpl();
    private DeltaRecordValidation deltaRecordValidation = new DeltaRecordValidation();

    private QueryService queryService = new QueryService();
    private final String NEW_CUSTOMER = "New Customer";
    private final String CUSTOMER_WITHOUT_DISCOUNT_LINE = "Customer without discount line";
    private final String NEW_SERVICE = "New Service";
    private final String NEW_ROUTES = "New Routes";
    private final String NEW_DATES = "New Dates";
    private final String OVERLAP_WITH_EXISTING_DATES = "Overlap with anyone of dates";
    private final String OVERLAP_WITH_BOTH_DATES = "Overlap with both start-end dates";
    private final String NEW_PRICES = "New Prices";
    private final String RECORD_ALREADY_EXIST = "Already exist";
    private final String ITEM_ID_NOT_CONFIGURED = "Item not configured in PEDB";

    public void processDeltaAgreements(String filecountry, Logger logger) throws ParseException {

        ArrayList<Percentagebaseddump> newContractdumps = new ArrayList<Percentagebaseddump>();
        Set<Percentagebaseddeltadump> deltacontractdumpsToUpload = new HashSet<Percentagebaseddeltadump>();
        ArrayList<Long> deltacontractdumpNewRoutesIdPERCENT = new ArrayList<Long>();
        ArrayList<Long> contractPricesToDelete = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney1 = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney2 = new ArrayList<Long>();

        HashMap<Long, ArrayList<Percentagebaseddeltadump>> contractPricesBothJourneyMap = new HashMap<Long, ArrayList<Percentagebaseddeltadump>>();
        HashSet<Percentagebaseddeltadump> deltalist = queryService.findAllPercentbasedDeltaContractdumpsWithJDBC(filecountry,logger);
        if (null != deltalist && !deltalist.isEmpty()) {
            HashMap<String, Set<String>> organizationMap = filterOrgnizationAndCustomers(deltalist);
            if (!organizationMap.isEmpty()) {
                deltaRecordValidation.validateCustomerStatusPercent(organizationMap, filecountry, logger, true);
            }
            deltalist = queryService.findAllPercentbasedDeltaContractdumpsWithJDBC(filecountry,logger);
            ArrayList<Percentagebaseddeltadump> newOrganization = new ArrayList<Percentagebaseddeltadump>();
            for (Percentagebaseddeltadump percentagebaseddeltadump : deltalist) {
                Boolean status = false;
                if (null == queryService.findPartyBySSPK(percentagebaseddeltadump.getParentCustomerNumber()))
                    newOrganization.add(percentagebaseddeltadump);
            }
            if (!newOrganization.isEmpty()) {
                databaseService.updateDeltaAgreementsPERCENTUsingJDBC(newOrganization, NEW_CUSTOMER, true,logger);
                deltacontractdumpsToUpload.addAll(newOrganization);
                //newContractdumps.addAll(deltaToContractdumpsPercent(newOrganization));
            }

            deltalist = queryService.findAllPercentbasedDeltaContractdumpsWithJDBC(filecountry,logger);
            ArrayList<ContractPrice> contractPricesBothJourney = new ArrayList<ContractPrice>();
            for (Percentagebaseddeltadump percentagebaseddeltadump : deltalist) {
               if(null!=percentagebaseddeltadump.getFromLocation() && percentagebaseddeltadump.getFromLocation().equalsIgnoreCase("null"))
                   percentagebaseddeltadump.setFromLocation(null);
                if(null!=percentagebaseddeltadump.getToLocation() && percentagebaseddeltadump.getToLocation().equalsIgnoreCase("null"))
                    percentagebaseddeltadump.setToLocation(null);

                //   ContractComponent contractComponent = queryService.findCustomerInContractComponent(percentagebaseddeltadump.getParentCustomerNumber());
                ContractRole contractRole = queryService.findCustomerInContractRoleAllType(percentagebaseddeltadump.getParentCustomerNumber());
                // Search if customer combination exist..
                if (null != contractRole) {
                    Item item = queryService.getItem(percentagebaseddeltadump.getProdno());
                    if (null != item) {
                        Item itemInCP = item;
                    if( item.getSourceSystemRecordPk().toString().length()==5 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                        itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 3)));
                        } else if( item.getSourceSystemRecordPk().toString().length()==7 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                        itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 5)));
                        }
                        List<ContractPrice> contractPrices = queryService.findContractPriceByComponentIDPERCENT(contractRole.getContractComponent().getContractComponentId());
                        if (contractPrices != null && !contractPrices.isEmpty()) {
                            Set<Integer> existinsItems = new HashSet<Integer>();
                            Iterator iter = contractPrices.iterator();
                            while (iter.hasNext()) {
                                ContractPrice cp = (ContractPrice) iter.next();
                                if (cp.getItemIdDup().equals(itemInCP.getItemId())) {
                                    existinsItems.add(cp.getItemIdDup().intValue());
                                } else
                                    iter.remove();
                            }
                            if (existinsItems.contains(itemInCP.getItemId().intValue()) && !contractPrices.isEmpty()) {
                                // CHECK ROUTE
                                Boolean isRouteExist = false;
                                Iterator itr = contractPrices.iterator();
                                while (itr.hasNext()) {
                                    ContractPrice cp = (ContractPrice) itr.next();
                                    boolean isServicePassiveReturned = false;
                                    if (percentagebaseddeltadump.getProdno().toString().substring(percentagebaseddeltadump.getProdno().toString().length() - 2, percentagebaseddeltadump.getProdno().toString().length()).equals("64"))
                                        isServicePassiveReturned = true;
                                    if (deltaServiceImpl.validateRoutesPERCENTBASED(cp, percentagebaseddeltadump, isServicePassiveReturned, contractPricesBothJourney, contractPricesBothJourneyMap)) {
                                        isRouteExist = true;
                                    } else
                                        itr.remove();
                                }
                                if (!isRouteExist) {
                                   // newContractdumps.add(deltaToPercentBasedsContractdump(percentagebaseddeltadump));
                                    databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, NEW_ROUTES, true, logger);
                                    // deltacontractdumpNewRoutesIdPERCENT.add(percentagebaseddeltadump.getId().longValue());
                                    deltacontractdumpsToUpload.add(percentagebaseddeltadump);
                                }
                                // CHECK DATES
                                if (!contractPrices.isEmpty()) {
                                    Iterator itrDate = contractPrices.iterator();
                                    Boolean isDateExist = false;
                                    Boolean isDateConflict = false;
                                    while (itrDate.hasNext()) {
                                        ContractPrice cp = (ContractPrice) itrDate.next();
                                        if (!deltaServiceImpl.isNotOverlapWithSameDatesPERCENT(cp, percentagebaseddeltadump)) {
                                            isDateExist = true;
                                            if (deltaServiceImpl.isSamePricePERCENT(cp, percentagebaseddeltadump)) {
                                                databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, RECORD_ALREADY_EXIST, false, logger);
                                                itrDate.remove();
                                            } else {
                                    //            newContractdumps.add(deltaToContractdump(percentagebaseddeltadump));
                                                percentagebaseddeltadump.setPriceId(cp.getPriceId().longValue());
                                                deltacontractdumpsToUpload.add(percentagebaseddeltadump);
                                                databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, NEW_PRICES, true, logger);
                                                itrDate.remove();
                                                //                               break;
                                            }
                                        }else{
                                          //  newContractdumps.add(deltaToContractdump(percentagebaseddeltadump));
                                            deltacontractdumpsToUpload.add(percentagebaseddeltadump);
                                            databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, NEW_DATES, true, logger);
                                        }
                                    }
                                }
                                //    break;
                            } else {
                               // newContractdumps.add(deltaToContractdump(percentagebaseddeltadump));
                                deltacontractdumpsToUpload.add(percentagebaseddeltadump);
                                databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, NEW_SERVICE, true, logger);
                                }
                        } else {
                            deltacontractdumpsToUpload.add(percentagebaseddeltadump);
                           // newContractdumps.add(deltaToContractdump(percentagebaseddeltadump));
                            databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, NEW_PRICES, true, logger);
                        }
                    }else{
                        databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, ITEM_ID_NOT_CONFIGURED, true, logger);
                    }
                } else {
                    deltacontractdumpsToUpload.add(percentagebaseddeltadump);
//                    newContractdumps.add(deltaToContractdump(percentagebaseddeltadump));
                    databaseService.updateDeltaAgreementPERCENT(percentagebaseddeltadump, CUSTOMER_WITHOUT_DISCOUNT_LINE, true, logger);
                }
            }
        }
        // COMPARE AND UPDATE
        if (!contractPricesBothJourneyMap.isEmpty()) {
            for (Long priceId : contractPricesBothJourneyMap.keySet()) {
                Boolean isForward = false;
                Boolean isBackward = false;
                Boolean deltaDumpHavPriceID = false;
                for (Percentagebaseddeltadump percentagebaseddeltadump : contractPricesBothJourneyMap.get(priceId)) {
                    if (percentagebaseddeltadump.getProdno().toString().endsWith("64"))
                        isBackward = true;
                    else
                        isForward = true;
                    if (percentagebaseddeltadump.getPriceId() != null)
                        deltaDumpHavPriceID = true;
                }
                if (isBackward && isForward && deltaDumpHavPriceID) {
                    contractPricesToDelete.add(priceId);
                } else if (isForward && !isBackward && deltaDumpHavPriceID) {
                    contractPricesToUpdateJourney2.add(priceId);
                    databaseService.updateDeltaAggRemovePrice(priceId);
                } else if (!isForward && isBackward && deltaDumpHavPriceID) {
                    contractPricesToUpdateJourney1.add(priceId);
                    databaseService.updateDeltaAggRemovePrice(priceId);
                }
            }
        }
        if (!contractPricesToUpdateJourney1.isEmpty()) {
            for (Long priceId : contractPricesToUpdateJourney1) {
                ContractPrice contractPrice = queryService.findContractPriceObject(priceId);
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                contractPrice.setApplJourneyTpCd(1);
                databaseService.updateContractPriceJourneyType(1, priceId);
            }
        }
        if (!contractPricesToUpdateJourney2.isEmpty()) {
            for (Long priceId : contractPricesToUpdateJourney2) {
                ContractPrice contractPrice = queryService.findContractPriceObject(priceId);
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                contractPrice.setApplJourneyTpCd(2);
                databaseService.updateContractPriceJourneyType(2, priceId);
            }
        }
        if (!contractPricesBothJourneyMap.isEmpty()) {
            for (Long priceId : contractPricesBothJourneyMap.keySet()) {
                databaseService.updateDeltaAggRemovePrice(priceId);
            }
        }

//        if(null!=newContractdumps && !newContractdumps.isEmpty())
//            databaseService.upsertContractData(newContractdumps);
//    }

//        if (null != newContractdumps && !newContractdumps.isEmpty()) {
//            PercentageDatabaseService percentageDatabaseService = new PercentageDatabaseService();
//            percentageDatabaseService.insertContractData(newContractdumps);
//        }

        if(!deltacontractdumpsToUpload.isEmpty()){
            PercentageDatabaseService percentageDatabaseService = new PercentageDatabaseService();
            ArrayList<Percentagebaseddeltadump> dumps = new ArrayList<Percentagebaseddeltadump>(deltacontractdumpsToUpload);
            newContractdumps.addAll(deltaToContractdumpsPercent(dumps));
            percentageDatabaseService.insertContractData(newContractdumps);
        }
    }

    private Percentagebaseddump deltaToContractdump(Percentagebaseddeltadump percentagebaseddeltadump){
        Percentagebaseddump percentagebaseddump = new Percentagebaseddump();

        percentagebaseddump.setParentCustomerNumber(percentagebaseddeltadump.getParentCustomerNumber());
        percentagebaseddump.setParentCustomerName(percentagebaseddeltadump.getParentCustomerName());
        percentagebaseddump.setCustomerNumber(percentagebaseddeltadump.getCustomerNumber());
        percentagebaseddump.setCustomerName(percentagebaseddeltadump.getCustomerName());

        percentagebaseddump.setFromLocation(percentagebaseddeltadump.getFromLocation());
        percentagebaseddump.setToLocation(percentagebaseddeltadump.getToLocation());
        percentagebaseddump.setEnabled(true);
        percentagebaseddump.setRemark(5);
        percentagebaseddump.setPrecentageDiscount(percentagebaseddeltadump.getPrecentageDiscount());

        percentagebaseddump.setFileCountry(percentagebaseddeltadump.getFileCountry());
        percentagebaseddump.setStartdate(percentagebaseddeltadump.getStartdate());

        percentagebaseddump.setProdno(percentagebaseddeltadump.getProdno());

        percentagebaseddump.setRouteType(percentagebaseddeltadump.getRouteType());
        percentagebaseddump.setEnddate(percentagebaseddeltadump.getEnddate());
        percentagebaseddump.setUpdated(false);
        percentagebaseddump.setZoneType(percentagebaseddeltadump.getZoneType());

        return  percentagebaseddump;
    }

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomers(HashSet<Percentagebaseddeltadump> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Percentagebaseddeltadump contractdump : contractdumps) {
                if (contractdump.getParentCustomerNumber().equals(contractdump.getCustomerNumber())){
                    if (!orgMap.containsKey(contractdump.getParentCustomerNumber())) {
                        Set<String> customerList = new HashSet<String>();
                        orgMap.put(contractdump.getParentCustomerNumber(), customerList);
                    }
                } else {
                    if (orgMap.containsKey(contractdump.getParentCustomerNumber())) {
                        if (!orgMap.get(contractdump.getParentCustomerNumber()).contains(contractdump.getCustomerNumber()))
                            orgMap.get(contractdump.getParentCustomerNumber()).add(contractdump.getCustomerNumber());
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber());
                        orgMap.put(contractdump.getParentCustomerNumber(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }



    private Percentagebaseddump deltaToPercentBasedsContractdump(Percentagebaseddeltadump deltacontractdump){
        Percentagebaseddump contractdump = new Percentagebaseddump();

        contractdump.setParentCustomerNumber(deltacontractdump.getParentCustomerNumber());
        contractdump.setParentCustomerName(deltacontractdump.getParentCustomerName());
        contractdump.setCustomerNumber(deltacontractdump.getCustomerNumber());
        contractdump.setCustomerName(deltacontractdump.getCustomerName());
        contractdump.setToLocation(deltacontractdump.getToLocation());
        contractdump.setFromLocation(deltacontractdump.getFromLocation());
        contractdump.setEnabled(true);
        contractdump.setRemark(5);
        contractdump.setFileCountry(deltacontractdump.getFileCountry());
        contractdump.setStartdate(deltacontractdump.getStartdate());
        contractdump.setProdno(deltacontractdump.getProdno());
        contractdump.setRouteType(deltacontractdump.getRouteType());
        contractdump.setEnddate(deltacontractdump.getEnddate());
        contractdump.setUpdated(false);
        contractdump.setZoneType(deltacontractdump.getZoneType());
        if(deltacontractdump.getPrecentageDiscount().contains("-"))
            contractdump.setPrecentageDiscount(deltacontractdump.getPrecentageDiscount().replace("-",""));
        else
            contractdump.setPrecentageDiscount(deltacontractdump.getPrecentageDiscount());


        return  contractdump;
    }

    private ArrayList<Percentagebaseddump> deltaToContractdumpsPercent(ArrayList<Percentagebaseddeltadump> deltacontractdumps){
        ArrayList<Percentagebaseddump> contractdumps = new ArrayList<Percentagebaseddump>();
        for(Percentagebaseddeltadump deltacontractdump : deltacontractdumps) {
            Percentagebaseddump contractdump = new Percentagebaseddump();

            contractdump.setParentCustomerNumber(deltacontractdump.getParentCustomerNumber());
            contractdump.setParentCustomerName(deltacontractdump.getParentCustomerName());
            contractdump.setCustomerNumber(deltacontractdump.getCustomerNumber());
            contractdump.setCustomerName(deltacontractdump.getCustomerName());
            contractdump.setToLocation(deltacontractdump.getToLocation());
            contractdump.setFromLocation(deltacontractdump.getFromLocation());
            contractdump.setEnabled(true);
            contractdump.setRemark(6);
            contractdump.setFileCountry(deltacontractdump.getFileCountry());
            contractdump.setStartdate(deltacontractdump.getStartdate());
            contractdump.setProdno(deltacontractdump.getProdno());
            contractdump.setRouteType(deltacontractdump.getRouteType());
            contractdump.setEnddate(deltacontractdump.getEnddate());
            contractdump.setUpdated(false);

            contractdump.setZoneType(deltacontractdump.getZoneType());
            if(deltacontractdump.getPrecentageDiscount().contains("-"))
                contractdump.setPrecentageDiscount(deltacontractdump.getPrecentageDiscount().replace("-",""));
            else
                contractdump.setPrecentageDiscount(deltacontractdump.getPrecentageDiscount());
            contractdumps.add(contractdump);
        }
        return  contractdumps;
    }


}
