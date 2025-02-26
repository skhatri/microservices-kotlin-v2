FROM cloudnativek8s/microservices-java21-alpine-u10k:v1.0.38
ARG APP_NAME="starter"
ARG APP_VERSION="latest"
ARG BUILD_DATE="unknown"
ARG COMMIT_HASH="unset"

ENV LANGUAGE='en_US:en'
ENV APP_NAME=${APP_NAME}
ENV APP_VERSION=${APP_VERSION}
ENV BUILD_DATE=${BUILD_DATE}
ENV COMMIT_HASH=${COMMIT_HASH}

LABEL APP_NAME=${APP_NAME}
LABEL APP_VERSION=${APP_VERSION}
LABEL COMMIT_HASH=${COMMIT_HASH}

RUN chown -R user:app /opt/app && chmod -R g+rwx /opt/app
USER user
COPY --chown=user:app  app/build/libs/app.jar /opt/app/app.jar
COPY --chown=user:app out/otel/opentelemetry-javaagent.jar /opt/app/opentelemetry-javaagent.jar
COPY --chown=user:app scripts/start-app.sh /opt/app/start-app.sh
EXPOSE 8080
ENV JAVA_OPTS=""

ENV OTEL_RESOURCE_ATTRIBUTES="service.name=starter"
ENV OTEL_EXPORTER_OTLP_ENDPOINT=""
ENV OTEL_LOGS_EXPORTER=otlp

CMD [ "/bin/bash", "-c", "/opt/app/start-app.sh" ]



