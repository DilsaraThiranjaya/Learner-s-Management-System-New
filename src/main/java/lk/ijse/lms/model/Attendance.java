package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Attendance {
    private String role;
    private String date;
    private String inTime;
    private String outTime;
    private String status;
    private String id;
}
