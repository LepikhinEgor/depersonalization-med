package scripts.number

import java.util.stream.Collectors

Random random = new Random()

return random.ints(oldValues.size(), minValue.toInteger(), maxValue.toInteger()).boxed().collect(Collectors.toList())



