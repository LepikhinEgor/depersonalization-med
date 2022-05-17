package scripts.text

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

def femaleNames = linesFromFile("common/fio/femaleNames.txt")
def maleNames = linesFromFile("common/fio/maleNames.txt")
def surnameNames = linesFromFile("common/fio/surnames.txt")

def translit = ['а': 'a', 'б': 'b', 'в': 'v', 'г': 'g', 'д': 'd', 'е': 'e', 'ё': 'e',
                'ж': 'zh', 'з': 'z', 'и': 'i', 'й': 'y', 'к': 'k', 'л': 'l', 'м': 'm',
                'н': 'n', 'о': 'o', 'п': 'p', 'р': 'r', 'с': 's', 'т': 't', 'у': 'u',
                'ф': 'ph', 'х': 'h', 'ц': 'ts', 'ч': 'ch', 'ш': 'sh', 'щ': 'sch', 'ъ': '',
                'ы': 'i', 'ь': '\'', 'э': 'e', 'ю': 'u', 'я': 'ya',
]

def hosts = ["yandex.ru", "mail.ru", "gmail.com"]

Random random = new Random()

List newValues = new ArrayList()
for (def oldValue : oldValues) {
    def name
    def surname
    def fatherName

    boolean isMale = random.nextBoolean()
    if (isMale) {
        name = getRandomFrom(maleNames, random)
        surname = getRandomFrom(surnameNames, random)
    } else {
        name = getRandomFrom(femaleNames, random)
        surname = getRandomFrom(surnameNames, random) + 'а'
    }
    def nameEng = getTranslit(name.toLowerCase(), translit)
    def surnameEng = getTranslit(surname.toLowerCase(), translit)
    def host = getRandomFrom(hosts, random)

    def email = "$nameEng.$surnameEng@$host".toString()

    newValues.add(email)
}

return newValues


private String getTranslit(String original, Map translit) {
    return original.toList().stream()
            .map({ rus -> translit.get(rus) })
            .collect(Collectors.joining())
}

private static String getRandomFrom(List<String> values, Random random) {
    def randomNumber = random.nextInt(values.size())

    return values.get(randomNumber)
}

private static List<String> linesFromFile(String filePath) {
    BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(filePath), StandardCharsets.UTF_8));
    try {
        return br.lines().collect(Collectors.toList())
    } catch (Exception e) {
        return Collections.emptyList()
    } finally {
        br.close()
    }
}