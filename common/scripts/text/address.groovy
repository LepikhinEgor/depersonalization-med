package scripts.text

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

def cities = linesFromFile("common/location/cities.txt")
def streets = linesFromFile("common/location/streets.txt")

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
    BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(filePath), StandardCharsets.UTF_8));
    try  {
        return br.lines().collect(Collectors.toList())
    } catch (Exception e) {
        return Collections.emptyList()
    } finally {
        br.close()
    }
}