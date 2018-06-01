#!/usr/bin/env bash

./sbt "project mass-docs" paradox
./scripts/publish-doc.sh
