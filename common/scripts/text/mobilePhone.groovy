package scripts.text

String alphabet = "0123456789"
Random random = new Random()
List newValues = new ArrayList()
for (def oldValue: oldValues) {
    def telNumber = "+" + countryCode + generatePhoneNumber(alphabet, 10, random)
    newValues.add(telNumber)
}

return newValues


static String generatePhoneNumber(String digits, def length, Random random)
{
    char[] text = new char[length];
    for (int i = 0; i < length; i++)
    {
        text[i] = digits.charAt(random.nextInt(digits.length()));
    }
    return new String(text);
}