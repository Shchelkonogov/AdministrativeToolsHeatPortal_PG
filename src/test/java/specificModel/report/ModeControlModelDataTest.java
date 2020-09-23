package specificModel.report;

import ru.tecon.admTools.specificModel.report.ejb.ModeControlLocal;
import ru.tecon.admTools.specificModel.report.model.ModeControlModel;

import java.util.ArrayList;
import java.util.List;

public class ModeControlModelDataTest implements ModeControlLocal {

    @Override
    public String getParamName(int paramID) {
        return String.valueOf(paramID);
    }

    @Override
    public String getStructPath(int structID) {
        return String.valueOf(structID);
    }

    @Override
    public List<ModeControlModel> getData(int objType, int structID, int filterType, String filter, int paramID, String user) {
        List<ModeControlModel> result = new ArrayList<>();
        result.add(new ModeControlModel("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        result.add(new ModeControlModel("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        result.add(new ModeControlModel("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        return result;
    }
}
