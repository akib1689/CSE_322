#=======================My parameters============================
#Wireless MAC protocol: 802.11
#Routing protocol: DSDV
#Agent: TCP Reno
#Application: FTP
#Node position: Random
#Flow: Random source and random destination
#================================================================

#=======================Command line arguments===================
#arg 0: Number of nodes
#arg 1: Number of flows
#arg 2: Area size
#art 3: Time of simulation
#================================================================
if { $argc != 4 } {
    puts "Usage: ns $argv0 <number of nodes> <number of flows> <area size> <simulation time>"
    exit 1
}

# simulator
set ns [new Simulator]


# ======================================================================
# Define options

set val(chan)         Channel/WirelessChannel  ;# channel type
set val(prop)         Propagation/TwoRayGround ;# radio-propagation model
set val(ant)          Antenna/OmniAntenna      ;# Antenna type
set val(ll)           LL                       ;# Link layer type
set val(ifq)          Queue/DropTail/PriQueue  ;# Interface queue type
set val(ifqlen)       50                       ;# max packet in ifq
set val(netif)        Phy/WirelessPhy          ;# network interface type
set val(mac)          Mac/802_11               ;# MAC type
set val(rp)           DSDV                     ;# ad-hoc routing protocol 
set val(nn)           [lindex $argv 0]         ;# number of mobilenodes
set val(nf)           [lindex $argv 1]         ;# number of flows
set val(area)         [lindex $argv 2]         ;# area size
set val(simtime)      [lindex $argv 3]         ;# simulation time
set val(speed)        50                       ;# node speed
# =======================================================================

# trace file
set trace_file [open trace.tr w]
$ns trace-all $trace_file

# nam file
set nam_file [open animation.nam w]
$ns namtrace-all-wireless $nam_file 500 500

# topology: to keep track of node movements
set topo [new Topography]
$topo load_flatgrid $val(area) $val(area)   ;# load with area size


# general operation director for mobilenodes
create-god $val(nn)


# node configs
# ======================================================================

# $ns node-config -addressingType flat or hierarchical or expanded
#                  -adhocRouting   DSDV or DSR or TORA
#                  -llType	   LL
#                  -macType	   Mac/802_11
#                  -propType	   "Propagation/TwoRayGround"
#                  -ifqType	   "Queue/DropTail/PriQueue"
#                  -ifqLen	   50
#                  -phyType	   "Phy/WirelessPhy"
#                  -antType	   "Antenna/OmniAntenna"
#                  -channelType    "Channel/WirelessChannel"
#                  -topoInstance   $topo
#                  -energyModel    "EnergyModel"
#                  -initialEnergy  (in Joules)
#                  -rxPower        (in W)
#                  -txPower        (in W)
#                  -agentTrace     ON or OFF
#                  -routerTrace    ON or OFF
#                  -macTrace       ON or OFF
#                  -movementTrace  ON or OFF

# ======================================================================

$ns node-config -adhocRouting $val(rp) \
                -llType $val(ll) \
                -macType $val(mac) \
                -ifqType $val(ifq) \
                -ifqLen $val(ifqlen) \
                -antType $val(ant) \
                -propType $val(prop) \
                -phyType $val(netif) \
                -topoInstance $topo \
                -channelType $val(chan) \
                -agentTrace ON \
                -routerTrace ON \
                -macTrace OFF \
                -movementTrace OFF

# ======================================================================

# generate random integer number in the range [min,max]
# (the range is inclusive)
proc random_int {min max} {
    return [expr {int(rand()*($max-$min+1)+$min)}]
}

# Create nodes
for {set i 0} {$i < $val(nn)} {incr i} {
    set node($i) [$ns node]
    $node($i) random-motion 0           ;#disable random motion
    $node($i) set X_ [random_int 1 [expr $val(area) - 1]]
    $node($i) set Y_ [random_int 1 [expr $val(area) - 1]]
    $node($i) set Z_ 0
    $ns initial_node_pos $node($i) 20
}


# generating random movement of the nodes
for {set t 0} {$t < $val(simtime)} {incr t} {
    for {set i 0} {$i < $val(nn)} {incr i} {
        $ns at $t "$node($i) setdest [random_int 1 [expr $val(area) - 1]] [random_int 1 [expr $val(area) - 1]] $val(speed)"
    }
}


# ======================================================================
# generate random flows between nodes
for {set i 0} {$i < $val(nf)} {incr i} {
    set src [random_int 0 [expr $val(nn) - 1]]
    set dst [random_int 0 [expr $val(nn) - 1]]
    while {$src == $dst} {
        set dst [random_int 0 [expr $val(nn) - 1]]
    }
    # create agent
    set tcp [new Agent/TCP/Vegas]
    set tcp_sink [new Agent/TCPSink]
    # attach to nodes
    $ns attach-agent $node($src) $tcp
    $ns attach-agent $node($dst) $tcp_sink

    # connect agents
    $ns connect $tcp $tcp_sink

    # set flow id
    $tcp set fid_ $i

    # Traffic generator
    set ftp [new Application/FTP]

    # attach to agent
    $ftp attach-agent $tcp

    # start traffic generation
    $ns at 1.0 "$ftp start"

    # puts "Flow $i: $src -> $dst"
}
# ======================================================================



# End Simulation

# Stop nodes
for {set i 0} {$i < $val(nn)} {incr i} {
    $ns at $val(simtime) "$node($i) reset"
}


# call final function
proc finish {} {
    global ns trace_file nam_file
    $ns flush-trace
    close $trace_file
    close $nam_file
    exit 0
}




$ns at [expr $val(simtime) + 0.01] "finish"




# Run simulation
puts "Simulation starting"
$ns run
