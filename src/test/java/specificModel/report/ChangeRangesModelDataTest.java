package specificModel.report;

import ru.tecon.admTools.specificModel.report.ejb.ChangeRangesLocal;
import ru.tecon.admTools.specificModel.report.model.ChangeRangesModel;

import java.util.ArrayList;
import java.util.List;

public class ChangeRangesModelDataTest implements ChangeRangesLocal {

    @Override
    public String getPath(Integer structID, Integer objectID) {
        return "ПАО \"МОЭК\" / Филиал 4 / Предприятие 1";
    }

    @Override
    public List<ChangeRangesModel> loadReportData(int objType, Integer structID, Integer objID, int filterType,
                                                  String filter, String date, String user) {
        List<ChangeRangesModel> result = new ArrayList<>();
        result.add(new ChangeRangesModel("1", "2", "3", "4", "5", "6", "7", "8"));
        result.add(new ChangeRangesModel("1", "2", "3", "4", "5", "6", "7", "8"));
        result.add(new ChangeRangesModel("1", "2", "3", "4", "5", "6", "7", "8"));
        return result;
    }

    @Override
    public List<ChangeRangesModel> loadReportData(int objType, Integer structID, Integer objID,
                                                  int filterType, String filter, String date, String user, boolean eco) {
        return loadReportData(objType, structID, objID, filterType, filter, date, user);
    }
}
