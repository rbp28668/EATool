#!/bin/bash

function count {
  for d in "$1"/*; do
    if [ -d "$d" ]; then
      count "$d"
    else 
      wc -l  "$d"
    fi
  done
}

count .

