# AdministrativeToolsHeatPortal

Форма Конкретная модель.

Строка вызова: 
{url}/specificModel?objectID={objectID}&formID={formID}&sessionID={sessionID}

Отчеты:
Режимная карта
{url}/specificModel/report/modeMap?objectID={objectID}&sessionID={sessionID}
Отчет по изменению тех границ
{url}/specificModel/report/technicalLimitsChangeReport?
    id={("S" + objectID) || ("O" + objectID)}&objectType={objectType}&date={dd.mm.yyyy}&sessionID={sessionID}
Контроль режима
{url}/specificModel/report/modeControl?
    objectID={objectID}&paramID={paramID}&objectType={objectType}&sessionID={sessionID}
    
Форма Матрица проблем.

Отчеты:
Анализ первичных измерений
{url}/admTools/matrixProblems/report 
    (post запрос данные вида {"date":"08.10.2020","filterID":0,"filterValue":"текон","structID":1508})

Форма Анализ достоверности.
{url}/dataAnalysis?
    id={("S" + objectID) || ("O" + objectID)}&filterID={filterID}&filterValue={filterValue}&sessionID={sessionID}
    
Отчеты:
Анализ достоверности
{url}/dataAnalysis/report
    (post запрос данные вида {"date":"01.02.2021","filterID":0,"filterValue":"ТЕКОН","objectID":"S1509","problemID":["3","4"],"problemOdpuID":["10","11"],"sessionID":"vCXGs2wtEmPgUxT5EKz36Q=="})