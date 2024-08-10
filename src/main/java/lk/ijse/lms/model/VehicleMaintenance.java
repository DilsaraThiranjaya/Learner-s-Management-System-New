package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class VehicleMaintenance {
    private String mId;
    private String description;
    private String date;
    private String cost;
    private String vId;
}
