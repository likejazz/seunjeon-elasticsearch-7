elastic_path=~/Programs/elasticsearch
$elastic_path/bin/plugin remove analysis-seunjeon
$elastic_path/bin/plugin list
$elastic_path/bin/plugin install file:../target/scala-2.11/elasticsearch-analysis-seunjeon-1.0.0-SNAPSHOT.zip
$elastic_path/bin/plugin list
