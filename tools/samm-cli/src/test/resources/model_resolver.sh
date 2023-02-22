#!/bin/bash

root="$1"
bamm_ver="$2"
model_urn="$3"

no_bamm_urn="${model_urn/urn:bamm:/}"
path="${no_bamm_urn/://}"
path="${path/\#//}"
path="${root}/${bamm_ver}/${path}.ttl"

cat "$path"