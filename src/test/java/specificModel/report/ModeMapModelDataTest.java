package specificModel.report;

import ru.tecon.admTools.specificModel.report.ejb.ModeMapLocal;

import java.util.*;

public class ModeMapModelDataTest implements ModeMapLocal {

    @Override
    public Map<String, String> loadSingleData(int objectID) {
        Map<String, String> singleData = new HashMap<>();

        singleData.put("{filial}", "ФИЛИАЛ");
        singleData.put("{objName}", "ИМЯ");
        singleData.put("{objAddress}", "АДРЕС");
        singleData.put("{district}", "РАЙОН");
        singleData.put("{vvod}", "ВВОД");
        singleData.put("{mks}", "МКС");
        singleData.put("{Q1}", "1");
        singleData.put("{Q2}", "2");
        singleData.put("{Q3}", "3");
        singleData.put("{Q4}", "4");
        singleData.put("{buildings}", "5");
        singleData.put("{floors}", "6");
        singleData.put("{T1Graph}", "1");
        singleData.put("{T2Graph}", "2");
        singleData.put("{P1}", "3");
        singleData.put("{P2}", "4");
        singleData.put("{P1Min}", "5");
        singleData.put("{P2Min}", "6");
        singleData.put("{P1Max}", "7");
        singleData.put("{P2Max}", "8");
        singleData.put("{Q}", "9");

        return singleData;
    }

    @Override
    public Map<String, List<Map<String, String>>> loadArrayData(int objectID) {
        Map<String, List<Map<String, String>>> arrayData = new HashMap<>();

        Map<String, String> m1 = new HashMap<>();
        m1.put("{zone[]}", "1");
        m1.put("{scheme[]}", "2");
        m1.put("{T3Graph[]}", "3");
        m1.put("{T4Graph[]}", "4");
        m1.put("{Q[]}", "5");
        Map<String, String> m2 = new HashMap<>();
        m2.put("{zone[]}", "6");
        m2.put("{scheme[]}", "7");
        m2.put("{T3Graph[]}", "8");
        m2.put("{T4Graph[]}", "9");
        m2.put("{Q[]}", "10");

        arrayData.put("CO", new ArrayList<>(Arrays.asList(m1, m2)));
        arrayData.put("GVS", new ArrayList<>(Collections.singletonList(m1)));
        arrayData.put("HVS", new ArrayList<>(Arrays.asList(m1, m2, m2)));

        return arrayData;
    }

    @Override
    public String getName(Integer objectID) {
        return null;
    }
}
