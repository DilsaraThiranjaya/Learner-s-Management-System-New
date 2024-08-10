package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class VehicleMaintenanceTm {
    private String mId;
    private String description;
    private String date;
    private String cost;
    private String vId;
}
