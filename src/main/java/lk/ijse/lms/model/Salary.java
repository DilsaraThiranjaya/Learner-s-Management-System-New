package lk.ijse.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Salary {
    private String monthOfPay;
    private String date;
    private String BasicP;
    private String OtP;
    private String employeeId;
}
