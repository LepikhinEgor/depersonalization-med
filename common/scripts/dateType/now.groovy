package scripts.dateType

import java.time.LocalDateTime

List newValues = new ArrayList()

for (def oldValue : oldValues) {
    newValues.add(LocalDateTime.now())
}

return newValues