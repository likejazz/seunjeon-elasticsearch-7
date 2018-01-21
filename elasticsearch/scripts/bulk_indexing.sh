export es=localhost:9200
export index=kowikiquote
cd chunks

for file in *; do
  echo -n "${file}:  "
  took=$(curl -s -H 'Content-Type: application/x-ndjson' -XPOST $es/$index/_bulk?pretty --data-binary @$file | grep took | cut -d':' -f 2 | cut -d',' -f 1)
  printf '%7s\n' $took
  [ "x$took" = "x" ] || rm $file
done
