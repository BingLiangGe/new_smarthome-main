#! /bin/sh

JAR_OPT=${JAR_OPT:--Xms128m -Xmx512m}

exec java -jar ${JAR_OPT}  ${JAR_FILE}  ${CLOUD_OPTION}  ${EXT_OPTION}
