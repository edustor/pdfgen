#!/usr/bin/env bash
set -e
./gradlew assemble
docker build -t quay.io/wutiarn/edustor-pdfgen .