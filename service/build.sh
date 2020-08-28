#!/usr/bin/env bash

mvn clean
mvn install dockerfile:build -Ddocker.tag-1.0.0
