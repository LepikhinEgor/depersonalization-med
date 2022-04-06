package com.lepikhina.model.scripts.text

import java.util.stream.Collectors

def cities = linesFromFile("resources/location/cities.txt")
def streets = linesFromFile("resources/location/streets.txt")

Random random = new Random()

List newValues = new ArrayList()
for (def oldValue: oldValues) {
    def city = getRandomFrom(cities, random)
    def street = getRandomFrom(streets, random)
    def building = random.nextInt(100)
    def apartment = random.nextInt(300)

    newValues.add("г. $city, ул. $street, д.$building, кв. $apartment".toString())
}

return newValues



private static String getRandomFrom(List<String> values, Random random) {
    def randomNumber = random.nextInt(values.size())

    return values.get(randomNumber)
}

private static List<String> linesFromFile(String filePath) {
    BufferedReader br = new BufferedReader(new FileReader(filePath))
    try  {
        return br.lines().collect(Collectors.toList())
    } catch (Exception e) {
        return Collections.emptyList()
    } finally {
        br.close()
    }
}