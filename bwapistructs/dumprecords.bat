set BWAPI=<PATH TO BWAPI 4.4 \ include>
clang++ -Xclang -fdump-record-layouts -I"%BWAPI%" Blub.h > dump