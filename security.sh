#!/usr/bin/env bash

echo ${TEST_URL}
zap-api-scan.py -t ${TEST_URL}/v2/api-docs -f openapi -S -d -u ${SecurityRules} -P 1005 -l FAIL
cat zap.out
zap-cli --zap-url http://0.0.0.0 -p 1005 report -o /zap/api-report.html -f html
cp /zap/api-report.html functional-output/
curl --fail http://0.0.0.0:1005/OTHER/core/other/jsonreport/?formMethod=GET --output report.json
cp *.* functional-output/
zap-cli --zap-url http://0.0.0.0 -p 1005 alerts -l Medium --exit-code False
