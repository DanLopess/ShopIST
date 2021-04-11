package pt.ulisboa.tecnico.cmov.shopist.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ProductWithInfo {
    private Product product;
    private Integer price;
    @Setter(AccessLevel.NONE)
    private Integer quantityWanted;
    @Setter(AccessLevel.NONE)
    private Integer quantityAvailable;

    public void increaseQuantityWanted() {
        quantityWanted++;
    }

    public void setQuantityWanted(Integer quantity) {
        quantityWanted = quantity;
        if (quantityWanted < 0)
            quantityWanted = 0;
    }

    public void setQuantityAvailable(Integer quantity) {
        quantityAvailable = quantity;
        if (quantityAvailable < 0)
            quantityAvailable = 0;
    }
}
