#!/usr/bin/env bash


show_help() {
echo "$(basename "$0") [-h] -e x.x.x -p x.x.x.x -- The program is a installer to install seunjeon.

where:
    -h  show this help text
    -e  elasticsearch version
    -p  seunjeon plugin version"
}

ES_VERSION=""
PLUGIN_VERSION=""

while [[ $# -ge 1 ]]
do
key="$1"

case $key in
    -e|--esversion)
        ES_VERSION="$2"
        shift # past argument
        ;;
    -p|--pluginversion)
        PLUGIN_VERSION="$2"
        shift # past argument
        ;;
    -h|--help)
        show_help
        exit
        ;;
esac
shift # past argument or value
done

if [ "$ES_VERSION" == "" ] || [ "$PLUGIN_VERSION" == "" ]; then
    show_help
    exit 1
fi


ZIP_NAME="elasticsearch-analysis-seunjeon-${PLUGIN_VERSION}.zip"
TMP_DIR="/tmp/elasticsearch-analysis-seunjeon"
mkdir -p $TMP_DIR/elasticsearch

########################################################################################################################
# download zip
REMOTE_FILE_NAME="https://oss.sonatype.org/service/local/repositories/releases/content/org/bitbucket/eunjeon/elasticsearch-analysis-seunjeon/${PLUGIN_VERSION}/${ZIP_NAME}"
wget -O ${TMP_DIR}/${ZIP_NAME} $REMOTE_FILE_NAME
if [ "$?" -ne "0" ]; then
    echo "invalid path $REMOTE_FILE_NAME"
    exit 1
fi


pushd $TMP_DIR

########################################################################################################################
# build properties file
PROPERTI_FILE="elasticsearch/plugin-descriptor.properties"

cat > $PROPERTI_FILE << EOF
description=The Korean(seunjeon) analysis plugin.
version=${PLUGIN_VERSION}
name=analysis-seunjeon
classname=org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis.AnalysisSeunjeonPlugin
java.version=1.8
elasticsearch.version=${ES_VERSION}
EOF

########################################################################################################################
# zipping...
zip -r $ZIP_NAME elasticsearch
if [ "$?" -ne "0" ]; then
    exit 1
fi

popd

########################################################################################################################
# copy a plugin file to current directory.
cp $TMP_DIR/$ZIP_NAME .
