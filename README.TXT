
To run the unit test, cobertura code coverage and checkstyle tasks use the 
following commands on the command line ($ denotes the command prompt) or their
equivalents in Eclipse.

Tests:
$ gradle test
$ open build/reports/tests/index.html

Cobertura:
$ gradle cobertura
$ open build/reports/cobertura/index.html

Checkstyle:
$ gradle check
$ open build/reports/checkstyle/main.html
