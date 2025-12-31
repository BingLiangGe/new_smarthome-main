#! /bin/sh

JAR_OPT=${JAR_OPT:--Xms1024m -Xmx2048m}

exec java -jar ${JAR_OPT}  ${JAR_FILE}  ${CLOUD_OPTION}  ${EXT_OPTION}
