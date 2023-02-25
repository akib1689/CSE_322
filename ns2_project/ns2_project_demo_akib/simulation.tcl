# simulator
set ns [new Simulator]



# https://stackoverflow.com/questions/21624119/adjusting-the-values-of-pt-and-rxthresh-variables-for-changing-transmission-ra
set val(coverage_area) [lindex $argv 3]         ;# Coverage area
Phy/WirelessPhy set CPThresh_ 100.0
Phy/WirelessPhy set CSThresh_ 4.21756e-11 ;#transmission range
Phy/WirelessPhy set RXThresh_ 4.4613e-10 ;#transmission range
Phy/WirelessPhy set bandwidth_ 512kb
Phy/WirelessPhy set Pt_ [expr { 0.2818 * $val(coverage_area) }]
Phy/WirelessPhy set freq_ 2.4e+9
Phy/WirelessPhy set L_ 1.0 


set val(chan)         Channel/WirelessChannel  ;# channel type
set val(prop)         Propagation/TwoRayGround ;# radio-propagation model
set val(ant)          Antenna/OmniAntenna      ;# Antenna type
set val(ll)           LL                       ;# Link layer type
set val(ifq)          CMUPriQueue              ;# Interface queue type
set val(ifqlen)       100                      ;# max packet in ifq
set val(netif)        Phy/WirelessPhy          ;# network interface type
set val(mac)          Mac/802_11               ;# MAC type
set val(rp)           DSDV                     ;# ad-hoc routing protocol 
set val(nn)           [lindex $argv 0]         ;# number of mobilenodes
set val(nf)           [lindex $argv 1]         ;# number of flows
set val(size)         500                      ;# size of the area
set val(pps)          [lindex $argv 2]         ;# packets per second
# =======================================================================

# puts "${val(nn)} nodes, ${val(nf)} flows, ${val(size)}m x ${val(size)}m area"

# trace file
set trace_file [open trace.tr w]
$ns trace-all $trace_file

# Create channel #1
set chan_1_ [new $val(chan)]

# nam file
set nam_file [open animation.nam w]
$ns namtrace-all-wireless $nam_file $val(size) $val(size)

# topology: to keep track of node movements
set topo [new Topography]
$topo load_flatgrid $val(size) $val(size) ;# $val(size)m x $val(size)m area


# general operation director for mobilenodes
create-god $val(nn)

 proc hsvToRgb {h s v} {
    set Hi [expr { int( double($h) / 60 ) % 6 }]
    set f [expr { double($h) / 60 - $Hi }]
    set s [expr { double($s)/255 }]
    set v [expr { double($v)/255 }]
    set p [expr { double($v) * (1 - $s) }]
    set q [expr { double($v) * (1 - $f * $s) }]
    set t [expr { double($v) * (1 - (1 - $f) * $s) }]
    switch -- $Hi {
        0 {
            set r $v
            set g $t
            set b $p
        }
        1 {
            set r $q
            set g $v
            set b $p
        }
        2 {
            set r $p
            set g $v
            set b $t
        }
        3 {
            set r $p
            set g $q
            set b $v
        }
        4 {
            set r $t
            set g $p
            set b $v
        }
        5 {
            set r $v
            set g $p
            set b $q
        }
        default {
            error "Wrong Hi value in hsvToRgb procedure! This should never happen!"
        }
    }
    set r [expr {round($r*255)}]
    set g [expr {round($g*255)}]
    set b [expr {round($b*255)}]
    return [list $r $g $b]
 }

proc getRandomColor {} {
    set h [expr { int(100 * rand())  + 100}]
    set s [expr { int(100 * rand())  + 100}]
    set v [expr { int(100 * rand())  + 100}]

    lassign [hsvToRgb $h $s $v] r g b

    return [format "#%02x%02x%02x" $r $g $b]
}

$ns node-config -adhocRouting $val(rp) \
                -llType $val(ll) \
                -macType $val(mac) \
                -ifqType $val(ifq) \
                -ifqLen $val(ifqlen) \
                -antType $val(ant) \
                -propType $val(prop) \
                -phyType $val(netif) \
                -topoInstance $topo \
                -energyModel "EnergyModel" \
                -agentTrace ON \
                -initialEnergy 100 \
                -rxPower 1.0 \
                -idlePower 1.0 \
                -sleepPower 0.001 \
                -routerTrace ON \
                -macTrace ON \
                -movementTrace ON \
                -txPower 1.0 \
                -channel $chan_1_

expr { srand(47) }


set dest [expr {int($val(nn) * rand())}]
set rem $val(nn)
set columns 10
set rows [expr {int(($val(nn) - 1) / $columns)}]
set row_gap [expr {int($val(size) / ($rows + 1))}]
set row_gap [expr {$row_gap - int($row_gap * 0.2)}]
set column_gap [expr {int($val(size) / ($columns))}]
set column_gap [expr {$column_gap - int($column_gap * 0.2)}]
set time 100
# puts -nonewline "row_gap "
# puts $row_gap
for {set i 0} {$i <= $rows} {incr i} {
    set y [expr { $i * $row_gap + $row_gap }]
    set jmax [expr { $rem > $columns ? $columns : $rem }]
    for {set j 0} {$j < $jmax } {incr j} {
        set x [expr { $j * $column_gap + $column_gap }]
        set node_no [expr { $i * 10 + $j }]
        set node($node_no) [$ns node]
        $node($node_no) random-motion 1       ;# disable random motion
        $node($node_no) set X_ $y
        $node($node_no) set Y_ $x
        $node($node_no) set Z_ 0
        $ns initial_node_pos $node($node_no) [expr { min($row_gap, $column_gap) * 0.6}]
        # set newColor [getRandomColor]
        # puts $newColor
        if {$node_no == $dest} {
            set newColor red
            $node($node_no) color $newColor
            $ns at 0 "$node($node_no) color $newColor"
            $ns color $node_no $newColor
        }
        set rem [expr { $rem - 1 }]
    }
}


# puts -nonewline "dest "
# puts $dest;
set src 0
set dest 1

for {set i 0} {$i < $val(nf)} {incr i} {
    set src [expr {int($val(nn) * rand())}]
    set dest [expr {int($val(nn) * rand())}]
    # puts $src;
    
    if {$src == $dest} {
        continue
    }
    
    $node($src) color blue
    $ns at 0 "$node($src) color blue"
    $ns color $src blue
    $node($dest) color red
    $ns at 0 "$node($dest) color brown"
    $ns color $dest red

    
    set tcp [new Agent/TCP/Vegas]
    
    set tcp_sink [new Agent/TCPSink]
    # attach to nodes
    $ns attach-agent $node($src) $tcp
    $ns attach-agent $node($dest) $tcp_sink
    # connect agents
    $ns connect $tcp $tcp_sink

    $tcp set fid_ $i
    $tcp set maxseq_ $val(pps)
    $tcp trace cwnd_
    set trace_ch [open "cwnd.tr" w]
    $tcp attach $trace_ch

    # Traffic generator
    set ftp [new Application/FTP]

    $ftp set packetSize_ 120
    # $ftp set rate_ [ expr { $val(pps) * 120 * 8} ]
    # attach to agent
    $ftp attach-agent $tcp
    
    # start traffic generation
    $ns at 1.0 "$ftp start"

    # set src [expr { $src + 2 }]
    # set dest [expr { $dest + 2 }]
}



# End Simulation

# Stop nodes
for {set i 0} {$i < $val(nn)} {incr i} {
    $ns at $time "$node($i) reset"
}



# call final function
proc finish {} {
    global ns trace_file nam_file
    $ns flush-trace
    close $trace_file
    close $nam_file
}

proc halt_simulation {} {
    global ns
    puts "Simulation ending"
    $ns halt
}

$ns at [expr ($time + 0.001)] "finish"
$ns at [expr ($time + 0.002)] "halt_simulation"




# Run simulation
puts "Simulation starting"
$ns run
