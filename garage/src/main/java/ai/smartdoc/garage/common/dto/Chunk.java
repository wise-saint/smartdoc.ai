package ai.smartdoc.garage.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chunk {
    private String text;
    private Integer startPgNo;
    private Integer endPgNo;
}
