package com.lepikhina.model.scripts

String alphabet = "abcdefghijklmnopqrstuvxyz"

List newValues = new ArrayList()
for (def oldValue: oldValues) {
    newValues.add(generateString(alphabet, 10))
}

return newValues


static String generateString(String characters, int length)
{
    Random random = new Random()
    char[] text = new char[length];
    for (int i = 0; i < length; i++)
    {
        text[i] = characters.charAt(random.nextInt(characters.length()));
    }
    return new String(text);
}
