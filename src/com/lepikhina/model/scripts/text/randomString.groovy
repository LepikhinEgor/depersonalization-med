package com.lepikhina.model.scripts.text

List newValues = new ArrayList()
for (def oldValue: oldValues) {
    newValues.add(generateString(alphabet, length))
}

return newValues


static String generateString(String characters, def length)
{
    Random random = new Random()
    char[] text = new char[length];
    for (int i = 0; i < length; i++)
    {
        text[i] = characters.charAt(random.nextInt(characters.length()));
    }
    return new String(text);
}
