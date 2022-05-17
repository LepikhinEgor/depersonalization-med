package scripts.booleanType

Random random = new Random()
List newValues = new ArrayList()
for (def oldValue: oldValues) {
    newValues.add(random.nextBoolean())
}

return newValues


