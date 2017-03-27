javac -d "bin" src\*.java
mkdir release
cd bin
jar cfm ..\release\Dexter.jar ..\Manifest.txt *.class
cd ..\release
mkdir res
cd ..\
xcopy /e /y res release\res
