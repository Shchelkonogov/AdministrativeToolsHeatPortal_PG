package specificModel.report;

import org.apache.poi.ss.usermodel.Workbook;
import ru.tecon.admTools.report.ModeMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ModeMapTest {

    public static void main(String[] args) throws IOException {
        try (OutputStream out = new FileOutputStream("C:/Programs/1.xlsx");
             Workbook workbook = ModeMap.generateModeMap(455897, new ModeMapModelDataTest())) {
            workbook.write(out);
            out.flush();
        }
    }
}
