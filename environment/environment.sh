#!/bin/bash
#                             !!! RUN AS ROOT !!! 
#
# Run this bash script to automatically configure your environment to
# be compatible with this project. This script auto-installs:
# 	1. Apache Spark v 1.5.0 w/ Hadoop v.2.6 components. 
#

USR_LIB_BASE_DIR=/usr/lib
USR_BIN_BASE_DIR=/usr/bin
INSTALL='INSTALL'
UNINSTALL='UNINSTALL'
COMPLETE_UNINSTALL_FILENAME='COMPLETE_UNINSTALL.txt'
PATH_MOD_FILE=${HOME}/.bashrc
CWD=$(pwd)

# Make sure run as root
if [ ! $(id -u) = "0" ]
	then
	echo "This script must be run as root!"
	exit 1
fi

key="$1"

case $key in
	'-i' | '--install')
	MODE=${INSTALL}
	;;
	'-u' | '--uninstall')
	MODE=${UNINSTALL}
	;;
	*)
	echo "Usage: environment.sh (-i | --install) || (-u | --uninstall)"
	exit 1
	;;
esac


SPARK_BASE_FILE_NAME=spark-1.5.0-bin-hadoop2.6
SPARK_TGZ=${SPARK_BASE_FILE_NAME}.tgz
SPARK_SYM_LINK=${USR_BIN_BASE_DIR}/spark-apache
SPARK_PATH=${SPARK_BASE_DIR}/bin
SPARK_MIRROR=http://www.us.apache.org/dist/spark/spark-1.5.0/spark-1.5.0-bin-hadoop2.6.tgz

MAVEN_BASE_FILE_NAME=apache-maven-3.3.3-bin
MAVEN_TGZ=${MAVEN_BASE_FILE_NAME}.tar.gz
MAVEN_SYM_LINK=${USR_BIN_BASE_DIR}/maven-apache
MAVEN_PATH=${MAVEN_BASE_DIR}/bin
MAVEN_MIRROR=http://www.eu.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz

if [ ${MODE} = "${INSTALL}" ] 
then
echo "Running install..."

# Spark Install
wget ${SPARK_MIRROR}  
tar -xzf ./${SPARK_TGZ}
mv ${SPARK_BASE_FILE_NAME} ${USR_LIB_BASE_DIR} 
ln -s ${USR_LIB_BASE_DIR}/${SPARK_BASE_FILE_NAME} ${SPARK_SYM_LINK} 
echo "export PATH=$PATH:${SPARK_PATH}" >> ${PATH_MOD_FILE}

# Maven Install
wget ${MAVEN_MIRROR} 
tar -xzf ./${MAVEN_TGZ}
mv ${MAVEN_BASE_FILE_NAME} ${USR_LIB_BASE_DIR}
ln -s ${USR_LIB_BASE_DIR}/${MAVEN_BASE_FILE_NAME} ${MAVEN_SYM_LINK}
echo "export PATH=$PATH:${MAVEN_PATH}" >> ${PATH_MOD_FILE}

# Source the path file
source ${PATH_MOD_FILE}

fi

if [ ${MODE} = "${UNINSTALL}" ] 
then
echo "Running uninstall..."
echo "Directory of file with PATH modifications: ${PATH_MOD_FILE}" > ${COMPLETE_UNINSTALL_FILE} 

# Spark Uninstall
rm -r ${USR_LIB_BASE_DIR}/${SPARK_BASE_FILE_NAME}
rm ${SPARK_SYM_LINK}
echo "+ ${SPARK_PATH}" >> ${COMPLETE_UNINSTALL_FILE}

# Maven Uninstall
rm -r ${USR_LIB_BASE_DIR}/${MAVEN_BASE_FILE_NAME}
rm ${MAVEN_SYM_LINK}
echo "+ ${MAVEN_PATH}" >> ${COMPLETE_UNINSTALL_FILE}

echo "!!IMPORTANT!! Check ${COMPLETE_UNINSTALL_FILE} for paths to remove from PATH environment variable"

fi 

echo "Operation successful!"
