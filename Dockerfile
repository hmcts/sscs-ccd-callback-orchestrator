ARG APP_INSIGHTS_AGENT_VERSION=2.5.1
FROM hmctspublic.azurecr.io/base/java:openjdk-11-distroless-1.4
COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/sscs-ccd-callback-orchestrator.jar /opt/app/

CMD ["sscs-ccd-callback-orchestrator.jar"]

EXPOSE 8070
