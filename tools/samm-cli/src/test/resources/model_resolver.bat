@echo off
set "root=%~1"
set "samm_ver=%~2"
set "model_urn=%~3"

set no_samm_urn=%model_urn:urn:samm:=%
set path=%no_samm_urn::=\%
set path=%path:#=\%
set path=%root%\%samm_ver%\%path%.ttl

type %path%
