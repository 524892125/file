```shell
@echo off
:: source path
set SOURCE=E:\Code\Kilf_Web\spring_api\target
:: target path
set TARGET=C:\ProgramData\Jenkins\.jenkins\workspace\spring

:: not exists
if not exist "%TARGET%" (
    mkdir "%TARGET%"
)

:: copy start
xcopy "%SOURCE%\*" "%TARGET%\" /s /e /y

echo copy complete
pause

```