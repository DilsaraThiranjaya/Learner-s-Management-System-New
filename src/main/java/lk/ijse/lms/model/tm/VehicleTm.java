package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class VehicleTm {
    private String id;
    private String assignedEm;
    private String type;
    private String model;
    private String fType;
    private String transmission;
    private String rNumber;
    private String status;
}
