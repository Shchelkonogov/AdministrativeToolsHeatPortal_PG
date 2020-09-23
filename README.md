# AdministrativeToolsHeatPortal

Форма Конкретная модель.

Строка вызова: 
http://{host}:{port}/admTools/specificModel?objectID={objectID}&formID={formID}&sessionID={sessionID}

Отчеты:
Режимная карта
http://{host}:{port}/admTools/specificModel/report/modeMap?objectID={objectID}&sessionID={sessionID}
Отчет по изменению тех границ
http://{host}:{port}/admTools/specificModel/report/technicalLimitsChangeReport?
    id={("S" + structID) || ("O" + objectID)}&objectType={objectType}&date={dd.mm.yyyy}&sessionID={sessionID}
Контроль режима
http://{host}:{port}/admTools/specificModel/report/modeControl?
    structID={structID}&paramID={paramID}&objectType={objectType}&sessionID={sessionID}