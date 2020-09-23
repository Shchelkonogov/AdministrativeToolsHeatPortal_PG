package specificModel.report;

import ru.tecon.admTools.report.ModeControl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ModeControlTest {

    public static void main(String[] args) throws IOException {
        OutputStream out = new FileOutputStream("C:/Programs/1.xlsx");
        ModeControl.generateModeControl(1, 0, "", 456, 123, "ALLACCESS", new ModeControlModelDataTest()).write(out);
    }
}
