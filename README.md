JWMTool -- Java Watermarking Tool
=======

COMPILATION
-----------

- Please, make sure environment variable JAVA_HOME and/or JAVA5_HOME exists
  and points to your JDK1.5 (or above) installation directory.

- Run:

	./Buildit

EXECUTION
---------

- Please, make sure environment variable JWMTOOL_HOME exists
  and points to JWMTool installation directory (this one).

- Perform:

	./run

DEPLOYMENT
----------

- Execute:

	./paquetize linux

  to create a distribution file for linux systems, or

	./paquetize windows

  to create a distribution file for windows machines.

LANGUAGES
---------

- Edit jwmtool.conf if you want to change JWMTool language.
  Currently supported languages are en_US and es_ES.

- If you want to add support for another language (e.g. xx_XX),
  just create Messages_xx_XX.properties, fill it with the
  translation to the application messages and place it under
  the messages directory.

