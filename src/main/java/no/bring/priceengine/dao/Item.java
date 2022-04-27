package no.bring.priceengine.dao;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "item", schema = "core")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_tp_cd", discriminatorType = DiscriminatorType.INTEGER)
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public abstract class Item implements DynamicAttributeComputable, Serializable {


    @Id
    @SequenceGenerator(sequenceName = "core.item_item_id_seq", name = "itemIdSequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemIdSequence")
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

//    @OneToMany(mappedBy = "item" , fetch=FetchType.LAZY)
//    @JsonManagedReference
//    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
//    @Where(clause = " price_tp_cd <> 3 ")
//    private Set<Price> itemPriceSet = new HashSet<>();

    @Column(name = "item_tp_cd")
    private long itemTpCd;

    @Column(name = "source_system_record_pk")
    private Long sourceSystemRecordPk;


    @Transient
    protected DynamicAttributeService dynamicAttributeService = null;


    protected Item() {
    }

    public Item(Long itemId, String itemName) {
        super();
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public long getItemTpCd() {
        return itemTpCd;
    }

    public Long getSourceSystemRecordPk() {
        return sourceSystemRecordPk;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

//    public void setItemPriceSet(Set<Price> itemPriceSet) {
//        this.itemPriceSet = itemPriceSet;
//    }

    public void setItemTpCd(long itemTpCd) {
        this.itemTpCd = itemTpCd;
    }

    public void setSourceSystemRecordPk(Long sourceSystemRecordPk) {
        this.sourceSystemRecordPk = sourceSystemRecordPk;
    }


    public boolean isServiceZoneItem()
    {
        boolean isService = false ;
        if( itemTpCd == 4)
        {
            isService = true;
        }
        return isService;
    }


    @Override
    public final void setDynamicAttributeService(DynamicAttributeService dynamicAttributeService)
    {
        this.dynamicAttributeService = dynamicAttributeService;
    }

    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        sb.append("itemId", itemId);
        sb.append("itemName", itemName);
        sb.append("itemTpCd", itemTpCd);
        sb.append("sourceSystemRecordPk", sourceSystemRecordPk);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (itemId ^ (itemId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (itemId != other.itemId)
            return false;
        return true;
    }


}