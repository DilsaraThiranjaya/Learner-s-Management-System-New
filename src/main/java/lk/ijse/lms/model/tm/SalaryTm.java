package lk.ijse.lms.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SalaryTm {
    private String employeeId;
    private String name;
    private String monthOfPay;
    private String date;
    private String basicP;
    private String otP;
    private double totalP;
}
