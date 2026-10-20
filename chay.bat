@echo off
rem =====================================================================
rem  Bien dich va chay ung dung Quan ly Nhan su & Cham cong
rem  Cach dung:
rem     chay.bat          -> bien dich roi mo ung dung
rem     chay.bat build    -> chi bien dich, khong chay
rem  Yeu cau: JDK 8+ trong PATH, driver JDBC trong thu muc lib
rem           (SQL Server: mssql-jdbc-*.jar | MySQL: mysql-connector-j-*.jar)
rem =====================================================================
setlocal
chcp 65001 >nul

cd /d "%~dp0"

echo [1/3] Kiem tra moi truong...
where javac >nul 2>nul
if errorlevel 1 (
    echo   [LOI] Khong tim thay javac. Hay cai JDK 8 tro len va them vao PATH.
    exit /b 1
)
javac -version

if not exist lib (
    echo   [CANH BAO] Chua co thu muc lib - can chep driver JDBC vao lib\
)
if exist lib (
    dir /b lib\*.jar >nul 2>nul
    if errorlevel 1 echo   [CANH BAO] Thu muc lib chua co file .jar nao ^(can driver JDBC^)
)
if not exist db.properties (
    echo   [CANH BAO] Khong tim thay db.properties - ung dung se dung cau hinh mac dinh.
)

echo [2/3] Bien dich ma nguon...
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java src\dao\*.java src\model\*.java src\service\*.java src\util\*.java src\view\*.java
if errorlevel 1 (
    echo   [LOI] Bien dich that bai, xem thong bao ben tren.
    exit /b 1
)
echo   Bien dich thanh cong.

if /i "%~1"=="build" (
    echo [3/3] Bo qua buoc chay ^(tham so build^).
    exit /b 0
)

echo [3/3] Mo ung dung...
java -cp "out;lib/*" Main
if errorlevel 1 (
    echo   [LOI] Ung dung ket thuc voi ma loi. Kiem tra driver JDBC va db.properties.
    exit /b 1
)

endlocal
