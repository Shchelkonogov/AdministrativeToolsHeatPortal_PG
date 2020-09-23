package specificModel.report;

import ru.tecon.admTools.specificModel.report.ChangeRanges;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChangeRangesTest {

    public static void main(String[] args) throws IOException {
        OutputStream out = new FileOutputStream("C:/Programs/1.xlsx");
        ChangeRanges.generateChangeRanges(0,0,0,0,"","10.10.2020","", new ChangeRangesModelDataTest()).write(out);
    }
}
