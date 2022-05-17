package scripts.text

List newValues = new ArrayList()
for (def oldValue: oldValues) {
    newValues.add(UUID.randomUUID().toString())
}

return newValues
