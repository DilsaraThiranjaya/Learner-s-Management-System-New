package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class AttendanceTm {
    private String id;
    private String name;
    private String date;
    private String inTime;
    private String outTime;
    private String status;
}
