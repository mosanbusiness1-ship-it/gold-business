docker run --rm \
docker.elastic.co/beats/filebeat:9.1.2 \
setup -E setup.kibana.host=kibana:5601 \
-E output.elasticsearch.hosts=["elasticsearch:9200"]


docker run -d \
  --name=filebeat \
  --user=root \
  --volume="$(pwd)/filebeat.docker.yml:/usr/share/filebeat/filebeat.yml:ro" \
  --volume="/var/lib/docker/containers:/var/lib/docker/containers:ro" \
  --volume="/var/run/docker.sock:/var/run/docker.sock:ro" \
  --volume="registry:/usr/share/filebeat/data:rw" \
  docker.elastic.co/beats/filebeat:9.1.2 filebeat -e --strict.perms=false \
  -E output.elasticsearch.hosts=["elasticsearch:9200"]