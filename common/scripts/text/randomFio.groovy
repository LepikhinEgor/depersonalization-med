package scripts.text

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

def femaleNames = linesFromFile("common/fio/femaleNames.txt")
def maleNames = linesFromFile("common/fio/maleNames.txt")
def surnameNames = linesFromFile("common/fio/surnames.txt")
def femaleFatherNames = linesFromFile("common/fio/femaleFatherNames.txt")
def maleFatherNames = linesFromFile("common/fio/maleFatherNames.txt")

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
    String fullname = Arrays.asList(surname, name, fatherName).stream()
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