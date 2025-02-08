FROM cloudnativek8s/microservices-java21-alpine:v1.0.35

ENV LANGUAGE='en_US:en'

RUN chown -R app:app /opt/app && chmod -R g+rwx /opt/app
USER app
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=app:app  app/build/libs/app.jar /opt/app/app.jar
COPY --chown=app:app out/otel/opentelemetry-javaagent.jar /opt/app/opentelemetry-javaagent.jar
COPY --chown=app:app scripts/start-app.sh /opt/app/start-app.sh
EXPOSE 8080
ENV JAVA_OPTS=""

ENV OTEL_RESOURCE_ATTRIBUTES="service.name=starter"
ENV OTEL_EXPORTER_OTLP_ENDPOINT=""
ENV OTEL_LOGS_EXPORTER=otlp

CMD [ "/bin/bash", "-c", "/opt/app/start-app.sh" ]



