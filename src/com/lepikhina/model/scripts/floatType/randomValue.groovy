package com.lepikhina.model.scripts.floatType

import java.util.stream.Collectors

Random random = new Random()

return random.doubles(oldValues.size(), minValue, maxValue).boxed().collect(Collectors.toList())

