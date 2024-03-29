package pt.ulisboa.tecnico.cmov.shopist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PantryProductDto {
    private Long productId;
    private String uuid;
    private String productName;
    private String productDescription;
    private String barcode;
    private Integer qttAvailable;
    private Integer qttNeeded;
    private ProductRating rating;
}