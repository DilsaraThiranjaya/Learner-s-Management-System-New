package lk.ijse.lms.model;

import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Vehicle {
    private String id;
    private String type;
    private String model;
    private String fType;
    private String transmission;
    private String rNumber;
    private String status;
    private ObservableList<String> employeeList;
}
