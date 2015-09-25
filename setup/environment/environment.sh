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
DOWNLOAD_TARS='DOWNLOAD'
COMPLETE_UNINSTALL_FILENAME=COMPLETE_UNINSTALL.txt
PATH_MOD_FILE=${HOME}/.bashrc
LOG_FILE=log4j.properties
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
	'-d' | '--download')
	MODE=${DOWNLOAD_TARS}
	;;
	*)
	printf "\nUsage: environment.sh (-i | --install) or (-u | --uninstall) or (-d | --download)\n"
	exit 1
	;;
esac

SPARK_CONFIG_FILE=log4j.properties
SPARK_BASE_FILE_NAME=spark-1.5.0-bin-hadoop2.6
SPARK_TGZ=${SPARK_BASE_FILE_NAME}.tgz
SPARK_SYM_LINK=${USR_BIN_BASE_DIR}/spark-apache
SPARK_PATH=${SPARK_SYM_LINK}/bin
SPARK_MIRROR=http://www.us.apache.org/dist/spark/spark-1.5.0/spark-1.5.0-bin-hadoop2.6.tgz
SPARK_CONFIG_DIR=${SPARK_SYM_LINK}/conf

MAVEN_BASE_FILE_NAME=apache-maven-3.3.3-bin
MAVEN_TGZ=${MAVEN_BASE_FILE_NAME}.tar.gz
MAVEN_SYM_LINK=${USR_BIN_BASE_DIR}/maven-apache
MAVEN_PATH=${MAVEN_SYM_LINK}/bin
MAVEN_MIRROR=http://www.eu.apache.org/dist/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz

if [ ${MODE} = "${INSTALL}" ] 
then
echo "Running install..."

# Spark Install
wget ${SPARK_MIRROR}
mkdir ${SPARK_BASE_FILE_NAME} 
tar -xzf ./${SPARK_TGZ} -C ./${SPARK_BASE_FILE_NAME} --strip-components 1
mv ${SPARK_BASE_FILE_NAME} ${USR_LIB_BASE_DIR} 
ln -s ${USR_LIB_BASE_DIR}/${SPARK_BASE_FILE_NAME} ${SPARK_SYM_LINK} 
echo 'export PATH=$PATH:'"${SPARK_PATH}" >> ${PATH_MOD_FILE}
rm ${SPARK_TGZ}

# Spark Config
cp ${SPARK_CONFIG_FILE} ${SPARK_CONFIG_DIR}

# Maven Install
wget ${MAVEN_MIRROR}
mkdir ${MAVEN_BASE_FILE_NAME}
tar -xzf ./${MAVEN_TGZ} -C ./${MAVEN_BASE_FILE_NAME} --strip-components 1
mv ${MAVEN_BASE_FILE_NAME} ${USR_LIB_BASE_DIR}
ln -s ${USR_LIB_BASE_DIR}/${MAVEN_BASE_FILE_NAME} ${MAVEN_SYM_LINK}
echo 'export PATH=$PATH:'"${MAVEN_PATH}" >> ${PATH_MOD_FILE}
rm ${MAVEN_TGZ}

printf '\n!!! Make sure to run: source '"${PATH_MOD_FILE}"' !!!\n'
fi

if [ ${MODE} = "${UNINSTALL}" ] 
then
echo "Running uninstall..."
echo "Directory of file with PATH modifications: ${PATH_MOD_FILE}" > ${COMPLETE_UNINSTALL_FILE}

# Spark Uninstall
sudo rm -r ${USR_LIB_BASE_DIR}/${SPARK_BASE_FILE_NAME}
sudo rm -r ${SPARK_SYM_LINK}
echo "\+ ${SPARK_PATH}" >> ${COMPLETE_UNINSTALL_FILE}

# Maven Uninstall
sudo rm -r ${USR_LIB_BASE_DIR}/${MAVEN_BASE_FILE_NAME}
sudo rm -r ${MAVEN_SYM_LINK}
echo "\+ ${MAVEN_PATH}" >> ${COMPLETE_UNINSTALL_FILE}

`printf '\n !!IMPORTANT!! Check '${COMPLETE_UNINSTALL_FILE}' for paths to remove from PATH environment variable.\n'`

fi

if [ ${MODE} = "${DOWNLOAD_TARS}" ]
then
	# Download all components
	wget ${SPARK_MIRROR} 
	wget ${MAVEN_MIRROR}
fi

printf "\nOperation successful!\n"
