package pt.ulisboa.tecnico.cmov.shopist.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BeaconTime {
    private LocalDateTime timestamp;
    private Coordinates coordinates;
    private UUID uuid;
}
