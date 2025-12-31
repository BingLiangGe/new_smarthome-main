#! /bin/sh

JAR_OPT=${JAR_OPT:--Xms1024m -Xmx4096m}

exec java -jar ${JAR_OPT}  ${JAR_FILE}  ${CLOUD_OPTION}  ${EXT_OPTION}
