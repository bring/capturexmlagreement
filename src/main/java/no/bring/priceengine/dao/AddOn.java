package no.bring.priceengine.dao;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "addon", schema = "core")

@PrimaryKeyJoinColumn(name = "item_id")
@DiscriminatorValue(value = "1")
@Cacheable(true)
public class AddOn  extends Item implements Serializable {


    @Column(name = "addon_name")
    private String addOnName;

    public AddOn() {
    }

    public AddOn(String addOnName) {
        this.addOnName = addOnName;
    }

    public String getAddOnName() {
        return addOnName;
    }

    public void setAddOnName(String addOnName) {
        this.addOnName = addOnName;
    }

}
