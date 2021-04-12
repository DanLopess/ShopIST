package pt.ulisboa.tecnico.cmov.shopist.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class StoreProduct {
    private Product product;
    private Float price;
    @Setter(AccessLevel.NONE)
    private Integer quantityToBuy;
    @Setter(AccessLevel.NONE)
    private Integer quantityInCart;

    public void increaseQuantity() {
        quantityToBuy++;
    }
}