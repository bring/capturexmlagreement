package no.bring.priceengine.service;

import no.bring.priceengine.dao.ContractPrice;
import no.bring.priceengine.dao.Deltacontractdump;
import no.bring.priceengine.dao.Percentagebaseddeltadump;
import no.bring.priceengine.dao.Price;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PushDeltaAgreement {

    private DatabaseService databaseService = new DatabaseService();
    private QueryService queryService = new QueryService();
    private ExcelService excelService = new ExcelService();
    private DeltaServiceImpl deltaServiceImpl = new DeltaServiceImpl();


    public void deleteUpdatedPriceDetails(String fileCountry) throws  ParseException {
        List<Deltacontractdump> dumps = queryService.findDeltaContractdumpsWithPriceIDForDelete(fileCountry);
        ArrayList<Integer> priceIdDelete = new ArrayList<Integer>();
        if(null!=dumps && !dumps.isEmpty()){
            for(Deltacontractdump deltacontractdump : dumps) {
                ContractPrice contractPrice = queryService.findContractPriceObject(deltacontractdump.getPriceId().longValue());
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                priceIdDelete.add(price.getPriceId().intValue());
            }
        }
        if(!priceIdDelete.isEmpty()){
            databaseService.deleteContractPrice(priceIdDelete);
        }
    }

    public void deleteUpdatedPriceDetailsPERCENT(String fileCountry) throws  ParseException {
        List<Percentagebaseddeltadump> dumps = queryService.findDeltaContractdumpsWithPriceIDForDeletePERCENT(fileCountry);
        ArrayList<Integer> priceIdDelete = new ArrayList<Integer>();
        if(null!=dumps && !dumps.isEmpty()){
            for(Percentagebaseddeltadump percentagebaseddeltadump : dumps) {
                ContractPrice contractPrice = queryService.findContractPriceObject(percentagebaseddeltadump.getPriceId().longValue());
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                priceIdDelete.add(price.getPriceId().intValue());
            }
        }
        if(!priceIdDelete.isEmpty()){
            databaseService.deleteContractPrice(priceIdDelete);
        }
    }

}
