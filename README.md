# AdministrativeToolsHeatPortal

Форма Конкретная модель.

Строка вызова: 
{url}/specificModel?objectID={objectID}&formID={formID}&sessionID={sessionID}

Отчеты:
Режимная карта
{url}/specificModel/report/modeMap?objectID={objectID}&sessionID={sessionID}
Отчет по изменению тех границ
{url}/specificModel/report/technicalLimitsChangeReport?
    id={("S" + structID) || ("O" + objectID)}&objectType={objectType}&date={dd.mm.yyyy}&sessionID={sessionID}
Контроль режима
{url}/specificModel/report/modeControl?
    structID={structID}&paramID={paramID}&objectType={objectType}&sessionID={sessionID}