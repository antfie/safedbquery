#!/usr/bin/env bash

# Exit if any command fails
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[1;36m'
NC='\033[0m' # No Color


mkdir -p scan
rm -rf -- scan/packages


echo -e "\n${CYAN}Downloading the Veracode CLI...${NC}"
cd scan
set +e # Ignore failure which happens if the CLI is the current latest version
curl -fsS https://tools.veracode.com/veracode-cli/install | sh
set -e
cd ..


echo -e "\n${CYAN}Packaging for SAST scanning...${NC}"
veracode package --trust --source . --output scan/packages
zipFilePath="scan/packages/safedbquery-0.0.1-SNAPSHOT.jar"
entrypointModules="safedbquery-0.0.1-SNAPSHOT.jar"


echo -e "\n${CYAN}SAST Scanning with Veracode (Pipeline)...${NC}"
./scan/veracode static scan $zipFilePath \
                            --baseline-file sast_baseline.json \
                            --results-file scan/sast_results.json \
                            --include "$entrypointModules"


echo -e "\n${CYAN}SAST Scanning with Veracode (Policy)...${NC}"

if [ ! -e "scan/veracode-api.jar" ]; then
  # Refer to this page: https://central.sonatype.com/artifact/com.veracode.vosp.api.wrappers/vosp-api-wrappers-java/versions
  apiWrapperVersion="24.10.15.0"
  curl "https://repo1.maven.org/maven2/com/veracode/vosp/api/wrappers/vosp-api-wrappers-java/$apiWrapperVersion/vosp-api-wrappers-java-$apiWrapperVersion.jar" --output scan/veracode-api.jar
fi

java -jar scan/veracode-api.jar \
     -action uploadandscan \
     -appname "safedbquery" \
     -createprofile true \
     -criticality VeryHigh \
     -version "`date "+%Y-%m-%d %H:%M:%S"`" \
     -deleteincompletescan 2 \
     -filepath scan/packages \
     -include "$entrypointModules" \
     -scanpollinginterval 30 \
     -scantimeout 15