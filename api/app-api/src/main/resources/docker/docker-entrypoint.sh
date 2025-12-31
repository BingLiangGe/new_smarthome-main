#! /bin/sh

JAR_OPT=${JAR_OPT:--Xms512m -Xmx1024m}

exec java -jar ${JAR_OPT}  ${JAR_FILE}  ${CLOUD_OPTION}  ${EXT_OPTION}
