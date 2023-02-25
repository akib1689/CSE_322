import sys
import subprocess


received_packets = 0
sent_packets = 0
dropped_packets = 0
total_delay = 0.0
received_bytes = 0
total_energy_consumption = 0

start_time = 1000000.0
end_time = 0.0

# constants
header_bytes = 20

# parse the trace file
event = 1 - 1
time_sec = 2 - 1
node = 3 - 1
layer = 4 - 1
packet_id = 6 - 1
packet_type = 7 - 1
packet_bytes = 8 - 1

idle_energy_consumption = 16 - 1
sleep_energy_consumption = 18 - 1
transmit_energy_consumption = 20 - 1
received_energy_consumption = 22 - 1


sent_time = dict()


def openFileInfo(fileName):
    allval = []
    lines = ""
    with open(fileName) as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip("\n").split()
            line[node] = line[node].strip("_")
            if (received_energy_consumption < len(line)):
                line[received_energy_consumption] = line[received_energy_consumption].strip(
                    "]")
            allval.append(line)

    return allval


filename = "trace.tr"
values = openFileInfo(filename)
output_file = sys.argv[1]


for index, line in enumerate(values):

    if (line[event] == "N"):
        continue
    if (float(start_time) > float(line[time_sec])):
        start_time = line[time_sec]
    end_time = line[time_sec]

    if (line[layer] == "AGT" and line[packet_type] == "tcp"):

        if (line[event] == "s"):
            sent_time[line[packet_id]] = line[time_sec]
            sent_packets += 1

        elif (line[event] == "r"):
            delay = float(line[time_sec]) - float(sent_time[line[packet_id]])
            if (delay < 0):  # sanity check
                print("ERROR")

            total_delay += delay
            

            bytes = (int(line[packet_bytes]) - int(header_bytes))
            if(bytes < 0): 
                print(str(index) + " " + line[packet_bytes])
            received_bytes += bytes

            received_packets += 1
            if (received_energy_consumption < len(line)):
                total_energy_consumption += (float(line[idle_energy_consumption]) + float(line[sleep_energy_consumption]) + float(
                    line[transmit_energy_consumption]) + float(line[received_energy_consumption]))

    if (line[packet_type] == "tcp" and line[event] == "D"):
        dropped_packets += 1


# end_time = values[-1][time_sec]

simulation_time = float(end_time) - float(start_time)
throughput = (received_bytes * 8) / simulation_time
avg_delay = total_delay / received_packets
delivery_ratio = received_packets / sent_packets
drop_ratio = dropped_packets / sent_packets
energy_per_packet = total_energy_consumption / received_packets
energy_per_byte = total_energy_consumption / received_bytes

print("Received bytes: ", received_bytes)
print("Simulation time: ", simulation_time, end_time, start_time, "seconds")

print("Sent Packets: ", sent_packets)
print("Dropped Packets: ", dropped_packets)
print("Received Packets: ", received_packets)

print("-------------------------------------------------------------")

print("Total energy consumption: ", total_energy_consumption)
print ("Energy consumption per packet: ", energy_per_packet, "Joules")
print ("Energy consumption per byte: ", energy_per_byte, "Joules")

print("Throughput: ", throughput, "bits/sec")
print("Average Delay: ", avg_delay, "seconds")
print("Delivery ratio: ", delivery_ratio)
print("Drop ratio: ", drop_ratio)
subprocess.call("echo {throughput}, {avg_delay}, {delivery_ratio}, {drop_ratio}, {epp}, {epb} >> {output_file}"
                .format(throughput=throughput, avg_delay=avg_delay, delivery_ratio=delivery_ratio, drop_ratio=drop_ratio, epp=energy_per_packet, epb=energy_per_byte,
                        output_file=output_file
                        ), shell=True)
# print (throughput, ",", avg_delay, ",", delivery_ratio, ",", drop_ratio , "output_area.csv")
