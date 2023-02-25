#!/bin/bash

# This script is used to run the simulation for different number of nodes

# define the base line parameters
# Area
baseline_area=500
# Number of nodes
baseline_nodes=40
# Number of flows
baseline_flows=20

# Number of flows
baseline_packet_rate=200

# Area array
area_array=(250 500 750 1000 1250)

# Number of nodes array
nodes_array=(20 40 60 80 100)

# Number of flows array
flows_array=(10 20 30 40 50)

# Numer of packet rate
packet_rate=(100 200 300 400 500)

# Simulation time
sim_time=20

#open trace.txt file
echo "Throughput, Average Delay, Delivery ratio, Drop ratio" > trace.csv


# for area 
for area in "${area_array[@]}"
do
    # echo "Area: $area" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wired.tcl $baseline_nodes $baseline_flows $area $baseline_packet_rate $sim_time 
    python3 wired_parse.py >> trace.csv
done

echo "-----------------------------------------------------------" >> trace.csv

# for nodes
for nodes in "${nodes_array[@]}"
do
    # echo "Number of nodes: $nodes" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wired.tcl $nodes $baseline_flows $baseline_area $baseline_packet_rate $sim_time 
    python3 wired_parse.py >> trace.csv
done

echo "-----------------------------------------------------------" >> trace.csv
# for flows
for flows in "${flows_array[@]}"
do
    # echo "Number of flows: $flows" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wired.tcl $baseline_nodes $flows $baseline_area $baseline_packet_rate $sim_time 
    python3 wired_parse.py >> trace.csv
done

echo "-----------------------------------------------------------" >> trace.csv
# for packet rate
for rate in "${packet_rate[@]}"
do
    # echo "Number of flows: $flows" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wired.tcl $baseline_nodes $baseline_flows $baseline_area $rate $sim_time 
    python3 wired_parse.py >> trace.csv
done