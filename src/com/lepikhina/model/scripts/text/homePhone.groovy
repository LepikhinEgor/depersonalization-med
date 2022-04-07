package com.lepikhina.model.scripts.text

String alphabet = "0123456789"

Random random = new Random()

List newValues = new ArrayList()

for (def oldValue : oldValues) {
    def telNumber = generatePhoneNumber(alphabet, length, random)
    newValues.add(telNumber)
}

return newValues


static String generatePhoneNumber(String digits, def length, Random random) {
    char[] text = new char[length]

    text[0] = digits.charAt(random.nextInt(digits.length() - 1)) + 1 //первая цифра не 0

    for (int i = 1; i < length; i++) {
        text[i] = digits.charAt(random.nextInt(digits.length()));
    }
    return new String(text);
}