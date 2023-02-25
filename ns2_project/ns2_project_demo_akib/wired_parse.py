receivedPackets = 0;
sentPackets = 0;
droppedPackets = 0;
totalReceivedBytes = 0;
totalDelay = 0;
simStartTime = 1000000;
simEndTime = 0;
headerBytes = 20;

# maintain a dictionary of int -> double (packet id , start time)
sentPacketsDict = dict()


# path to the trace file
traceFile = "trace.tr"
# open the trace file
trace = open(traceFile, "r")

# read the trace file line by line
for line in trace:
    # divide the line into following fields (seperated by spaces)
    # event_type time from_node to_node packet_type packet_size flags flow_id src_addr dst_addr seq_no pkt_id
    fields = line.split(" ")
    # the last line is empty
    if len(fields) < 12:
        continue
    # get the event type
    eventType = fields[0]
    # get the time
    time = float(fields[1])
    # get the to node
    toNode = fields[3]
    # get the packet type
    packetType = fields[4]
    # get the packet size
    packetSize = int(fields[5])
    # get the flow id
    flowId = fields[7]
    # get the source address
    srcAddr = fields[8]
    # get the destination address
    dstAddr = fields[9]
    # get the packet id
    pktId = fields[11]

    # if the time is less than the start time
    # then update the start time
    if time < simStartTime:
        simStartTime = time
    # if the time is greater than the end time
    # then update the end time
    if time > simEndTime:
        simEndTime = time
    # if the packet type is tcp
    if packetType == "tcp":
        # if the event is packet enqueue (+) 
        if eventType == "+":
            # if the packet id is not in the dictionary
            # then add it to the dictionary with time (this is the start time of the packet)
            # and increment the sent packets counter
            if pktId not in sentPacketsDict.keys():
                sentPacketsDict[pktId] = time
                sentPackets += 1
        # if the event is packet receive (r) 
        elif eventType == "r":
            # check if the to node is same as the destination address's 1st part
            # the destination address is divided into node.port format
            # so we need to check if the to node is same as the node part of the destination address
            if toNode == dstAddr.split(".")[0]:
                # if the packet id is in the dictionary
                # then calculate the delay and increment the received packets counter
                if pktId in sentPacketsDict:
                    delay = time - sentPacketsDict[pktId]
                    totalDelay += delay
                    receivedPackets += 1
                    # remove the packet id from the dictionary
                    sentPacketsDict.pop(pktId, None)
                    # increment the total received bytes
                    totalReceivedBytes += (packetSize - headerBytes)
        # if the event is packet drop (d)
        elif eventType == "d":
            # if the packet id is in the dictionary
            # then increment the dropped packets counter
            # and remove the packet id from the dictionary
            if pktId in sentPacketsDict:
                droppedPackets += 1
                sentPacketsDict.pop(pktId, None)

# close the trace file
trace.close()
# simulation time
simTime = simEndTime - simStartTime
# calculate the throughput
throughput = totalReceivedBytes * 8 / (simTime)
# calculate the average delay
avgDelay = totalDelay / receivedPackets
# calculate the packet delivery ratio
packetDeliveryRatio = receivedPackets / sentPackets
# calculate the packet drop ratio
packetDropRatio = droppedPackets / sentPackets

# print the results in comma seperated format
# print("Throughput (Mbps), Average Delay (ms), Packet Delivery Ratio, Packet Drop Ratio")
print(throughput,",",avgDelay,",",packetDeliveryRatio,",",packetDropRatio)