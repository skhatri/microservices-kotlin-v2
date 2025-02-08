if [[ "${OTEL_EXPORTER_OTLP_ENDPOINT}" != "" ]] && [[ -f /opt/app/opentelemetry-javaagent.jar ]];
then
  java -javaagent:/opt/app/opentelemetry-javaagent.jar -jar /opt/app/app.jar
else
  java -jar /opt/app/app.jar
fi;

