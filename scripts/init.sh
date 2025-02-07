#!/usr/bin/env bash

CSV_DIR=scripts/containers/postgres/csv

if [[ ! -d "${CSV_DIR}" ]];
then
  mkdir -p ${CSV_DIR}
fi;

download_data_file(){
  local fragment=$1
  local path=$2
  curl -sL -o ${CSV_DIR}/$path https://raw.githubusercontent.com/skhatri/app-data/refs/heads/main/$fragment
  if [[ -f "${CSV_DIR}/$path" ]]; then
    num_lines=$(wc -l < $CSV_DIR/$path|sed s/' '//g)
    if [[ $num_lines -eq 0 ]]; then
      rm ${CSV_DIR}/$path
    fi;
  fi;
}

if [[ ! -f ${CSV_DIR}/epl-historical-1992-2024.csv ]];
then
  download_data_file epl/epl-historical-1992-2024.csv epl-historical-1992-2024.csv
fi;

if [[ ! -f ${CSV_DIR}/epl-table-1992-2024.csv ]];
then
  download_data_file epl/epl-table-1992-2024.csv epl-table-1992-2024.csv
fi;
