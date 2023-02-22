@echo off
set "root=%~1"
set "bamm_ver=%~2"
set "model_urn=%~3"

set no_bamm_urn=%model_urn:urn:bamm:=%
set path=%no_bamm_urn::=\%
set path=%path:#=\%
set path=%root%\%bamm_ver%\%path%.ttl

type %path%
