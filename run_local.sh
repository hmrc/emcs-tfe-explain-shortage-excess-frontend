#!/bin/bash

sbt "run 8317 -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"