package com.lepikhina.model.scripts.text

import java.util.stream.Collectors

def femaleNames = linesFromFile("resources/fio/femaleNames.txt")
def maleNames = linesFromFile("resources/fio/maleNames.txt")
def surnameNames = linesFromFile("resources/fio/surnames.txt")
def femaleFatherNames = linesFromFile("resources/fio/femaleFatherNames.txt")
def maleFatherNames = linesFromFile("resources/fio/maleFatherNames.txt")

Random random = new Random()

List newValues = new ArrayList()
for (def oldValue: oldValues) {
    def name = ""
    def surname = ""
    def fatherName = ""

    boolean isMale = random.nextBoolean()
    if (isMale) {
        if (needName)
            name = getRandomFrom(maleNames, random)
        if (needSurname)
            surname = getRandomFrom(surnameNames, random)
        if (needFatherName)
            fatherName = getRandomFrom(maleFatherNames, random)
    } else {
        if (needName)
            name = getRandomFrom(femaleNames, random)
        if (needSurname)
            surname = getRandomFrom(surnameNames, random) + 'а'
        if (needFatherName)
            fatherName = getRandomFrom(femaleFatherNames, random)
    }
    String fullname = Arrays.asList(name, surname, fatherName).stream()
            .filter({ s -> !s.isBlank() })
            .collect(Collectors.joining(" "));

    newValues.add(fullname)
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