echo off
java -classpath .;.\lib\* org.nhindirect.common.crypto.tools.PKCS11SecretKeyManager %*
