#!/bin/bash

if [ $# -lt 1 ]; then
    echo "need dict name: ex) mecab-ko-dic-2.0.1-20150920"
    exit 1
fi
DICT_NAME=$1     # mecab-ko-dic-2.0.1-20150920

SCRIPTS_DIR=$(dirname "${BASH_SOURCE[0]}")
HOME_DIR=$SCRIPTS_DIR/..

pushd $HOME_DIR &> /dev/null

if [ -e $DICT_NAME ]; then
    echo "already exist dict."
    exit 1
fi

wget -O ${DICT_NAME}.tar.gz "https://bitbucket.org/eunjeon/mecab-ko-dic/downloads/${DICT_NAME}.tar.gz"
tar zxvf $DICT_NAME.tar.gz
rm mecab-ko-dic
ln -sf $DICT_NAME mecab-ko-dic

popd &> /dev/null
