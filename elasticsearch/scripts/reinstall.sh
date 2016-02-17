#!/usr/bin/env bash

elastic_path=~/Programs/elasticsearch
${elastic_path}/bin/plugin list
${elastic_path}/bin/plugin remove analysis-seunjeon
${elastic_path}/bin/plugin list
${elastic_path}/bin/plugin install file:../target/elasticsearch-analysis-seunjeon-assembly-2.1.1.3-SNAPSHOT.zip
${elastic_path}/bin/plugin list
cp user_dict.csv ${elastic_path}/config
ls -al ${elastic_path}/config/user_dict.csv

