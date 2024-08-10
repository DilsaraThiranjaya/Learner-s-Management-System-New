package lk.ijse.lms.model;

import javafx.collections.ObservableList;
import lk.ijse.lms.model.tm.CoursePriceTm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Payment {
    private String pId;
    private String desc;
    private String date;
    private String method;
    private String amount;
    private String sId;
    private ObservableList<CoursePriceTm> cp;
}
