# Script to filter out the databinding code coverage as it is not representative of our code
#   as it is auto generated. I'm sorry
with open("index.html", "r") as file:
    data = file.read()
data = data.split("><")

# Find all percentages in the c column and add it to a list
coverages = []
for tag in data:
    idx = tag.find('c')
    while(idx := tag.find('c')) >= 0:
        tag = tag[idx+1:]
        if tag[0%len(tag)].isdigit():
            tag = tag.split(">")[1]
            coverages.append(int(''.join(c for c in tag if c.isdigit())))
        
# Remove the lowest as the lowest will always be databinding
coverages = sorted(coverages)
print("coverage-" + str(sum(coverages[1:])/len(coverages[1:])) + "%")