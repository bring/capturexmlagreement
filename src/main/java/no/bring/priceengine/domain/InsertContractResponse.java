package no.bring.priceengine.domain;

import no.bring.priceengine.dao.Contract;

import java.io.Serializable;
import java.util.List;

public class InsertContractResponse implements Serializable {

    private List<Contract> contracts;


    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }
}
