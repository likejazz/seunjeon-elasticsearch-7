#!/bin/bash -e

script_dir=$(cd "$(dirname "$0")" && pwd)

elastic_path=~/Programs/elasticsearch
plugin_bin=bin/elasticsearch-plugin
${elastic_path}/${plugin_bin} list
${elastic_path}/${plugin_bin} remove analysis-seunjeon
${elastic_path}/${plugin_bin} list
${elastic_path}/${plugin_bin} install file://${script_dir}/../target/elasticsearch-analysis-seunjeon-assembly-5.1.1.0.zip
${elastic_path}/${plugin_bin} list
cp user_dict.csv ${elastic_path}/config
ls -al ${elastic_path}/config/user_dict.csv

