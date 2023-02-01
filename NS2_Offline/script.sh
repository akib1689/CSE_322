#!/bin/bash

# This script is used to run the simulation for different number of nodes

# define the base line parameters
# Area
baseline_area=500
# Number of nodes
baseline_nodes=40
# Number of flows
baseline_flows=20

# Area array
area_array=(250 500 750 1000 1250)

# Number of nodes array
nodes_array=(20 40 60 80 100)

# Number of flows array
flows_array=(10 20 30 40 50)

# Simulation time
sim_time=50

#open trace.txt file
echo "Sent Packets, Dropped Packets, Received Packets, Throughput, Average Delay, Delivery ratio, Drop ratio" > trace.csv


# for area 
for area in "${area_array[@]}"
do
    # echo "Area: $area" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wireless.tcl $baseline_nodes $baseline_flows $area $sim_time 
    gawk -f parse.awk trace.tr >> trace.csv
done

echo "-----------------------------------------------------------" >> trace.csv

# for nodes
for nodes in "${nodes_array[@]}"
do
    # echo "Number of nodes: $nodes" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wireless.tcl $nodes $baseline_flows $baseline_area $sim_time 
    gawk -f parse.awk trace.tr >> trace.csv
done

echo "-----------------------------------------------------------" >> trace.csv
# for flows
for flows in "${flows_array[@]}"
do
    # echo "Number of flows: $flows" >> trace.txt
    # iterate the number of nodes with baseline parameters
    ns wireless.tcl $baseline_nodes $flows $baseline_area $sim_time 

    gawk -f parse.awk trace.tr >> trace.csv
done
