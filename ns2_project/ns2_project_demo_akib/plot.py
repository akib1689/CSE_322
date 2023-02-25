from fileinput import filename
import matplotlib.pyplot as plt
from numpy import double
import os

# plt.style.use('fivethirtyeight')

def openFileInfo(fileName):
    allval = []
    lines = ""
    with open(fileName) as f:
        lines = f.readlines()
        items = lines[0].strip("\n").split(",")
        for idx, item in enumerate(items):
            items[idx] = item.strip()
        
        allval.append(items)
        for line in lines[1:]:
            line = line.strip("\n").split(",")
            if len(line) < 4:
                continue
            allval.append(double(line)) 

    return allval

flow = [20,40,60,80,100]
node = [10,20,30,40,50]
packet = [100,200,300,400,500]
speed = [5,10,15,20,25]
coverage = [1,2,3,4,5]
area=[250, 500, 750, 1000, 1250]

all = [flow,node,area]


opt0 = ['']
opt1 = [('#flow','#Flow',flow),('#node','#Node',node),('packet_per_second','#Packet',node),('coverage_area','Coverage_Area',area)]
# opt0 = ['taskA2/']
# opt1 = [('FLOW','nFlows',flow),('NODE','nNodes',node),('tx_range','Range*i',coverage),('packetPerSec','nPackets',packet)]
opt2 = [('Throughput','throughput(kbps)'),('Average Delay','time(sec)'),('Drop Ratio','lost %'),('Delivery Ratio','delivered %')]

for k in opt0:
    for i in opt1:
        filename = "output_"+i[1]+".csv"
        y = openFileInfo(filename)
        # print(y)
        # opt2 = y[0]
        folder_name = filename.split(".")[0]
        if not os.path.exists(folder_name):
            os.mkdir(folder_name)
        
        
        for idx, j in enumerate(y[0]):
            
            # print(i)
            # # print(y)
            # print(j)
            # print("-------x--------")
            # x_values = i[2]
            # y_values = y[j]
            
            
            
            x_values = list(map(lambda x: x[0], y[1:]))
            y_values = list(map(lambda x: x[idx], y[1:]))
            

            plt.plot(x_values,y_values)
            # plt.legend()
            plt.xlabel(i[0])
            plt.ylabel(y[0][idx])
            plt.title(y[0][idx] + " vs " + y[0][0])
            plt.grid()

            plt.savefig(folder_name+"/"+j+".png")
            plt.clf()


# ax = plt.gca()
# ax.set_xscale('log')
# ax.set_yscale('log')
# plt.legend()
# plt.show()