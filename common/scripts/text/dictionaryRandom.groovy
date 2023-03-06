package scripts.text

Random random = new Random()
List newValues = new ArrayList()

List<String> dictionaryValues = Arrays.asList(dictionary.split(","));


for (def oldValue: oldValues) {
    newValues.add(getRandomFrom(dictionaryValues, random))
}

return newValues



private static String getRandomFrom(List<String> values, Random random) {
    def randomNumber = random.nextInt(values.size())

    return values.get(randomNumber)
}

