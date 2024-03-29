package pt.ulisboa.tecnico.cmov.shopist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BeaconTimes {
    private LocalDateTime in;
    private LocalDateTime out;
}
